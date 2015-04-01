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
        jdbc-uri (get-in m [:closp-crud :jdbc-url])
        migr-out-path (get-in project [:closp-crud :migrations-output-path])]
    ;(println project)
    ;(println (ent/))
    ;(println "main: " (ent/load-entity-from-path fp))
    ;(println  (ent/generate-sql-statemens (ent/load-entity-from-path fp)))
    ;(println "Hi!" (ent/load-entity-from-path fp))
    ))


;(closp-crud "" "-f C:/Users/sveri/IdeaProjects/closp-crud/test-resources/table1-definition.edn")




; proposed syntax









;
;(ns closp-crud.core
;  (:require
;    [clojure.pprint :as pp]
;    [clj-miscutil.core :as mu]
;    [clj-liquibase.core :as lb]
;    [clj-liquibase.change :as ch]
;    [clj-liquibase.cli :as cli]
;    [clj-dbcp.core :as dbcp]
;    [clj-jdbcutil.core :as spec])
;  (:gen-class)
;  (:import (liquibase.sql Sql)
;           (liquibase.statement SqlStatement)
;           (liquibase.sqlgenerator SqlGeneratorFactory)
;           (liquibase.database Database)
;           (liquibase.change Change)
;           (java.util List)))
;
;(def ds (dbcp/make-datasource :h2 {:target :memory :database :default}))
;;;(def ds (dbcp/make-datasource :mysql {:host "localhost" :database "bituf" :user "root" :password "root"}))
;(def dbspec (spec/make-dbspec ds))
;(def ct-change1 (mu/! (ch/create-table "sampletable1"
;                                       [[:id :int :null false :pk true :autoinc true]
;                                        [:name [:varchar 40] :null false]
;                                        [:gender [:char 1] :null false]])))
;(def ct-change2 (mu/! (ch/create-table "sampletable2"
;                                       [[:id :int :null false :pk true :autoinc true]
;                                        [:name [:varchar 40] :null false]
;                                        [:gender [:char 1] :null false]])))
;(def ct-change3 (mu/! (ch/sql "SELECT * FROM sampletable1")))
;(def changeset-ct ["id=1" "author=shantanu" [ct-change1]])
;(def changeset-1 ["id=1" "author=shantanu" [ct-change1]])
;(def changeset-2 ["id=2" "author=shantanu" [ct-change2]])
;(def changeset-3 ["id=3" "author=shantanu" [ct-change3]])
;
;(lb/defchangelog changelog-ct "example" [changeset-ct])
;(lb/defchangelog changelog-1 "example" [changeset-1])
;(lb/defchangelog changelog-2 "example" [changeset-1 changeset-2])
;(lb/defchangelog changelog-3 "example" [changeset-1 changeset-2 changeset-3])
;
;(defn ^List change-sql
;  "Return a list of SQL statements (string) that would be required to execute
;  the given Change object instantly for current database without versioning."
;  [^Change change ds]
;  ;{:post [(mu/verify-cond (vector? %))
;  ;                         (mu/verify-cond (every? string? %))]
;  ;                  :pre  [(mu/verify-arg (instance? Change change))
;  ;                         (mu/verify-cond (instance? Database *db-instance*))]}
;  (let [sgf (SqlGeneratorFactory/getInstance)
;        sql (map (fn [^SqlStatement stmt]
;                   (map (fn [^Sql sql]
;                          ^String (.toSql sql))
;                        (.generateSql sgf stmt ds)))
;                 (.generateStatements change ds))]
;    (into [] (flatten sql))))
