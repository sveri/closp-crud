(ns de.sveri.clospcrud.html-creator
  (:require [hiccup.core :as hicc]
            [de.sveri.clospcrud.td-to-hiccup :as ds-conv]
            [de.sveri.clospcrud.helper :as h]
            [de.sveri.clojure.commons.files.faf :as faf]
            [selmer.parser :as selm]
            [clojure.string :as str]
            [de.sveri.clospcrud.schema :as schem]
            [schema.core :as s])
  (:import (java.lang System)))

(def line-sep (System/lineSeparator))

(s/defn hicc->html :- s/Str [se :- [s/Any]]
  (-> se
      (hicc/html)
      (str/replace #"/div>" (str "/div>" line-sep))))

(s/defn get-and-create-templ-fp-path! :- s/Str
  [filename :- s/Str templ-path :- s/Str dataset :- schem/entity-description]
  (let [folder-path (str templ-path "/" (:name dataset))
        fp (str folder-path "/" filename)]
    (faf/create-if-not-exists folder-path)
    fp))

(defmulti wrap-with-form-group  (fn [col-vec] (:type (second (second col-vec)))))

(s/defmethod wrap-with-form-group "checkbox" :- [s/Keyword schem/html-form-group]
             [col-vec :- schem/html-form-group]
  (vec (concat [:div] col-vec)))

(defmethod wrap-with-form-group :default [col-vec]
  (vec (concat [:div.form-group] col-vec)))

; create
(s/defn insert-extra-tags :- s/Str [form-groups-str :- s/Str entity-name :- s/Str]
  (if (.contains form-groups-str "checkbox")
    (let [field (second (re-find #"id=\"([a-zA-Z-_]*)\"" form-groups-str))]
     (str/replace form-groups-str #"type=\"checkbox\""
                  (str "type=\"checkbox\" " "{%if " entity-name "." field " = 1 %}checked{% endif %}")))
    form-groups-str))

(s/defn create-html :- s/Str [dataset :- schem/entity-description]
  (let [cleaned-dataset (h/filter-dataset dataset)
        entity-name (:name dataset)
        form-groups (map
                      #(-> (ds-conv/dt->hiccup % entity-name :create)
                           (wrap-with-form-group)
                           hicc->html
                           (insert-extra-tags entity-name))
                      (:columns cleaned-dataset))]
    (selm/render-file "templates/create.html" {:entityname  entity-name
                                               :form-groups (str/join line-sep form-groups)}
                      {:tag-open \[ :tag-close \]})))

(s/defn store-create-template :- nil [dataset :- schem/entity-description templ-path :- s/Str]
  (-> (get-and-create-templ-fp-path! "/create.html" templ-path dataset)
      (spit (create-html dataset))))

; delete
(s/defn delete-html :- s/Str [dataset :- schem/entity-description]
  (selm/render-file "templates/delete.html" {:entityname (:name dataset)} {:tag-open \[ :tag-close \]}))

(s/defn store-delete-template :- nil [dataset :- schem/entity-description templ-path :- s/Str]
  (-> (get-and-create-templ-fp-path! "/delete.html" templ-path dataset)
      (spit (delete-html dataset))))

; index
; TODO needs test
(s/defn create-tds-for-index :- s/Str [dataset :- schem/entity-description]
  (let [e-name (:name dataset)
        conv-col-name #(:name %)
        san-cols (h/filter-id-columns (:columns dataset))
        first-col (first san-cols)
        hicc-first-col [:td [:a {:href (str "/" e-name "/{{" e-name ".id}}")}
                             (str "{{" e-name "." (conv-col-name first-col) "}}")]]
        rest-cols (rest san-cols)
        hicc-rest-cols (map (fn [col] [:td (str "{{" e-name "." (conv-col-name col) "}}")]) rest-cols)
        hicc-delete-col [:td [:a.btn.btn-primary {:href (str "/" e-name "/delete/{{" e-name ".id}}")} "Delete"]]]
    (str (hicc->html hicc-first-col) (str line-sep "\t\t\t\t") (hicc->html hicc-rest-cols) (str line-sep "\t\t\t\t")
         (hicc->html hicc-delete-col))))

(s/defn index-html :- s/Str [dataset :- schem/entity-description]
  (selm/render-file "templates/index.html" {:entityname (:name dataset)
                                            :tds (create-tds-for-index dataset)}
                    {:tag-open \[ :tag-close \]}))

(s/defn store-index-template :- nil [dataset :- schem/entity-description templ-path :- s/Str]
  (-> (get-and-create-templ-fp-path! "/index.html" templ-path dataset)
      (spit (index-html dataset))))

(s/defn store-html-files :- nil [dataset :- schem/entity-description templ-path :- s/Str]
  (store-create-template dataset templ-path)
  (store-delete-template dataset templ-path)
  (store-index-template dataset templ-path)
  (println "Generated HTML templates."))