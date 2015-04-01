(ns leiningen.cli-options
  (:require
    [clojure.string :as string]))

(def cli-options
  [["-f" "--filepath FILEPATH" "Filepath to the entity definition"
    :parse-fn #(str %)]
   ;["-d" "--database DATABASE" "Database key from the joplin configuration"
   ; :parse-fn #(str %)]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["This is my program. There are many like it, but this one is mine."
        ""
        "Usage: program-name [options] action"
        ""
        "Options:"
        options-summary
        ""
        "Actions:"
        "  start    Start a new server"
        "  stop     Stop an existing server"
        "  status   Print a server's status"
        ""
        "Please refer to the manual page for more information."]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))
