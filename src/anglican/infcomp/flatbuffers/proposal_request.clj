(ns anglican.infcomp.flatbuffers.proposal-request
  (:require [anglican.infcomp.flatbuffers.core :as fbs])
  (:import [infcomp.protocol MessageBody ProposalRequest]
           [java.nio ByteBuffer]))

(deftype ProposalRequestClj [current-sample previous-sample]
  fbs/PPackBuilder
  (pack-builder [this builder] (let [current-sample-packed (if current-sample
                                                             (fbs/pack-builder current-sample builder))
                                     previous-sample-packed (if previous-sample
                                                              (fbs/pack-builder previous-sample builder))]
                                 (ProposalRequest/startProposalRequest builder)
                                 (if current-sample
                                   (ProposalRequest/addCurrentSample builder current-sample-packed))
                                 (if previous-sample
                                   (ProposalRequest/addPreviousSample builder previous-sample-packed))
                                 (ProposalRequest/endProposalRequest builder)))

  fbs/PMessageBodyType
  (message-body-type [this] MessageBody/ProposalRequest))

(extend-type ProposalRequest
  fbs/PUnpack
  (unpack [this] (let [current-sample (fbs/unpack (.currentSample this))
                       previous-sample (fbs/unpack (.previousSample this))]
                   (ProposalRequestClj. current-sample previous-sample))))
