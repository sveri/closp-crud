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

(t/ann ^:no-check dt->hiccup [pt/et-column Keyword -> pt/html-form-group])
(defmulti dt->hiccup
          ;(fn [col t]
          (t/fn [col :- pt/et-column t :- Keyword]
            (let [[_ s] col]
              [(if (vector? s) (first s) s) t])))

(defmethod dt->hiccup [:int :create] [col _]
  (wrap-with-vec-and-label col [:input.form-control (merge-required {:id   (name (first col))
                                                                     :name (name (first col))} col)]))

(defmethod dt->hiccup [:varchar :create] [col _]
  {:pre [(second (second col))]}
  (wrap-with-vec-and-label
    col
    [:input.form-control (merge-required {:id        (name (first col))
                                          :name      (name (first col))
                                          :maxlength (nth (nth col 1) 1)}
                                         col)]))

(defmethod dt->hiccup [:char :create] [col _]
  (dt->hiccup (assoc col 1 (assoc (second col) 0 :varchar)) :create))

(defmethod dt->hiccup [:boolean :create] [col _]
  (wrap-with-vec-and-label
    col
    (let [col-m (apply assoc (sorted-map) col)]
      [:input.form-control (merge (when (= true (:default col-m)) {:checked "checked"})
                                  (merge-required {:id   (name (first col))
                                                   :name (name (first col))} col))])))

(defmethod dt->hiccup [:default :create] [col _]
  (wrap-with-vec-and-label col [:input.form-control (merge-required {:id   (name (first col))
                                                                     :name (name (first col))} col)]))