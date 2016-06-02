(ns de.sveri.clospcrud.helper
  (:require [clj-time.format :as time-fmt]
            [clj-time.core :as time-core]
            [clojure.java.io :as io]
            [de.sveri.clojure.commons.files.faf :as comm-faf]
            [clojure.string :as str]
            [de.sveri.clojure.commons.files.faf :as faf]
            [de.sveri.clospcrud.spec.clospcrud :as schem]
            [clojure.spec :as s]))





(defn sanitize-filename [filename]
  (clojure.string/replace filename #"-" "_"))

(s/fdef remove-autoinc-columns :args (s/cat :cols (s/spec ::schem/columns))
        :ret ::schem/columns)
(defn remove-autoinc-columns [cols]
  (remove #(= true (:autoinc %)) cols))

(s/fdef filter-dataset :args (s/cat :dataset ::schem/entity-description)
        :ret ::schem/entity-description)
(defn filter-dataset [dataset]
  (assoc dataset :columns (remove-autoinc-columns (:columns dataset))))

(s/fdef get-colname-or-bool-convert :args (s/cat :col ::schem/column) :ret string?)
(defn get-colname-or-bool-convert [col]
  (let [name (:name col)]
    (if (= :boolean (:type col)) (str "(convert-boolean " name ")") name)))

(s/fdef ds-columns->template-columns :args (s/cat :cols (s/spec ::schem/columns))
        :ret (s/cat :ds-column (s/* ::schem/ds-column)))
(defn ds-columns->template-columns [cols]
  (println (mapv (fn [col]
                   {:colname (:name col)
                    :colname-fn (get-colname-or-bool-convert col)})
                 (remove-autoinc-columns cols)))
  (mapv (fn [col]
          {:colname (:name col)
           :colname-fn (get-colname-or-bool-convert col)})
        (remove-autoinc-columns cols)))

(s/fdef get-ns-file-path :args (s/cat :ns string? :filename string? :src-path string?)
        :ret string?)
(defn get-ns-file-path [ns filename src-path]
  (str src-path "/" (str/replace ns #"\." "/") "/" filename))

(s/fdef store-content-in-ns :args (s/cat :ns string? :filename string? :src-path string? :content string?)
        :ret nil?)
(defn store-content-in-ns [ns filename src-path content]
  (let [ns-path (str src-path "/" (str/replace ns #"\." "/"))
        ns-file-path (str ns-path "/" filename)]
    (faf/create-if-not-exists ns-path)
    (spit ns-file-path content)))

(s/fdef jdbc-uri->classname :args (s/cat :uri string?) :ret string?)
(defn jdbc-uri->classname
  "Maps the jdbc uri to the adapter that clj-liquibase expects"
  [uri]
  (cond
    (.contains uri "h2") "org.h2.Driver"
    (.contains uri "mysql") "com.mysql.jdbc.Driver"
    (.contains uri "sqlite") "org.sqlite.JDBC"
    (.contains uri "postgresql") "org.postgresql.Driver"

    :else (throw (IllegalArgumentException. "Either you did not provide a correct jdbc uri, or the protocol is
    not supported yet."))))

(s/fdef store-table-migrations :args (s/cat :sql-up string? :ent-name string? :sql-down string? :out-path string?)
        :ret nil?)
(defn store-table-migrations [sql-up ent-name sql-down out-path]
  (comm-faf/create-if-not-exists (io/file out-path))
  (let [time-str (time-fmt/unparse (time-fmt/formatters :basic-date-time-no-ms) (time-core/now))
        out-up-fp (io/file (str out-path "/" ent-name "-" time-str ".up.sql"))
        out-down-fp (io/file (str out-path "/" ent-name "-" time-str ".down.sql"))]
    (spit out-up-fp sql-up)
    (spit out-down-fp sql-down)))
