(ns de.sveri.clospcrud.html-schema
  (:require [clojure.spec :as s]))


(s/def ::id string?)
(s/def ::required string?)
(s/def ::type string?)
(s/def ::form-map (s/keys :req-un [::id ::name]
                          :opt-un [::required ::type]))


(s/def ::attribute-map (s/keys :req-un [::id ::name] :opt-un [::type ::required ::maxlength ::value ::checked]))
(s/def ::form-control (s/cat :input-type keyword? :attributes ::attribute-map :text (s/? string?)))

(s/def ::for string?)
(s/def ::for-map (s/keys :req-un [::for]))
(s/def ::html-label (s/cat :label keyword? :for-map ::for-map :name string?))

(s/def ::form-with-simple-label
  (s/cat :div (s/? keyword?)
         :hicc-container (s/spec (s/cat :label keyword? :form-control (s/spec ::form-control) :name string?))))

(s/def ::form-with-complete-label (s/cat :div (s/? keyword?) :label (s/spec ::html-label) :form-control (s/spec ::form-control)))

(s/def ::html-form (s/or :simple (s/spec ::form-with-simple-label) :complete (s/spec ::form-with-complete-label)))
