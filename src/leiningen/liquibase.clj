(ns leiningen.liquibase
  (:require [clj-jdbcutil.core :as spec]
            [clj-liquibase.core :as lb]
            [clj-dbcp.core :as dbcp])
  (:import (liquibase.sql Sql)
           (liquibase.statement SqlStatement)
           (liquibase.sqlgenerator SqlGeneratorFactory)
           (liquibase.change Change)
           (java.util List)))


(def ds (dbcp/make-datasource :h2 {:target :memory :database :default}))
(def dbspec (spec/make-dbspec ds))

(def dbinst (lb/make-db-instance (.getConnection (:datasource dbspec))))

;(defn get-db-connection [adapter opts]
;  (lb/make-db-instance (.getConnection (:datasource (spec/make-dbspec (dbcp/make-datasource adapter opts)))))
;  ;(dbcp/make-datasource :h2 {:target :memory :database :default})
;  )

(defn get-db-connection [adapter opts]
  (-> (dbcp/make-datasource adapter opts)
      (spec/make-dbspec)
      :datasource
      (.getConnection)
      (lb/make-db-instance)))

(defn get-h2-memory-connection []
  (get-db-connection :h2 {:target :memory :database :default}))

(defn ^List change-sql
  "Return a list of SQL statements (string) that would be required to execute
  the given Change object instantly for current database without versioning."
  [^Change change ds]
  ;{:post [(mu/verify-cond (vector? %))
  ;                         (mu/verify-cond (every? string? %))]
  ;                  :pre  [(mu/verify-arg (instance? Change change))
  ;                         (mu/verify-cond (instance? Database *db-instance*))]}
  (let [sgf (SqlGeneratorFactory/getInstance)
        sql (map (fn [^SqlStatement stmt]
                   (map (fn [^Sql sql]
                          ^String (.toSql sql))
                        (.generateSql sgf stmt ds)))
                 (.generateStatements change ds))]
    (into [] (flatten sql))))
