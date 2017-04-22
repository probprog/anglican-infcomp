(ns anglican.infcomp.flatbuffers.sample
  (:require [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [infcomp.flatbuffers Sample Distribution Categorical
            Discrete Flip Normal UniformDiscrete]
           [java.nio ByteBuffer]))

(deftype SampleClj [time address instance distribution value]
  p/PPackBuilder
  (pack-builder [this builder] (let [packed-address (if address
                                                      (.createString builder address))
                                     packed-distribution (if distribution
                                                           (p/pack-builder distribution builder))
                                     packed-value (if value
                                                    (p/pack-builder value builder))]
                                 (Sample/startSample builder)
                                 (if time
                                   (Sample/addTime builder time))
                                 (if address
                                   (Sample/addAddress builder packed-address))
                                 (if instance
                                   (Sample/addInstance builder instance))
                                 (if distribution
                                   (Sample/addDistributionType builder (p/distribution-type distribution)))
                                 (if distribution
                                   (Sample/addDistribution builder packed-distribution))
                                 (if value
                                   (Sample/addValue builder packed-value))
                                 (Sample/endSample builder))))

(extend-type Sample
  p/PUnpack
  (unpack [this] (let [time (.time this)
                       address (.address this)
                       instance (.instance this)
                       distribution-type (.distributionType this)
                       distribution (condp = distribution-type
                                      Distribution/Categorical
                                      (p/unpack (cast Categorical (.distribution this (Categorical.))))

                                      Distribution/Discrete
                                      (p/unpack (cast Discrete (.distribution this (Discrete.))))

                                      Distribution/Flip
                                      (p/unpack (cast Flip (.distribution this (Flip.))))

                                      Distribution/Normal
                                      (p/unpack (cast Normal (.distribution this (Normal.))))

                                      Distribution/UniformDiscrete
                                      (p/unpack (cast UniformDiscrete (.distribution this (UniformDiscrete.)))))
                       value (p/unpack (.value this))]
                   (SampleClj. time address instance distribution value))))
