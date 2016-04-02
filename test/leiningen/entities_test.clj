(ns leiningen.entities-test
  (:require [clojure.test :refer :all]
            [leiningen.entities :as ent]
            [schema.core :as s]))

(s/set-fn-validation! true)

(def table1-definition {:name    "table1"
                        :columns [[:id :int :null false :pk true :autoinc true]
                                  [:fooname [:varchar 40] :null false]
                                  [:age :int :null false]]})

(def h2-uri "jdbc:h2:mem:test_mem")

(deftest generate-sql-statement
  (let [sql (ent/generate-sql-statements table1-definition h2-uri)]
    (is (.contains sql "CREATE"))
    (is (.contains sql "id INT AUTO_INCREMENT NOT NULL"))
    (is (.contains sql "fooname VARCHAR(40) NOT NULL"))
    (is (.contains sql "age INT NOT NULL"))))