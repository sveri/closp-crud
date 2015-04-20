(ns leiningen.td-to-hiccup
  (:require [clojure.core.typed :refer [defalias] :as t]
            [leiningen.pre-types :as pt])
  (:import (clojure.lang Keyword)))



(t/ann wrap-with-vec-and-label [pt/et-column pt/html-form -> pt/html-form-group])
(defn wrap-with-vec-and-label [col hicc-col]
  [(let [n (name (first col))] [:label {:for n} n]) hicc-col])

(t/ann merge-required [pt/form-map pt/et-column -> pt/form-map])
(defn merge-required [m col]
  (if (and (nth col 2) (= (nth col 2) :null))
    (merge m {:required "required"})
    m))

(t/ann ^:no-check dt->hiccup [(t/HVec [Keyword (t/U Keyword (t/HVec [Keyword Number])) t/Any t/Any *])
                              -> pt/html-form-group])
(defmulti dt->hiccup (t/fn [col :- (t/HVec [Keyword (t/U Keyword (t/HVec [Keyword Number])) t/Any t/Any *])]
                       (let [[_ s] col]
                         (if (vector? s) (first s) s))))

(defmethod dt->hiccup :int [col]
  (wrap-with-vec-and-label col [:input.form-control (merge-required {:id (name (first col))
                                                                     :name (name (first col))} col)]))

(defmethod dt->hiccup :varchar [col]
  {:pre [(second (second col))]}
  (wrap-with-vec-and-label
    col
    [:input.form-control (merge-required {:id (name (first col))
                                          :name (name (first col))
                                          :maxlength (nth
                                                                              (nth col 1)
                                                                              1)} col)]))

(defmethod dt->hiccup :char [col]
  (dt->hiccup (assoc col 1 (assoc (second col) 0 :varchar))))

(defmethod dt->hiccup :boolean [col]
  (wrap-with-vec-and-label
    col
    (let [col-m (apply assoc (sorted-map) col)]
      [:input.form-control (merge (when (= true (:default col-m)) {:checked "checked"})
                                  (merge-required {:id (name (first col))
                                                   :name (name (first col))} col))])))

(defmethod dt->hiccup :default [col]
  (wrap-with-vec-and-label col [:input.form-control (merge-required {:id (name (first col))
                                                                     :name (name (first col))} col)]))
