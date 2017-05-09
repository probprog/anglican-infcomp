(ns anglican.infcomp.flatbuffers.discrete
  (:require [anglican.infcomp.flatbuffers.core :as fbs])
  (:import [infcomp.protocol Distribution Discrete]
           [java.nio ByteBuffer]))

(deftype DiscreteClj [prior-size proposal-probabilities]
  fbs/PPackBuilder
  (pack-builder [this builder] (let [packed-proposal-probabilities (if proposal-probabilities
                                                                     (fbs/pack-builder proposal-probabilities builder))]
                                 (Discrete/startDiscrete builder)
                                 (if prior-size
                                   (Discrete/addPriorSize builder prior-size))
                                 (if proposal-probabilities
                                   (Discrete/addProposalProbabilities builder packed-proposal-probabilities))
                                 (Discrete/endDiscrete builder)))

  fbs/PDistributionType
  (distribution-type [this] Distribution/Discrete))

(extend-type Discrete
  fbs/PUnpack
  (unpack [this] (let [prior-size (.priorSize this)
                       proposal-probabilities (fbs/unpack (.proposalProbabilities this))]
                   (DiscreteClj. prior-size proposal-probabilities))))
