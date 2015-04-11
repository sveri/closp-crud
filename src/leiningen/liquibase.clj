(ns leiningen.liquibase
  (:require [clj-jdbcutil.core :as spec]
            [clj-liquibase.core :as lb]
            [clj-dbcp.core :as dbcp]
            [clj-miscutil.core :as mu]
            [clj-liquibase.change :as ch]
            [clojure.core.typed :as t]
            [leiningen.pre-types :as pt])
  (:import (liquibase.sql Sql)
           (liquibase.statement SqlStatement)
           (liquibase.sqlgenerator SqlGeneratorFactory)
           (liquibase.change Change)
           (java.util List)
           (liquibase.database Database)
           (java.sql Connection)
           (javax.sql DataSource)
           (liquibase.change.core CreateTableChange)))

(t/ann ^:no-check clj-liquibase.core/make-db-instance [Connection -> Database])
(t/ann get-db-connection [(t/U t/Keyword (t/HMap)) (t/U nil (t/HMap)) -> Database])
(defn get-db-connection
  "Create datasource from a given option-map. Some examples are below:
(make-datasource :derby {:target :memory :database :emp})            ;; embedded databases
(make-datasource :mysql {:host :localhost :database :emp
                         :username \"root\" :password \"s3cr3t\"})   ;; standard OSS databases
(make-datasource :jdbc  {:jdbc-url   \"jdbc:mysql://localhost/emp\"
                         :class-name \"com.mysql.Driver\"})          ;; JDBC arguments
(make-datasource :odbc  {:dsn :sales_report})                        ;; ODBC connections
(make-datasource :jndi  {:context \"whatever\"})                     ;; JNDI connections
(make-datasource {:adapter :pgsql :host :localhost :database :emp
                  :username :foo :password :bar})                    ;; :adapter in opts
(make-datasource {:adapter :odbc-lite :dsn :moo})                    ;; ODBC-lite (MS-Access, MS-Excel etc.)
(make-datasource {:class-name 'com.mysql.Driver
                  :jdbc-url   \"jdbc:mysql://localhost/emp\"})       ;; JDBC is default adapter"
  [adapter opts]
  (let [ds (dbcp/make-datasource adapter opts)
        spec (spec/make-dbspec ds)
        ^DataSource spec-ds (:datasource spec)
        ^Connection conn (.getConnection spec-ds)]
    (assert conn)
    (lb/make-db-instance conn)))

(t/ann ^:no-check change-sql [Change Database -> (t/HSeq [String])])
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

(t/ann ^:no-check create-table [pt/entity-description -> CreateTableChange])
(defn create-table [ent-description]
  (mu/! (ch/create-table (:name ent-description)
                    (:columns ent-description))))

(t/ann drop-table-sql [(t/HMap :mandatory {:name String}) -> String])
(defn drop-table-sql [table1-definition]
  (str "DROP TABLE " (:name table1-definition)))