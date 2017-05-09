(ns anglican.infcomp.flatbuffers.uniform-discrete
  (:require [anglican.infcomp.flatbuffers.core :as fbs])
  (:import [infcomp.protocol Distribution UniformDiscrete]
           [java.nio ByteBuffer]))

(deftype UniformDiscreteClj [prior-min prior-size proposal-probabilities]
  fbs/PPackBuilder
  (pack-builder [this builder] (let [packed-proposal-probabilities (if proposal-probabilities
                                                                     (fbs/pack-builder proposal-probabilities builder))]
                                 (UniformDiscrete/startUniformDiscrete builder)
                                 (if prior-min
                                   (UniformDiscrete/addPriorMin builder prior-min))
                                 (if prior-size
                                   (UniformDiscrete/addPriorSize builder prior-size))
                                 (if proposal-probabilities
                                   (UniformDiscrete/addProposalProbabilities builder packed-proposal-probabilities))
                                 (UniformDiscrete/endUniformDiscrete builder)))

  fbs/PDistributionType
  (distribution-type [this] Distribution/UniformDiscrete))

(extend-type UniformDiscrete
  fbs/PUnpack
  (unpack [this] (let [prior-min (.priorMin this)
                       prior-size (.priorSize this)
                       proposal-probabilities (fbs/unpack (.proposalProbabilities this))]
                   (UniformDiscreteClj. prior-min prior-size proposal-probabilities))))
