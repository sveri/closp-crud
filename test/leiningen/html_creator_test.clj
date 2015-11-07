(ns leiningen.html-creator-test
  (:require [clojure.test :refer :all]
            [leiningen.html-creator :as ht]))

(def table1-definition {:name    "table1"
                        :columns [[:id :int :null false :pk true :autoinc true]
                                  [:fooname [:varchar 40] :null false]
                                  [:male :boolean :null false]
                                  [:description :text]]})

(def underscore-def {:name "table2"
                     :columns [[:some_thing :boolean :null false]]})

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
    (println html)
    (is (= true (.contains html (str "{%if table2.some_thing = 1 %}checked{% endif %}"))))))