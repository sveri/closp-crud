(ns leiningen.entities
  (:require [leiningen.liquibase :as liq]
            [de.sveri.clojure.commons.files.edn :as comm-edn]
            [leiningen.helper :as h]
            [schema.core :as s]
            [leiningen.schema :as schem]))

(s/defn load-entity-from-path :- s/Any [fp :- s/Str]
  (comm-edn/filepath->edn fp))

(s/defn generate-sql-statements :- s/Str [ent-description :-  schem/entity-description jdbc-uri :- s/Str]
  (let [classname (h/jdbc-uri->classname jdbc-uri)
        db-connection (liq/get-db-connection :jdbc {:jdbc-url jdbc-uri :classname classname})]
    (first (liq/change-sql (liq/create-table ent-description) db-connection))))

(s/defn write-sql-statements :- nil
  [ent-description :-  schem/entity-description jdbc-uri :- s/Str migr-out-path :- s/Str]
  (let [sql (generate-sql-statements ent-description jdbc-uri)]
    (h/store-table-migrations sql (:name ent-description) (liq/drop-table-sql ent-description) migr-out-path)
    (println "Generated database SQL Files.")))