(ns anglican.infcomp.flatbuffers.traces-from-prior-reply
  (:require [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [infcomp TracesFromPriorReply]
           [java.nio ByteBuffer]))

(deftype TracesFromPriorReplyClj [traces]
  p/PPackBuilder
  (pack-builder [this builder] (let [packed-traces (if traces
                                                     (let [traces (mapv #(p/pack-builder % builder) traces)]
                                                       (TracesFromPriorReply/createTracesVector builder (int-array traces))))]
                                 (TracesFromPriorReply/startTracesFromPriorReply builder)
                                 (if traces
                                   (TracesFromPriorReply/addTraces builder packed-traces))
                                 (TracesFromPriorReply/endTracesFromPriorReply builder))))

(extend-protocol p/PUnpack
  (Class/forName "[B")
  (unpack [this] (let [buf (ByteBuffer/wrap this)
                       traces-from-prior-reply (TracesFromPriorReply/getRootAsTracesFromPriorReply buf)]
                   (p/unpack traces-from-prior-reply)))

  TracesFromPriorReply
  (unpack [this] (let [traces (mapv #(p/unpack (.traces this %))
                                    (range (.tracesLength this)))]
                   (TracesFromPriorReplyClj. traces))))

;; (.traces (p/unpack (p/pack (TracesFromPriorReplyClj. [(TraceClj. (NDArrayClj. [1 2 3] [3])
;;                                                         [(SampleClj. 1 "x1" 1 nil nil)
;;                                                          (SampleClj. 1 "x1" 1 nil nil)
;;                                                          (SampleClj. 1 "x1" 1 nil nil)])
;;                                              (TraceClj. (NDArrayClj. [1 2 3] [3])
;;                                                         [(SampleClj. 1 "x1" 1 nil nil)
;;                                                          (SampleClj. 1 "x1" 1 nil nil)
;;                                                          (SampleClj. 1 "x1" 1 nil nil)])]))))
