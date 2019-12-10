(ns gene-dosage-sepio-transformer.core
  (:require [gene-dosage-sepio-transformer.transform :as transform]
            [clojure.java.io :as io]
            [jackdaw.streams :as j]
            [jackdaw.serdes :as j-serde]
            [taoensso.timbre :as log]
            [org.httpkit.server :as server])
  (:gen-class))

(def app-config {:kafka-host (System/getenv "KAFKA_HOST")
                 :kafka-user (System/getenv "KAFKA_USER")
                 :kafka-password (System/getenv "KAFKA_PASSWORD")})

(defonce stream (atom nil))
(defonce server (atom nil))

(defn kafka-config 
  "Expects, at a minimum, :user and :password in opts. "
  [opts]
  {"application.id" "gene-dosage-sepio-transformer"
   "ssl.endpoint.identification.algorithm" "https"
   "sasl.mechanism" "PLAIN"
   "request.timeout.ms" "20000"
   "bootstrap.servers" (:kafka-host opts)
   "retry.backoff.ms" "500"
   "security.protocol" "SASL_SSL"
   "key.serializer" "org.apache.kafka.common.serialization.StringSerializer"
   "value.serializer" "org.apache.kafka.common.serialization.StringSerializer"
   "sasl.jaas.config" 
   (str "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" 
        (:kafka-user opts) "\" password=\"" (:kafka-password opts) "\";")})

(def topic-metadata
  {:input
   {:topic-name "gene_dosage_raw"
    :partition-count 1
    :replication-factor 1
    :key-serde (j-serde/string-serde)
    :value-serde (j-serde/string-serde)}
   :output
   {:topic-name "gene_dosage_sepio_in"
    :partition-count 1
    :replication-factor 1
    :key-serde (j-serde/string-serde)
    :value-serde (j-serde/string-serde)}})

(defn topology [in-topic out-topic]
  (let [builder (j/streams-builder)]
    (-> (j/kstream builder in-topic)
        (j/map (fn [[_ v]] (transform/interpretation-to-sepio v)))
        (j/to out-topic))
    builder))

(defn convert-local-dir [source destination]
  (let [files (file-seq (io/file source))
        xf (comp (filter #(re-find #"\.json$" (.getName %)))
                 (map slurp)
                 transform/sepio-xf)
        result (sequence xf files)]
    (doseq [r result]
      (if-let [fname (re-find #"ISCA.*$" (first r))]
        (spit (str destination fname) (second r))))))

(def running-states #{:re-balancing :running :created})

(defn status [req]
  (let [msg (str "gene-dosage-sepio-transformer is " (name (j/state @stream)))]
    (if (and @stream (running-states (j/state @stream)))
      {:status 200
       :headers {"Content-Type" "text/plain"}
       :body msg}
      {:status 503
       :headers {"Content-Type" "text/plain"}
       :body msg})))

(defn -main
  "Run the Kafka stream converting raw JIRA messages to SEPIO."
  [& args]
  (log/set-level! :info)
  (reset! server (server/run-server status {:port 8080}))
  (let [builder (topology (:input topic-metadata) (:output topic-metadata))
        app (j/kafka-streams builder (kafka-config app-config))]
    (j/start app)
    (reset! stream app)))
