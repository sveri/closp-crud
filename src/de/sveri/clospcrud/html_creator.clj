(ns de.sveri.clospcrud.html-creator
  (:require [hiccup.core :as hicc]
            [de.sveri.clospcrud.td-to-hiccup :as ds-conv]
            [de.sveri.clospcrud.helper :as h]
            [de.sveri.clojure.commons.files.faf :as faf]
            [selmer.parser :as selm]
            [clojure.string :as str]
            [de.sveri.clospcrud.schema :as schem]
            [de.sveri.clospcrud.html-schema :as hschem]
            [clojure.spec :as s])
  (:import (java.lang System)))





(def line-sep (System/lineSeparator))

(s/fdef hicc->html :args (s/cat :se (s/cat :hiccup (s/+ ::s/any))) :ret string?)
(defn hicc->html [se]
  (-> se
      (hicc/html)
      (str/replace #"/div>" (str "/div>" line-sep))))

(s/fdef get-and-create-templ-fp-path! :args (s/cat :filename string? :templ-path string? :dataset ::schem/entity-description)
        :ret string?)
(defn get-and-create-templ-fp-path! [filename templ-path dataset]
  (let [folder-path (str templ-path "/" (:name dataset))
        fp (str folder-path "/" filename)]
    (faf/create-if-not-exists folder-path)
    fp))


(s/fdef wrap-with-form-group :args (s/cat :col-vec ::hschem/html-form) :ret ::hschem/html-form)
(defmulti wrap-with-form-group (fn [col-vec] (:type (second (second col-vec)))))

(defmethod wrap-with-form-group "checkbox" [col-vec]
  (vec (concat [:div] col-vec)))

(defmethod wrap-with-form-group :default [col-vec]
  (vec (concat [:div.form-group] col-vec)))

; create
(s/fdef insert-extra-tags :args (s/cat :form-groups-str string? :entity-nam string?) :ret string?)
(defn insert-extra-tags [form-groups-str entity-name]
  (if (.contains form-groups-str "checkbox")
    (let [field (second (re-find #"id=\"([a-zA-Z-_]*)\"" form-groups-str))]
      (str/replace form-groups-str #"type=\"checkbox\""
                   (str "type=\"checkbox\" " "{%if " entity-name "." field " = 1 %}checked{% endif %}")))
    form-groups-str))

(s/fdef create-html :args (s/cat :dataset ::schem/entity-description) :ret string?)
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
                                               :form-groups (str/join line-sep form-groups)}
                      {:tag-open \[ :tag-close \]})))

(s/fdef store-create-template :args (s/cat :dataset ::schem/entity-description :templ-path string?) :ret nil?)
(defn store-create-template [dataset templ-path]
  (-> (get-and-create-templ-fp-path! "/create.html" templ-path dataset)
      (spit (create-html dataset))))

; delete
(s/fdef delete-html :args (s/cat :dataset ::schem/entity-description) :ret string?)
(defn delete-html [dataset]
  (selm/render-file "templates/delete.html" {:entityname (:name dataset)} {:tag-open \[ :tag-close \]}))

(s/fdef store-delete-template :args (s/cat :dataset ::schem/entity-description :templ-path string?) :ret nil?)
(defn store-delete-template [dataset templ-path]
  (-> (get-and-create-templ-fp-path! "/delete.html" templ-path dataset)
      (spit (delete-html dataset))))

; index
(s/fdef create-tds-for-index :args (s/cat :dataset ::schem/entity-description) :ret string?)
(defn create-tds-for-index [dataset]
  (let [e-name (:name dataset)
        conv-col-name #(:name %)
        san-cols (h/remove-autoinc-columns (:columns dataset))
        first-col (first san-cols)
        hicc-first-col [:td [:a {:href (str "/" e-name "/{{" e-name ".id}}")}
                             (str "{{" e-name "." (conv-col-name first-col) "}}")]]
        rest-cols (rest san-cols)
        hicc-rest-cols (map (fn [col] [:td (str "{{" e-name "." (conv-col-name col) "}}")]) rest-cols)
        hicc-delete-col [:td [:a.btn.btn-primary {:href (str "/" e-name "/delete/{{" e-name ".id}}")} "Delete"]]]
    (str (hicc->html hicc-first-col) (str line-sep "\t\t\t\t") (hicc->html hicc-rest-cols) (str line-sep "\t\t\t\t")
         (hicc->html hicc-delete-col))))

(s/fdef index-html :args (s/cat :dataset ::schem/entity-description) :ret string?)
(defn index-html [dataset]
  (selm/render-file "templates/index.html" {:entityname (:name dataset)
                                            :tds        (create-tds-for-index dataset)}
                    {:tag-open \[ :tag-close \]}))

(s/fdef store-index-template :args (s/cat :dataset ::schem/entity-description :templ-path string?) :ret nil?)
(defn store-index-template [dataset templ-path]
  (-> (get-and-create-templ-fp-path! "/index.html" templ-path dataset)
      (spit (index-html dataset))))

(s/fdef store-html-files :args (s/cat :dataset ::schem/entity-description :templ-path string?) :ret nil?)
(defn store-html-files [dataset templ-path]
  (store-create-template dataset templ-path)
  (store-delete-template dataset templ-path)
  (store-index-template dataset templ-path)
  (println "Generated HTML templates."))