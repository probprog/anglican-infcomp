(ns anglican.infcomp.flatbuffers.discrete-proposal
  (:require [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [infcomp.flatbuffers ProposalDistribution DiscreteProposal]
           [java.nio ByteBuffer]))

(deftype DiscreteProposalClj [size probabilities]
  p/PPackBuilder
  (pack-builder [this builder] (let [packed-probabilities (if probabilities
                                                            (p/pack-builder probabilities builder))]
                                 (DiscreteProposal/startDiscreteProposal builder)
                                 (if size
                                   (DiscreteProposal/addSize builder size))
                                 (if probabilities
                                   (DiscreteProposal/addProbabilities builder packed-probabilities))
                                 (DiscreteProposal/endDiscreteProposal builder)))

  p/PProposalDistributionType
  (proposal-distribution-type [this] ProposalDistribution/DiscreteProposal))

(extend-type DiscreteProposal
  p/PUnpack
  (unpack [this] (let [size (.size this)
                       probabilities (p/unpack (.probabilities this))]
                   (DiscreteProposalClj. size probabilities))))
