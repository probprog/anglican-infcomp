(ns anglican.infcomp.flatbuffers.traces-from-prior-request
  (:require [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [infcomp TracesFromPriorRequest]
           [java.nio ByteBuffer]))

(deftype TracesFromPriorRequestClj [num-traces]
  p/PPackBuilder
  (pack-builder [this builder] (do
                                 (TracesFromPriorRequest/startTracesFromPriorRequest builder)
                                 (if num-traces
                                   (TracesFromPriorRequest/addNumTraces builder num-traces))
                                 (TracesFromPriorRequest/endTracesFromPriorRequest builder))))

(extend-protocol p/PUnpack
  (Class/forName "[B")
  (unpack [this] (let [buf (ByteBuffer/wrap this)
                       traces-from-prior-request (TracesFromPriorRequest/getRootAsTracesFromPriorRequest buf)]
                   (p/unpack traces-from-prior-request)))

  TracesFromPriorRequest
  (unpack [this] (let [num-traces (.numTraces this)]
                   (TracesFromPriorRequestClj. num-traces))))
