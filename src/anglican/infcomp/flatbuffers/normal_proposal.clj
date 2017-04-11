(ns anglican.infcomp.flatbuffers.normal-proposal
  (:require [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [infcomp NormalProposal]
           [java.nio ByteBuffer]))

(deftype NormalProposalClj [mean std]
  p/PPackBuilder
  (pack-builder [this builder] (do
                                 (NormalProposal/startNormalProposal builder)
                                 (if mean
                                   (NormalProposal/addMean builder mean))
                                 (if std
                                   (NormalProposal/addStd builder std))
                                 (NormalProposal/endNormalProposal builder))))

(extend-protocol p/PUnpack
  (Class/forName "[B")
  (unpack [this] (let [buf (ByteBuffer/wrap this)
                       normal-proposal (NormalProposal/getRootAsNormalProposal buf)]
                   (p/unpack normal-proposal)))

  NormalProposal
  (unpack [this] (let [mean (.mean this)
                       std (.std this)]
                   (NormalProposalClj. mean std))))
