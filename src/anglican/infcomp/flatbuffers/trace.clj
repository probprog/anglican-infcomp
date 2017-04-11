(ns anglican.infcomp.flatbuffers.trace
  (:require [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [infcomp.protocol Trace]
           [java.nio ByteBuffer]))

(deftype TraceClj [observes samples]
  p/PPackBuilder
  (pack-builder [this builder] (let [packed-observes (if observes
                                                       (p/pack-builder observes builder))
                                     packed-samples (if samples
                                                      (let [samples (mapv #(p/pack-builder % builder) samples)]
                                                        (Trace/createSamplesVector builder (int-array samples))))]
                                 (Trace/startTrace builder)
                                 (if observes
                                   (Trace/addObserves builder packed-observes))
                                 (if samples
                                   (Trace/addSamples builder packed-samples))
                                 (Trace/endTrace builder))))

(extend-protocol p/PUnpack
  (Class/forName "[B")
  (unpack [this] (let [buf (ByteBuffer/wrap this)
                       trace (Trace/getRootAsTrace buf)]
                   (p/unpack trace)))

  Trace
  (unpack [this] (let [observes (p/unpack (.observes this))
                       samples (mapv #(p/unpack (.samples this %))
                                     (range (.samplesLength this)))]
                   (TraceClj. observes samples))))
