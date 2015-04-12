(ns leiningen.pre-types
  (:require [clojure.core.typed :as t :refer [defalias HMap HSeq]])
  (:import (clojure.lang Keyword)))

(defalias entity-description  (HMap :mandatory {:name String :columns (HSeq [(HSeq [Keyword Keyword *])
                                                                             (HSeq [Keyword Keyword *]) *])}))


(def cols [[:id "foo"] [:fooname "foo"]])

(t/ann mapf [(t/HSequential [Keyword t/Any *]) -> (t/HMap :mandatory {:colname String})])
(defn mapf [v]
  {:colname (name (first v))})

(t/ann conv [(t/NonEmptyVec (t/HSequential [t/Kw t/Any *]))
             -> (t/NonEmptyVec (t/HMap :mandatory {:colname String}))])
(defn conv [cols]
  (mapv mapf cols))
