(ns leiningen.helper)

(defn jdbc-uri->classname
  "Maps the jdbc uri to the adapter that clj-liquibase expects"
  [uri]
  (cond
    (.contains uri "h2") "org.h2.Driver"
    :else (throw (IllegalArgumentException. "Either you did not provide a correct jdbc uri, or the protocol is
    not supported yet."))))

(defn project-map->jdbc-uri [m]
  (get-in m [:closp-crud :jdbc-url]))
