(ns de.sveri.clospcrud.schema
  (:require [schema.core :as s :refer [Str]]))

(def et-column [(s/one s/Keyword "name")
                (s/one (s/cond-pre s/Keyword [(s/one s/Keyword "texttype") (s/one s/Num "size")]) "type")
                (s/optional s/Keyword "attr1-type")
                (s/optional s/Any "attr1-value")
                s/Any])

(def et-columns [et-column])

(def entity-description {:name Str :columns et-columns})

(def html-label (s/cond-pre s/Keyword
                            [(s/one s/Keyword "label")
                             (s/one {:for s/Str} "for")
                             (s/one Str "name")]))
(def form-map {:id                        Str :name s/Str
               (s/optional-key :required) s/Str
               (s/optional-key :type)     s/Str
               s/Any s/Any})

(def html-form [(s/one s/Keyword "input-type") (s/one form-map "input-attributes") s/Any])
(def html-form-group [(s/one html-label "html-label") (s/one html-form "form") Str])

