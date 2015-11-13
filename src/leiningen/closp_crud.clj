(ns leiningen.closp-crud
  (:require [leiningen.cli-options :as opt-helper]
            [clojure.tools.cli :as t-cli]
            [leiningen.entities :as ent]
            [leiningen.db-code-generator :as dcg]
            [clojure.core.typed :as t]
            [leiningen.html-creator :as hc]
            [leiningen.helper :as h]
            [leiningen.routes-generator :as rg])
  (:import (java.io File)))

(defn files-exist? [entity-name ns-db ns-routes templ-path src-path]
  (let [entity-name-sanitized (h/sanitize-filename entity-name)
        db-fn (h/get-ns-file-path ns-db (str entity-name-sanitized ".clj") src-path)
        routes-fn (h/get-ns-file-path ns-routes (str entity-name-sanitized ".clj") src-path)
        templ-filenames ["create.html" "index.html" "delete.html"]
        tmpl-path (str templ-path "/" entity-name)
        all-fps (concat [db-fn routes-fn] (map #(str tmpl-path "/" %) templ-filenames))]
    (some #{true} (map #(.exists (File. %)) all-fps))))

; TODO proper error handling
;(t/ann closp-crud [(t/HMap :mandatory {:closp-crud t/Any}) -> nil])
(defn closp-crud
  [project & args]
  (let [{:keys [options]} (t-cli/parse-opts args opt-helper/cli-options)
        file-in-path (:filepath options)
        jdbc-uri (get-in project [:closp-crud :jdbc-url])
        migr-out-path (get-in project [:closp-crud :migrations-output-path])
        ns-db (get-in project [:closp-crud :ns-db])
        ns-routes (get-in project [:closp-crud :ns-routes])
        ns-layout (get-in project [:closp-crud :ns-layout])
        clj-src (get-in project [:closp-crud :clj-src])
        templ-path (.getAbsolutePath (File. "./" (get-in project [:closp-crud :templates])))
        src-path (.getAbsolutePath (File. "./" clj-src))
        dataset (ent/load-entity-from-path file-in-path)]
    (if (files-exist? (:name dataset) ns-db ns-routes templ-path src-path)
      (println "Some file exists already. Cancelling.")
      (do (dcg/store-dataset ns-db dataset src-path)
          (ent/generate-sql-statements (ent/load-entity-from-path file-in-path) jdbc-uri migr-out-path)
          (hc/store-html-files dataset templ-path)
          (rg/store-route ns-routes ns-db ns-layout dataset src-path)
          (println "Done.")))))
