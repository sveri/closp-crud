(ns leiningen.routes-generator
  (:require [selmer.parser :as selm]
            [clojure.core.typed :as t :refer [ann]]
            [leiningen.pre-types :as pt]
            [de.sveri.clojure.commons.files.faf :as faf]
            [leiningen.helper :as h]))

(defn store-route [ns-routes ns-db ns-layout dataset src-path]
  (->>
    (selm/render-file "templates/routes.tmpl"
                      {:ns (str ns-routes "." (:name dataset))
                       :ns-db (str ns-db "." (:name dataset))
                       :ns-layout ns-layout
                       :cols (h/ds-columns->template-columns (:columns dataset))
                       :ent-name (:name dataset)}
                      {:tag-open \[ :tag-close \]})
    (h/store-content-in-ns ns-routes (str (:name dataset) ".clj") src-path)))
