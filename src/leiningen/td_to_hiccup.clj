(ns leiningen.td-to-hiccup
  (require [clojure.core.typed :as t]
           [leiningen.pre-types :as pt])
  (:import (clojure.lang Keyword)))


(t/ann merge-required [(t/HMap :complete? false) pt/et-column -> (t/HMap :complete? false)])
(defn merge-required [m col]
  (if (and (nth col 2) (= (nth col 2) :null))
    (merge m {:required "required"})
    m))

(t/ann dt->hiccup [pt/et-column -> (t/HVec [Keyword (t/HMap :mandatory {:id String})])])
(defmulti dt->hiccup (fn [col] (if (vector? (second col)) (first (second col))
                                                           (second col))))

(defmethod dt->hiccup :int [col]
  [:input.form-control (merge-required {:id (name (first col))} col)])

(defmethod dt->hiccup :varchar [col]
  [:input.form-control (merge-required {:id (name (first col)) :maxlength (second (second col))} col)])

(defmethod dt->hiccup :boolean [col]
  [:input.form-control (merge (when (contains? col :default )) (merge-required {:id (name (first col))} col))])
