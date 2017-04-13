(ns anglican.infcomp.csis
  "Compiled Sequential Importance Sampling"
  (:refer-clojure :exclude [rand rand-int rand-nth])
  (:require [clojure.string :as str]
            [clojure.core.matrix :as m]
            [zeromq.zmq :as zmq]
            [anglican.infcomp.flatbuffers.protocols :as fbs]
            [anglican.runtime :refer [sample* observe*]]
            [anglican.inference :refer [checkpoint infer exec]]
            [anglican.state :refer [add-log-weight]]
            [clojure.tools.logging :as log]
            [anglican.infcomp.proposal :refer [get-proposal get-proposal-constructor]]
            [anglican.infcomp.flatbuffers.ndarray :refer [to-NDArrayClj from-NDArrayClj]]
            [anglican.infcomp.flatbuffers observes-init-request ndarray proposal-request sample normal-proposal uniform-discrete-proposal message proposal-reply])
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
        socket (::socket state)
        samples (::samples state)

        sample-address (str (:id smp))
        sample-instance (count (filter #(= sample-address (:sample-address %))
                                       samples))

        ;; Prepare message
        prior-dist (:dist smp)
        proposal (get-proposal prior-dist)
        prev-sample-value (:value (last samples) -1)
        prev-sample-address (:sample-address (last samples) "")
        prev-sample-instance (:sample-instance (last samples) -1)
        _ (zmq/send socket (fbs/pack (MessageClj.
                                      (ProposalRequestClj.
                                       (SampleClj. nil
                                                   sample-address
                                                   sample-instance
                                                   proposal
                                                   nil)
                                       (SampleClj. nil
                                                   prev-sample-address
                                                   prev-sample-instance
                                                   nil
                                                   (to-NDArrayClj prev-sample-value))))))
        proposal-dist (let [proposal-reply (.body (fbs/unpack-message (zmq/receive socket)))]
                        (assert (instance? ProposalReplyClj proposal-reply))
                        (if (.success proposal-reply)
                          (let [proposal-from-nn (.proposal proposal-reply)
                                proposal-params (condp = (type proposal)
                                                  UniformDiscreteProposalClj [(.min proposal)
                                                                              (.max proposal)
                                                                              (from-NDArrayClj (.probabilities proposal-from-nn))]
                                                  NormalProposalClj [(.mean proposal-from-nn) (.std proposal-from-nn)])]
                            (apply (get-proposal-constructor prior-dist) proposal-params))
                          (do
                            (log/warn (str "Proposal parameters for " prior-dist " is not available: Using prior proposal instead."))
                            prior-dist)))
        value (sample* proposal-dist)
        log-q (observe* proposal-dist value)
        log-p (observe* prior-dist value)
        updated-state (update-in state
                                 [::samples]
                                 conj
                                 (array-map :sample-address sample-address
                                            :sample-instance sample-instance
                                            :value value))

        ;; Modify weights
        weight-update (- log-p log-q)
        updated-state (add-log-weight updated-state weight-update)]
    #((:cont smp) value updated-state)))

(defn- dissoc-in
  "Dissociates an entry from a nested associative structure returning a new
  nested structure. keys is a sequence of keys. Any empty maps that result
  will not be present in the new structure. Taken from:

  http://stackoverflow.com/questions/14488150/how-to-write-a-dissoc-in-command-for-clojure"
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
      (dissoc-in [:state ::tcp-endpoint])))

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
                                                                     (zmq/send socket (fbs/pack (MessageClj.
                                                                                                 (ObservesInitRequestClj.
                                                                                                  (to-NDArrayClj observe-embedder-input)))))
                                                                     (zmq/receive socket)
                                                                     {::context context
                                                                      ::socket socket
                                                                      ::tcp-endpoint tcp-endpoint}))))
                        (sample-seq))))]
    (sample-seq)))
