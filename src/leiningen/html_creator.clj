(ns leiningen.html-creator
  (:require [hiccup.core :as hicc]
            [clojure.core.typed :as t]))

(t/ann wrap-with-form-group [])
(defn wrap-with-form-group [col-vec]
  (vec (concat [:div.form-group] col-vec)))

