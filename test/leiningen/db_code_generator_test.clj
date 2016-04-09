(ns leiningen.db-code-generator-test
  (:require [clojure.test :refer :all]
            [de.sveri.clospcrud.db-code-generator :refer :all]
            [leiningen.common :refer [table1-definition]]
            [clojure.java.io :as io]
            [schema.test :as st]))

(use-fixtures :once st/validate-schemas)

(def db-ns "foo.bar")
(def entity-ns "foo.bar.db.entities")

(deftest generate-db-template
  (is (.contains (render-db-file db-ns entity-ns table1-definition) "get-all-persons"))
  (is (.startsWith (render-db-file db-ns entity-ns table1-definition) (str "(ns " db-ns)))
  (is (.startsWith (render-db-file db-ns entity-ns table1-definition) (str "(ns " db-ns))))

(deftest write-entity
  (let [txt (add-db-to-entities (io/file (io/resource "db/code/generator/entities.clj.txt")) "ent-name")]
    (is (.contains txt "(declare ent-name)"))
    (is (.contains txt "(defentity ent-name"))))

(deftest write-entity-dont-overwrite-existing-one
  (let [txt (add-db-to-entities (io/file (io/resource "db/code/generator/entities.clj.txt")) "user")
        freqs (frequencies (clojure.string/split txt #"\("))]
    (is (= 1 (get freqs "declare user)\n\n")))
    (is (= 1 (get freqs "defentity user)\n")))))

(deftest require-entity-from-entities
  (let [db-content (render-db-file db-ns entity-ns table1-definition)]
    (is (.contains db-content "[foo.bar.db.entities :refer [person]]"))))
