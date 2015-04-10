(ns leiningen.pre-types
  (:require [clojure.core.typed :refer [defalias HMap HSeq]])
  (:import (clojure.lang Keyword)))

                        ;(HMap :mandatory {:name String :columns (HSeq)})
(defalias entity-description  (HMap :mandatory {:name String :columns (HSeq [(HSeq [Keyword Keyword *])
                                                                             (HSeq [Keyword Keyword *]) *])}))
;(defn entity-description [] (HMap :mandatory {:name String :columns (HSeq)}))
