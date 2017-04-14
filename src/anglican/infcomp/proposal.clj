(ns anglican.infcomp.proposal
  "Various functions used for Inference Compilation"
  (:require [clojure.string :as str]
            [clojure.core.matrix :as m]
            [anglican.infcomp.dists :refer :all]
            [anglican.runtime :refer :all]
            [anglican.infcomp.flatbuffers discrete-proposal flip-proposal
             normal-proposal uniform-discrete-proposal])
  (:import anglican.infcomp.flatbuffers.discrete_proposal.DiscreteProposalClj
           anglican.infcomp.flatbuffers.flip_proposal.FlipProposalClj
           anglican.infcomp.flatbuffers.normal_proposal.NormalProposalClj
           anglican.infcomp.flatbuffers.uniform_discrete_proposal.UniformDiscreteProposalClj))

(defn get-proposal-constructor
  "Takes prior distribution and returns proposal distribution constructor."
  [prior-dist]
  (condp = (type prior-dist)
    anglican.runtime.discrete-distribution discrete-proposal
    anglican.runtime.flip-distribution flip-proposal
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
    anglican.runtime.discrete-distribution (DiscreteProposalClj. (count (:weights prior-dist)) nil)
    anglican.runtime.flip-distribution (FlipProposalClj. nil)
    anglican.runtime.normal-distribution (NormalProposalClj. nil nil)
    anglican.runtime.uniform-discrete-distribution (UniformDiscreteProposalClj. (:min prior-dist) (:max prior-dist) nil)))
