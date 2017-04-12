(ns anglican.infcomp.network
  "Networking tools between Clojure and Torch."
  (:require [zeromq.zmq :as zmq]
            [clojure.core.matrix :refer [shape]]
            [msgpack.core :as msg]
            [anglican.infcomp.flatbuffers.protocols :as fbs]
            [anglican.infcomp.prior :refer [sample-from-prior]]
            [clojure.walk :refer [stringify-keys]]
            [anglican.infcomp.flatbuffers.ndarray :refer [to-NDArrayClj]]
            [anglican.infcomp.flatbuffers traces-from-prior-request traces-from-prior-reply trace ndarray sample normal-proposal uniform-discrete-proposal message])
  (:import [anglican.infcomp.flatbuffers.traces_from_prior_request TracesFromPriorRequestClj]
           [anglican.infcomp.flatbuffers.traces_from_prior_reply TracesFromPriorReplyClj]
           [anglican.infcomp.flatbuffers.trace TraceClj]
           [anglican.infcomp.flatbuffers.ndarray NDArrayClj]
           [anglican.infcomp.flatbuffers.sample SampleClj]
           [anglican.infcomp.flatbuffers.normal_proposal NormalProposalClj]
           [anglican.infcomp.flatbuffers.uniform_discrete_proposal UniformDiscreteProposalClj]
           [anglican.infcomp.flatbuffers.message MessageClj]))

(defn start-torch-connection
  "Starts a ZeroMQ connection with Torch in order to compile the probabilistic
  program query with arguments query-args. Takes function combine-observes-fn
  to combine observations generated from the query and (optionally)
  tcp-endpoint to establish the connection via ZeroMQ. Returns a connection
  object which is used to stop the connection using stop-torch-connection.

  Inputs

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

  (Optional) tcp-endpoint: String. Default value is \"tcp://*:5555\". TCP value
    which is used to establish a connection with Torch via ZeroMQ. If
    \"tcp://*:<port-number>\" is chosen, the compile.lua should be run as
    follows:

      th compile.lua --server localhost:<port-number> <other-options>...

  combine-samples-fn: A one input function which takes in a list of samples
    obtained from sampling random variables in query defined via the `sample`
    statements and returns a modified list of samples in order to be fed to the
    Torch decoder LSTM. Default: `identity`.

    Example (increments each sample by one):

      (defn combine-samples-fn [samples]
        (map #(update % :value inc) samples))

    To see what a list of samples looks like, run `sample-samples-from-prior`
    with query and query-args to get one sample.

  Outputs

  A map, carrying information about this connection and ways to stop it. The
  recommended workflow is to bind it to a variable and after finishing
  compilation, call stop-torch-connection on the variable. Bad things can
  happen if this is not done."
  [query query-args combine-observes-fn &
   {:keys [tcp-endpoint combine-samples-fn]
    :or {tcp-endpoint "tcp://*:5555"
         combine-samples-fn identity}}]
  (let [context (zmq/context 1)
        socket (doto (zmq/socket context :rep)
                 (zmq/bind tcp-endpoint))
        server (future (try (while (not (.. Thread currentThread isInterrupted))
                              (let [traces-from-prior-request (.body (fbs/unpack-message (zmq/receive socket)))
                                    _ (assert (instance? TracesFromPriorRequestClj traces-from-prior-request))
                                    num-traces (.num-traces traces-from-prior-request)
                                    prior-samples (stringify-keys
                                                   (map (comp
                                                         ;; Update observes via the combine-observes-fn
                                                         (fn [smp]
                                                           (update smp
                                                                   :observes
                                                                   combine-observes-fn))

                                                         ;; Update samples via the combine-samples-fn
                                                         (fn [smp]
                                                           (update smp
                                                                   :samples
                                                                   combine-samples-fn)))
                                                        (take num-traces
                                                              (sample-from-prior query query-args))))
                                    traces-from-prior-reply (TracesFromPriorReplyClj.
                                                             (map (fn [trace]
                                                                    (TraceClj.
                                                                     (to-NDArrayClj (get trace "observes"))
                                                                     (map (fn [sample]
                                                                            (SampleClj.
                                                                             (get sample "time-index")
                                                                             (get sample "sample-address")
                                                                             (get sample "sample-instance")
                                                                             (get sample "proposal")
                                                                             (to-NDArrayClj (get sample "value"))))
                                                                          (get trace "samples"))))
                                                                  prior-samples))]
                                (zmq/send socket (fbs/pack (MessageClj. traces-from-prior-reply)))))
                         (catch org.zeromq.ZMQException e
                           (str "Torch connection terminated."))
                         (catch Exception e
                           (str "Unknown exception: " e))))]
    {:socket socket :server server :context context}))

(defn stop-torch-connection
  "Stops a ZeroMQ connection with Torch after the compilation is finished.
  Takes in torch-connection obtained from start-torch-connection."
  [torch-connection]
  (zmq/set-linger (:socket torch-connection) 0)
  (zmq/close (:socket torch-connection))
  (.term (:context torch-connection))
  (future-cancel (:server torch-connection))
  @(:server torch-connection))

(defn sample-observes-from-prior
  "Returns a sample from the random variables defined via the observe
  statements in query given the query arguments query-args."
  [query query-args]
  (:observes (first (sample-from-prior query query-args))))

(defn sample-samples-from-prior
  "Returns a sample from the random variables defined via the sample
  statements in query given the query arguments query-args."
  [query query-args]
  (:samples (first (sample-from-prior query query-args))))
