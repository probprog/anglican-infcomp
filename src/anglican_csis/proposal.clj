(ns anglican-csis.proposal
  "Various functions used for CSIS"
  (:require [clojure.string :as str]
            [clojure.core.matrix :as m]
            [anglican-csis.dists :refer :all]
            [anglican.rmh-dists :refer :all]
            [anglican.runtime :refer :all]))

(defn- dist-to-str [dist]
  "Takes distribution object and returns its distribution name (string)."
  (-> dist
      type
      str
      (str/split #" ")
      second
      (str/split #"\.")
      last
      (str/split #"-")
      butlast
      str/join))

(defn- get-proposal-constructor [prior-dist]
  "Takes prior distribution and returns proposal distribution constructor."
  (condp = (type prior-dist)
    anglican.runtime.beta-distribution continuous-min-max
    anglican.runtime.categorical-distribution categorical
    anglican.runtime.dirichlet-distribution dirichlet
    anglican.runtime.discrete-distribution discrete-min-max
    anglican.runtime.flip-distribution flip
    anglican.runtime.gamma-distribution folded-normal
    anglican.runtime.mvn-distribution mvn
    anglican.runtime.normal-distribution normal
    anglican.runtime.poisson-distribution folded-normal-discrete
    anglican.runtime.uniform-discrete-distribution discrete-min-max
    anglican.runtime.uniform-continuous-distribution continuous-min-max
    :no-dist))

(defn- get-proposal-name [prior-dist]
  "Takes prior distribution and returns proposal distribution name."
  (condp = (type prior-dist)
    anglican.runtime.beta-distribution "continuousminmax"
    anglican.runtime.categorical-distribution "categorical"
    anglican.runtime.dirichlet-distribution "dirichlet"
    anglican.runtime.discrete-distribution "discreteminmax"
    anglican.runtime.flip-distribution "flip"
    anglican.runtime.gamma-distribution "foldednormal"
    anglican.runtime.mvn-distribution "mvn"
    anglican.runtime.normal-distribution "normal"
    anglican.runtime.poisson-distribution "foldednormaldiscrete"
    anglican.runtime.uniform-discrete-distribution "discreteminmax"
    anglican.runtime.uniform-continuous-distribution "continuousminmax"
    :no-dist))

(defn get-proposal [prior-dist]
  "Takes prior distribution. Returns a map which has metadata about the
  proposal distribution:

    - prior-name: name of the prior distribution.
    - proposal-name: name of the proposal distribution.
    - proposal-constructor: constructor of the proposal distribution.
    - proposal-extra-params: extra parameters for the proposal distribution."
  (let [prior-name (dist-to-str prior-dist)
        proposal-name (get-proposal-name prior-dist)
        proposal-constructor (get-proposal-constructor prior-dist)
        proposal-extra-params (case prior-name
                                "beta" [0 1]
                                "categorical" [(count (:categories prior-dist)) (mapv first (:categories prior-dist))]
                                "dirichlet" [(count (:alpha prior-dist))]
                                "discrete" [0 (count (:weights prior-dist))]
                                "mvn" [(first (m/shape (:mean prior-dist)))]
                                "uniformdiscrete" [(:min prior-dist) (:max prior-dist)]
                                "uniformcontinuous" [(:min prior-dist) (:max prior-dist)]
                                nil)]
    {:prior-name prior-name
     :proposal-name proposal-name
     :proposal-constructor proposal-constructor
     :proposal-extra-params proposal-extra-params}))
