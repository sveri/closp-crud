(ns leiningen.db-code-generator
  (:require [selmer.parser :as selm]
            [clojure.core.typed :as t :refer [ann]]
            [leiningen.pre-types :as pt]
            [clojure.string :as s]
            [de.sveri.clojure.commons.files.faf :as faf]
            [leiningen.helper :as h])
  (:import (clojure.lang Seqable)))


(ann dataset->template-map [String pt/entity-description ->
                            (t/HMap :mandatory {:entityname String
                                                :ns String
                                                :cols (t/Option (Seqable (t/HMap :mandatory {:colname String})))})])
(defn dataset->template-map
  "Will remove every column with the name :id"
  [ns ds]
  (let [cols (h/ds-columns->template-columns (:columns ds))]
    {:entityname       (:name ds)
     :ns               (str ns "." (:name ds))
     :cols             cols}))

(ann ^:no-check render-db-file [String pt/entity-description -> String])
(defn render-db-file [ns dataset]
  (let [templ-map (dataset->template-map ns dataset)]
    (selm/render-file "templates/db.tmpl" templ-map {:tag-open \[ :tag-close \]})))

(ann store-dataset [String pt/entity-description String -> nil])
(defn store-dataset [ns dataset src-path]
  (let [file-content (render-db-file ns dataset)
        filename (str (:name dataset) ".clj")]
    (h/store-content-in-ns ns filename src-path file-content)
    (println "Generated database namespace.")))
