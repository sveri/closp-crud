(ns de.sveri.clospcrud.html-creator
  (:require [hiccup.core :as hicc]
            [clojure.core.typed :as t]
            [de.sveri.clospcrud.pre-types :as pt]
            [de.sveri.clospcrud.td-to-hiccup :as ds-conv]
            [de.sveri.clospcrud.helper :as h]
            [de.sveri.clojure.commons.files.faf :as faf]
            [selmer.parser :as selm]
            [clojure.string :as s])
  (:import (clojure.lang Seqable Keyword)
           (java.lang System)))

(def line-sep (System/lineSeparator))

(t/ann insert-line-endings [Seqable -> String])
(defn- hicc->html [se]
  (-> se
      (hicc/html)
      (s/replace #"/div>" (str "/div>" line-sep))))

(t/ann get-and-create-templ-fp-path! [String String pt/entity-description -> String])
(defn- get-and-create-templ-fp-path! [filename templ-path dataset]
  (let [folder-path (str templ-path "/" (:name dataset))
        fp (str folder-path "/" filename)]
    (faf/create-if-not-exists folder-path)
    fp))

(t/ann wrap-with-form-group [pt/html-form-group -> (t/HVec [Keyword pt/html-form-group])])
(defmulti wrap-with-form-group (fn [col-vec] (:type (second (second col-vec)))))

(defmethod wrap-with-form-group "checkbox" [col-vec]
  (vec (concat [:div] col-vec)))

(defmethod wrap-with-form-group :default [col-vec]
  (vec (concat [:div.form-group] col-vec)))

(t/ann wrap-create-with-form [String (Seqable pt/html-form-group) -> (t/HVec [t/Any])])
(defn wrap-create-with-form [action form-groups]
  (let [form [:form {:action (str "/" action "/create") :method "post"}]
        af-token [[:input {:name "__anti-forgery-token" :type "hidden" :value "{{af-token}}"}]]
        btns [[:button.btn.btn-primary {:type "submit"} (str "Add " action)]]]
    (vec (concat form af-token form-groups btns))))

(t/ann wrap-extend [String -> String])
(defn wrap-extend [form]
  (str "{% extends \"base.html\" %}" line-sep "{% block content %}" line-sep form line-sep "{% endblock %}"))

; create
(defn insert-extra-tags [form-groups-str entity-name]
  (if (.contains form-groups-str "checkbox")
    (let [field (second (re-find #"id=\"([a-zA-Z-_]*)\"" form-groups-str))]
     (s/replace form-groups-str #"type=\"checkbox\""
                (str "type=\"checkbox\" " "{%if " entity-name "." field " = 1 %}checked{% endif %}")))
    form-groups-str))

(def t "<input class=\"form-control\" id=\"male\" name=\"male\" type=\"checkbox\" />")

(t/ann create-html [pt/entity-description -> String])
(defn create-html [dataset]
  (let [cleaned-dataset (h/filter-dataset dataset)
        entity-name (:name dataset)
        form-groups (map
                      #(-> (ds-conv/dt->hiccup % entity-name :create)
                           (wrap-with-form-group)
                           hicc->html
                           (insert-extra-tags entity-name))
                      (:columns cleaned-dataset))]
    (selm/render-file "templates/create.html" {:entityname  entity-name
                                               :form-groups (s/join line-sep form-groups)}
                      {:tag-open \[ :tag-close \]})))

(t/ann store-create-template [pt/entity-description String -> nil])
(defn store-create-template [dataset templ-path]
  (-> (get-and-create-templ-fp-path! "/create.html" templ-path dataset)
      (spit (create-html dataset))))

; delete
(t/ann delete-html [pt/entity-description -> String])
(defn delete-html [dataset]
  (selm/render-file "templates/delete.html" {:entityname (:name dataset)} {:tag-open \[ :tag-close \]}))

(t/ann store-delete-template [pt/entity-description String -> nil])
(defn store-delete-template [dataset templ-path]
  (-> (get-and-create-templ-fp-path! "/delete.html" templ-path dataset)
      (spit (delete-html dataset))))

; index
(defn create-tds-for-index [dataset]
  (let [e-name (:name dataset)
        conv-col-name #(name (first %))
        san-cols (h/filter-id-columns (:columns dataset))
        first-col (first san-cols)
        hicc-first-col [:td [:a {:href (str "/" e-name "/{{" e-name ".id}}")}
                             (str "{{" e-name "." (conv-col-name first-col) "}}")]]
        rest-cols (rest san-cols)
        hicc-rest-cols (map (fn [col] [:td (str "{{" e-name "." (conv-col-name col) "}}")]) rest-cols)
        hicc-delete-col [:td [:a.btn.btn-primary {:href (str "/" e-name "/delete/{{" e-name ".id}}")} "Delete"]]]
    (str (hicc->html hicc-first-col) (str line-sep "\t\t\t\t") (hicc->html hicc-rest-cols) (str line-sep "\t\t\t\t")
         (hicc->html hicc-delete-col))))

(t/ann index-html [pt/entity-description -> String])
(defn index-html [dataset]
  (selm/render-file "templates/index.html" {:entityname (:name dataset)
                                            :tds (create-tds-for-index dataset)}
                    {:tag-open \[ :tag-close \]}))

(t/ann store-index-template [pt/entity-description String -> nil])
(defn store-index-template [dataset templ-path]
  (-> (get-and-create-templ-fp-path! "/index.html" templ-path dataset)
      (spit (index-html dataset))))

; entry point
(t/ann store-html-files [pt/entity-description String -> nil])
(defn store-html-files [dataset templ-path]
  (store-create-template dataset templ-path)
  (store-delete-template dataset templ-path)
  (store-index-template dataset templ-path)
  (println "Generated HTML templates."))