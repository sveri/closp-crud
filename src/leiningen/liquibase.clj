(ns leiningen.liquibase
  (:require [clj-jdbcutil.core :as spec]
            [clj-liquibase.core :as lb]
            [clj-dbcp.core :as dbcp]
            [clj-miscutil.core :as mu]
            [clj-liquibase.change :as ch]
            [clojure.core.typed :as t]
            [de.sveri.ctanns.clj-liquibase-clj-misc-util])
  (:import (liquibase.sql Sql)
           (liquibase.statement SqlStatement)
           (liquibase.sqlgenerator SqlGeneratorFactory)
           (liquibase.change Change)
           (java.util List)
           (liquibase.database Database)
           (java.sql Connection)
           (javax.sql DataSource)
           (java.lang.reflect Array)
           (liquibase.change.core CreateTableChange)))

(t/ann ^:no-check clj-liquibase.core/make-db-instance [Connection -> Database])
(t/ann ^:no-check clj-dbcp.core/make-datasource [t/Any t/Any -> Connection])
(t/ann ^:no-check clj-jdbcutil.core/make-dbspec [Connection -> (t/HMap :mandatory {:datasource DataSource})])
(t/ann ^:no-check clj-liquibase.change/create-table [String t/Any -> t/Any])

(t/ann get-db-connection [t/Any t/Any -> Database])
(defn get-db-connection [adapter opts]
  (let [ds (dbcp/make-datasource adapter opts)
        spec (spec/make-dbspec ds)
        ^DataSource spec-ds (:datasource spec)
        ^Connection conn (.getConnection spec-ds)]
    (assert conn)
    (lb/make-db-instance conn)))

(t/ann ^:no-check change-sql [Change Database -> (t/HVec [String])])
(defn ^List change-sql
  "Return a list of SQL statements (string) that would be required to execute
  the given Change object instantly for current database without versioning."
  [^Change change ds]
  {:post [(mu/verify-cond (vector? %))
          (mu/verify-cond (every? string? %))]
   :pre  [(mu/verify-arg (instance? Change change))
          (mu/verify-cond (instance? Database ds))]}
  (let [sgf (SqlGeneratorFactory/getInstance)
        sql (map (fn [^SqlStatement stmt]
                   (map (fn [^Sql sql]
                          ^String (.toSql sql))
                        (.generateSql sgf stmt ds)))
                 (.generateStatements change ds))]
    (into [] (flatten sql))))

(t/ann create-table [(t/HMap :mandatory {:name String :columns String}) -> CreateTableChange])
(defn create-table [ent-description]
  (ch/create-table (:name ent-description)
                         (:columns ent-description))
  ;(mu/! (ch/create-table (:name ent-description)
  ;                       (:columns ent-description)))
  )
;
;(defn drop-table-sql [table1-definition]
;  (str "DROP TABLE " (:name table1-definition)))