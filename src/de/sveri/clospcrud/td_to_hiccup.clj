(ns de.sveri.clospcrud.td-to-hiccup
  (:require [de.sveri.clospcrud.spec.clospcrud :as schem]
            [de.sveri.clospcrud.spec.html :as hschem]
            [clojure.spec :as s]))


(s/fdef wrap-with-vec-and-label' :args (s/cat :col (s/spec ::schem/column) :hicc-col (s/spec ::hschem/hicc-col))
        :ret (s/spec ::hschem/html-form))
(defmulti wrap-with-vec-and-label' (fn [_ hicc-col] (:type (second hicc-col))))

(defmethod wrap-with-vec-and-label' "checkbox"
  [col hicc-col]
  [[:label hicc-col (:name col)]])

(defmethod wrap-with-vec-and-label' :default [col hicc-col]
  [(let [n (:name col)] [:label {:for n} n]) hicc-col])

(s/fdef add-required-attr :args (s/cat :m ::hschem/form-map :col ::schem/column) :ret ::hschem/form-map)
(defn add-required-attr [m col]
  (if (= false (:null col))
    (merge m {:required "required"})
    m))

(defn add-value [m ent-name col]
  (merge m {:value (str "{{" ent-name "." (:name col) "}}")}))

(defn- wrap-with-vec-and-label [form-key col ent-name]
  (wrap-with-vec-and-label' col [form-key (-> {:id   (:name col)
                                               :name (:name col)}
                                              (add-required-attr col)
                                              (add-value ent-name col))]))

(s/fdef dt->hiccup :args (s/cat :col ::schem/column :ent-name string? :rest (s/? ::s/any))
        :ret ::hschem/html-form)
(defmulti dt->hiccup
          (fn [col _ t]
            [(:type col) t]))

(defmethod dt->hiccup [:int :create]
              [col ent-name _]
              (let [field-name (:name col)]
                (wrap-with-vec-and-label' col
                                          [:input.form-control
                                           (-> {:id   field-name
                                                :name field-name}
                                               (add-required-attr col)
                                               (add-value ent-name col))])))

(defmethod dt->hiccup [:varchar :create] [col ent-name _]
  (wrap-with-vec-and-label'
    col
    [:input.form-control (-> {:id        (:name col)
                              :name      (:name col)
                              :maxlength (:max-length col)}
                             (add-required-attr col)
                             (add-value ent-name col))]))

(defmethod dt->hiccup [:char :create] [col ent-name _]
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