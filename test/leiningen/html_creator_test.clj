(ns leiningen.html-creator-test
  (:require [clojure.test :refer :all]
            [de.sveri.clospcrud.html-creator :as ht]
            [leiningen.common :refer [person-definition]]
            [schema.test :as st]))

(use-fixtures :once st/validate-schemas)

(def underscore-def {:name    "table2"
                     :columns [{:name "some_thing" :type :boolean :null false}]})

(def car-definition {:name    "car"
                     :columns [{:name "id" :type :int :null false :pk true :autoinc true}
                               {:name "first_name" :type :varchar :max-length 30}
                               {:name "email" :type :varchar :max-length 30 :null false :unique true}]})

(deftest create-html
  (let [html (ht/create-html person-definition)]
    (is (= true (.contains html "person/{{crea")))
    (is (= true (.contains html "value=\"{{person.fooname")))))

(deftest text-html
  (let [html (ht/create-html person-definition)]
    (is (= true (.contains html "{{person.description}}")))))

(deftest bool-html
  (let [html (ht/create-html person-definition)]
    (is (= true (.contains html (str "{%if person.male = 1 %}checked{% endif %}"))))))

(deftest underscore-in-col-name
  (let [html (ht/create-html underscore-def)]
    (is (= true (.contains html (str "{%if table2.some_thing = 1 %}checked{% endif %}"))))))

(deftest car-def-test
  (let [html (ht/create-html car-definition)]
    (is (.contains html "value=\"{{car.first_name}}\""))
    (is (.contains html "value=\"{{car.email}}\""))
    (is (.contains html "required=\"required\" value=\"{{car.email}}\""))))

(deftest create-tds-for-index
  (let [html (ht/create-tds-for-index person-definition)]
    (is (.contains html "<td>{{person.age}}</td><td>{{person.male}}</td><td>{{person.description}}</td>"))
    (is (.contains html "<a href=\"/person/{{person.id}}\">{{person.fooname}}</a></td>"))))