(ns anglican.infcomp.flatbuffers.trace
  (:require [anglican.infcomp.flatbuffers.core :as fbs])
  (:import [infcomp.protocol Trace]
           [java.nio ByteBuffer]))

(deftype TraceClj [observes samples]
  fbs/PPackBuilder
  (pack-builder [this builder] (let [packed-observes (if observes
                                                       (fbs/pack-builder observes builder))
                                     packed-samples (if samples
                                                      (let [samples (mapv #(fbs/pack-builder % builder) samples)]
                                                        (Trace/createSamplesVector builder (int-array samples))))]
                                 (Trace/startTrace builder)
                                 (if observes
                                   (Trace/addObserves builder packed-observes))
                                 (if samples
                                   (Trace/addSamples builder packed-samples))
                                 (Trace/endTrace builder))))

(extend-type Trace
  fbs/PUnpack
  (unpack [this] (let [observes (fbs/unpack (.observes this))
                       samples (mapv #(fbs/unpack (.samples this %))
                                     (range (.samplesLength this)))]
                   (TraceClj. observes samples))))
