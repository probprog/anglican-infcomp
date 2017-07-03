(ns anglican.infcomp.zmq
  "ZMQ tools"
  (:require [zeromq.zmq :as zmq]
            [anglican.infcomp.flatbuffers.core :as fbs]
            [anglican.infcomp.prior :as prior]
            [anglican.infcomp.flatbuffers traces-from-prior-request message])
  (:import anglican.infcomp.flatbuffers.traces_from_prior_request.TracesFromPriorRequestClj
           anglican.infcomp.flatbuffers.message.MessageClj))

(defn start-replier
  "Starts ZMQ replier on a separate thread.

  input:
    query: Anglican query obtained via the query or defquery macros.
    query-args: Arguments to query.
    combine-observes-fn: A one input function which takes in an observes object
      obtained from sampling random variables in query defined via the `observe`
      statements and returns an N-D numeric array of values to be fed to the
      neural network to use as input the observe embedder.

      Example:

        (defn combine-observes-fn [observes]
          (:value (first observes)))

      To see what an observes object looks like, run `sample-observes-from-prior`
      with query and query-args to get one sample.
    combine-samples-fn: A one input function which takes in a list of samples
      obtained from sampling random variables in query defined via the `sample`
      statements and returns a modified list of samples in order to be fed to the
      decoder LSTM. Default: `identity`.

      Example (increments each sample by one):

        (defn combine-samples-fn [samples]
          (map #(update % :value inc) samples))

      To see what a list of samples looks like, run `sample-samples-from-prior`
      with query and query-args to get one sample.
    endpoint: String. Default value is \"tcp://*:5555\". Binds the replier socket
      to this endpoint.

  output: a map, carrying information about this connection. The recommended
    workflow is to bind it to a variable and after finishing compilation, call
    stop-replier on the variable."
  [query query-args combine-observes-fn &
   {:keys [endpoint combine-samples-fn]
    :or {endpoint "tcp://*:5555"
         combine-samples-fn identity}}]
  (let [context (zmq/context 1)
        socket (zmq/bind (zmq/socket context :rep)
                         endpoint)
        server (future (try (while (not (.. Thread currentThread isInterrupted))
                              (let [traces-from-prior-request (.body (fbs/unpack-message (zmq/receive socket)))
                                    _ (assert (instance? TracesFromPriorRequestClj traces-from-prior-request))
                                    num-traces (.num-traces traces-from-prior-request)
                                    traces-from-prior-reply (prior/generate-traces-from-prior-reply query query-args combine-observes-fn combine-samples-fn num-traces)]
                                (zmq/send socket (fbs/pack (MessageClj. traces-from-prior-reply)))))
                         (catch org.zeromq.ZMQException e
                           (str "ZMQ connection terminated."))
                         (catch Exception e
                           (str "Unknown exception: " e))))]
    {:socket socket :server server :context context}))

(defn stop-replier [replier]
  (zmq/set-linger (:socket replier) 0)
  (zmq/close (:socket replier))
  (.term (:context replier))
  (future-cancel (:server replier))
  @(:server replier))
