(ns leiningen.html-creator-test
  (:require [clojure.test :refer :all]
            [de.sveri.clospcrud.html-creator :as ht]
            [schema.core :as s]))

(s/set-fn-validation! true)

(def table1-definition {:name    "table1"
                        :columns [[:id :int :null false :pk true :autoinc true]
                                  [:fooname [:varchar 40] :null false]
                                  [:male :boolean :null false]
                                  [:description :text]]})

(def underscore-def {:name "table2"
                     :columns [[:some_thing :boolean :null false]]})

(def car-definition {:name    "car"
                     :columns [[:id :int :null false :pk true :autoinc true]
                               [:first_name [:varchar 30]]
                               [:email [:varchar 30] :null false :unique true]
                               [:last_login :time]
                               [:is_active :boolean :null false :default false]
                               [:activationid [:varchar 100] :unique true]]})

(deftest create-html
  (let [html (ht/create-html table1-definition)]
    (is (= true (.contains html "table1/{{crea")))
    (is (= true (.contains html "value=\"{{table1.fooname")))))

(deftest text-html
  (let [html (ht/create-html table1-definition)]
    (is (= true (.contains html "{{table1.description}}")))))

(deftest bool-html
  (let [html (ht/create-html table1-definition)]
    (is (= true (.contains html (str "{%if table1.male = 1 %}checked{% endif %}"))))))

(deftest underscore-in-col-name
  (let [html (ht/create-html underscore-def)]
    (is (= true (.contains html (str "{%if table2.some_thing = 1 %}checked{% endif %}"))))))

(deftest car-def-test
  (let [html (ht/create-html car-definition)]
    (is (.contains html "value=\"{{car.first_name}}\""))
    (is (.contains html "value=\"{{car.email}}\""))
    (is (.contains html "required=\"required\" value=\"{{car.email}}\""))))