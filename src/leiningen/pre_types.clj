(ns leiningen.pre-types
  (:refer-clojure :exclude [fn])
  (:require [clojure.core.typed :as t :refer [defalias HMap HSeq fn Any HVec]])
  (:import (clojure.lang Keyword Seqable)))

(defalias et-column (HVec [Keyword t/Any *]))
(defalias et-columns (HVec [et-column
                            et-column *]))

(defalias entity-description (HMap :mandatory {:name String :columns et-columns}))
