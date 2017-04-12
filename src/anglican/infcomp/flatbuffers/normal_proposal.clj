(ns anglican.infcomp.flatbuffers.normal-proposal
  (:require [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [infcomp.flatbuffers ProposalDistribution NormalProposal]
           [java.nio ByteBuffer]))

(deftype NormalProposalClj [mean std]
  p/PPackBuilder
  (pack-builder [this builder] (do
                                 (NormalProposal/startNormalProposal builder)
                                 (if mean
                                   (NormalProposal/addMean builder mean))
                                 (if std
                                   (NormalProposal/addStd builder std))
                                 (NormalProposal/endNormalProposal builder)))

  p/PProposalDistributionType
  (proposal-distribution-type [this] ProposalDistribution/NormalProposal))

(extend-type NormalProposal
  p/PUnpack
  (unpack [this] (let [mean (.mean this)
                       std (.std this)]
                   (NormalProposalClj. mean std))))
