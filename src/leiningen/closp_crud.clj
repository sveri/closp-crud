(ns leiningen.closp-crud
  (:require [leiningen.cli-options :as opt-helper]
            [clojure.tools.cli :as t-cli]
            [leiningen.entities :as ent]
            [clojure.string :as s]
            [leiningen.helper :as h]))

(defn closp-crud
  "I don't do a lot."
  [project & args]
  (let [{:keys [options arguments errors summary]} (t-cli/parse-opts args opt-helper/cli-options)
        file-in-path (:filepath options)
        jdbc-uri (get-in project [:closp-crud :jdbc-url])
        migr-out-path (get-in project [:closp-crud :migrations-output-path])
        ns (get-in project [:closp-crud :ns])]
    (ent/generate-sql-statements (ent/load-entity-from-path file-in-path) jdbc-uri migr-out-path)))
