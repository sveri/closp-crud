(ns leiningen.code-generator
  (:require [stencil.core :as stenc]
            [clojure.core.typed :as t :refer [ann]]
            [leiningen.pre-types :as pt]))

(ann dataset->template-map [String pt/entity-description -> (t/HMap :mandatory {:entityname String
                                                                         :ns String
                                                                         :cols (t/HSeq)})])
(defn dataset->template-map
  "Will remove every column with the name :id"
  [ns ds]
  (let [cols (mapv (fn [v] {:colname (first v)}) (remove #(= :id (first %)) (:columns ds)))]
    {:entityname (:name ds)
     :ns ns
     :cols cols}))

(ann ^:no-check render-db-file [String pt/entity-description -> String])
(defn render-db-file [ns dataset]
  (let [templ-map (dataset->template-map ns dataset)]
    (stenc/render-file "templates/db.mustache" templ-map)))
