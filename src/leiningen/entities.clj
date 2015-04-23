(ns leiningen.entities
  (:require [leiningen.liquibase :as liq]
            [de.sveri.clojure.commons.files.edn :as comm-edn]
            [leiningen.helper :as h]
            [clojure.core.typed :as t]
            [leiningen.pre-types :as pt]))

(t/ann uuid-col pt/et-column)
(def uuid-col [:uuid [:varchar 43] :null false])

(t/ann conj-it [pt/et-columns -> pt/et-columns])
(defn conj-it [cols]
  (conj cols uuid-col))

(t/ann add-uuid-col [pt/entity-description -> pt/entity-description])
(defn add-uuid-col [ent-description]
  (let [cols (:columns ent-description)]
    (if (empty? (filter (t/fn [col :- pt/et-column] (= :uuid (first col))) cols))
      (assoc ent-description :columns (conj-it cols))
      ent-description)))

(t/ann ^:no-check load-entity-from-path [String -> t/Any])
(defn load-entity-from-path [fp]
  (comm-edn/filepath->edn fp))

(t/ann ^:no-check generate-sql-statements [pt/entity-description String String -> nil])
(defn generate-sql-statements [ent-description jdbc-uri migr-out-path]
  (let [classname (h/jdbc-uri->classname jdbc-uri)
        db-connection (liq/get-db-connection :jdbc {:jdbc-url jdbc-uri :classname classname})
        gen-sql (liq/change-sql (liq/create-table (add-uuid-col ent-description)) db-connection)]
    (h/store-table-migrations gen-sql (:name ent-description) (liq/drop-table-sql ent-description) migr-out-path)
    (println "Generated database SQL Files.")))