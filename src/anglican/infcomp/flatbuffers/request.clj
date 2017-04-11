(ns anglican.infcomp.flatbuffers.request
  (:require [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [anglican.infcomp.flatbuffers.traces_from_prior_request TracesFromPriorRequestClj]
           [anglican.infcomp.flatbuffers.observes_init_request ObservesInitRequestClj]
           [anglican.infcomp.flatbuffers.proposal_request ProposalRequestClj]
           [infcomp Request TracesFromPriorRequest ObservesInitRequest ProposalRequest]
           [java.nio ByteBuffer]))

(extend-protocol p/PUnionType
  TracesFromPriorRequestClj
  (union-type [this] Request/TracesFromPriorRequest)

  ObservesInitRequestClj
  (union-type [this] Request/ObservesInitRequest)

  ProposalRequestClj
  (union-type [this] Request/ProposalRequest))
