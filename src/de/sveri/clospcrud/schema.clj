(ns de.sveri.clospcrud.schema
  (:require [schema.core :as s :refer [Str Num Bool Any]]))


; luiquibase schema
(def liqui-column [(s/one s/Keyword "name")
                   (s/one (s/cond-pre s/Keyword [(s/one s/Keyword "texttype") (s/one s/Num "size")]) "type")
                   (s/optional s/Keyword "attr1-type")
                   (s/optional s/Any "attr1-value")
                   s/Any])

(def liqui-columns [liqui-column])

(def liqui-entity-description {:name Str :columns liqui-columns})


; closp-crud schema
(def column-types (s/enum :int :varchar :boolean :text :time :date
                          :char :binary :smallint :bigint :decimal
                          :float :double :real :timestamp))

(def column {:name                        Str :type column-types
             (s/optional-key :null)       Bool
             (s/optional-key :max-length) Num
             (s/optional-key :required)   Bool
             (s/optional-key :pk)         Bool
             (s/optional-key :autoinc)    Bool
             (s/optional-key :unique)     Bool
             (s/optional-key :default)    Any
             (s/optional-key :refs)       Str
             (s/optional-key :fkname)     Str})

(def columns [column])

(def entity-description {:name Str :columns columns})



(def html-label (s/cond-pre s/Keyword
                            [(s/one s/Keyword "label")
                             (s/one {:for s/Str} "for")
                             (s/one Str "name")]))
(def form-map {:id                        Str :name s/Str
               (s/optional-key :required) s/Str
               (s/optional-key :type)     s/Str
               s/Any                      s/Any})

(def html-form [(s/one s/Keyword "input-type") (s/one form-map "input-attributes") s/Any])
(def html-form-group [(s/one html-label "html-label") (s/one html-form "form") Str])

