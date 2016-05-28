(ns de.sveri.clospcrud.entities
  (:require [de.sveri.clospcrud.liquibase :as liq]
            [de.sveri.clojure.commons.files.edn :as comm-edn]
            [de.sveri.clospcrud.helper :as h]
            [schema.core :as sc]
            [de.sveri.clospcrud.schema :as schem]
            [clojure.spec :as s]))

(s/fdef load-entity-from-path :args (s/cat :fp string?) :ret ::s/any)
(defn load-entity-from-path [fp]
  (comm-edn/filepath->edn fp))

(s/fdef generate-sql-statements :args (s/cat :ent-description ::schem/entity-description :jdbc-uri string?)
        :ret string?)
(defn generate-sql-statements [ent-description jdbc-uri]
  (let [classname (h/jdbc-uri->classname jdbc-uri)
        db-connection (liq/get-db-connection :jdbc {:jdbc-url jdbc-uri :classname classname})]
    (first (liq/change-sql (liq/create-table ent-description) db-connection))))

(s/fdef write-sql-statements
        :args (s/cat :ent-description ::schem/entity-description :jdbc-uri string? :migr-out-path string?)
        :ret nil?)
(defn write-sql-statements [ent-description jdbc-uri migr-out-path]
  (let [sql (generate-sql-statements ent-description jdbc-uri)]
    (h/store-table-migrations sql (:name ent-description) (liq/drop-table-sql ent-description) migr-out-path)
    (println "Generated database SQL Files.")))