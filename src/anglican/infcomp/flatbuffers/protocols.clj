(ns anglican.infcomp.flatbuffers.protocols
  (:import [com.google.flatbuffers FlatBufferBuilder]))

(defprotocol PDeepEquals
  (deep-equals [this other]))

;; Unions
(defprotocol PDistributionType
  (distribution-type [this]))

(defprotocol PMessageBodyType
  (message-body-type [this]))

;; Pack
(defprotocol PPack
  (pack [this]))

(defprotocol PPackBuilder
  (pack-builder [this builder]))

;; Unpack
(defprotocol PUnpack
  (unpack [this]))

(defprotocol PUnpackMessage
  (unpack-message [this]))

;; Defaults
(extend-type java.lang.Object
  PPack
  (pack [this] (let [builder (FlatBufferBuilder.)
                     packed-object (pack-builder this builder)]
                 (.finish builder packed-object)
                 (.sizedByteArray builder))))

(extend-type nil
  PPack
  (pack [this] nil)

  PPackBuilder
  (pack-builder [this builder] nil)

  PUnpack
  (unpack [this] nil)

  PUnpackMessage
  (unpack-message [this]))
