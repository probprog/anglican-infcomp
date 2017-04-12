(ns anglican.infcomp.flatbuffers.observes-init-request
  (:require [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [infcomp.flatbuffers MessageBody ObservesInitRequest]
           [java.nio ByteBuffer]))

(deftype ObservesInitRequestClj [observes]
  p/PPackBuilder
  (pack-builder [this builder] (let [observes-packed (if observes
                                                       (p/pack-builder observes builder))]
                                 (ObservesInitRequest/startObservesInitRequest builder)
                                 (if observes
                                   (ObservesInitRequest/addObserves builder observes-packed))
                                 (ObservesInitRequest/endObservesInitRequest builder)))

  p/PMessageBodyType
  (message-body-type [this] MessageBody/ObservesInitRequest))

(extend-type ObservesInitRequest
  p/PUnpack
  (unpack [this] (let [observes (p/unpack (.observes this))]
                   (ObservesInitRequestClj. observes))))
