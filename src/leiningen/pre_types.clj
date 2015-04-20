(ns leiningen.pre-types
  (:refer-clojure :exclude [fn])
  (:require [clojure.core.typed :as t :refer [defalias HMap HSeq fn Any HVec U]])
  (:import (clojure.lang Keyword)))

(defalias et-column (HVec [Keyword (U Keyword (HVec [Keyword Number])) t/Any t/Any *]))
(defalias et-columns (HVec [et-column
                            et-column *]))

(defalias entity-description (HMap :mandatory {:name String :columns et-columns}))


(defalias html-label (t/HVec [Keyword (t/HMap :mandatory {:for String}) String]))
(defalias form-map (t/HMap :mandatory {:id String}
                           :optional {:required String}
                           :complete? false))
(defalias html-form (t/HVec [Keyword form-map t/Any *]))
(defalias html-form-group (t/HVec [html-label html-form]))
