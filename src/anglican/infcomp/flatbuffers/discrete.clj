(ns anglican.infcomp.flatbuffers.discrete
  (:require [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [infcomp.flatbuffers Distribution Discrete]
           [java.nio ByteBuffer]))

(deftype DiscreteClj [prior-size proposal-probabilities]
  p/PPackBuilder
  (pack-builder [this builder] (let [packed-proposal-probabilities (if proposal-probabilities
                                                            (p/pack-builder proposal-probabilities builder))]
                                 (Discrete/startDiscrete builder)
                                 (if prior-size
                                   (Discrete/addPriorSize builder prior-size))
                                 (if proposal-probabilities
                                   (Discrete/addProposalProbabilities builder packed-proposal-probabilities))
                                 (Discrete/endDiscrete builder)))

  p/PDistributionType
  (distribution-type [this] Distribution/Discrete))

(extend-type Discrete
  p/PUnpack
  (unpack [this] (let [prior-size (.priorSize this)
                       proposal-probabilities (p/unpack (.proposalProbabilities this))]
                   (DiscreteClj. prior-size proposal-probabilities))))
