(defproject gene-dosage-sepio-transformer "v2"
  :description "Uses Kafka Streams to transform raw JIRA output to Gene Dosage SEPIO form"
  :url "https://github.com/clingen-data-model/gene-dosage-sepio-transformer"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [cheshire "5.9.0"]
                 [http-kit "2.3.0"] 
                 [com.taoensso/timbre "4.10.0"]
                 [com.fzakaria/slf4j-timbre "0.3.14"]
                 [fundingcircle/jackdaw "0.6.9"]
                 [camel-snake-kebab "0.4.1"]
                 [org.flatland/ordered "1.5.7"]]
  :main ^:skip-aot gene-dosage-sepio-transformer.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :uberjar-name "gene-dosage-sepio-transformer.jar"}})
