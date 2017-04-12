(ns anglican.infcomp.csis
  "Compiled Sequential Importance Sampling"
  (:refer-clojure :exclude [rand rand-int rand-nth])
  (:require [clojure.string :as str]
            [clojure.core.matrix :as m]
            [zeromq.zmq :as zmq]
            [anglican.infcomp.flatbuffers.protocols :as fbs]
            [msgpack clojure-extensions]
            [anglican.runtime :refer [sample* observe*]]
            [anglican.inference :refer [checkpoint infer exec]]
            [anglican.state :refer [add-log-weight]]
            [anglican.infcomp.proposal :refer [get-proposal]]
            anglican.infcomp.flatbuffers.observes-init-request
            anglican.infcomp.flatbuffers.ndarray
            anglican.infcomp.flatbuffers.proposal-request
            anglican.infcomp.flatbuffers.sample
            anglican.infcomp.flatbuffers.normal-proposal
            anglican.infcomp.flatbuffers.uniform-discrete-proposal
            anglican.infcomp.flatbuffers.message
            anglican.infcomp.flatbuffers.proposal-reply)
  (:import [anglican.infcomp.flatbuffers.observes_init_request ObservesInitRequestClj]
           [anglican.infcomp.flatbuffers.ndarray NDArrayClj]
           [anglican.infcomp.flatbuffers.proposal_request ProposalRequestClj]
           [anglican.infcomp.flatbuffers.sample SampleClj]
           [anglican.infcomp.flatbuffers.normal_proposal NormalProposalClj]
           [anglican.infcomp.flatbuffers.uniform_discrete_proposal UniformDiscreteProposalClj]
           [anglican.infcomp.flatbuffers.message MessageClj]
           [anglican.infcomp.flatbuffers.proposal_reply ProposalReplyClj]))

(derive ::algorithm :anglican.inference/algorithm)

(def initial-state
  "initial state for Compiled SIS (CUDA)"
  (into anglican.state/initial-state
        {::tcp-endpoint nil
         ::context nil
         ::socket nil
         ::samples []}))

(defmethod checkpoint [::algorithm anglican.trap.sample] [_ smp]
  (let [state (:state smp)
        samples (::samples state)

        sample-address (str (:id smp))
        sample-instance (inc (count (filter #(= sample-address (:sample-address %))
                                            samples)))
        socket (::socket state)

        ;; Prepare message
        proposal (get-proposal (:dist smp))
        proposal-name (:proposal-name proposal)
        prev-sample-value (:value (last samples) 0)
        prev-sample-address (:sample-address (last samples) "")
        prev-sample-instance (:sample-instance (last samples) 0)
        _ (zmq/send socket (fbs/pack (MesssageClj.
                                      (ProposalRequestClj.
                                       (SampleClj. nil
                                                   sample-address
                                                   sample-instance
                                                   (cond
                                                    (= proposal-name "normal")
                                                    (NormalProposalClj. nil nil)

                                                    (= proposal-name "discreteminmax")
                                                    (UniformDiscreteProposalClj. nil nil nil))
                                                   nil)
                                       (SampleClj. nil
                                                   prev-sample-address
                                                   prev-sample-instance
                                                   nil
                                                   (let [value prev-sample-value
                                                         value (if (number? value)
                                                                 [value] value)
                                                         data (flatten value)
                                                         shape (m/shape data)]
                                                     (NDArrayClj. data shape))))))
        proposal-extra-params (:proposal-extra-params proposal)
        proposal-params-from-torch (let [message-body (.body (fbs/unpack (zmq/receive socket)))]
                                     (assert (instance? ProposalReplyClj message-body))
                                     (.proposal message-body))
        proposal-params (case proposal-name
                          "categorical" (list (mapv vector (second proposal-extra-params))
                                              (take (first proposal-extra-params)
                                                    proposal-params-from-torch))
                          "continuousminmax" (let [normalised-mode (first proposal-params-from-torch)
                                                   certainty (second proposal-params-from-torch)
                                                   min (first proposal-extra-params)
                                                   max (second proposal-extra-params)
                                                   mode (+ min (* (- max min) normalised-mode))]
                                               [min max mode certainty])
                          "dirichlet" (take (first proposal-extra-params)
                                            proposal-params-from-torch)
                          "discreteminmax" [(first proposal-extra-params)
                                            (m/reshape (.data (.probabilities proposal-params-from-torch))
                                                       (.shape (.probabilities proposal-params-from-torch)))]
                          "flip" proposal-params-from-torch
                          "foldednormal" proposal-params-from-torch
                          "foldednormaldiscrete" proposal-params-from-torch
                          "mvn" (let [mean (vec (first proposal-params-from-torch))
                                      dim (first proposal-extra-params)
                                      pre-cov (vec (map vec (second proposal-params-from-torch)))
                                      cov (m/add pre-cov (m/transpose pre-cov) (m/mmul dim (m/identity-matrix dim)))]
                                  [mean cov])
                          "mvnmeanvars" (let [mean (vec (first proposal-params-from-torch))
                                              vars (vec (second proposal-params-from-torch))]
                                          [mean vars])
                          "mvnmeanvar" (let [mean (vec (first proposal-params-from-torch))
                                             var (second proposal-params-from-torch)]
                                         [mean var])
                          "normal" [(.mean proposal-params-from-torch) (.std proposal-params-from-torch)]
                          :unimplemented)
        proposal-dist (apply (:proposal-constructor proposal) proposal-params)
        value (sample* proposal-dist)
        log-q (observe* proposal-dist value)
        log-p (observe* (:dist smp) value)
        updated-state (update-in state
                                 [::samples]
                                 conj
                                 (array-map :sample-address sample-address
                                            :sample-instance sample-instance
                                            :value value
                                            :proposal-params proposal-params))

        ;; Modify weights
        weight-update (- log-p log-q)
        updated-state (add-log-weight updated-state weight-update)]
    #((:cont smp) value updated-state)))

;; From http://stackoverflow.com/questions/14488150/how-to-write-a-dissoc-in-command-for-clojure
(defn- dissoc-in
  "Dissociates an entry from a nested associative structure returning a new
  nested structure. keys is a sequence of keys. Any empty maps that result
  will not be present in the new structure."
  [m [k & ks :as keys]]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (dissoc-in nextmap ks)]
        (if (seq newmap)
          (assoc m k newmap)
          (dissoc m k)))
      m)
    (dissoc m k)))

(defmethod checkpoint [::algorithm anglican.trap.result] [_ res]
  (zmq/close (::socket (:state res)))
  (.term (::context (:state res)))
  (-> res
      (dissoc-in [:state ::context])
      (dissoc-in [:state ::socket])
      (dissoc-in [:state ::tcp-endpoint])
      #_(dissoc-in [:state ::samples])))

(defmethod infer :csis [_ prog value & {:keys [tcp-endpoint observe-embedder-input]
                                        :or {tcp-endpoint "tcp://localhost:6666"}}]
  (letfn [(sample-seq []
                      (lazy-seq
                       (cons
                        (:state (exec ::algorithm prog value (into initial-state
                                                                   (let [context (zmq/context 1)
                                                                         socket (doto (zmq/socket context :req)
                                                                                  (zmq/connect tcp-endpoint))
                                                                         observe-embedder-input (or observe-embedder-input (first value))]
                                                                     (zmq/send socket (fbs/pack (ObservesInitRequestClj.
                                                                                                 (NDArrayClj.
                                                                                                  (flatten observe-embedder-input)
                                                                                                  (m/shape observe-embedder-input)))))
                                                                     (zmq/receive socket)
                                                                     {::context context
                                                                      ::socket socket
                                                                      ::tcp-endpoint tcp-endpoint}))))
                        (sample-seq))))]
    (sample-seq)))
