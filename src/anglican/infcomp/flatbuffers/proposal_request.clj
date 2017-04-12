(ns anglican.infcomp.flatbuffers.proposal-request
  (:require [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [infcomp.flatbuffers MessageBody ProposalRequest]
           [java.nio ByteBuffer]))

(deftype ProposalRequestClj [current-sample previous-sample]
  p/PPackBuilder
  (pack-builder [this builder] (let [current-sample-packed (if current-sample
                                                             (p/pack-builder current-sample builder))
                                     previous-sample-packed (if previous-sample
                                                             (p/pack-builder previous-sample builder))]
                                 (ProposalRequest/startProposalRequest builder)
                                 (if current-sample
                                   (ProposalRequest/addCurrentSample builder current-sample-packed))
                                 (if previous-sample
                                   (ProposalRequest/addPreviousSample builder previous-sample-packed))
                                 (ProposalRequest/endProposalRequest builder)))

  p/PMessageBodyType
  (message-body-type [this] MessageBody/ProposalRequest))

(extend-type ProposalRequest
  p/PUnpack
  (unpack [this] (let [current-sample (p/unpack (.currentSample this))
                       previous-sample (p/unpack (.previousSample this))]
                   (ProposalRequestClj. current-sample previous-sample))))
