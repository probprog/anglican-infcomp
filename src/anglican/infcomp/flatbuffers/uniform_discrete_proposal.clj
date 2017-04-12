(ns anglican.infcomp.flatbuffers.uniform-discrete-proposal
  (:require [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [infcomp.flatbuffers ProposalDistribution UniformDiscreteProposal]
           [java.nio ByteBuffer]))

(deftype UniformDiscreteProposalClj [min max probabilities]
  p/PPackBuilder
  (pack-builder [this builder] (let [packed-probabilities (if probabilities
                                                            (p/pack-builder probabilities builder))]
                                 (UniformDiscreteProposal/startUniformDiscreteProposal builder)
                                 (if min
                                   (UniformDiscreteProposal/addMin builder min))
                                 (if max
                                   (UniformDiscreteProposal/addMax builder max))
                                 (if probabilities
                                   (UniformDiscreteProposal/addProbabilities builder packed-probabilities))
                                 (UniformDiscreteProposal/endUniformDiscreteProposal builder)))

  p/PProposalDistributionType
  (proposal-distribution-type [this] ProposalDistribution/UniformDiscreteProposal))

(extend-type UniformDiscreteProposal
  p/PUnpack
  (unpack [this] (let [min (.min this)
                       max (.max this)
                       probabilities (p/unpack (.probabilities this))]
                   (UniformDiscreteProposalClj. min max probabilities))))
