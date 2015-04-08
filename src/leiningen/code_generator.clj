(ns leiningen.code-generator
  (:require [fipp.clojure :as f-clj]))

(defn generate-ns [ns]
  (let [ns `(~'ns ~(symbol ns)
              (:require [foo.bar :as ~'bar]))]
    (clojure.pprint/pprint ns)))

(defmacro code-praiser
  [code]
  `(println
     "Sweet gorilla of Manila, this is good code:"
     (quote ~code)))
