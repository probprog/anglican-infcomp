(ns anglican.infcomp.flatbuffers.proposal-distributions
  (:require [anglican.infcomp.flatbuffers.protocols :as p]
            anglican.infcomp.flatbuffers.normal-proposal
            anglican.infcomp.flatbuffers.uniform-discrete-proposal)
  (:import [anglican.infcomp.flatbuffers.normal_proposal NormalProposalClj]
           [anglican.infcomp.flatbuffers.uniform_discrete_proposal UniformDiscreteProposalClj]
           [infcomp ProposalDistribution NormalProposal UniformDiscreteProposal]
           [java.nio ByteBuffer]))

(extend-protocol p/PUnionType
  NormalProposalClj
  (union-type [this] ProposalDistribution/NormalProposal)

  UniformDiscreteProposalClj
  (union-type [this] ProposalDistribution/UniformDiscreteProposal))
