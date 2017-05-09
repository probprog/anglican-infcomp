(ns anglican.infcomp.flatbuffers.ndarray
  (:require [anglican.infcomp.flatbuffers.java-interop :refer [byte-buffer-to-double-vec byte-buffer-to-int-vec]]
            [anglican.infcomp.flatbuffers.core :as fbs]
            [clojure.core.matrix :as m])
  (:import [infcomp.protocol NDArray]
           [java.nio ByteBuffer]))

(deftype NDArrayClj [data shape]
  fbs/PPackBuilder
  (pack-builder [this builder] (let [data (NDArray/createDataVector builder (double-array data))
                                     shape (NDArray/createShapeVector builder (int-array shape))]
                                 (NDArray/startNDArray builder)
                                 (NDArray/addData builder data)
                                 (NDArray/addShape builder shape)
                                 (NDArray/endNDArray builder)))

  fbs/PDeepEquals
  (deep-equals [this other] (and (= data (.data other))
                                 (= shape (.shape other)))))

(defn to-NDArrayClj
  "Input:
    data: number or nested list/vector

  Output:
    NDArrayClj object"
  [x]
  (let [x (if (number? x) [x] x)]
    (NDArrayClj. (flatten x) (m/shape x))))

(defn from-NDArrayClj
  "Input:
  nd-array-clj: NDArrayClj object

  Output:
  nested list/vector."
  [nd-array-clj]
  (m/reshape (.data nd-array-clj)
             (.shape nd-array-clj)))

(extend-type NDArray
  fbs/PUnpack
  (unpack [this] (let [data (byte-buffer-to-double-vec (.dataAsByteBuffer this))
                       shape (byte-buffer-to-int-vec (.shapeAsByteBuffer this))]
                   (NDArrayClj. data shape))))
