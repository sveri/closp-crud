(ns leiningen.sql-generation-test
  (:require [clojure.test :refer :all]
            [schema.test :as st]
            [de.sveri.clospcrud.liquibase :as liq]
            [leiningen.common :refer [table1-definition]]))

(use-fixtures :once st/validate-schemas)

(defn create-h2-connection []
  (liq/get-db-connection :h2 {:target :memory :database :default}))

(deftest create-table-h2
  (let [cs-string (first (liq/change-sql (liq/create-table table1-definition) (create-h2-connection)))]
    (is (.startsWith cs-string "CREATE TABLE PUBLIC.person"))
    (is (.contains cs-string "fooname"))
    (is (.contains cs-string "age"))
    (is (.contains cs-string "CONSTRAINT PK_PERSON PRIMARY KEY"))))

(deftest drop-table-h2
  (is (= (str "DROP TABLE " (:name table1-definition)) (liq/drop-table-sql table1-definition))))
