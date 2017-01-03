(ns anglican.csis.csis
  "Compiled Sequential Importance Sampling"
  (:refer-clojure :exclude [rand rand-int rand-nth])
  (:require [clojure.string :as str]
            [clojure.core.matrix :as m]
            [zeromq.zmq :as zmq]
            [msgpack.core :as msg]
            [msgpack clojure-extensions]
            [anglican.runtime :refer [sample* observe*]]
            [anglican.inference :refer [checkpoint infer exec]]
            [anglican.state :refer [add-log-weight]]
            [anglican.csis.proposal :refer [get-proposal]]))

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
        prev-sample-address (:sample-address (last samples) 0)
        prev-sample-instance (:sample-instance (last samples) 0)
        _ (zmq/send socket (msg/pack {"command" "proposal-params"
                                      "command-param" {"sample-address" sample-address
                                                       "sample-instance" sample-instance
                                                       "prev-sample-value" prev-sample-value
                                                       "prev-sample-address" prev-sample-address
                                                       "prev-sample-instance" prev-sample-instance
                                                       "proposal-name" proposal-name}}))
        proposal-extra-params (:proposal-extra-params proposal)
        proposal-params-from-torch (msg/unpack (zmq/receive socket))
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
                                            (take (- (second proposal-extra-params) (first proposal-extra-params))
                                                  proposal-params-from-torch)]
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
                          "normal" proposal-params-from-torch
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
                                        :or {tcp-endpoint "tcp://localhost:6666"
                                             observe-embedder-input nil}}]
  (letfn [(sample-seq []
                      (lazy-seq
                       (cons
                        (:state (exec ::algorithm prog value (into initial-state
                                                                   (let [context (zmq/context 1)
                                                                         socket (doto (zmq/socket context :req)
                                                                                  (zmq/connect tcp-endpoint))
                                                                         msg-pack-obs (if (nil? observe-embedder-input)
                                                                                        {"shape" (m/shape (first value)) "data" (flatten (first value))}
                                                                                        observe-embedder-input)]
                                                                     (zmq/send socket (msg/pack {"command" "observe-init"
                                                                                                 "command-param" msg-pack-obs}))
                                                                     (zmq/receive socket)
                                                                     {::context context
                                                                      ::socket socket
                                                                      ::tcp-endpoint tcp-endpoint}))))
                        (sample-seq))))]
    (sample-seq)))
