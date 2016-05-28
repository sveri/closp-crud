(ns leiningen.db-code-generator-test
  (:require [clojure.test :refer :all]
            [de.sveri.clospcrud.db-code-generator :refer :all]
            [leiningen.common :refer [person-definition]]
            [clojure.java.io :as io]
            [clojure.spec :as s]))

(def db-ns "foo.bar")
(def entity-ns "foo.bar.db.entities")

(deftest generate-db-template
  (is (.contains (render-db-file db-ns entity-ns person-definition) "get-all-persons"))
  (is (.startsWith (render-db-file db-ns entity-ns person-definition) (str "(ns " db-ns)))
  (is (.startsWith (render-db-file db-ns entity-ns person-definition) (str "(ns " db-ns))))

(deftest write-entity
  (let [txt (add-db-to-entities (io/file (io/resource "db/code/generator/entities.clj.txt")) "ent-name")]
    (is (.contains txt "(declare ent-name)"))
    (is (.contains txt "(defentity ent-name"))))

(deftest write-entity-dont-overwrite-existing-one
  (let [txt (add-db-to-entities (io/file (io/resource "db/code/generator/entities.clj.txt")) "user")]
    (is (= 1 (count (re-seq #"declare user" txt))))
    (is (= 1 (count (re-seq #"defentity user" txt))))))

(deftest require-entity-from-entities
  (let [db-content (render-db-file db-ns entity-ns person-definition)]
    (is (.contains db-content "[foo.bar.db.entities :refer [person]]"))))

(s/instrument-all)
