(ns de.sveri.clospcrud.td-to-hiccup
  (:require [de.sveri.clospcrud.schema :as schem]
            [schema.core :as sc]
            [de.sveri.clospcrud.html-schema :as hschem]
            [clojure.spec :as s]))


(s/fdef wrap-with-vec-and-label' :args (s/cat :col (s/spec ::schem/column) :hicc-col (s/spec ::hschem/html-form)))
(defmulti wrap-with-vec-and-label' (fn [_ hicc-col] (:type (second hicc-col))))

;(sc/defmethod wrap-with-vec-and-label' "checkbox" :- [schem/html-form-group]
;              [col :- schem/column hicc-col :- schem/html-form]
;              [[:label hicc-col (:name col)]])
(sc/defmethod wrap-with-vec-and-label' "checkbox"
              [col hicc-col]
              [[:label hicc-col (:name col)]])

(defmethod wrap-with-vec-and-label' :default [col hicc-col]
  [(let [n (:name col)] [:label {:for n} n]) hicc-col])

(sc/defn add-required-attr
  [m col]
  ;(if (and (< 2 (count col)) (= (nth col 2) :null))
  (if (= false (:null col))
    (merge m {:required "required"})
    m))
;(sc/defn add-required-attr :- schem/form-map
;  [m :- schem/form-map col :- schem/column]
;  ;(if (and (< 2 (count col)) (= (nth col 2) :null))
;  (if (= false (:null col))
;    (merge m {:required "required"})))
    ;m))

(defn add-value [m ent-name col]
  (merge m {:value (str "{{" ent-name "." (:name col) "}}")}))

(defn- wrap-with-vec-and-label [form-key col ent-name]
  (wrap-with-vec-and-label' col [form-key (-> {:id   (:name col)
                                               :name (:name col)}
                                              (add-required-attr col)
                                              (add-value ent-name col))]))

(defmulti dt->hiccup
          (fn [col _  t]
            #_(let [[_ s] col]
                [(if (vector? s) (first s) s) t])
            [(:type col) t]))

(sc/defmethod dt->hiccup [:int :create]
              [col ent-name :- sc/Str _]
              (let [field-name (:name col)]
                (wrap-with-vec-and-label' col
                                          [:input.form-control
                                           (-> {:id   field-name
                                                :name field-name}
                                               (add-required-attr col)
                                               (add-value ent-name col))])))
;(sc/defmethod dt->hiccup [:int :create] :- schem/html-form-group
;              [col :- schem/column ent-name :- sc/Str _]
;              (let [field-name (:name col)]
;                (wrap-with-vec-and-label' col
;                                          [:input.form-control
;                                           (-> {:id   field-name
;                                                :name field-name}
;                                               (add-required-attr col)
;                                               (add-value ent-name col))])))

(defmethod dt->hiccup [:varchar :create] [col ent-name _]
  (wrap-with-vec-and-label'
    col
    [:input.form-control (-> {:id        (:name col)
                              :name      (:name col)
                              :maxlength (:max-length col)}
                             (add-required-attr col)
                             (add-value ent-name col))]))

(defmethod dt->hiccup [:char :create] [col ent-name _]
  ;(dt->hiccup (assoc col 1 (assoc (second col) 0 :varchar)) ent-name :create)
  (dt->hiccup (assoc col :type :varchar) ent-name :create))

(defmethod dt->hiccup [:boolean :create] [col _ _]
  (wrap-with-vec-and-label'
    col
    [:input.form-control (merge (when (= true (:default col)) {:checked "checked"})
                                {:id   (:name col)
                                 :name (:name col)
                                 :type "checkbox"})]))

(defmethod dt->hiccup [:int :index] [col ent-name _]
  (wrap-with-vec-and-label :input.form-control col ent-name))

(defmethod dt->hiccup [:text :create] [col ent-name _]
  (let [field-name (:name col)]
    (wrap-with-vec-and-label' col [:textarea.form-control (-> {:id   field-name
                                                               :name field-name}
                                                              (add-required-attr col)
                                                              (add-value ent-name col))
                                   (str "{{" ent-name "." field-name "}}")])))

(defmethod dt->hiccup :default [col ent-name _]
  (wrap-with-vec-and-label :input.form-control col ent-name))