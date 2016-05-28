(ns de.sveri.clospcrud.liquibase
  (:require [clj-jdbcutil.core :as spec]
            [clj-liquibase.core :as lb]
            [clj-dbcp.core :as dbcp]
            [clj-miscutil.core :as mu]
            [clj-liquibase.change :as ch]
            [schema.core :as sc]
            [de.sveri.clospcrud.schema :as schem]
            [de.sveri.clospcrud.liquibase-schema :as lschem]
            [clojure.spec :as s])
  (:import (liquibase.sql Sql)
           (liquibase.statement SqlStatement)
           (liquibase.sqlgenerator SqlGeneratorFactory)
           (liquibase.change Change)
           (java.util List)
           (liquibase.database Database)
           (liquibase.change.core CreateTableChange)))

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
        spec-ds (:datasource spec)
        conn (.getConnection spec-ds)]
    (assert conn)
    (lb/make-db-instance conn)))

(s/fdef change-sql :ret (s/cat :content string?))
(defn ^List change-sql
  "Return a list of SQL statements (string) that would be required to execute
  the given Change object instantly for current database without versioning."
  [^Change change ^Database ds]
  {:post [(mu/verify-cond (vector? %))
          (mu/verify-cond (every? string? %))]
   :pre  [(mu/verify-arg (instance? Change change))
          (mu/verify-cond (instance? Database ds))]}
  (let [sgf (SqlGeneratorFactory/getInstance)
        sql (map (fn [^SqlStatement stmt]
                   (map (fn [^Sql sql]
                          (.toSql sql))
                        (.generateSql sgf stmt ds)))
                 (.generateStatements change ds))]
    (into [] (flatten sql))))

(s/fdef retrieve-column-type :args (s/cat :col ::schem/column)
        :ret (s/or :standard-type ::schem/column-types
                   :length-type (s/cat :type keyword? :length number?)))
(defn retrieve-column-type [col]
  (if (some #{:char :varchar} [(:type col)]) [(:type col) (get col :max-length 100)]
                                             (:type col)))

(s/fdef convert-optional-attr :args (s/cat :col ::schem/column) :ref (s/cat :ret ::s/any))
(defn convert-optional-attr [col]
  (let [cleaned-col (dissoc col :name :type :max-length)]
    (if (not-empty cleaned-col)
      (reduce #(into %2 %1) cleaned-col)
      [])))

(s/fdef entity->liquibase-entity :args (s/cat :desc ::schem/entity-description)
        :ret ::lschem/entity-description)
(defn entity->liquibase-entity [desc]
  (let [cols (:columns desc)
        conv-cols (mapv (fn [col] (vec (concat [(keyword (:name col))
                                                (retrieve-column-type col)]
                                               (convert-optional-attr col))))
                        cols)]
    (assoc desc :columns conv-cols)))

(s/fdef create-table :args (s/cat :ent-description ::schem/entity-description)
        :ret #(instance? CreateTableChange %))
(defn create-table [ent-description]
  (let [liq-desc (entity->liquibase-entity ent-description)]
    (mu/! (ch/create-table (:name liq-desc)
                           (:columns liq-desc)))))


(s/fdef drop-table-sql :args (:table-definition (s/keys :req-un [::schem/name])) :ret string?)
(defn drop-table-sql [table-definition]
  (str "DROP TABLE " (:name table-definition)))
