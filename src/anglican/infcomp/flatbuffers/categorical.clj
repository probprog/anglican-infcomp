(ns anglican.infcomp.flatbuffers.categorical
  (:require [anglican.infcomp.flatbuffers.core :as fbs])
  (:import [infcomp.protocol Distribution Categorical]
           [java.nio ByteBuffer]))

(deftype CategoricalClj [prior-size proposal-probabilities]
  fbs/PPackBuilder
  (pack-builder [this builder] (let [packed-proposal-probabilities (if proposal-probabilities
                                                                     (fbs/pack-builder proposal-probabilities builder))]
                                 (Categorical/startCategorical builder)
                                 (if prior-size
                                   (Categorical/addPriorSize builder prior-size))
                                 (if proposal-probabilities
                                   (Categorical/addProposalProbabilities builder packed-proposal-probabilities))
                                 (Categorical/endCategorical builder)))

  fbs/PDistributionType
  (distribution-type [this] Distribution/Categorical))

(extend-type Categorical
  fbs/PUnpack
  (unpack [this] (let [prior-size (.priorSize this)
                       proposal-probabilities (fbs/unpack (.proposalProbabilities this))]
                   (CategoricalClj. prior-size proposal-probabilities))))
