(ns leiningen.code-generator-test
  (:require [clojure.test :refer :all]
            [de.sveri.clospcrud.db-code-generator :refer :all]))

(def db-ns "foo.bar")

(def table1-definition {:name    "person"
                        :columns [[:id :int :null false :pk true :autoinc true]
                                  [:fooname [:varchar 40] :null false]
                                  [:age :int :null false]]})

(deftest generate-db-template
  (is (.contains (render-db-file db-ns table1-definition) "get-all-persons"))
  (is (.startsWith (render-db-file db-ns table1-definition) (str "(ns " db-ns)))
  (is (.startsWith (render-db-file db-ns table1-definition) (str "(ns " db-ns))))
