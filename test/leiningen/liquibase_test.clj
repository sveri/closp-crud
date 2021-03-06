(ns leiningen.liquibase-test
  (:require [clojure.test :refer :all]
            [de.sveri.clospcrud.liquibase :as liq]
            [leiningen.common :refer [person-definition]]
            [de.sveri.clospcrud.spec.clospcrud :as schem]
            [clojure.spec :as s]
            [clojure.spec.gen :as gen]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]))

(deftest retrieve-type
  (is (= :int (liq/retrieve-column-type {:name "id" :type :int})))
  (is (= :time (liq/retrieve-column-type {:name "id" :type :time})))
  (is (= [:varchar 40] (liq/retrieve-column-type {:name "id" :type :varchar :max-length 40})))
  (is (= [:varchar 100] (liq/retrieve-column-type {:name "id" :type :varchar})))
  (is (= [:char 100] (liq/retrieve-column-type {:name "id" :type :char}))))

(deftest convert-optional-attrs
  (is (= [:null true] (liq/convert-optional-attr {:name "id" :type :varchar :null true})))
  (let [conv (liq/convert-optional-attr {:name "id" :type :varchar :null true :unique false
                                         :pk true :autoinc true})]
    (is (.contains conv :null))
    (is (.contains conv :pk))
    (is (.contains conv :autoinc))))


(deftest convert-entity
  (let [converted-cols (:columns (liq/entity->liquibase-entity person-definition))]
    ;(is (= [:id :int :null false :pk true :autoinc true] (first converted-cols)))
    (is (= [:fooname [:varchar 40] :null false] (second converted-cols)))
    (is (= [:age :int :null false] (nth converted-cols 2)))))


(s/instrument-all)