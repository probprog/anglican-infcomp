(ns anglican.infcomp.flatbuffers.proposal-reply
  (:require [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [infcomp.flatbuffers MessageBody ProposalReply Distribution
            Categorical Discrete Flip Normal UniformDiscrete]
           [java.nio ByteBuffer]))

(deftype ProposalReplyClj [success distribution]
  p/PPackBuilder
  (pack-builder [this builder] (let [distribution-packed (if distribution
                                                           (p/pack-builder distribution builder))]
                                 (ProposalReply/startProposalReply builder)
                                 (if (not (nil? success))
                                   (ProposalReply/addSuccess builder success))
                                 (if distribution
                                   (ProposalReply/addDistributionType builder (p/distribution-type distribution)))
                                 (if distribution
                                   (ProposalReply/addDistribution builder distribution-packed))
                                 (ProposalReply/endProposalReply builder)))

  p/PMessageBodyType
  (message-body-type [this] MessageBody/ProposalReply))

(extend-type ProposalReply
  p/PUnpack
  (unpack [this] (let [success (.success this)
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
                                      (p/unpack (cast UniformDiscrete (.distribution this (UniformDiscrete.)))))]
                   (ProposalReplyClj. success distribution))))
