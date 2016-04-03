(ns de.sveri.clospcrud.td-to-hiccup
  (:require [de.sveri.clospcrud.schema :as schem]
            [schema.core :as s]))



(defmulti wrap-with-vec-and-label' (fn [_ hicc-col] (:type (second hicc-col))))

(s/defmethod wrap-with-vec-and-label' "checkbox" :- [schem/html-form-group]
             [col :- schem/et-column hicc-col :- schem/html-form]
             [[:label hicc-col (name (first col))]])

(defmethod wrap-with-vec-and-label' :default [col hicc-col]
  [(let [n (name (first col))] [:label {:for n} n]) hicc-col])

(s/defn add-required-attr :- schem/form-map
  [m :- schem/form-map col :- schem/et-column]
  (if (and (< 2 (count col)) (= (nth col 2) :null))
    (merge m {:required "required"})
    m))

(defn add-value [m ent-name col]
  (merge m {:value (str "{{" ent-name "." (name (first col)) "}}")}))

(defn- wrap-with-vec-and-label [form-key col ent-name]
  (wrap-with-vec-and-label' col [form-key (-> {:id   (name (first col))
                                               :name (name (first col))}
                                              (add-required-attr col)
                                              (add-value ent-name col))]))

(defmulti dt->hiccup
          (fn [col _  t]
            (let [[_ s] col]
              [(if (vector? s) (first s) s) t])))

(s/defmethod dt->hiccup [:int :create] :- schem/html-form-group
             [col :- schem/et-column ent-name :- s/Str _]
  (let [field-name (name (first col))]
    (wrap-with-vec-and-label' col
                              [:input.form-control
                               (-> {:id   field-name
                                    :name field-name}
                                   (add-required-attr col)
                                   (add-value ent-name col))])))

(defmethod dt->hiccup [:varchar :create] [col ent-name _]
  {:pre [(second (second col))]}
  (wrap-with-vec-and-label'
    col
    [:input.form-control (-> {:id        (name (first col))
                              :name      (name (first col))
                              :maxlength (nth (nth col 1) 1)}
                             (add-required-attr col)
                             (add-value ent-name col))]))

(defmethod dt->hiccup [:char :create] [col ent-name _]
  (dt->hiccup (assoc col 1 (assoc (second col) 0 :varchar)) ent-name :create))

(defmethod dt->hiccup [:boolean :create] [col _ _]
  (wrap-with-vec-and-label'
    col
    (let [col-m (apply assoc (sorted-map) col)]
      [:input.form-control (merge (when (= true (:default col-m)) {:checked "checked"})
                                  {:id   (name (first col))
                                   :name (name (first col))
                                   :type "checkbox"})])))

(defmethod dt->hiccup [:int :index] [col ent-name _]
  (wrap-with-vec-and-label :input.form-control col ent-name))

(defmethod dt->hiccup [:text :create] [col ent-name _]
  (let [field-name (name (first col))]
    (wrap-with-vec-and-label' col [:textarea.form-control (-> {:id   field-name
                                                               :name field-name}
                                                              (add-required-attr col)
                                                              (add-value ent-name col))
                                   (str "{{" ent-name "." field-name "}}")])))

(defmethod dt->hiccup :default [col ent-name _]
  (wrap-with-vec-and-label :input.form-control col ent-name))