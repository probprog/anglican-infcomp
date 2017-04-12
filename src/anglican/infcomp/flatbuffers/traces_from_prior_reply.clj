(ns anglican.infcomp.flatbuffers.traces-from-prior-reply
  (:require [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [infcomp.flatbuffers MessageBody TracesFromPriorReply]
           [java.nio ByteBuffer]))

(deftype TracesFromPriorReplyClj [traces]
  p/PPackBuilder
  (pack-builder [this builder] (let [packed-traces (if traces
                                                     (let [traces (mapv #(p/pack-builder % builder) traces)]
                                                       (TracesFromPriorReply/createTracesVector builder (int-array traces))))]
                                 (TracesFromPriorReply/startTracesFromPriorReply builder)
                                 (if traces
                                   (TracesFromPriorReply/addTraces builder packed-traces))
                                 (TracesFromPriorReply/endTracesFromPriorReply builder)))

  p/PMessageBodyType
  (message-body-type [this] MessageBody/TracesFromPriorReply))

(extend-type TracesFromPriorReply
  p/PUnpack
  (unpack [this] (let [traces (mapv #(p/unpack (.traces this %))
                                    (range (.tracesLength this)))]
                   (TracesFromPriorReplyClj. traces))))
