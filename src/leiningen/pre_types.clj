(ns leiningen.pre-types
  (:refer-clojure :exclude [fn])
  (:require [clojure.core.typed :as t :refer [defalias HMap HSeq fn]])
  (:import (clojure.lang Keyword Seqable)))

(defalias entity-description  (HMap :mandatory {:name String :columns (HSeq [(HSeq [Keyword Keyword *])
                                                                             (HSeq [Keyword Keyword *]) *])}))


(def cols [[:id "foo"] [:fooname "foo"]])

(t/ann filter-ids [(t/NonEmptyVec (t/HSequential [Keyword t/Any *]))
                   -> (t/Option (Seqable (t/HSequential [Keyword t/Any *])))])
(defn filter-ids [cols]
  (remove (fn [col :- (t/HSequential [Keyword t/Any *])] (= :id (first col))) cols))

(t/ann conv [(t/NonEmptyVec (t/HSequential [Keyword t/Any *]))
             -> (t/Option (Seqable (t/HMap :mandatory {:colname String})))])
(defn conv [cols]
  (let [f-cols (filter-ids cols)]
      (mapv (fn [col :- (t/HSequential [Keyword t/Any *])] {:colname (name (first col))}) f-cols)))
