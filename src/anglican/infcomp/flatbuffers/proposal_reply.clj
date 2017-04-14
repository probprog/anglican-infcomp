(ns anglican.infcomp.flatbuffers.proposal-reply
  (:require [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [infcomp.flatbuffers MessageBody ProposalReply ProposalDistribution
            FlipProposal NormalProposal UniformDiscreteProposal]
           [java.nio ByteBuffer]))

(deftype ProposalReplyClj [success proposal]
  p/PPackBuilder
  (pack-builder [this builder] (let [proposal-packed (if proposal
                                                       (p/pack-builder proposal builder))]
                                 (ProposalReply/startProposalReply builder)
                                 (if (not (nil? success))
                                   (ProposalReply/addSuccess builder success))
                                 (if proposal
                                   (ProposalReply/addProposalType builder (p/proposal-distribution-type proposal)))
                                 (if proposal
                                   (ProposalReply/addProposal builder proposal-packed))
                                 (ProposalReply/endProposalReply builder)))

  p/PMessageBodyType
  (message-body-type [this] MessageBody/ProposalReply))

(extend-type ProposalReply
  p/PUnpack
  (unpack [this] (let [success (.success this)
                       proposal-type (.proposalType this)
                       proposal (condp = proposal-type
                                  ProposalDistribution/FlipProposal
                                  (p/unpack (cast FlipProposal (.proposal this (FlipProposal.))))

                                  ProposalDistribution/NormalProposal
                                  (p/unpack (cast NormalProposal (.proposal this (NormalProposal.))))

                                  ProposalDistribution/UniformDiscreteProposal
                                  (p/unpack (cast UniformDiscreteProposal (.proposal this (UniformDiscreteProposal.)))))]
                   (ProposalReplyClj. success proposal))))
