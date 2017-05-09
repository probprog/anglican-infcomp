(ns anglican.infcomp.flatbuffers.uniform-continuous
  (:require [anglican.infcomp.flatbuffers.core :as fbs])
  (:import [infcomp.protocol Distribution UniformContinuous]
           [java.nio ByteBuffer]))

(deftype UniformContinuousClj [prior-min prior-max proposal-mode proposal-certainty]
  fbs/PPackBuilder
  (pack-builder [this builder] (do
                                 (UniformContinuous/startUniformContinuous builder)
                                 (if prior-min
                                   (UniformContinuous/addPriorMin builder prior-min))
                                 (if prior-max
                                   (UniformContinuous/addPriorMax builder prior-max))
                                 (if proposal-mode
                                   (UniformContinuous/addProposalMode builder proposal-mode))
                                 (if proposal-certainty
                                   (UniformContinuous/addProposalCertainty builder proposal-certainty))
                                 (UniformContinuous/endUniformContinuous builder)))

  fbs/PDistributionType
  (distribution-type [this] Distribution/UniformContinuous))

(extend-type UniformContinuous
  fbs/PUnpack
  (unpack [this] (let [prior-min (.priorMin this)
                       prior-max (.priorMax this)
                       proposal-mode (.proposalMode this)
                       proposal-certainty (.proposalCertainty this)]
                   (UniformContinuousClj. prior-min prior-max proposal-mode proposal-certainty))))
