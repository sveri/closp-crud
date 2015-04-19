(ns leiningen.code-generator
  (:require [selmer.parser :as selm]
            [clojure.core.typed :as t :refer [ann]]
            [leiningen.pre-types :as pt]
            [clojure.string :as s]
            [de.sveri.clojure.commons.files.faf :as faf]
            [leiningen.helper :as h])
  (:import (clojure.lang Seqable)))

(ann ds-columns->template-columns [pt/et-columns -> (t/AVec (t/HMap :mandatory {:colname String}))])
(defn ds-columns->template-columns [cols]
    (mapv (t/fn [col :- pt/et-column] {:colname (name (first col))})
          (h/filter-id-columns cols)))


(ann dataset->template-map [String pt/entity-description ->
                            (t/HMap :mandatory {:entityname String
                                                :ns String
                                                :cols (t/Option (Seqable (t/HMap :mandatory {:colname String})))})])
(defn dataset->template-map
  "Will remove every column with the name :id"
  [ns ds]
  (let [cols (ds-columns->template-columns (:columns ds))]
    {:entityname       (:name ds)
     :ns               (str ns "." (:name ds))
     :cols             cols}))

(ann ^:no-check render-db-file [String pt/entity-description -> String])
(defn render-db-file [ns dataset]
  (let [templ-map (dataset->template-map ns dataset)]
    (selm/render-file "templates/db.tmpl" templ-map {:tag-open \[ :tag-close \]})))

;(ann ^:no-check store-db-file [String String String String -> nil])
;(defn store-db-file [ns file-content filename proj-fp]
;  (let [ns-path (str proj-fp "/" (s/replace ns #"\." "/"))
;        ns-file-path (str ns-path "/" filename)]
;    (faf/create-if-not-exists ns-path)
;    (spit ns-file-path file-content)))

(ann store-dataset [String pt/entity-description String -> nil])
(defn store-dataset [ns dataset proj-fp]
  (let [file-content (render-db-file ns dataset)
        filename (str (:name dataset) ".clj")]
    (h/store-content-in-ns ns filename proj-fp file-content)))
