(ns anglican.infcomp.flatbuffers.proposal-reply
  (:require [anglican.infcomp.flatbuffers.core :as fbs])
  (:import [infcomp.protocol MessageBody ProposalReply Distribution
            Beta Categorical Discrete Flip Gamma Laplace MultivariateNormal
            Normal UniformContinuous UniformDiscrete]
           [java.nio ByteBuffer]))

(deftype ProposalReplyClj [success distribution]
  fbs/PPackBuilder
  (pack-builder [this builder] (let [distribution-packed (if distribution
                                                           (fbs/pack-builder distribution builder))]
                                 (ProposalReply/startProposalReply builder)
                                 (if (not (nil? success))
                                   (ProposalReply/addSuccess builder success))
                                 (if distribution
                                   (ProposalReply/addDistributionType builder (fbs/distribution-type distribution)))
                                 (if distribution
                                   (ProposalReply/addDistribution builder distribution-packed))
                                 (ProposalReply/endProposalReply builder)))

  fbs/PMessageBodyType
  (message-body-type [this] MessageBody/ProposalReply))

(extend-type ProposalReply
  fbs/PUnpack
  (unpack [this] (let [success (.success this)
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
                                      (fbs/unpack (cast UniformDiscrete (.distribution this (UniformDiscrete.)))))]
                   (ProposalReplyClj. success distribution))))
