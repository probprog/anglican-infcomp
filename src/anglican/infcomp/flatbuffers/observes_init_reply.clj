(ns anglican.infcomp.flatbuffers.observes-init-reply
  (:require [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [infcomp ObservesInitReply]
           [java.nio ByteBuffer]))

(deftype ObservesInitReplyClj [ok]
  p/PPackBuilder
  (pack-builder [this builder] (do
                                 (ObservesInitReply/startObservesInitReply builder)
                                 (if (not (nil? ok))
                                   (ObservesInitReply/addOk builder ok))
                                 (ObservesInitReply/endObservesInitReply builder))))

(extend-protocol p/PUnpack
  (Class/forName "[B")
  (unpack [this] (let [buf (ByteBuffer/wrap this)
                       observes-init-reply (ObservesInitReply/getRootAsObservesInitReply buf)]
                   (p/unpack observes-init-reply)))

  ObservesInitReply
  (unpack [this] (let [ok (.ok this)]
                   (ObservesInitReplyClj. ok))))
