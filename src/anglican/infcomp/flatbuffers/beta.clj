(ns anglican.infcomp.flatbuffers.beta
  (:require [anglican.infcomp.flatbuffers.core :as fbs])
  (:import [infcomp.protocol Distribution Beta]
           [java.nio ByteBuffer]))

(deftype BetaClj [proposal-mode proposal-certainty]
  fbs/PPackBuilder
  (pack-builder [this builder] (do
                                 (Beta/startBeta builder)
                                 (if proposal-mode
                                   (Beta/addProposalMode builder proposal-mode))
                                 (if proposal-certainty
                                   (Beta/addProposalCertainty builder proposal-certainty))
                                 (Beta/endBeta builder)))

  fbs/PDistributionType
  (distribution-type [this] Distribution/Beta))

(extend-type Beta
  fbs/PUnpack
  (unpack [this] (let [proposal-mode (.proposalMode this)
                       proposal-certainty (.proposalCertainty this)]
                   (BetaClj. proposal-mode proposal-certainty))))
