(ns anglican.infcomp.flatbuffers.laplace
  (:require [anglican.infcomp.flatbuffers.core :as fbs])
  (:import [infcomp.protocol Distribution Laplace]
           [java.nio ByteBuffer]))

(deftype LaplaceClj [prior-location prior-scale proposal-location proposal-scale]
  fbs/PPackBuilder
  (pack-builder [this builder] (do
                                 (Laplace/startLaplace builder)
                                 (if prior-location
                                   (Laplace/addPriorLocation builder prior-location))
                                 (if prior-scale
                                   (Laplace/addPriorScale builder prior-scale))
                                 (if proposal-location
                                   (Laplace/addProposalLocation builder proposal-location))
                                 (if proposal-scale
                                   (Laplace/addProposalScale builder proposal-scale))
                                 (Laplace/endLaplace builder)))

  fbs/PDistributionType
  (distribution-type [this] Distribution/Laplace))

(extend-type Laplace
  fbs/PUnpack
  (unpack [this] (let [prior-location (.priorLocation this)
                       prior-scale (.priorScale this)
                       proposal-location (.proposalLocation this)
                       proposal-scale (.proposalScale this)]
                   (LaplaceClj. prior-location prior-scale proposal-location proposal-scale))))
