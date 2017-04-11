(ns anglican.infcomp.flatbuffers.observes-init-request
  (:require [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [infcomp.protocol ObservesInitRequest]
           [java.nio ByteBuffer]))

(deftype ObservesInitRequestClj [observes]
  p/PPackBuilder
  (pack-builder [this builder] (let [observes-packed (if observes
                                                       (p/pack-builder observes builder))]
                                 (ObservesInitRequest/startObservesInitRequest builder)
                                 (if observes
                                   (ObservesInitRequest/addObserves builder observes-packed))
                                 (ObservesInitRequest/endObservesInitRequest builder))))

(extend-protocol p/PUnpack
  (Class/forName "[B")
  (unpack [this] (let [buf (ByteBuffer/wrap this)
                       observes-init-request (ObservesInitRequest/getRootAsObservesInitRequest buf)]
                   (p/unpack observes-init-request)))

  ObservesInitRequest
  (unpack [this] (let [observes (p/unpack (.observes this))]
                   (ObservesInitRequestClj. observes))))
