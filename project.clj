(defproject dwc-bot-es "0.0.5"
  :description "Insert DarwinCore Archives Occurrences from IPT into ElasticSearch"
  :url "http://github.com/diogok/dwc-bot-es"
  :license {:name "MIT"}
  :main dwc-bot-es.core
  :dependencies [[org.clojure/clojure "1.8.0"]

                 [org.clojure/core.async "0.2.391"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/data.xml "0.0.8"]

                 [clj-http-lite "0.3.0"]
                 [clj-time "0.12.0"]

                 [batcher "0.1.1"]
                 [dwc-io "0.0.59"]

                 [com.taoensso/timbre "4.7.4"]
                 [environ "1.1.0"]]
  :repositories [["clojars" {:sign-releases false}]]
  :source-paths ["src"]
  :profiles {:uberjar {:aot :all}})
