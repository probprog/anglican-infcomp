(ns anglican-csis.network
  "Networking tools between Clojure and Torch."
  (:require [zeromq.zmq :as zmq]
            [msgpack.core :as msg]
            [anglican-csis.prior :refer [sample-from-prior]]
            [clojure.walk :refer [stringify-keys]]))

(defn start-torch-connection [query query-args combine-observes-fn tcp-endpoint]
  "doc"
  (let [context (zmq/context 1)
        socket (doto (zmq/socket context :rep)
                 (zmq/bind tcp-endpoint))
        server (future (try (while (not (.. Thread currentThread isInterrupted))
                              (let [msg (msg/unpack (zmq/receive socket))
                                    command (get msg "command")
                                    command-param (get msg "command-param")]
                                (cond (= command "new-batch") (let [prior-samples (map combine-observes-fn (take command-param (sample-from-prior query query-args)))]
                                                                (zmq/send socket (msg/pack (stringify-keys prior-samples))))
                                      :else (zmq/send-str socket "invalid command"))))
                         (catch org.zeromq.ZMQException e
                           (str "Torch connection terminated."))
                         (catch Exception e
                           (str "Unknown exception: " e))))]
    {:socket socket :server server :context context}))

(defn stop-torch-connection [torch-connection]
  (zmq/set-linger (:socket torch-connection) 0)
  (zmq/close (:socket torch-connection))
  (.term (:context torch-connection))
  (future-cancel (:server torch-connection))
  @(:server torch-connection))
