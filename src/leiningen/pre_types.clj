(ns leiningen.pre-types
  (:require [clojure.core.typed :refer [defalias HMap HSeq]]))

                        ;(HMap :mandatory {:name String :columns (HSeq)})
(defalias entity-description  (HMap :mandatory {:name String :columns (HSeq)}))
;(defn entity-description [] (HMap :mandatory {:name String :columns (HSeq)}))
