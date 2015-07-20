(ns leiningen.html-creator-test
  (:require [clojure.test :refer :all]
            [leiningen.html-creator :as ht]))

(def table1-definition {:name    "table1"
                        :columns [[:id :int :null false :pk true :autoinc true]
                                  [:fooname [:varchar 40] :null false]
                                  [:male :boolean :null false]
                                  [:description :text]]})

(deftest create-html
  (let [html (ht/create-html table1-definition)]
    (is (= true (.contains html "table1/{{crea")))
    (is (= true (.contains html "value=\"{{table1.FOONAME")))))

(deftest text-html
  (let [html (ht/create-html table1-definition)]
    (println "desc: " html)
    (is (= true (.contains html "description")))))