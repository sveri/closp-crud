(ns leiningen.html-creator
  (:require [hiccup.core :as hicc]
            [clojure.core.typed :as t]
            [leiningen.pre-types :as pt]
            [leiningen.td-to-hiccup :as ds-conv]
            [leiningen.helper :as h]
            [de.sveri.clojure.commons.files.faf :as faf]))

(t/ann wrap-with-form-group [])
(defn wrap-with-form-group [col-vec]
  (vec (concat [:div.form-group] col-vec)))

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
    (wrap-with-selmer-extend (hicc/html comp-form)) ))

(defn store-create-html [dataset templ-path]
  (let [folder-path (str (str templ-path "/" (:name dataset)))
        fp (str folder-path "/create.html")]
    (faf/create-if-not-exists folder-path)
    (spit fp (create-html dataset))))