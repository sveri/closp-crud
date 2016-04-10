(ns de.sveri.clospcrud.routes-generator
  (:require [selmer.parser :as selm]
            [clojure.pprint :as pp]
            [de.sveri.clospcrud.helper :as h]
            [de.sveri.clospcrud.schema :as schem]
            [schema.core :as s]))

(def bool-conv-fn '(defn convert-boolean [b] (if (= "on" b) true false)))

(s/defn boolean? :- s/Bool [col :- schem/column]
        (= :boolean (get col :type)))

(s/defn create-add-fns :- (s/maybe s/Str) [cols :- schem/columns]
  (when (< 0 (count (filter boolean? cols)))
    (str (pp/with-pprint-dispatch pp/code-dispatch bool-conv-fn))))

(s/defn store-route :- nil
  [ns-routes :- s/Str ns-db :- s/Str ns-layout :- s/Str dataset :- schem/entity-description src-path :- s/Str]
  (->>
    (selm/render-file "templates/routes.tmpl"
                      {:ns                (str ns-routes "." (:name dataset))
                       :ns-db             (str ns-db "." (:name dataset))
                       :ns-layout         ns-layout
                       :cols              (h/ds-columns->template-columns (:columns dataset))
                       :functionized-cols (h/ds-columns->template-columns (:columns dataset))
                       :ent-name          (:name dataset)
                       :add-fns           (create-add-fns (:columns dataset))}
                      {:tag-open \[ :tag-close \]})
    (h/store-content-in-ns ns-routes (str (h/sanitize-filename (:name dataset)) ".clj") src-path)
    (println "Generated routes namespace.")))