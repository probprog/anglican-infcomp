(ns anglican.infcomp.flatbuffers.traces-from-prior-request
  (:require [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [infcomp.flatbuffers MessageBody TracesFromPriorRequest]
           [java.nio ByteBuffer]))

(deftype TracesFromPriorRequestClj [num-traces]
  p/PPackBuilder
  (pack-builder [this builder] (do
                                 (TracesFromPriorRequest/startTracesFromPriorRequest builder)
                                 (if num-traces
                                   (TracesFromPriorRequest/addNumTraces builder num-traces))
                                 (TracesFromPriorRequest/endTracesFromPriorRequest builder)))

  p/PMessageBodyType
  (message-body-type [this] MessageBody/TracesFromPriorRequest))

(extend-type TracesFromPriorRequest
  p/PUnpack
  (unpack [this] (let [num-traces (.numTraces this)]
                   (TracesFromPriorRequestClj. num-traces))))
