(ns anglican-csis.utils
  "Various functions used for CSIS"
  (:require [clojure.string :as str]
            [clojure.core.matrix :as m]
            [dists :refer :all]
            [anglican.rmh-dists :refer :all]
            [anglican.runtime :refer :all]))

;; Proposal distribution extraction
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

(defn get-proposal-dist-const [prior-dist]
  "Takes prior distribution object and returns proposal distribution constructor."
  (condp = (type prior-dist)
    anglican.runtime.beta-distribution continuous-min-max
    anglican.runtime.categorical-distribution categorical
    anglican.runtime.dirichlet-distribution dirichlet
    anglican.runtime.discrete-distribution discrete-min-max
    anglican.runtime.flip-distribution flip
    anglican.runtime.gamma-distribution folded-normal
;    anglican.runtime.laplace-distribution normal
    anglican.runtime.mvn-distribution mvn
;    anglican.runtime.mvn-diag1-distribution mvn-diag1-proposal
;    anglican.runtime.mvn-diag2-distribution mvn-diag2-proposal
;    anglican.runtime.mvn-diag3-distribution mvn-diag3-proposal
    anglican.runtime.normal-distribution normal
    anglican.runtime.poisson-distribution folded-normal-discrete
 ;   anglican.runtime.student-t-distribution normal
    anglican.runtime.uniform-discrete-distribution discrete-min-max
    anglican.runtime.uniform-continuous-distribution continuous-min-max
    :no-dist)) ;; TODO: find idiomatic way to do this

(defn get-proposal-dist-str [prior-dist-str]
  "Takes prior distribution name and returns proposal distribution name."
  (case prior-dist-str
    "beta"              "continuousminmax"
    "categorical"       "categorical"
    "dirichlet"         "dirichlet"
    "discrete"          "discreteminmax"
    "flip"              "flip"
    "gamma"             "foldednormal"
;    "laplace"           "normal"
    "normal"            "normal"
    "mvn"               "mvn"
;    "mvndiag1"          "mvndiag1proposal"
;    "mvndiag2"          "mvndiag2proposal"
;    "mvndiag3"          "mvndiag3proposal"
    "poisson"           "foldednormaldiscrete"
;    "studentt"          "normal"
    "uniformcontinuous" "continuousminmax"
    "uniformdiscrete"   "discreteminmax"
    prior-dist-str))

(defn get-proposal-object [prior-dist]
  "Takes prior distribution object. Returns a map which has metadata about the
  proposal distribution:

    - prior-str: name of the prior distribution.
    - proposal-str: name of the proposal distribution.
    - proposal-dist-const: constructor of the proposal distribution object.
    - proposal-extra-params: extra parameters for the proposal distribution.
    - proposal-act-fun: activation function mapping to proposal distribution
      parameters."
  (let [prior-str (dist-to-str prior-dist)
        proposal-str (get-proposal-dist-str prior-str)
        proposal-dist-const (get-proposal-dist-const prior-dist)
        proposal-extra-params (case prior-str
                                "beta" [0 1]
                                "categorical" [(count (:categories prior-dist)) (mapv first (:categories prior-dist))]
                                "dirichlet" [(count (:alpha prior-dist))]
                                "discrete" [0 (count (:weights prior-dist))]
                                "mvn" [(first (m/shape (:mean prior-dist)))]
;;                                 "mvndiag1" [(first (m/shape (:mean prior-dist)))]
;;                                 "mvndiag2" [(first (m/shape (:mean prior-dist)))]
;;                                 "mvndiag3" [(first (m/shape (:mean prior-dist)))]
                                "uniformdiscrete" [(:min prior-dist) (:max prior-dist)]
                                "uniformcontinuous" [(:min prior-dist) (:max prior-dist)]
                                nil)]
    {:prior-str prior-str
     :proposal-str proposal-str
     :proposal-dist-const proposal-dist-const
     :proposal-extra-params proposal-extra-params}))
