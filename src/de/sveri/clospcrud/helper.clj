(ns de.sveri.clospcrud.helper
  (:require [clj-time.format :as time-fmt]
            [clj-time.core :as time-core]
            [clojure.java.io :as io]
            [de.sveri.clojure.commons.files.faf :as comm-faf]
            [clojure.string :as str]
            [de.sveri.clojure.commons.files.faf :as faf]
            [de.sveri.clospcrud.schema :as schem]
            [schema.core :as s]))


(defn sanitize-filename [filename]
  (clojure.string/replace filename #"-" "_"))

(s/defn remove-autoinc-columns :- [schem/column] [cols :- schem/columns]
  (vec (remove #(= true (:autoinc %)) cols)))

(s/defn filter-dataset :- schem/entity-description [dataset :- schem/entity-description]
  (assoc dataset :columns (remove-autoinc-columns (:columns dataset))))

(s/defn get-colname-or-bool-convert :- s/Str [col :- schem/column]
  (let [name (:name col)]
    (if (= :boolean (:type col)) (str "(convert-boolean " name ")") name)))

(s/defn ds-columns->template-columns :- [{:colname s/Str :colname-fn s/Str}]
  [cols :- schem/columns]
  (mapv (fn [col]
          {:colname (:name col)
           :colname-fn (get-colname-or-bool-convert col)})
        (remove-autoinc-columns cols)))

(s/defn get-ns-file-path :- s/Str [ns :- s/Str filename :- s/Str src-path :- s/Str]
  (str src-path "/" (str/replace ns #"\." "/") "/" filename))

(s/defn store-content-in-ns :- nil [ns :- s/Str filename :- s/Str src-path :- s/Str content :- s/Str]
  (let [ns-path (str src-path "/" (str/replace ns #"\." "/"))
        ns-file-path (str ns-path "/" filename)]
    (faf/create-if-not-exists ns-path)
    (spit ns-file-path content)))

(s/defn jdbc-uri->classname :- s/Str
  "Maps the jdbc uri to the adapter that clj-liquibase expects"
  [uri :- s/Str]
  (cond
    (.contains uri "h2") "org.h2.Driver"
    (.contains uri "mysql") "com.mysql.jdbc.Driver"
    (.contains uri "sqlite") "org.sqlite.JDBC"
    (.contains uri "postgresql") "org.postgresql.Driver"

    :else (throw (IllegalArgumentException. "Either you did not provide a correct jdbc uri, or the protocol is
    not supported yet."))))

(s/defn store-table-migrations :- nil
  [sql-up :- s/Str ent-name :- s/Str sql-down :- s/Str out-path :- s/Str]
  (comm-faf/create-if-not-exists (io/file out-path))
  (let [time-str (time-fmt/unparse (time-fmt/formatters :basic-date-time-no-ms) (time-core/now))
        out-up-fp (io/file (str out-path "/" ent-name "-" time-str ".up.sql"))
        out-down-fp (io/file (str out-path "/" ent-name "-" time-str ".down.sql"))]
    (spit out-up-fp sql-up)
    (spit out-down-fp sql-down)))
