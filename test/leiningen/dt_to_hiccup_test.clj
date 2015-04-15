(ns leiningen.dt-to-hiccup-test
  (:require [clojure.test :refer :all]
            [leiningen.td-to-hiccup :as tth]))

(def int-c [:id :int :null false :pk true :autoinc true])
(def str-c [:name [:varchar 40] :null false :pk true])
(def bool-c [:male :boolean :default true])

(deftest conv-int
  (is (= [:input.form-control {:id "id" :required "required"}] (tth/dt->hiccup int-c))))

(deftest conv-varchar
  (is (= [:input.form-control {:id "name" :required "required" :maxlength 40}] (tth/dt->hiccup str-c))))

(deftest conv-boolean
  (is (= [:input.form-control {:id "name" :required "required" :maxlength 40}] (tth/dt->hiccup bool-c))))