(ns anglican.infcomp.flatbuffers.sample
  (:require [anglican.infcomp.flatbuffers.core :as fbs])
  (:import [infcomp.protocol Sample Distribution Beta Categorical Discrete Flip
            Gamma Laplace MultivariateNormal Normal UniformContinuous
            UniformDiscrete]
           [java.nio ByteBuffer]))

(deftype SampleClj [time address instance distribution value]
  fbs/PPackBuilder
  (pack-builder [this builder] (let [packed-address (if address
                                                      (.createString builder address))
                                     packed-distribution (if distribution
                                                           (fbs/pack-builder distribution builder))
                                     packed-value (if value
                                                    (fbs/pack-builder value builder))]
                                 (Sample/startSample builder)
                                 (if time
                                   (Sample/addTime builder time))
                                 (if address
                                   (Sample/addAddress builder packed-address))
                                 (if instance
                                   (Sample/addInstance builder instance))
                                 (if distribution
                                   (Sample/addDistributionType builder (fbs/distribution-type distribution)))
                                 (if distribution
                                   (Sample/addDistribution builder packed-distribution))
                                 (if value
                                   (Sample/addValue builder packed-value))
                                 (Sample/endSample builder))))

(extend-type Sample
  fbs/PUnpack
  (unpack [this] (let [time (.time this)
                       address (.address this)
                       instance (.instance this)
                       distribution-type (.distributionType this)
                       distribution (condp = distribution-type
                                      Distribution/NONE
                                      nil

                                      Distribution/Beta
                                      (fbs/unpack (cast Beta (.distribution this (Beta.))))

                                      Distribution/Categorical
                                      (fbs/unpack (cast Categorical (.distribution this (Categorical.))))

                                      Distribution/Discrete
                                      (fbs/unpack (cast Discrete (.distribution this (Discrete.))))

                                      Distribution/Flip
                                      (fbs/unpack (cast Flip (.distribution this (Flip.))))

                                      Distribution/Gamma
                                      (fbs/unpack (cast Gamma (.distribution this (Gamma.))))

                                      Distribution/Laplace
                                      (fbs/unpack (cast Laplace (.distribution this (Laplace.))))

                                      Distribution/MultivariateNormal
                                      (fbs/unpack (cast MultivariateNormal (.distribution this (MultivariateNormal.))))

                                      Distribution/Normal
                                      (fbs/unpack (cast Normal (.distribution this (Normal.))))

                                      Distribution/UniformContinuous
                                      (fbs/unpack (cast UniformContinuous (.distribution this (UniformContinuous.))))

                                      Distribution/UniformDiscrete
                                      (fbs/unpack (cast UniformDiscrete (.distribution this (UniformDiscrete.)))))
                       value (fbs/unpack (.value this))]
                   (SampleClj. time address instance distribution value))))
