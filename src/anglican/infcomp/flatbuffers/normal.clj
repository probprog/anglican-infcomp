(ns anglican.infcomp.flatbuffers.normal
  (:require [anglican.infcomp.flatbuffers.core :as fbs])
  (:import [infcomp.protocol Distribution Normal]
           [java.nio ByteBuffer]))

(deftype NormalClj [prior-mean prior-std proposal-mean proposal-std]
  fbs/PPackBuilder
  (pack-builder [this builder] (do
                                 (Normal/startNormal builder)
                                 (if prior-mean
                                   (Normal/addPriorMean builder prior-mean))
                                 (if prior-std
                                   (Normal/addPriorStd builder prior-std))
                                 (if proposal-mean
                                   (Normal/addProposalMean builder proposal-mean))
                                 (if proposal-std
                                   (Normal/addProposalStd builder proposal-std))
                                 (Normal/endNormal builder)))

  fbs/PDistributionType
  (distribution-type [this] Distribution/Normal))

(extend-type Normal
  fbs/PUnpack
  (unpack [this] (let [prior-mean (.priorMean this)
                       prior-std (.priorStd this)
                       proposal-mean (.proposalMean this)
                       proposal-std (.proposalStd this)]
                   (NormalClj. prior-mean prior-std proposal-mean proposal-std))))
