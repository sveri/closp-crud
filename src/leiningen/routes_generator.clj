(ns leiningen.routes-generator
  (:require [selmer.parser :as selm]
            [clojure.core.typed :as t :refer [ann]]
            [leiningen.pre-types :as pt]
            [clojure.pprint :as pp]
            [leiningen.helper :as h])
  (:import (clojure.lang Keyword)))

(def bool-conv-fn '(defn convert-boolean [b] (if (= "on" b) true false)))

(t/ann contains-boolean? [pt/et-column -> (t/Option Keyword)])
(defn contains-boolean? [col]
  (some #{:boolean} col))

(t/ann create-add-fns [pt/et-columns -> String])
(defn create-add-fns [cols]
  (when (< 0 (count (filter contains-boolean? cols)))
    (pp/with-pprint-dispatch pp/code-dispatch bool-conv-fn)))

(t/ann store-route [String String String pt/entity-description -> nil])
(defn store-route [ns-routes ns-db ns-layout dataset src-path]
  (->>
    (selm/render-file "templates/routes.tmpl"
                      {:ns                (str ns-routes "." (:name dataset))
                       :ns-db             (str ns-db "." (:name dataset))
                       :ns-layout         ns-layout
                       :cols              (h/ds-columns->template-columns (:columns dataset))
                       :functionized-cols (h/ds-columns->template-columns (:columns dataset) true)
                       :ent-name          (:name dataset)
                       :add-fns           (create-add-fns (:columns dataset))}
                      {:tag-open \[ :tag-close \]})
    (h/store-content-in-ns ns-routes (str (:name dataset) ".clj") src-path)
    (println "Generated routes namespace.")))