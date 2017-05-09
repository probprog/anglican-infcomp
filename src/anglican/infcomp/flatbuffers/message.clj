(ns anglican.infcomp.flatbuffers.message
  (:require [anglican.infcomp.flatbuffers.core :as fbs]
            [anglican.infcomp.flatbuffers traces-from-prior-request
             traces-from-prior-reply observes-init-request observes-init-reply
             proposal-request proposal-reply])
  (:import [infcomp.protocol Message MessageBody TracesFromPriorRequest
            TracesFromPriorReply ObservesInitRequest ObservesInitReply
            ProposalRequest ProposalReply]
           [java.nio ByteBuffer]))

(deftype MessageClj [body]
  fbs/PPackBuilder
  (pack-builder [this builder] (let [packed-body (if body
                                                   (fbs/pack-builder body builder))]
                                 (Message/startMessage builder)
                                 (if body
                                   (Message/addBodyType builder (fbs/message-body-type body)))
                                 (if body
                                   (Message/addBody builder packed-body))
                                 (Message/endMessage builder))))

(extend-type (Class/forName "[B")
  fbs/PUnpackMessage
  (unpack-message [this] (let [buf (ByteBuffer/wrap this)
                               message (Message/getRootAsMessage buf)]
                           (fbs/unpack message))))

(extend-type Message
  fbs/PUnpack
  (unpack [this] (let [body-type (.bodyType this)
                       body (cond
                             (= body-type MessageBody/TracesFromPriorRequest)
                             (fbs/unpack (cast TracesFromPriorRequest (.body this (TracesFromPriorRequest.))))

                             (= body-type MessageBody/TracesFromPriorReply)
                             (fbs/unpack (cast TracesFromPriorReply (.body this (TracesFromPriorReply.))))

                             (= body-type MessageBody/ObservesInitRequest)
                             (fbs/unpack (cast ObservesInitRequest (.body this (ObservesInitRequest.))))

                             (= body-type MessageBody/ObservesInitReply)
                             (fbs/unpack (cast ObservesInitReply (.body this (ObservesInitReply.))))

                             (= body-type MessageBody/ProposalRequest)
                             (fbs/unpack (cast ProposalRequest (.body this (ProposalRequest.))))

                             (= body-type MessageBody/ProposalReply)
                             (fbs/unpack (cast ProposalReply (.body this (ProposalReply.)))))]
                   (MessageClj. body))))
