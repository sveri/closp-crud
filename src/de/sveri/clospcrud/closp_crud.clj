(ns de.sveri.clospcrud.closp-crud
  (:require [de.sveri.clospcrud.cli-options :as opt-helper]
            [clojure.tools.cli :as t-cli]
            [de.sveri.clospcrud.entities :as ent]
            [de.sveri.clospcrud.db-code-generator :as dcg]
            [clojure.core.typed :as t]
            [de.sveri.clospcrud.html-creator :as hc]
            [de.sveri.clospcrud.helper :as h]
            [de.sveri.clospcrud.routes-generator :as rg]
            [de.sveri.clojure.commons.files.edn :as f-edn])
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
  [& args]
  (let [{:keys [options]} (t-cli/parse-opts args opt-helper/cli-options)
        config (f-edn/from-edn "closp-crud.edn")
        file-in-path (:filepath options)
        jdbc-uri (:jdbc-url config)
        migr-out-path (:migrations-output-path config)
        ns-db (:ns-db config)
        ns-routes (:ns-routes config)
        ns-layout (:ns-layout config)
        clj-src (:clj-src config)
        templ-path (.getAbsolutePath (File. "./" (:templates config)))
        src-path (.getAbsolutePath (File. "./" clj-src))
        dataset (ent/load-entity-from-path file-in-path)]
    (if (files-exist? (:name dataset) ns-db ns-routes templ-path src-path)
      (println "Some file exists already. Cancelling.")
      (do (dcg/store-dataset ns-db dataset src-path)
          (ent/write-sql-statements (ent/load-entity-from-path file-in-path) jdbc-uri migr-out-path)
          (hc/store-html-files dataset templ-path)
          (rg/store-route ns-routes ns-db ns-layout dataset src-path)
          (println "Done.")))))
