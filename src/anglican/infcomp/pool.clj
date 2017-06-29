(ns anglican.infcomp.pool
  "Pool tools"
  (:require [zeromq.zmq :as zmq]
            [anglican.infcomp.flatbuffers.core :as fbs]
            [anglican.infcomp.prior :as prior]
            [clojure.java.io :as io]
            [anglican.infcomp.flatbuffers traces-from-prior-request message]
            [clj-uuid :as uuid])
  (:import anglican.infcomp.flatbuffers.traces_from_prior_request.TracesFromPriorRequestClj
           anglican.infcomp.flatbuffers.message.MessageClj))

(defn- folder-size [path]
  "Folder size in bytes"
  (let [file (io/file path)]
    (if (.isDirectory file)
      (apply + (map folder-size (.listFiles file)))
      (.length file))))

(defn- is-directory? [path]
  (.isDirectory (io/file path)))

(defn- exists? [path]
  (.exists (io/file path)))

(defn- empty-directory? [path]
  (let [file (io/file path)]
    (assert (.exists file))
    (assert (.isDirectory file))
    (-> file .list empty?)))

(defn- make-directory-recursively [path]
  (let [dir (io/file path)]
    (if (.exists dir)
      true
      (.mkdirs dir))))

(defn start-pool [query query-args combine-observes-fn folder-name &
                  {:keys [combine-samples-fn traces-per-file max-folder-size]
                   :or {combine-samples-fn identity
                        traces-per-file 1000
                        max-folder-size 1000}}]
  "Starts a thread that writes to folder.

  input:
    query: Anglican query obtained via the query or defquery macros.
    query-args: Arguments to query.
    combine-observes-fn: A one input function which takes in an observes object
      obtained from sampling random variables in query defined via the `observe`
      statements and returns an N-D numeric array of values to be fed to Torch
      to use as input the observe embedder.

      Example:

        (defn combine-observes-fn [observes]
          (:value (first observes)))

      To see what an observes object looks like, run `sample-observes-from-prior`
      with query and query-args to get one sample.
    folder-name: folder name (must be empty or non-existent
    combine-samples-fn: A one input function which takes in a list of samples
      obtained from sampling random variables in query defined via the `sample`
      statements and returns a modified list of samples in order to be fed to the
      decoder LSTM. (optional; default = `identity`).

      Example (increments each sample by one):

        (defn combine-samples-fn [samples]
          (map #(update % :value inc) samples))

      To see what a list of samples looks like, run `sample-samples-from-prior`
      with query and query-args to get one sample.
    traces-per-file: number of traces per file (optional; default = 10)
    max-folder-size: maximum folder size in bytes (optional; default = 1e6)

  output: future object that can be used to cancel data generation before reaching max-folder-size"

  (assert (or (not (exists? folder-name))
              (and (is-directory? folder-name)
                   (empty-directory? folder-name)))
          (str "Pool folder " folder-name " must be either non-existent or an empty directory."))
  (if (not (exists? folder-name)) (make-directory-recursively folder-name))
  (future (try (while (and (not (.. Thread currentThread isInterrupted))
                           (< (folder-size folder-name) max-folder-size))
                 (let [traces-from-prior-reply (prior/generate-traces-from-prior-reply query query-args combine-observes-fn combine-samples-fn traces-per-file)
                       file-name (str folder-name "/" (uuid/v1))
                       message-fbs (fbs/pack (MessageClj. traces-from-prior-reply))]
                   (spit file-name message-fbs))))))

(defn stop-pool [replier]
  (future-cancel (:server replier)))
