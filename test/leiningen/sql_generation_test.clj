(ns leiningen.sql-generation-test
  (:require [clojure.test :refer :all]
            [de.sveri.clospcrud.liquibase :as liq]))

(defn create-h2-connection []
  (liq/get-db-connection :h2 {:target :memory :database :default}))

(def table1-definition {:name    "table1"
                        :columns [[:id :int :null false :pk true :autoinc true]
                                  [:fooname [:varchar 40] :null false]
                                  [:age :int :null false]
                                  [:male :boolean :default true]]})

(deftest create-table-h2
  (let [cs-string (first (liq/change-sql (liq/create-table table1-definition) (create-h2-connection)))]
    (is (.startsWith cs-string "CREATE TABLE PUBLIC.table1"))
    (is (.contains cs-string "fooname"))
    (is (.contains cs-string "age"))
    (is (.contains cs-string "male BOOLEAN DEFAULT TRUE"))))

(deftest drop-table-h2
  (is (= (str "DROP TABLE " (:name table1-definition)) (liq/drop-table-sql table1-definition))))
