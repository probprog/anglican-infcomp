(ns anglican.infcomp.flatbuffers.gamma
  (:require [anglican.infcomp.flatbuffers.core :as fbs])
  (:import [infcomp.protocol Distribution Gamma]
           [java.nio ByteBuffer]))

(deftype GammaClj [proposal-location proposal-scale]
  fbs/PPackBuilder
  (pack-builder [this builder] (do
                                 (Gamma/startGamma builder)
                                 (if proposal-location
                                   (Gamma/addProposalLocation builder proposal-location))
                                 (if proposal-scale
                                   (Gamma/addProposalScale builder proposal-scale))
                                 (Gamma/endGamma builder)))

  fbs/PDistributionType
  (distribution-type [this] Distribution/Gamma))

(extend-type Gamma
  fbs/PUnpack
  (unpack [this] (let [proposal-location (.proposalLocation this)
                       proposal-scale (.proposalScale this)]
                   (GammaClj. proposal-location proposal-scale))))
