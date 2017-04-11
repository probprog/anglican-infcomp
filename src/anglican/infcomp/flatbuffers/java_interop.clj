(ns anglican.infcomp.flatbuffers.java-interop)

(defn byte-buffer-to-double-vec [byte-buffer]
  (let [double-buffer (.asDoubleBuffer byte-buffer)]
    (if (.hasArray double-buffer)
      (.array double-buffer)
      (let [double-array (double-array (.remaining double-buffer))]
        (.get double-buffer double-array)
        (vec double-array)))))
(def byte-array? (partial instance? (Class/forName "[B")))

(defn byte-buffer-to-int-vec [byte-buffer]
  (let [int-buffer (.asIntBuffer byte-buffer)]
    (if (.hasArray int-buffer)
      (.array int-buffer)
      (let [int-array (int-array (.remaining int-buffer))]
        (.get int-buffer int-array)
        (vec int-array)))))
