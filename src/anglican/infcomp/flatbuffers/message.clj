(ns anglican.infcomp.flatbuffers.message
  (:require [anglican.infcomp.flatbuffers.protocols :as p]
            [anglican.infcomp.flatbuffers traces-from-prior-request traces-from-prior-reply observes-init-request observes-init-reply proposal-request proposal-reply])
  (:import [infcomp.flatbuffers Message MessageBody TracesFromPriorRequest TracesFromPriorReply ObservesInitRequest ObservesInitReply ProposalRequest ProposalReply]
           [java.nio ByteBuffer]))

(deftype MessageClj [body]
  p/PPackBuilder
  (pack-builder [this builder] (let [packed-body (if body
                                                    (p/pack-builder body builder))]
                                 (Message/startMessage builder)
                                 (if body
                                   (Message/addBodyType builder (p/message-body-type body)))
                                 (if body
                                   (Message/addBody builder packed-body))
                                 (Message/endMessage builder))))

(extend-type (Class/forName "[B")
  p/PUnpackMessage
  (unpack-message [this] (let [buf (ByteBuffer/wrap this)
                               message (Message/getRootAsMessage buf)]
                           (p/unpack message))))

(extend-type Message
  p/PUnpack
  (unpack [this] (let [body-type (.bodyType this)
                       body (cond
                              (= body-type MessageBody/TracesFromPriorRequest)
                              (p/unpack (cast TracesFromPriorRequest (.body this (TracesFromPriorRequest.))))

                              (= body-type MessageBody/TracesFromPriorReply)
                              (p/unpack (cast TracesFromPriorReply (.body this (TracesFromPriorReply.))))

                              (= body-type MessageBody/ObservesInitRequest)
                              (p/unpack (cast ObservesInitRequest (.body this (ObservesInitRequest.))))

                              (= body-type MessageBody/ObservesInitReply)
                              (p/unpack (cast ObservesInitReply (.body this (ObservesInitReply.))))

                              (= body-type MessageBody/ProposalRequest)
                              (p/unpack (cast ProposalRequest (.body this (ProposalRequest.))))

                              (= body-type MessageBody/ProposalReply)
                              (p/unpack (cast ProposalReply (.body this (ProposalReply.)))))]
                   (MessageClj. body))))

;; MessageBody/TracesFromPriorRequest
;; (case (byte 1)
;;   MessageBody/TracesFromPriorRequest
;;   47)

;; (cond
;;  (= (byte 1) MessageBody/TracesFromPriorRequest)
;;  47)
