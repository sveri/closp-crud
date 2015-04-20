(ns leiningen.html-creator
  (:require [hiccup.core :as hicc]
            [clojure.core.typed :as t]
            [leiningen.pre-types :as pt]
            [leiningen.td-to-hiccup :as ds-conv]
            [leiningen.helper :as h]
            [de.sveri.clojure.commons.files.faf :as faf]
            [selmer.parser :as selm])
  (:import (clojure.lang Seqable)))

(t/ann get-and-create-templ-fp-path! [String String pt/entity-description -> String])
(defn get-and-create-templ-fp-path! [filename templ-path dataset]
  (let [folder-path (str (str templ-path "/" (:name dataset)))
        fp (str folder-path "/" filename)]
    (faf/create-if-not-exists folder-path)
    fp))

(t/ann wrap-with-form-group [])
(defn wrap-with-form-group [col-vec]
  (vec (concat [:div.form-group] col-vec)))

(t/ann wrap-create-with-form [String (Seqable pt/html-form-group) -> (t/HVec [t/Any])])
(defn wrap-create-with-form [action form-groups]
  (let [form [:form {:action (str "/" action "/create" :method "post")}]
        af-token [[:input {:name "__anti-forgery-token" :type "hidden" :value "{{af-token}}"}]]
        btns [[:button.btn.btn-primary {:type "submit"} (str "Add " action)]]]
    (vec (concat form af-token form-groups btns))))

(t/ann wrap-with-selmer-extend [String -> String])
(defn wrap-with-selmer-extend [form]
  (str "{% extends \"base.html\" %}\r\n{% block content %}\r\n" form "\r\n{% endblock %}"))

(t/ann create-html [pt/entity-description -> String])
(defn create-html [dataset]
  (let [cleaned-dataset (h/filter-dataset dataset)
        form-groups (map #(wrap-with-form-group (ds-conv/dt->hiccup %)) (:columns cleaned-dataset))
        comp-form (wrap-create-with-form (:name dataset) form-groups)]
    (wrap-with-selmer-extend (hicc/html comp-form))))

(t/ann store-create-template [pt/entity-description String -> nil])
(defn store-create-template [dataset templ-path]
  (-> (get-and-create-templ-fp-path! "/create.html" templ-path dataset)
      (spit (create-html dataset))))

(t/ann delete-html [pt/entity-description -> String])
(defn delete-html [dataset]
  (selm/render-file "templates/db.tmpl" {:entityname (:name dataset)} {:tag-open \[ :tag-close \]}))

(t/ann store-delete-template [pt/entity-description String -> nil])
(defn store-delete-template [dataset templ-path]
  (-> (get-and-create-templ-fp-path! "/delete.html" templ-path dataset)
      (spit (delete-html dataset))))

(t/ann index-html [pt/entity-description -> String])
(defn index-html [dataset]
  (selm/render-file "templates/delete.html" {:entityname (:name dataset)} {:tag-open \[ :tag-close \]}))

(t/ann store-index-template [pt/entity-description String -> nil])
(defn store-index-template [dataset templ-path]
  (-> (get-and-create-templ-fp-path! "/index.html" templ-path dataset)
      (spit (delete-html dataset))))

(t/ann store-html-files [pt/entity-description String -> nil])
(defn store-html-files [dataset templ-path]
  (store-create-template dataset templ-path)
  (store-delete-template dataset templ-path)
  (store-index-template dataset templ-path)
  )