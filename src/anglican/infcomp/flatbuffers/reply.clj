(ns anglican.infcomp.flatbuffers.reply
  (:require [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [anglican.infcomp.flatbuffers.traces_from_prior_reply TracesFromPriorReplyClj]
           [anglican.infcomp.flatbuffers.observes_init_reply ObservesInitReplyClj]
           [anglican.infcomp.flatbuffers.proposal_reply ProposalReplyClj]
           [infcomp Reply TracesFromPriorReply ObservesInitReply ProposalReply]
           [java.nio ByteBuffer]))

(extend-protocol p/PUnionType
  TracesFromPriorReplyClj
  (union-type [this] Reply/TracesFromPriorReply)

  ObservesInitReplyClj
  (union-type [this] Reply/ObservesInitReply)

  ProposalReplyClj
  (union-type [this] Reply/ProposalReply))
