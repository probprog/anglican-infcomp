(ns anglican.infcomp.proposal-dists
  "Proposal distributions used in Inference Compilation"
  (:require [anglican.runtime :refer [observe* sample* defdist discrete beta categorical dirichlet flip log mvn normal]]
            [anglican.rmh-dists :refer :all]
            [clojure.core.matrix :as m]))

(m/set-current-implementation :vectorz)

(defdist beta2
  "Beta distribution parameterized by mode and certainty. Taken from
  http://doingbayesiandataanalysis.blogspot.co.uk/2012/06/beta-distribution-parameterized-by-mode.html"
  [mode certainty]
  [_ (assert (and (>= mode 0) (<= mode 1) (>= certainty 2))
             (str "Invalid beta2 parameters: mode = " mode " certainty = " certainty
                  ". mode must be in [0, 1], certainty must be in [2, infinity)."))
   a (inc (* mode (- certainty 2)))
   b (inc (* (- 1 mode) (- certainty 2)))
   dist (beta a b)]
  (sample* [this] (sample* dist))
  (observe* [this value] (observe* dist value)))

(defdist continuous-min-max
  "Continuous distribution with a specified minimum min and maximum max,
  parameterized by its mode and certainty where

    mode is in [min, max] and is the mode of the distribution and
    certainty is in (0, infinity) and determines the peakedness of the
      distribution."
  [min max mode certainty]
  [normalised-mode (/ (- mode min) (- max min))
   normalised-certainty (+ 2 certainty) ;(+ 2 (/ certainty (- max min))) which out of these two should it be? (we can decide for either)
   dist (beta2 normalised-mode normalised-certainty)]
  (sample* [this] (+ min (* (- max min) (sample* dist))))
  (observe* [this value] (- (observe* dist (/ (- value min) (- max min)))
                            (log (- max min)))))

(defdist discrete-min-max
  "Discrete distribution with a specified minimum and a maximum implicitly
  determined from the dimension of the weight vector"
  [min max weights]
  [dist (discrete weights)
   _ (assert (= (count weights) (- max min)))]
  (sample* [this] (+ min (sample* dist)))
  (observe* [this value] (observe* dist (- value min))))

(defdist mvn-mean-var
  "Multivariate normal distribution, with the same variance across all
  dimensions and zero covariance between different dimensions"
  [mean var]
  [dist (mvn mean (m/mmul var (m/identity-matrix (count mean))))]
  (sample* [this] (sample* dist))
  (observe* [this value] (observe* dist value)))

(defdist mvn-mean-vars
  "Multivariate normal distribution, with zero covariance between different
  dimensions"
  [mean vars]
  [dist (mvn mean (m/diagonal-matrix vars))]
  (sample* [this] (sample* dist))
  (observe* [this value] (observe* dist value)))

;; proposal distributions...
(defdist beta-proposal
  "Proposal distribution for beta"
  [mode certainty]
  [dist (continuous-min-max 0 1 mode certainty)]
  (sample* [this] (sample* dist))
  (observe* [this value] (observe* dist value)))

(defdist categorical-proposal
  "Proposal distribution for categorical"
  [categories]
  [dist (categorical categories)]
  (sample* [this] (sample* dist))
  (observe* [this value] (observe* dist value)))

(defdist dirichlet-proposal
  "Proposal distribution for dirichlet"
  [alpha]
  [dist (dirichlet alpha)]
  (sample* [this] (sample* dist))
  (observe* [this value] (observe* dist value)))

(defdist flip-proposal
  "Proposal distribution for flip"
  [p]
  [dist (flip p)]
  (sample* [this] (sample* dist))
  (observe* [this value] (observe* dist value)))

(defdist gamma-proposal
  "Proposal distribution for gamma"
  [mean std]
  [dist (folded-normal mean std)]
  (sample* [this] (sample* dist))
  (observe* [this value] (observe* dist value)))

(defdist mvn-proposal
  "Proposal distribution for mvn"
  [mean var]
  [dist (mvn-mean-var mean var)]
  (sample* [this] (sample* dist))
  (observe* [this value] (observe* dist value)))

(defdist normal-proposal
  "Proposal distribution for normal"
  [mean std]
  [dist (normal mean std)]
  (sample* [this] (sample* dist))
  (observe* [this value] (observe* dist value)))

(defdist poisson-proposal
  "Proposal distribution for poisson"
  [mean std]
  [dist (folded-normal-discrete mean std)]
  (sample* [this] (sample* dist))
  (observe* [this value] (observe* dist value)))

(defdist uniform-discrete-proposal
  "Proposal distribution for uniform-discrete"
  [min max weights]
  [dist (discrete-min-max min max weights)]
  (sample* [this] (sample* dist))
  (observe* [this value] (observe* dist value)))

(defdist uniform-continuous-proposal
  "Proposal distribution for uniform-continuous"
  [min max mode certainty]
  [dist (continuous-min-max min max mode certainty)]
  (sample* [this] (sample* dist))
  (observe* [this value] (observe* dist value)))


