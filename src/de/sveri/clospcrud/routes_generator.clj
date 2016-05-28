(ns de.sveri.clospcrud.routes-generator
  (:require [selmer.parser :as selm]
            [clojure.pprint :as pp]
            [de.sveri.clospcrud.helper :as h]
            [de.sveri.clospcrud.schema :as schem]
            [clojure.spec :as s]))

(def bool-conv-fn '(defn convert-boolean [b] (if (= "on" b) true false)))

(s/fdef boolean? :args (s/cat :col ::schem/column) :ret ::schem/boolean)
(defn boolean? [col] (= :boolean (get col :type)))

(s/fdef create-add-fns :args (s/cat :cols (s/spec ::schem/columns))
        :ret (s/nilable string?))
(defn create-add-fns [cols]
  (when (< 0 (count (filter boolean? cols)))
    (str (pp/with-pprint-dispatch pp/code-dispatch bool-conv-fn))))

(s/fdef store-route :args (s/cat :ns-routes string? :ns-db string? :ns-layout string?
                                 :dataset ::schem/entity-description :src-path string?)
        :ret nil?)
(defn store-route [ns-routes ns-db ns-layout dataset src-path]
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