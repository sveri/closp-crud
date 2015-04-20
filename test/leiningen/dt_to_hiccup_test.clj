(ns leiningen.dt-to-hiccup-test
  (:require [clojure.test :refer :all]
            [leiningen.td-to-hiccup :as tth]))

(def int-c [:id :int :null false :pk true :autoinc true])
(def varchar-c [:name [:varchar 40] :null false :pk true])
(def varchar-invalid-c [:name [:varchar] :null false :pk true])
(def char-c [:name [:char 40] :null false :pk true])
(def bool-c [:male :boolean :default true])

(deftest conv-int
  (is (= [[:label {:for "id"} "id"] [:input.form-control {:id "id" :required "required" :name "id"}]]
         (tth/dt->hiccup int-c :create))))

(deftest conv-varchar
  (is (= [[:label {:for "name"} "name"]
          [:input.form-control {:id "name" :required "required" :maxlength 40 :name "name"}]]
         (tth/dt->hiccup varchar-c :create)))
  (is (thrown? AssertionError (tth/dt->hiccup varchar-invalid-c :create))))

(deftest conv-char
  (is (= [[:label {:for "name"} "name"]
          [:input.form-control {:id "name" :required "required" :maxlength 40 :name "name"}]]
         (tth/dt->hiccup char-c :create))))

(deftest conv-boolean
  (is (= [[:label {:for "male"} "male"]
          [:input.form-control {:id "male" :checked "checked" :name "male"}]] (tth/dt->hiccup bool-c :create)))
  (is (= [[:label {:for "male"} "male"]
          [:input.form-control {:id "male" :name "male"}]] (tth/dt->hiccup (assoc bool-c 3 false) :create))))