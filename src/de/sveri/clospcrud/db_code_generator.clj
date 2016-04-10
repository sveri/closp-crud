(ns de.sveri.clospcrud.db-code-generator
  (:require [selmer.parser :as selm]
            [de.sveri.clospcrud.helper :as h]
            [schema.core :as s]
            [de.sveri.clospcrud.schema :as schem]
            [clojure.string :as str]
            [clojure.java.io :as io])
  (:import (java.io File)))

(s/defn dataset->template-map :- {:entityname s/Str :ns s/Str :entity-ns s/Str
                                  :cols       [{:colname    s/Str
                                                :colname-fn s/Str}]}
  "Will remove every column with the name :id"
  [ns :- s/Str entity-ns :- s/Str ds :- schem/entity-description]
  (let [cols (h/ds-columns->template-columns (:columns ds))]
    {:entityname (:name ds)
     :ns         (str ns "." (:name ds))
     :entity-ns  entity-ns
     :cols       cols}))

(s/defn render-db-file :- s/Str [ns :- s/Str entity-ns :- s/Str dataset :- schem/entity-description]
  (let [templ-map (dataset->template-map ns entity-ns dataset)]
    (selm/render-file "templates/db.tmpl" templ-map {:tag-open \[ :tag-close \]})))

(s/defn store-dataset :- nil [ns :- s/Str entity-ns :- s/Str dataset :- schem/entity-description src-path :- s/Str]
  (let [file-content (render-db-file ns entity-ns dataset)
        filename (str (h/sanitize-filename (:name dataset)) ".clj")]
    (h/store-content-in-ns ns filename src-path file-content)
    (println "Generated database namespace.")))

(s/defn add-db-to-entities :- s/Str [file :- File entity-name :- s/Str]
  (let [file-content (slurp file)
        add-fn #(if (not (.contains %1 %2))
                 (str %1 "\n" %2 "\n")
                 %1)]
    (-> file-content
        (add-fn (str "(declare " entity-name ")"))
        (add-fn (str "(defentity " entity-name ")")))))


(s/defn write-db-entities :- nil [ns-entities :- s/Str dataset :- schem/entity-description src-path :- s/Str]
  (let [fp-path (str src-path "/" (str/replace ns-entities #"\." "/") ".clj")
        file (io/file fp-path)
        new-content (add-db-to-entities file (:name dataset))]
    (spit file new-content)
    (println "Added entity to entities file.")))

