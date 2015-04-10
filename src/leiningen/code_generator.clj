(ns leiningen.code-generator
  (:require [stencil.core :as stenc]
            [clojure.core.typed :as t :refer [ann]]
            [leiningen.pre-types :as pt]
            [clojure.string :as s]
            [de.sveri.clojure.commons.files.faf :as faf])
  (:import (clojure.lang Keyword)))

    ;[[:id :int :null false :pk true :autoinc true]
    ;[:fooname [:varchar 40] :null false]
    ;[:age :int :null false]]

;(t/All [x] (t/IFn [(t/HSequential [x t/Any *]) -> x :object {:path [(Nth 0)], :id 0}]
;                  [(t/Option (t/EmptySeqable x)) -> nil]
;                  [(t/NonEmptySeqable x) -> x]
;                  [(t/Option (clojure.lang.Seqable x)) -> (t/Option x)]))

;(ann filter-id-columns [(t/HSequential [(t/NonEmptySeqable t/Any)])
;                        -> t/Any])
                        ;-> (t/HSeq [(t/HSeq [Keyword *]) *])])

;(ann filter-id-columns [(t/HSequential [(t/CountRange 1)]) -> t/Any])

;(ann filter-id-columns (t/All [x] [(t/Option (t/HSeq x)) -> t/Any]))
(defn filter-id-columns [l]
  ;(first l))
  (remove #(= :id (first %)) l))

;(ann ds-columns->template-columns [(t/HSeq [(t/HSeq [Keyword *]) *]) -> (t/HSeq [Keyword *])])
(defn ds-columns->template-columns [l]
  (mapv (fn [v] {:colname (name (first v))}) (filter-id-columns l)))

;(ann ^:no-check dataset->template-map [String pt/entity-description ->
;                            (t/HMap :mandatory {:entityname String
;                                                :ns         String
;                                                :cols       (t/HSeq [t/Any])})])
;(ann ^:no-check dataset->template-map [String pt/entity-description -> t/Any])
(defn dataset->template-map
  "Will remove every column with the name :id"
  [ns ds]
  (let [cols (ds-columns->template-columns (:columns ds))]
    {:entityname (:name ds)
     :ns         (str ns "." (:name ds))
     :cols       cols}))

;(ann ^:no-check render-db-file [String pt/entity-description -> String])
(defn render-db-file [ns dataset]
  (let [templ-map (dataset->template-map ns dataset)]
    (stenc/render-file "templates/db.mustache" templ-map)))

(defn store-db-file [ns string filename proj-fp]
  (let [ns-path (str proj-fp "/" (s/replace ns #"\." "/"))
        ns-file-path (str ns-path "/" filename)]
    (faf/create-if-not-exists ns-path)
    (spit ns-file-path string)))

(defn store-dataset [ns dataset proj-fp]
  (let [file-content (render-db-file ns dataset)
        filename (str (:name dataset) ".clj")]
    (store-db-file ns file-content filename proj-fp)))
