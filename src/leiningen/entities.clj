(ns leiningen.entities
  (:require [leiningen.liquibase :as liq]
            [de.sveri.clojure.commons.files.edn :as comm-edn]
            [leiningen.helper :as h]
            [clojure.core.typed :as t]
            [leiningen.pre-types :as pt]))

(t/ann load-entity-from-path [String -> t/Any])
(defn load-entity-from-path [fp]
  (comm-edn/filepath->edn fp))

(t/ann generate-sql-statements [pt/entity-description String String -> nil])
(defn generate-sql-statements [ent-description jdbc-uri migr-out-path]
  (let [classname (h/jdbc-uri->classname jdbc-uri)
        db-connection (liq/get-db-connection :jdbc {:jdbc-url jdbc-uri :classname classname})
        gen-sql (liq/change-sql (liq/create-table ent-description) db-connection)]
    (h/store-table-migrations gen-sql (:name ent-description) (liq/drop-table-sql ent-description) migr-out-path)))