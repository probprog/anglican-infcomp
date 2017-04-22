(ns anglican.infcomp.flatbuffers.flip
  (:require [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [infcomp.flatbuffers Distribution Flip]
           [java.nio ByteBuffer]))

(deftype FlipClj [proposal-probability]
  p/PPackBuilder
  (pack-builder [this builder] (do
                                 (Flip/startFlip builder)
                                 (if proposal-probability
                                   (Flip/addProposalProbability builder proposal-probability))
                                 (Flip/endFlip builder)))

  p/PDistributionType
  (distribution-type [this] Distribution/Flip))

(extend-type Flip
  p/PUnpack
  (unpack [this] (let [proposal-probability (.proposalProbability this)]
                   (FlipClj. proposal-probability))))
