(ns anglican.infcomp.flatbuffers.uniform-discrete
  (:require [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [infcomp.flatbuffers Distribution UniformDiscrete]
           [java.nio ByteBuffer]))

(deftype UniformDiscreteClj [prior-min prior-size proposal-probabilities]
  p/PPackBuilder
  (pack-builder [this builder] (let [packed-proposal-probabilities (if proposal-probabilities
                                                                     (p/pack-builder proposal-probabilities builder))]
                                 (UniformDiscrete/startUniformDiscrete builder)
                                 (if prior-min
                                   (UniformDiscrete/addPriorMin builder prior-min))
                                 (if prior-size
                                   (UniformDiscrete/addPriorSize builder prior-size))
                                 (if proposal-probabilities
                                   (UniformDiscrete/addProposalProbabilities builder packed-proposal-probabilities))
                                 (UniformDiscrete/endUniformDiscrete builder)))

  p/PDistributionType
  (distribution-type [this] Distribution/UniformDiscrete))

(extend-type UniformDiscrete
  p/PUnpack
  (unpack [this] (let [prior-min (.priorMin this)
                       prior-size (.priorSize this)
                       proposal-probabilities (p/unpack (.proposalProbabilities this))]
                   (UniformDiscreteClj. prior-min prior-size proposal-probabilities))))
