(ns anglican.csis.prior
  "Non-standard interpretation for sampling from the prior."
  (:refer-clojure :exclude [rand rand-int rand-nth])
  (:require [clojure.string :as str]
            [clojure.core.matrix :as m]
            [anglican.inference :refer [checkpoint exec]]
            [anglican.runtime :refer [sample*]]
            [anglican.csis.proposal :refer [get-proposal]]))

(derive ::algorithm :anglican.inference/algorithm)

(defmethod checkpoint [::algorithm anglican.trap.observe] [_ obs]
  "Overrides the observe checkpoint in order to sample from the specified
  distribution instead of observing under it.

  Stores samples in state."
  (let [state (:state obs)
        observe-address (:id obs)
        number (inc (count (filter #(= observe-address (:observe-address %))
                                   (:observes state))))
        time-index (inc (count (:observes state)))
        value (sample* (:dist obs))
        updated-state (update-in state
                                 [:observes]
                                 conj
                                 (array-map :time-index time-index
                                            :observe-address observe-address
                                            :number number
                                            :value value))]
    #((:cont obs) value updated-state)))

(defmethod checkpoint [::algorithm anglican.trap.sample] [_ smp]
  "Samples from the specified prior distribution.

  Stores samples in state."
  (let [state (:state smp)
        sample-address (:id smp)
        sample-instance (inc (count (filter #(= sample-address (:sample-address %))
                                            (:samples state))))
        proposal (get-proposal (:dist smp))
        time-index (inc (count (:samples state)))
        value (sample* (:dist smp))
        updated-state (update-in state
                                 [:samples]
                                 conj
                                 (array-map :time-index time-index
                                            :sample-address sample-address
                                            :sample-instance sample-instance
                                            :prior-dist-str (:prior-name proposal)
                                            :proposal-name (:proposal-name proposal)
                                            :proposal-extra-params (:proposal-extra-params proposal)
                                            :value value))]
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
                       (let [state (:state (exec ::algorithm query query-args {:samples [] :observes []}))
                             state-without-predicts (dissoc state :predicts)]
                         (cons state-without-predicts
                               (sample-seq)))))]
    (sample-seq)))