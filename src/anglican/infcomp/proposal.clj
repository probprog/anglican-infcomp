(ns anglican.infcomp.proposal
  "Various functions used for Inference Compilation"
  (:require [clojure.string :as str]
            [clojure.core.matrix :as m]
            [anglican.infcomp.dists :refer :all]
            [anglican.runtime :refer :all]
            [anglican.infcomp.flatbuffers categorical
             discrete flip normal uniform-continuous
             uniform-discrete])
  (:import anglican.infcomp.flatbuffers.categorical.CategoricalClj
           anglican.infcomp.flatbuffers.discrete.DiscreteClj
           anglican.infcomp.flatbuffers.flip.FlipClj
           anglican.infcomp.flatbuffers.normal.NormalClj
           anglican.infcomp.flatbuffers.uniform_continuous.UniformContinuousClj
           anglican.infcomp.flatbuffers.uniform_discrete.UniformDiscreteClj))

(defn get-proposal-constructor
  "Takes prior distribution and returns proposal distribution constructor."
  [prior-dist]
  (condp = (type prior-dist)
    anglican.runtime.categorical-distribution categorical-proposal
    anglican.runtime.discrete-distribution discrete-proposal
    anglican.runtime.flip-distribution flip-proposal
    anglican.runtime.normal-distribution normal-proposal
    anglican.runtime.uniform-continuous-distribution uniform-continuous-proposal
    anglican.runtime.uniform-discrete-distribution uniform-discrete-proposal))

(defn get-prior-distribution-clj
  "Takes prior distribution object. Returns a flatbuffers compatible *Clj object.

  Input:
    prior-dist: prior distribution object defined by defdist

  Output: One of the following:
    - CategoricalClj
    - DiscreteClj
    - FlipClj
    - NormalClj
    - UniformDiscreteClj"
  [prior-dist]
  (condp = (type prior-dist)
    anglican.runtime.categorical-distribution (CategoricalClj. (count (:categories prior-dist)) nil)
    anglican.runtime.discrete-distribution (DiscreteClj. (count (:weights prior-dist)) nil)
    anglican.runtime.flip-distribution (FlipClj. nil)
    anglican.runtime.normal-distribution (NormalClj. (:mean prior-dist) (:sd prior-dist) nil nil)
    anglican.runtime.uniform-continuous-distribution (UniformContinuousClj. (:min prior-dist) (:max prior-dist) nil nil)
    anglican.runtime.uniform-discrete-distribution (UniformDiscreteClj. (:min prior-dist) (- (:max prior-dist) (:min prior-dist)) nil)))
