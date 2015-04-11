(ns leiningen.code-generator
  (:require [stencil.core :as stenc]
            [clojure.core.typed :as t :refer [ann]]
            [leiningen.pre-types :as pt]
            [clojure.string :as s]
            [de.sveri.clojure.commons.files.faf :as faf])
  (:import (clojure.lang Keyword Seqable)))

(ann filter-id-columns [(t/HVec [(t/HSeq [t/Kw t/Any])]) ->
                        (t/Option (clojure.lang.Seqable t/Any))
                        ])


(defn filter-id-columns [cols]
  (remove (t/fn [v :- (Seqable t/Any)] (= :id (first v))) cols))

(ann ds-columns->template-columns [(t/HVec [(t/HSeq [t/Kw t/Any])]) ->
                                   (t/HVec [(t/HMap :mandatory {:colname String})])
                        ;(t/Option (clojure.lang.Seqable t/Any))
                        ])
;[{:colname "foo" } {:colname "bar"}]
(defn ds-columns->template-columns [cols]
  (mapv (t/fn [v :- (t/HSeq [t/Kw t/Any])] {:colname (name (first v))}) (filter-id-columns cols)))

;(All [c a b ...]
     ;(t/IFn [[a b ... b -> c] (t/NonEmptySeqable a) (t/NonEmptySeqable b) ... b -> (t/NonEmptyAVec c)]
     ;       [[a b ... b -> c] (t/U (Seqable a) nil) (t/U (Seqable b) nil) ... b -> (t/AVec c)]))

;(ann ^:no-check dataset->template-map [String pt/entity-description ->
;                            (t/HMap :mandatory {:entityname String
;                                                :ns         String
;                                                :cols       (t/HSeq [t/Any])})])
(ann dataset->template-map [String pt/entity-description -> t/Any])
(defn dataset->template-map
  "Will remove every column with the name :id"
  [ns ds]
  (let [cols (ds-columns->template-columns (:columns ds))]
    {:entityname       (:name ds)
     :entityname-upper (.toUpperCase (:name ds))
     :ns               (str ns "." (:name ds))
     :cols             cols}))

(ann render-db-file [String pt/entity-description -> String])
(defn render-db-file [ns dataset]
  (let [templ-map (dataset->template-map ns dataset)]
    (stenc/render-file "templates/db.mustache" templ-map)))

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
