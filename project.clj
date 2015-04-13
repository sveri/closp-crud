(defproject de.sveri/closp-crud "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-liquibase "0.5.2"]
                 [clj-jdbcutil "0.1.0"]
                 [clj-dbcp "0.8.1"]
                 [org.clojure/tools.cli "0.3.1"]
                 [de.sveri/clojure-commons "0.2.0"]
                 [clj-time "0.9.0"]
                 [selmer "0.8.2"]
                 [com.h2database/h2 "1.4.185"]
                 ]
  :source-paths ["src"]
  :test-paths ["test"]
  :profiles {:dev {:resource-paths ["test-resources"]}}
  ;:eval-in-leiningen true
  )
