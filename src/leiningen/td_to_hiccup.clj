(ns leiningen.td-to-hiccup
  (:require [clojure.core.typed :refer [defalias] :as t]
            [leiningen.pre-types :as pt])
  (:import (clojure.lang Keyword)))



(t/ann wrap-with-vec-and-label [pt/et-column pt/html-form -> pt/html-form-group])
(defmulti wrap-with-vec-and-label (fn [_ hicc-col] (:type (second hicc-col))))

(defmethod wrap-with-vec-and-label "checkbox" [col hicc-col]
  [(let [n (name (first col))] [:label hicc-col n])])

(defmethod wrap-with-vec-and-label :default [col hicc-col]
  [(let [n (name (first col))] [:label {:for n} n]) hicc-col])

(t/ann merge-required [pt/form-map String pt/et-column -> pt/form-map])
(defn merge-required [m ent-name col]
  (if (and (< 2 (count col)) (= (nth col 2) :null))
    (merge m {:required "required" :value (str "{{" ent-name "." (.toUpperCase (name (first col))) "}}")})
    m))

(t/ann ^:no-check dt->hiccup [pt/et-column Keyword -> pt/html-form-group])
(defmulti dt->hiccup
          (t/fn [col :- pt/et-column _ :- String t :- Keyword]
            (let [[_ s] col]
              [(if (vector? s) (first s) s) t])))

(defmethod dt->hiccup [:int :create] [col ent-name _]
  (wrap-with-vec-and-label col [:input.form-control (merge-required {:id   (name (first col))
                                                                     :name (name (first col))}
                                                                    ent-name col)]))

(defmethod dt->hiccup [:varchar :create] [col ent-name _]
  {:pre [(second (second col))]}
  (wrap-with-vec-and-label
    col
    [:input.form-control (merge-required {:id        (name (first col))
                                          :name      (name (first col))
                                          :maxlength (nth (nth col 1) 1)}
                                         ent-name col)]))

(defmethod dt->hiccup [:char :create] [col ent-name _]
  (dt->hiccup (assoc col 1 (assoc (second col) 0 :varchar)) ent-name :create))

(defmethod dt->hiccup [:boolean :create] [col ent-name _]
  (wrap-with-vec-and-label
    col
    (let [col-m (apply assoc (sorted-map) col)]
      [:input.form-control (merge (when (= true (:default col-m)) {:checked "checked"})
                                  {:id   (name (first col))
                                   :name (name (first col))
                                   :type "checkbox"})])))

(defmethod dt->hiccup [:int :index] [col ent-name _]
  (wrap-with-vec-and-label col [:input.form-control (merge-required {:id   (name (first col))
                                                                     :name (name (first col))}
                                                                    ent-name col)]))

(defmethod dt->hiccup [:text :create] [col ent-name _]
  (wrap-with-vec-and-label col [:textarea.form-control (merge-required {:id   (name (first col))
                                                                        :name (name (first col))}
                                                                       ent-name col)]))

(defmethod dt->hiccup :default [col ent-name _]
  (wrap-with-vec-and-label col [:input.form-control (merge-required {:id   (name (first col))
                                                                     :name (name (first col))}
                                                                    ent-name col)]))