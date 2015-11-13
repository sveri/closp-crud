(ns leiningen.helper
  (:require [clj-time.format :as time-fmt]
            [clj-time.core :as time-core]
            [clojure.java.io :as io]
            [de.sveri.clojure.commons.files.faf :as comm-faf]
            [clojure.core.typed :as t]
            [leiningen.pre-types :as pt]
            [clojure.string :as s]
            [de.sveri.clojure.commons.files.faf :as faf]))


(defn sanitize-filename [filename]
  (clojure.string/replace filename #"-" "_"))

(t/ann filter-id-columns [pt/et-columns -> (t/AVec pt/et-column)])
(defn filter-id-columns [cols]
  (vec (remove (t/fn [col :- pt/et-column] (= :id (first col))) cols)))

(t/ann filter-dataset [pt/entity-description -> pt/entity-description])
(defn filter-dataset [dataset]
  (assoc dataset :columns (filter-id-columns (:columns dataset))))

(t/ann get-colname-or-bool-convert [pt/et-column -> String])
(defn- get-colname-or-bool-convert [col]
  (let [name (name (first col))]
    (if (= :boolean (second col)) (str "(convert-boolean " name ")") name)))

(t/ann ds-columns->template-columns [pt/et-columns -> (t/AVec (t/HMap :mandatory {:colname String}))])
(defn ds-columns->template-columns [cols & [functionize]]
  (mapv (t/fn [col :- pt/et-column]
          {:colname (name (first col))
           :colname-fn (get-colname-or-bool-convert col)})
        (filter-id-columns cols)))

(t/ann get-ns-file-path [String String String -> String])
(defn get-ns-file-path [ns filename src-path]
  (str src-path "/" (s/replace ns #"\." "/") "/" filename))

(t/ann ^:no-check store-content-in-ns [String String String String -> nil])
(defn store-content-in-ns [ns filename src-path content]
  (let [ns-path (str src-path "/" (s/replace ns #"\." "/"))
        ns-file-path (str ns-path "/" filename)]
    (faf/create-if-not-exists ns-path)
    (spit ns-file-path content)))

(t/ann ^:no-check jdbc-uri->classname [String -> String])
(defn jdbc-uri->classname
  "Maps the jdbc uri to the adapter that clj-liquibase expects"
  [^String uri]
  (cond
    (.contains uri "h2") "org.h2.Driver"
    (.contains uri "mysql") "com.mysql.jdbc.Driver"
    (.contains uri "sqlite") "org.sqlite.JDBC"
    (.contains uri "postgresql") "org.postgresql.Driver"

    :else (throw (IllegalArgumentException. "Either you did not provide a correct jdbc uri, or the protocol is
    not supported yet."))))

(t/ann ^:no-check store-table-migrations [(t/HSeq [String]) String String String -> nil])
(defn store-table-migrations [sql-up ent-name sql-down out-path]
  (comm-faf/create-if-not-exists (io/file out-path))
  (let [time-str (time-fmt/unparse (time-fmt/formatters :basic-date-time-no-ms) (time-core/now))
        out-up-fp (io/file (str out-path "/" ent-name "-" time-str ".up.sql"))
        out-down-fp (io/file (str out-path "/" ent-name "-" time-str ".down.sql"))]
    (spit out-up-fp (first sql-up))
    (spit out-down-fp sql-down)))
