(ns de.sveri.clospcrud.spec.liquibase
  (:require [clojure.spec :as s]))

(s/def ::name string?)

(s/def ::column (s/cat :keyword keyword?
                       :type (s/alt :keyword keyword?
                                    :varchar (s/spec (s/cat :keyword keyword? :size number?)))
                       :first-attr-type (s/? keyword?)
                       :first-attr-value (s/? ::s/any)
                       :rest (s/* ::s/any)))

(s/def ::columns (s/cat :column (s/+ (s/spec ::column))))

(s/def ::entity-description (s/keys :req-un [::name ::columns]))