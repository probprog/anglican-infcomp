(ns anglican.infcomp.flatbuffers.observes-init-request
  (:require [anglican.infcomp.flatbuffers.core :as fbs])
  (:import [infcomp.protocol MessageBody ObservesInitRequest]
           [java.nio ByteBuffer]))

(deftype ObservesInitRequestClj [observes]
  fbs/PPackBuilder
  (pack-builder [this builder] (let [observes-packed (if observes
                                                       (fbs/pack-builder observes builder))]
                                 (ObservesInitRequest/startObservesInitRequest builder)
                                 (if observes
                                   (ObservesInitRequest/addObserves builder observes-packed))
                                 (ObservesInitRequest/endObservesInitRequest builder)))

  fbs/PMessageBodyType
  (message-body-type [this] MessageBody/ObservesInitRequest))

(extend-type ObservesInitRequest
  fbs/PUnpack
  (unpack [this] (let [observes (fbs/unpack (.observes this))]
                   (ObservesInitRequestClj. observes))))
