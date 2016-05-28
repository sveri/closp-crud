(ns de.sveri.clospcrud.schema
  (:require [schema.core :as sp :refer [Str Num Bool Any]]
            [clojure.spec :as s]))

(s/def ::name string?)

(s/def ::entityname string?)
(s/def ::entity-ns string?)
(s/def ::ns string?)
(s/def ::colname string?)
(s/def ::colname-fn string?)
(s/def ::ds-column (s/keys :req-un [::colname ::colname-fn]))
(s/def ::cols (s/cat :cols (s/+ ::ds-column)))
(s/def ::template-map (s/keys :req-un [::entityname ::entity-ns ::ns ::cols]))

;(def liqui-column [(sp/one sp/Keyword "name")
;                   (sp/one (sp/cond-pre sp/Keyword [(sp/one sp/Keyword "texttype") (sp/one sp/Num "size")]) "type")
;                   (sp/optional sp/Keyword "attr1-type")
;                   (sp/optional sp/Any "attr1-value")
;                   sp/Any])


;(def liqui-columns [liqui-column])
;
;(def liqui-entity-description {:name Str :columns liqui-columns})
;
;
;
;(def column-types (sp/enum :int :varchar :boolean :text :time :date
;                           :char :binary :smallint :bigint :decimal
;                           :float :double :real :timestamp))

(s/def ::column-types #{:int :varchar :boolean :text :time :date
                        :char :binary :smallint :bigint :decimal
                        :float :double :real :timestamp})


(s/def ::type ::column-types)
(s/def ::null #(instance? Boolean %))
(s/def ::max-length number?)
(s/def ::required #(instance? Boolean %))
(s/def ::pk #(instance? Boolean %))
(s/def ::autoinc #(instance? Boolean %))
(s/def ::unique #(instance? Boolean %))
(s/def ::default ::s/any)
(s/def ::refs string?)
(s/def ::fk-name string?)

(s/def ::column (s/keys :req-un [::name ::type]
                        :opt-un [::null ::max-length ::required ::pk ::autoinc ::unique ::default
                                 ::refs ::fkname]))
(s/def ::columns (s/cat :column (s/* ::column)))

;(def column {:name                         Str :type column-types
;             (sp/optional-key :null)       Bool
;             (sp/optional-key :max-length) Num
;             (sp/optional-key :required)   Bool
;             (sp/optional-key :pk)         Bool
;             (sp/optional-key :autoinc)    Bool
;             (sp/optional-key :unique)     Bool
;             (sp/optional-key :default)    Any
;             (sp/optional-key :refs)       Str
;             (sp/optional-key :fkname)     Str})
;(def columns [column])
;
;(def entity-description {:name Str :columns columns})
(s/def ::entity-description (s/keys :req-un [::name ::columns]))



;(def html-label (sp/cond-pre sp/Keyword
;                             [(sp/one sp/Keyword "label")
;                              (sp/one {:for sp/Str} "for")
;                              (sp/one Str "name")]))
;
;
;(def form-map {:id                         Str :name sp/Str
;               (sp/optional-key :required) sp/Str
;               (sp/optional-key :type)     sp/Str
;               sp/Any                      sp/Any})
;(def html-form [(sp/one sp/Keyword "input-type") (sp/one form-map "input-attributes") sp/Any])
;(def html-form-group [(sp/one html-label "html-label") (sp/one html-form "form") Str])

