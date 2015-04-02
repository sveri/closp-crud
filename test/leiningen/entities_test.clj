(ns leiningen.entities-test
  (:require [clojure.test :refer :all]
            [leiningen.entities :as ent]))

(def table1-definition {:name    "table1"
                        :columns [[:id :int :null false :pk true :autoinc true]
                                  [:fooname [:varchar 40] :null false]
                                  [:age :int :null false]]})

(def h2-uri "jdbc:h2:mem:test_mem")


;(deftest generate-sql-statements
;  (let [ct-string (first (ent/generate-sql-statements table1-definition h2-uri))]
;    (is (.startsWith ct-string "CREATE TABLE PUBLIC.table1"))))
