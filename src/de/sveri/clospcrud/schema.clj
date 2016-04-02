(ns de.sveri.clospcrud.schema
  (:require [schema.core :as s :refer [Str]]))


;(defalias et-column (HVec [Keyword (U Keyword (HVec [Keyword Number])) t/Any t/Any *]))
;(defalias et-columns (HVec [et-column
;                            et-column *]))
;
;(defalias entity-description (HMap :mandatory {:name String :columns et-columns}))
;
;
;(defalias html-label (t/HVec [Keyword (t/HMap :mandatory {:for String}) String]))
;(defalias form-map (t/HMap :mandatory {:id String}
;                           :optional {:required String}
;                           :complete? false))
;(defalias html-form (t/HVec [Keyword form-map t/Any *]))
;(defalias html-form-group (t/HVec [html-label html-form]))


;(def varchar [(s/one (s/eq :varchar) "varchar") (s/one s/Num "varchar-length")])
;(def other-type (s/enum :text :time :int :boolean))
;
;(def table-column-name-and-type
;  [(s/one s/Keyword "col-name")
;   (s/one (s/cond-pre varchar other-type) "col-type")
;   s/Any])
;
;(def cc-table-column (s/constrained table-column-name-and-type schem-h/table-column-pred))
;
;(def cc-entity-definiton {:name s/Str :columns [cc-table-column]})
;(def cc-entity-definitons [cc-entity-definiton])


;(defalias entity-description (HMap :mandatory {:name String :columns et-columns}))

;(defalias et-column (HVec [Keyword (U Keyword (HVec [Keyword Number])) t/Any t/Any *]))
;(defalias et-columns (HVec [et-column
;                            et-column *]))

(def et-column [(s/one s/Keyword "name")
                (s/one (s/cond-pre s/Keyword [(s/one s/Keyword "texttype") (s/one s/Num "size")]) "type")
                (s/one s/Keyword "attr1-type")
                (s/one s/Any "attr1-value")
                s/Any])

(def et-columns [et-column])

(def entity-description {:name Str :columns et-columns})
