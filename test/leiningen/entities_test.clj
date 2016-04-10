(ns leiningen.entities-test
  (:require [clojure.test :refer :all]
            [de.sveri.clospcrud.entities :as ent]
            [leiningen.common :refer [person-definition]]
            [schema.test :as st]))

(use-fixtures :once st/validate-schemas)

(def h2-uri "jdbc:h2:mem:test_mem")

(deftest generate-sql-statement
  (let [sql (ent/generate-sql-statements person-definition h2-uri)]
    (is (.contains sql "CREATE"))
    (is (.contains sql "id INT AUTO_INCREMENT NOT NULL"))
    (is (.contains sql "fooname VARCHAR(40) NOT NULL"))
    (is (.contains sql "age INT NOT NULL"))))