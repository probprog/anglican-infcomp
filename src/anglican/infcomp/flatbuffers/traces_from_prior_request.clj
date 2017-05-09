(ns anglican.infcomp.flatbuffers.traces-from-prior-request
  (:require [anglican.infcomp.flatbuffers.core :as fbs])
  (:import [infcomp.protocol MessageBody TracesFromPriorRequest]
           [java.nio ByteBuffer]))

(deftype TracesFromPriorRequestClj [num-traces]
  fbs/PPackBuilder
  (pack-builder [this builder] (do
                                 (TracesFromPriorRequest/startTracesFromPriorRequest builder)
                                 (if num-traces
                                   (TracesFromPriorRequest/addNumTraces builder num-traces))
                                 (TracesFromPriorRequest/endTracesFromPriorRequest builder)))

  fbs/PMessageBodyType
  (message-body-type [this] MessageBody/TracesFromPriorRequest))

(extend-type TracesFromPriorRequest
  fbs/PUnpack
  (unpack [this] (let [num-traces (.numTraces this)]
                   (TracesFromPriorRequestClj. num-traces))))
