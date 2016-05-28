(ns de.sveri.clospcrud.db-code-generator
  (:require [selmer.parser :as selm]
            [de.sveri.clospcrud.helper :as h]
            [de.sveri.clospcrud.schema :as schem]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.spec :as s])
  (:import (java.io File)))

;(s/def ::entityname string?)
;(s/def ::entity-ns string?)
;(s/def ::ns string?)
;(s/def ::colname string?)
;(s/def ::colname-fn string?)
;(s/def ::ds-column (s/keys :req-un [::colname ::colname-fn]))
;(s/def ::cols (s/cat :cols (s/+ ::ds-column)))
;(s/def ::template-map (s/keys :req-un [::entityname ::entity-ns ::ns ::cols]))

(s/fdef dataset->template-map :args (s/cat :ns string? :entity-ns string? :ds ::schem/entity-description)
        :ret ::schem/template-map)
(defn dataset->template-map [ns entity-ns ds]
  (let [cols (h/ds-columns->template-columns (:columns ds))]
    {:entityname (:name ds)
     :ns         (str ns "." (:name ds))
     :entity-ns  entity-ns
     :cols       cols}))

(s/fdef render-db-file :args (s/cat :ns string? :entity-ns string? :dataset ::schem/entity-description)
        :ret string?)
(defn render-db-file [ns entity-ns dataset]
  (let [templ-map (dataset->template-map ns entity-ns dataset)]
    (selm/render-file "templates/db.tmpl" templ-map {:tag-open \[ :tag-close \]})))

(s/fdef store-dataset
        :args (s/cat :ns string? :entity-ns string? :dataset ::schem/entity-description :src-path string?)
        :ret nil?)
(defn store-dataset [ns entity-ns dataset src-path]
  (let [file-content (render-db-file ns entity-ns dataset)
        filename (str (h/sanitize-filename (:name dataset)) ".clj")]
    (h/store-content-in-ns ns filename src-path file-content)
    (println "Generated database namespace.")))



(s/fdef add-db-to-entities :args (s/cat :file #(instance? File %) :entity-name string?) :ret string?)
(defn add-db-to-entities [file entity-name]
  (let [file-content (slurp file)
        add-fn #(if (not (.contains %1 %2))
                 (str %1 "\n" %2 "\n")
                 %1)]
    (-> file-content
        (add-fn (str "(declare " entity-name ")"))
        (add-fn (str "(defentity " entity-name ")")))))

(s/fdef write-db-entities :args (s/cat :ns-entities string? :dataset ::schem/entity-description :src-path string?)
        :ret nil?)
(defn write-db-entities [ns-entities dataset src-path]
  (let [fp-path (str src-path "/" (str/replace ns-entities #"\." "/") ".clj")
        file (io/file fp-path)
        new-content (add-db-to-entities file (:name dataset))]
    (spit file new-content)
    (println "Added entity to entities file.")))

