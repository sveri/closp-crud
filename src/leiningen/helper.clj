(ns leiningen.helper)

(defn project-map->jdbc-uri [m database]
  (get-in m [:profiles :dev :joplin :databases database :url]))
