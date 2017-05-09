(ns anglican.infcomp.flatbuffers.observes-init-reply
  (:require [anglican.infcomp.flatbuffers.core :as fbs])
  (:import [infcomp.protocol MessageBody ObservesInitReply]
           [java.nio ByteBuffer]))

(deftype ObservesInitReplyClj [success]
  fbs/PPackBuilder
  (pack-builder [this builder] (do
                                 (ObservesInitReply/startObservesInitReply builder)
                                 (if (not (nil? success))
                                   (ObservesInitReply/addSuccess builder success))
                                 (ObservesInitReply/endObservesInitReply builder)))

  fbs/PMessageBodyType
  (message-body-type [this] MessageBody/ObservesInitReply))

(extend-type ObservesInitReply
  fbs/PUnpack
  (unpack [this] (let [success (.success this)]
                   (ObservesInitReplyClj. success))))
