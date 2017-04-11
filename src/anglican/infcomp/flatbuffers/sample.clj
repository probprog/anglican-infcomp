(ns anglican.infcomp.flatbuffers.sample
  (:require [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [infcomp.protocol Sample ProposalDistribution NormalProposal UniformDiscreteProposal]
           [java.nio ByteBuffer]))

(deftype SampleClj [time address instance proposal value]
  p/PPackBuilder
  (pack-builder [this builder] (let [packed-address (if address
                                                      (.createString builder address))
                                     packed-proposal (if proposal
                                                       (p/pack-builder proposal builder))
                                     packed-value (if value
                                                    (p/pack-builder value builder))]
                                 (Sample/startSample builder)
                                 (if time
                                   (Sample/addTime builder time))
                                 (if address
                                   (Sample/addAddress builder packed-address))
                                 (if instance
                                   (Sample/addInstance builder instance))
                                 (if proposal
                                   (Sample/addProposalType builder (p/union-type proposal)))
                                 (if proposal
                                   (Sample/addProposal builder packed-proposal))
                                 (if value
                                   (Sample/addValue builder packed-value))
                                 (Sample/endSample builder))))

(extend-protocol p/PUnpack
  (Class/forName "[B")
  (unpack [this] (let [buf (ByteBuffer/wrap this)
                       sample (Sample/getRootAsSample buf)]
                   (p/unpack sample)))

  Sample
  (unpack [this] (let [time (.time this)
                       address (.address this)
                       instance (.instance this)
                       proposal-type (.proposalType this)
                       proposal (cond
                                 (= proposal-type ProposalDistribution/NormalProposal)
                                 (p/unpack (cast NormalProposal (.proposal this (NormalProposal.))))

                                 (= proposal-type ProposalDistribution/UniformDiscreteProposal)
                                 (p/unpack (cast UniformDiscreteProposal (.proposal this (UniformDiscreteProposal.)))))
                       value (p/unpack (.value this))]
                   (SampleClj. time address instance proposal value))))
