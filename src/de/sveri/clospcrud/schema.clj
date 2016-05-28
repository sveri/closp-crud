(ns de.sveri.clospcrud.schema
  (:require [clojure.spec :as s]))

(s/def ::name string?)

(s/def ::entityname string?)
(s/def ::entity-ns string?)
(s/def ::ns string?)
(s/def ::colname string?)
(s/def ::colname-fn string?)
(s/def ::ds-column (s/keys :req-un [::colname ::colname-fn]))
(s/def ::cols (s/cat :cols (s/+ ::ds-column)))
(s/def ::template-map (s/keys :req-un [::entityname ::entity-ns ::ns ::cols]))

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

(s/def ::entity-description (s/keys :req-un [::name ::columns]))