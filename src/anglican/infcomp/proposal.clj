(ns anglican.infcomp.proposal
  "Various functions used for Inference Compilation"
  (:require [clojure.string :as str]
            [clojure.core.matrix :as m]
            [anglican.infcomp.proposal-dists :refer :all]
            [anglican.runtime :refer :all]
            [anglican.infcomp.flatbuffers normal-proposal uniform-discrete-proposal])
  (:import anglican.infcomp.flatbuffers.normal_proposal.NormalProposalClj
           anglican.infcomp.flatbuffers.uniform_discrete_proposal.UniformDiscreteProposalClj))

(defn get-proposal-constructor
  "Takes prior distribution and returns proposal distribution constructor."
  [prior-dist]
  (condp = (type prior-dist)
    anglican.runtime.normal-distribution normal-proposal
    anglican.runtime.uniform-discrete-distribution uniform-discrete-proposal))

(defn get-proposal
  "Takes prior distribution object. Returns a flatbuffers compatible *ProposalClj object.

  Input:
    prior-dist: prior distribution object defined by defdist

  Output: One of the following:
    - UniformDiscreteProposalClj
    - NormalProposalClj"
  [prior-dist]
  (condp = (type prior-dist)
    anglican.runtime.normal-distribution (NormalProposalClj. nil nil)
    anglican.runtime.uniform-discrete-distribution (UniformDiscreteProposalClj. (:min prior-dist) (:max prior-dist) nil)))
