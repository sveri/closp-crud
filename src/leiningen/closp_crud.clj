(ns leiningen.closp-crud
  (:require [leiningen.cli-options :as opt-helper]
            [clojure.tools.cli :as t-cli]
            [leiningen.entities :as ent]
            [leiningen.db-code-generator :as dcg]
            [clojure.core.typed :as t]
            [leiningen.html-creator :as hc])
  (:import (java.io File)))

; TODO proper error handling
;(t/ann closp-crud [(t/HMap :mandatory {:closp-crud t/Any}) -> nil])
(defn closp-crud
  "I don't do a lot."
  [project & args]
  (let [{:keys [options arguments errors summary]} (t-cli/parse-opts args opt-helper/cli-options)
        file-in-path (:filepath options)
        jdbc-uri (get-in project [:closp-crud :jdbc-url])
        migr-out-path (get-in project [:closp-crud :migrations-output-path])
        ns-db (get-in project [:closp-crud :ns-db])
        ns-routes (get-in project [:closp-crud :ns-routes])
        clj-src (get-in project [:closp-crud :clj-src])
        templ-path (.getAbsolutePath (File. "./" (get-in project [:closp-crud :templates])))
        src-path (.getAbsolutePath (File. "./" clj-src))
        dataset (ent/load-entity-from-path file-in-path)]
    (dcg/store-dataset ns-db dataset src-path)
    (ent/generate-sql-statements (ent/load-entity-from-path file-in-path) jdbc-uri migr-out-path)
    (hc/store-create-html dataset templ-path)))
