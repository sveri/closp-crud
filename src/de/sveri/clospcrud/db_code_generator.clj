(ns de.sveri.clospcrud.db-code-generator
  (:require [selmer.parser :as selm]
            [de.sveri.clospcrud.helper :as h]
            [schema.core :as s]
            [de.sveri.clospcrud.schema :as schem]))

(s/defn dataset->template-map :- {:entityname s/Str :ns s/Str :cols [{:colname s/Str
                                                                      :colname-fn s/Str}]}
  "Will remove every column with the name :id"
  [ns :- s/Str ds :- schem/entity-description]
  (let [cols (h/ds-columns->template-columns (:columns ds))]
    {:entityname       (:name ds)
     :ns               (str ns "." (:name ds))
     :cols             cols}))

(s/defn render-db-file :- s/Str [ns :- s/Str dataset :- schem/entity-description]
  (let [templ-map (dataset->template-map ns dataset)]
    (selm/render-file "templates/db.tmpl" templ-map {:tag-open \[ :tag-close \]})))

(s/defn store-dataset :- nil [ns :- s/Str dataset :- schem/entity-description src-path :- s/Str]
  (let [file-content (render-db-file ns dataset)
        filename (str (h/sanitize-filename (:name dataset)) ".clj")]
    (h/store-content-in-ns ns filename src-path file-content)
    (println "Generated database namespace.")))
