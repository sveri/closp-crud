(ns leiningen.helper
  (:require [clj-time.format :as time-fmt]
            [clj-time.core :as time-core]
            [clojure.java.io :as io]
            [de.sveri.clojure.commons.files.faf :as comm-faf]
            [clojure.core.typed :as t]
            [de.sveri.ctanns.clojure-core]
            [de.sveri.ctanns.clj-time])
  (:import (clojure.core.typed.type_rep CountRange)))

(t/ann jdbc-uri->classname [String -> String])
(defn jdbc-uri->classname
  "Maps the jdbc uri to the adapter that clj-liquibase expects"
  [^String uri]
  (cond
    (.contains uri "h2") "org.h2.Driver"
    (.contains uri "mysql") "com.mysql.jdbc.Driver"
    :else (throw (IllegalArgumentException. "Either you did not provide a correct jdbc uri, or the protocol is
    not supported yet."))))

(t/ann store-table-migrations [(t/HVec [String]) String String -> nil])
(defn store-table-migrations [sql-up sql-down out-path]
  (comm-faf/create-if-not-exists (io/file out-path))
  (let [time-str (time-fmt/unparse (time-fmt/formatters :basic-date-time-no-ms) (time-core/now))
        out-up-fp (io/file (str out-path "/" time-str "-up.sql"))
        out-down-fp (io/file (str out-path "/" time-str "-down.sql"))]
    (spit out-up-fp (first sql-up))
    (spit out-down-fp sql-down)))
