(ns de.sveri.clospcrud.spec.clospcrud
  (:require [clojure.spec :as s]))

(s/def ::name (s/and string? #(not= "" %)))
(s/def ::boolean (s/or :t true? :f false?))
(s/def ::none-empty-string (s/and string? #(not= "" %)))

(s/def ::entityname ::name)
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
(s/def ::null ::boolean)
(s/def ::max-length (s/and integer? #(< 0 %)))
(s/def ::required ::boolean)
(s/def ::pk ::boolean)
(s/def ::autoinc ::boolean)
(s/def ::unique ::boolean)
(s/def ::default (s/or :string string? :boolean ::boolean))
(s/def ::refs ::none-empty-string)
(s/def ::fk-name ::none-empty-string)

(s/def ::column (s/keys :req-un [::name ::type]
                        :opt-un [::null ::max-length ::required ::pk ::autoinc ::unique ::default
                                 ::refs ::fk-name]))
(s/def ::columns (s/cat :column (s/+ ::column)))

(s/def ::entity-description (s/keys :req-un [::name ::columns]))
