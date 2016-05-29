(ns leiningen.db-code-generator-test
  (:require [clojure.test :refer :all]
            [de.sveri.clospcrud.db-code-generator :refer :all]
            [de.sveri.clospcrud.spec.clospcrud :as schem]
            [leiningen.common :refer [person-definition]]
            [clojure.java.io :as io]
            [clojure.spec :as s]
            [clojure.spec.gen :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer [defspec]]))

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

;(defspec get-all-fdef 30
;         (prop/for-all [ent-desc (s/gen ::schem/entity-description)]
;                       (let [ent-name (:name ent-desc)]
;                         (println ent-desc)
;                         (println (render-db-file db-ns entity-ns ent-desc))
;                         (= ent-name ent-name))))
                         ;(.contains (render-db-file db-ns entity-ns ent-desc)))))
                                    ;(format "(s/fdef get-all-%ss :ret (s/cat :%ss ::%s-map))" ent-name ent-name ent-name)))))

(s/instrument-all)
