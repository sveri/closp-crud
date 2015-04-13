(ns leiningen.entities-test
  (:require [clojure.test :refer :all]
            [leiningen.entities :as ent]))

(def table1-definition {:name    "table1"
                        :columns [[:id :int :null false :pk true :autoinc true]
                                  [:fooname [:varchar 40] :null false]
                                  [:age :int :null false]]})

(def table1-definition-with-uuid
  {:name    "table1"
   :columns [[:id :int :null false :pk true :autoinc true]
             [:fooname [:varchar 40] :null false]
             [:age :int :null false]
             [:uuid [:varchar 43] :null false]]})

;(def h2-uri "jdbc:h2:mem:test_mem")

(defn filter-uuid-cols [cols]
  (filter #(= :uuid (first %)) cols))

(deftest add-uuid-col
  (let [cols (:columns (ent/add-uuid-col table1-definition))
        cols-with-uuid (:columns (ent/add-uuid-col table1-definition-with-uuid))]
    (is (= 1 (count (filter-uuid-cols cols))))
    (is (= 1 (count (filter-uuid-cols cols-with-uuid))))))
