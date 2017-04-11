(ns anglican.infcomp.flatbuffers.proposal-reply
  (:require [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [infcomp.protocol ProposalReply ProposalDistribution NormalProposal UniformDiscreteProposal]
           [java.nio ByteBuffer]))

(deftype ProposalReplyClj [proposal]
  p/PPackBuilder
  (pack-builder [this builder] (let [proposal-packed (if proposal
                                                       (p/pack-builder proposal builder))]
                                 (ProposalReply/startProposalReply builder)
                                 (if proposal
                                   (ProposalReply/addProposalType builder (p/union-type proposal)))
                                 (if proposal
                                   (ProposalReply/addProposal builder proposal-packed))
                                 (ProposalReply/endProposalReply builder))))

(extend-protocol p/PUnpack
  (Class/forName "[B")
  (unpack [this] (let [buf (ByteBuffer/wrap this)
                       proposal-reply (ProposalReply/getRootAsProposalReply buf)]
                   (p/unpack proposal-reply)))

  ProposalReply
  (unpack [this] (let [proposal-type (.proposalType this)
                       proposal (cond
                                 (= proposal-type ProposalDistribution/NormalProposal)
                                 (p/unpack (cast NormalProposal (.proposal this (NormalProposal.))))

                                 (= proposal-type ProposalDistribution/UniformDiscreteProposal)
                                 (p/unpack (cast UniformDiscreteProposal (.proposal this (UniformDiscreteProposal.)))))]
                   (ProposalReplyClj. proposal))))
