(ns leiningen.helper-test
  (:require [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [de.sveri.clospcrud.spec.clospcrud :as schem]
            [clojure.spec :as s]
            [de.sveri.clospcrud.helper :as h]))

(defspec remove-autoinc-column 100
         (prop/for-all [columns (s/gen ::schem/columns)]
                       (= 0 (count (filter :autoinc (h/remove-autoinc-columns columns))))))

(s/instrument-all)