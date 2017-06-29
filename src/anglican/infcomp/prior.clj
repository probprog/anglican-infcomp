(ns anglican.infcomp.prior
  "Non-standard interpretation for sampling from the prior."
  (:refer-clojure :exclude [rand rand-int rand-nth])
  (:require [clojure.string :as str]
            [clojure.core.matrix :as m]
            [anglican.inference :refer [checkpoint exec]]
            [anglican.runtime :refer [sample*]]
            [anglican.infcomp.proposal :refer [get-prior-distribution-clj]]
            [clojure.walk :as walk]
            [anglican.infcomp.flatbuffers.ndarray :as ndarray]
            [anglican.infcomp.flatbuffers traces-from-prior-reply trace sample])
  (:import anglican.infcomp.flatbuffers.traces_from_prior_reply.TracesFromPriorReplyClj
           anglican.infcomp.flatbuffers.trace.TraceClj
           anglican.infcomp.flatbuffers.sample.SampleClj))

(derive ::algorithm :anglican.inference/algorithm)

(defmethod checkpoint [::algorithm anglican.trap.observe] [_ obs]
  "Overrides the observe checkpoint in order to sample from the specified
  distribution instead of observing under it.

  Stores samples in state."
  (let [state (:state obs)
        observe-address (:id obs)
        observe-instance (count (filter #(= observe-address (:observe-address %))
                                        (:observes state)))
        time-index (count (:observes state))
        value (sample* (:dist obs))
        updated-state (update-in state
                                 [:observes]
                                 conj
                                 (array-map :time-index time-index
                                            :observe-address observe-address
                                            :observe-instance observe-instance
                                            :value (condp = (type (:dist obs))
                                                     anglican.runtime.categorical-distribution (get (:index (:dist obs)) value)
                                                     anglican.runtime.flip-distribution (if value 1 0)
                                                     value)))]
    #((:cont obs) value updated-state)))

(defmethod checkpoint [::algorithm anglican.trap.sample] [_ smp]
  "Samples from the specified prior distribution.

  Stores samples in state."
  (let [state (:state smp)
        sample-address (str (:id smp))
        sample-instance (count (filter #(= sample-address (:sample-address %))
                                       (:samples state)))
        prior-distribution-clj (get-prior-distribution-clj (:dist smp))
        time-index (count (:samples state))
        value (sample* (:dist smp))
        updated-state (update-in state
                                 [:samples]
                                 conj
                                 (array-map :time-index time-index
                                            :sample-address sample-address
                                            :sample-instance sample-instance
                                            :distribution prior-distribution-clj
                                            :value (condp = (type (:dist smp))
                                                     anglican.runtime.categorical-distribution (get (:index (:dist smp)) value)
                                                     anglican.runtime.flip-distribution (if value 1 0)
                                                     value)))]
    #((:cont smp) value updated-state)))


(defmethod checkpoint [::algorithm anglican.trap.result] [_ res]
  res)

(defn sample-from-prior
  "Returns infinite lazy sequence of samples from the joint distribution over
  all random variables from prior of a probabilistic program query given the
  query arguments query-args.

  Performs non-standard interpretation where observe statements perform
  sampling."
  [query query-args]
  (letfn [(sample-seq []
                      (lazy-seq
                       (let [state (:state (exec ::algorithm query query-args (into anglican.state/initial-state
                                                                                    {:samples [] :observes []})))
                             state-without-predicts-and-result (dissoc state :predicts :result)]
                         (cons state-without-predicts-and-result
                               (sample-seq)))))]
    (sample-seq)))

(defn sample-observes-from-prior
  "Returns a sample from the random variables defined via the observe
  statements in query given the query arguments query-args."
  [query query-args]
  (:observes (first (sample-from-prior query query-args))))

(defn sample-samples-from-prior
  "Returns a sample from the random variables defined via the sample
  statements in query given the query arguments query-args."
  [query query-args]
  (:samples (first (sample-from-prior query query-args))))

(defn generate-traces-from-prior-reply
  [query query-args combine-observes-fn combine-samples-fn num-traces]
  (let [prior-samples (walk/stringify-keys
                       (map (comp
                             ;; Update observes via the combine-observes-fn
                             (fn [smp]
                               (update smp
                                       :observes
                                       combine-observes-fn))

                             ;; Update samples via the combine-samples-fn
                             (fn [smp]
                               (update smp
                                       :samples
                                       combine-samples-fn)))
                            (take num-traces
                                  (sample-from-prior query query-args))))
        traces-from-prior-reply (TracesFromPriorReplyClj.
                                 (map (fn [trace]
                                        (TraceClj.
                                         (ndarray/to-NDArrayClj (get trace "observes"))
                                         (map (fn [sample]
                                                (SampleClj.
                                                 (get sample "time-index")
                                                 (get sample "sample-address")
                                                 (get sample "sample-instance")
                                                 (get sample "distribution")
                                                 (ndarray/to-NDArrayClj (get sample "value"))))
                                              (get trace "samples"))))
                                      prior-samples))]
    traces-from-prior-reply))
