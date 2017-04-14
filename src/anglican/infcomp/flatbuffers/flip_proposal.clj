(ns anglican.infcomp.flatbuffers.flip-proposal
  (:require [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [infcomp.flatbuffers ProposalDistribution FlipProposal]
           [java.nio ByteBuffer]))

(deftype FlipProposalClj [probability]
  p/PPackBuilder
  (pack-builder [this builder] (do
                                 (FlipProposal/startFlipProposal builder)
                                 (if probability
                                   (FlipProposal/addProbability builder probability))
                                 (FlipProposal/endFlipProposal builder)))

  p/PProposalDistributionType
  (proposal-distribution-type [this] ProposalDistribution/FlipProposal))

(extend-type FlipProposal
  p/PUnpack
  (unpack [this] (let [probability (.probability this)]
                   (FlipProposalClj. probability))))
