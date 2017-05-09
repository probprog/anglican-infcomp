(ns anglican.infcomp.flatbuffers.traces-from-prior-reply
  (:require [anglican.infcomp.flatbuffers.core :as fbs])
  (:import [infcomp.protocol MessageBody TracesFromPriorReply]
           [java.nio ByteBuffer]))

(deftype TracesFromPriorReplyClj [traces]
  fbs/PPackBuilder
  (pack-builder [this builder] (let [packed-traces (if traces
                                                     (let [traces (mapv #(fbs/pack-builder % builder) traces)]
                                                       (TracesFromPriorReply/createTracesVector builder (int-array traces))))]
                                 (TracesFromPriorReply/startTracesFromPriorReply builder)
                                 (if traces
                                   (TracesFromPriorReply/addTraces builder packed-traces))
                                 (TracesFromPriorReply/endTracesFromPriorReply builder)))

  fbs/PMessageBodyType
  (message-body-type [this] MessageBody/TracesFromPriorReply))

(extend-type TracesFromPriorReply
  fbs/PUnpack
  (unpack [this] (let [traces (mapv #(fbs/unpack (.traces this %))
                                    (range (.tracesLength this)))]
                   (TracesFromPriorReplyClj. traces))))
