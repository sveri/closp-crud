(ns leiningen.html-creator
  (:require [hiccup.core :as hicc]
            [clojure.core.typed :as t]
            [leiningen.pre-types :as pt]
            [leiningen.td-to-hiccup :as ds-conv]))

(t/ann wrap-with-form-group [])
(defn wrap-with-form-group [col-vec]
  (vec (concat [:div.form-group] col-vec)))

(t/ann create-html [pt/entity-description -> String])
(defn create-html [dataset]
  )