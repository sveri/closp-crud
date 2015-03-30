(defproject closp-crud "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.h2database/h2 "1.4.185"]
                 [clj-liquibase "0.5.2"]
                 [clj-dbcp "0.8.1"]]
  :main ^:skip-aot closp-crud.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
