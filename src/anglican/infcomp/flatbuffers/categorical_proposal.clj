(ns anglican.infcomp.flatbuffers.categorical-proposal
  (:require [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [infcomp.flatbuffers ProposalDistribution CategoricalProposal]
           [java.nio ByteBuffer]))

(deftype CategoricalProposalClj [size probabilities]
  p/PPackBuilder
  (pack-builder [this builder] (let [packed-probabilities (if probabilities
                                                            (p/pack-builder probabilities builder))]
                                 (CategoricalProposal/startCategoricalProposal builder)
                                 (if size
                                   (CategoricalProposal/addSize builder size))
                                 (if probabilities
                                   (CategoricalProposal/addProbabilities builder packed-probabilities))
                                 (CategoricalProposal/endCategoricalProposal builder)))

  p/PProposalDistributionType
  (proposal-distribution-type [this] ProposalDistribution/CategoricalProposal))

(extend-type CategoricalProposal
  p/PUnpack
  (unpack [this] (let [size (.size this)
                       probabilities (p/unpack (.probabilities this))]
                   (CategoricalProposalClj. size probabilities))))
