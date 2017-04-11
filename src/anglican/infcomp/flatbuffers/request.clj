(ns anglican.infcomp.flatbuffers.request
  (:require [anglican.infcomp.flatbuffers.protocols :as p]
            anglican.infcomp.flatbuffers.traces-from-prior-request
            anglican.infcomp.flatbuffers.observes-init-request
            anglican.infcomp.flatbuffers.proposal-request)
  (:import [anglican.infcomp.flatbuffers.traces_from_prior_request TracesFromPriorRequestClj]
           [anglican.infcomp.flatbuffers.observes_init_request ObservesInitRequestClj]
           [anglican.infcomp.flatbuffers.proposal_request ProposalRequestClj]
           [infcomp.protocol Request TracesFromPriorRequest ObservesInitRequest ProposalRequest]
           [java.nio ByteBuffer]))

(extend-protocol p/PUnionType
  TracesFromPriorRequestClj
  (union-type [this] Request/TracesFromPriorRequest)

  ObservesInitRequestClj
  (union-type [this] Request/ObservesInitRequest)

  ProposalRequestClj
  (union-type [this] Request/ProposalRequest))
