(ns leiningen.entities
  (:require [leiningen.liquibase :as liq]
            [de.sveri.clojure.commons.files.edn :as comm-edn]
            [leiningen.helper :as h]))

(defn load-entity-from-path [fp]
  (comm-edn/filepath->edn fp))

(defn generate-sql-statements [ent-description jdbc-uri]
  (let [classname (h/jdbc-uri->classname jdbc-uri)
        db-connection (liq/get-db-connection :jdbc {:jdbc-url jdbc-uri :classname classname})]
    (liq/change-sql (liq/create-table ent-description) db-connection)))

