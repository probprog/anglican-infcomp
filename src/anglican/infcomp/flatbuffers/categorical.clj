(ns anglican.infcomp.flatbuffers.categorical
  (:require [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [infcomp.flatbuffers Distribution Categorical]
           [java.nio ByteBuffer]))

(deftype CategoricalClj [prior-size proposal-probabilities]
  p/PPackBuilder
  (pack-builder [this builder] (let [packed-proposal-probabilities (if proposal-probabilities
                                                            (p/pack-builder proposal-probabilities builder))]
                                 (Categorical/startCategorical builder)
                                 (if prior-size
                                   (Categorical/addPriorSize builder prior-size))
                                 (if proposal-probabilities
                                   (Categorical/addProposalProbabilities builder packed-proposal-probabilities))
                                 (Categorical/endCategorical builder)))

  p/PDistributionType
  (distribution-type [this] Distribution/Categorical))

(extend-type Categorical
  p/PUnpack
  (unpack [this] (let [prior-size (.priorSize this)
                       proposal-probabilities (p/unpack (.proposalProbabilities this))]
                   (CategoricalClj. prior-size proposal-probabilities))))
