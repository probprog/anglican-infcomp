(ns anglican.infcomp.flatbuffers-test
  (:require [clojure.test :refer :all]
            [anglican.infcomp.flatbuffers.protocols :as fbs]
            anglican.infcomp.flatbuffers.ndarray)
  (:import [anglican.infcomp.flatbuffers.ndarray NDArrayClj]))

(deftest ndarray-test
  (let [ndarray (NDArrayClj. [1.0 2.0 3.0] [3])]
    (is (fbs/deep-equals ndarray (fbs/unpack (fbs/pack ndarray))))))

;; (run-tests 'anglican.infcomp.flatbuffers-test)
