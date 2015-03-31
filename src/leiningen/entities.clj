(ns leiningen.entities
  (:require [leiningen.liquibase :as liq]
            [de.sveri.clojure.commons.files.edn :as comm-edn]))

(defn load-entity-from-path [fp]
  (comm-edn/filepath->edn fp))

(defn generate-sql-statements [ent-description jdbc-uri]
  (let [classname "org.h2.Driver"
        db-connection (liq/get-db-connection classname jdbc-uri)]
    (liq/change-sql (liq/create-table ent-description) db-connection)))

