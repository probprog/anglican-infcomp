(ns anglican.infcomp.flatbuffers.observes-init-reply
  (:require [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [infcomp.flatbuffers MessageBody ObservesInitReply]
           [java.nio ByteBuffer]))

(deftype ObservesInitReplyClj [success]
  p/PPackBuilder
  (pack-builder [this builder] (do
                                 (ObservesInitReply/startObservesInitReply builder)
                                 (if (not (nil? success))
                                   (ObservesInitReply/addSuccess builder success))
                                 (ObservesInitReply/endObservesInitReply builder)))

  p/PMessageBodyType
  (message-body-type [this] MessageBody/ObservesInitReply))

(extend-type ObservesInitReply
  p/PUnpack
  (unpack [this] (let [success (.success this)]
                   (ObservesInitReplyClj. success))))
