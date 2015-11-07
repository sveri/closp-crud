(ns leiningen.prettyprint
  (:import (java.io StringWriter StringReader)
           (org.w3c.tidy Tidy))
  (:gen-class))

(defn configure-pretty-printer
  "Configure the pretty-printer (an instance of a JTidy Tidy class) to
generate output the way we want -- formatted and without sending warnings.
Return the configured pretty-printer."
  []
  (doto (new Tidy)
    (.setSmartIndent true)
    (.setTrimEmptyElements true)
    (.setShowWarnings false)
    (.setQuiet true)))

(defn pretty-print-html
  "Pretty-print the html and return it as a string."
  [html]
  (let [swrtr (new StringWriter)]
    (.parse (configure-pretty-printer) (new StringReader (str html)) swrtr)
    (str swrtr)))
