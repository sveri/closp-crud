(ns leiningen.entities
  (:require [leiningen.liquibase :as liq]
            [de.sveri.clojure.commons.files.edn :as comm-edn]))

(defn load-entity-from-path [fp]
  (comm-edn/filepath->edn fp))

(defn generate-sql-statemens [ent-description]
  (liq/change-sql (liq/create-table ent-description)))

