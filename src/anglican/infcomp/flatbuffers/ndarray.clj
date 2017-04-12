(ns anglican.infcomp.flatbuffers.ndarray
  (:require [anglican.infcomp.flatbuffers.java-interop :refer [byte-buffer-to-double-vec byte-buffer-to-int-vec]]
            [anglican.infcomp.flatbuffers.protocols :as p])
  (:import [infcomp.flatbuffers NDArray]
           [java.nio ByteBuffer]))

(deftype NDArrayClj [data shape]
  p/PPackBuilder
  (pack-builder [this builder] (let [data (NDArray/createDataVector builder (double-array data))
                                     shape (NDArray/createShapeVector builder (int-array shape))]
                                 (NDArray/startNDArray builder)
                                 (NDArray/addData builder data)
                                 (NDArray/addShape builder shape)
                                 (NDArray/endNDArray builder)))

  p/PDeepEquals
  (deep-equals [this other] (and (= data (.data other))
                                 (= shape (.shape other)))))

(extend-type NDArray
  p/PUnpack
  (unpack [this] (let [data (byte-buffer-to-double-vec (.dataAsByteBuffer this))
                       shape (byte-buffer-to-int-vec (.shapeAsByteBuffer this))]
                   (NDArrayClj. data shape))))
