(ns leiningen.code-generator
  (:require [fipp.clojure :as f-clj]
            [stencil.core :as stenc]))

(defn render-db-file [ns dataset]
  (stenc/render-file "templates/db.mustache" {:entityname (:name dataset)
                                              :ns         ns
                                              :cols [{:colname "foo"} {:colname "foo2"}]}))


