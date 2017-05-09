(ns anglican.infcomp.flatbuffers.flip
  (:require [anglican.infcomp.flatbuffers.core :as fbs])
  (:import [infcomp.protocol Distribution Flip]
           [java.nio ByteBuffer]))

(deftype FlipClj [proposal-probability]
  fbs/PPackBuilder
  (pack-builder [this builder] (do
                                 (Flip/startFlip builder)
                                 (if proposal-probability
                                   (Flip/addProposalProbability builder proposal-probability))
                                 (Flip/endFlip builder)))

  fbs/PDistributionType
  (distribution-type [this] Distribution/Flip))

(extend-type Flip
  fbs/PUnpack
  (unpack [this] (let [proposal-probability (.proposalProbability this)]
                   (FlipClj. proposal-probability))))
