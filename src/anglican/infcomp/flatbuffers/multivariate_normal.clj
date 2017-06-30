(ns anglican.infcomp.flatbuffers.multivariate-normal
  (:require [anglican.infcomp.flatbuffers.core :as fbs])
  (:import [infcomp.protocol Distribution MultivariateNormal]
           [java.nio ByteBuffer]))

(deftype MultivariateNormalClj [prior-mean prior-cov proposal-mean proposal-vars]
  fbs/PPackBuilder
  (pack-builder [this builder] (let [packed-prior-mean (if prior-mean
                                                         (fbs/pack-builder prior-mean builder))
                                     packed-prior-cov (if prior-cov
                                                        (fbs/pack-builder prior-cov builder))
                                     packed-proposal-mean (if proposal-mean
                                                            (fbs/pack-builder proposal-mean builder))
                                     packed-proposal-vars (if proposal-vars
                                                            (fbs/pack-builder proposal-vars builder))]
                                 (MultivariateNormal/startMultivariateNormal builder)
                                 (if prior-mean
                                   (MultivariateNormal/addPriorMean builder packed-prior-mean))
                                 (if prior-cov
                                   (MultivariateNormal/addPriorCov builder packed-prior-cov))
                                 (if proposal-mean
                                   (MultivariateNormal/addProposalMean builder packed-proposal-mean))
                                 (if proposal-vars
                                   (MultivariateNormal/addProposalVars builder packed-proposal-vars))
                                 (MultivariateNormal/endMultivariateNormal builder)))

  fbs/PDistributionType
  (distribution-type [this] Distribution/MultivariateNormal))

(extend-type MultivariateNormal
  fbs/PUnpack
  (unpack [this] (let [prior-mean (fbs/unpack (.priorMean this))
                       prior-cov (fbs/unpack (.priorCov this))
                       proposal-mean (fbs/unpack (.proposalMean this))
                       proposal-vars (fbs/unpack (.proposalVars this))]
                   (MultivariateNormalClj. prior-mean prior-cov proposal-mean proposal-vars))))
