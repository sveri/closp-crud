(ns leiningen.code-generator
  (:require [stencil.core :as stenc]
            [clojure.core.typed :as t :refer [ann]]
            [leiningen.pre-types :as pt]
            [clojure.string :as s]
            [de.sveri.clojure.commons.files.faf :as faf])
  (:import (clojure.lang Keyword Seqable)))


(ann filter-id-columns [(t/NonEmptySeq (t/HSequential [Keyword t/Any *]))
                        -> (t/Option (Seqable (t/HSequential [Keyword t/Any *])))])
(defn filter-id-columns [cols]
  (remove (t/fn [col :- (t/HSequential [Keyword t/Any *])] (= :id (first col))) cols))

(ann ds-columns->template-columns [(t/NonEmptySeq (t/HSequential [Keyword t/Any *]))
                                   -> (t/Option (Seqable (t/HMap :mandatory {:colname String})))])
(defn ds-columns->template-columns [cols]
  (let [filt-cols (filter-id-columns cols)]
    (mapv (t/fn [col :- (t/HSequential [Keyword t/Any *])] {:colname (name (first col))}) filt-cols)))


(ann dataset->template-map [String pt/entity-description -> t/Any])
(defn dataset->template-map
  "Will remove every column with the name :id"
  [ns ds]
  (let [cols (ds-columns->template-columns (:columns ds))]
    {:entityname       (:name ds)
     :entityname-upper (.toUpperCase ^String (:name ds))
     :ns               (str ns "." (:name ds))
     :cols             cols}))
;
;(ann ^:no-check render-db-file [String pt/entity-description -> String])
;(defn render-db-file [ns dataset]
;  (let [templ-map (dataset->template-map ns dataset)]
;    (stenc/render-file "templates/db.mustache" templ-map)))

;(defn store-db-file [ns string filename proj-fp]
;  (let [ns-path (str proj-fp "/" (s/replace ns #"\." "/"))
;        ns-file-path (str ns-path "/" filename)]
;    (faf/create-if-not-exists ns-path)
;    (spit ns-file-path string)))
;
;(defn store-dataset [ns dataset proj-fp]
;  (let [file-content (render-db-file ns dataset)
;        filename (str (:name dataset) ".clj")]
;    (store-db-file ns file-content filename proj-fp)))
