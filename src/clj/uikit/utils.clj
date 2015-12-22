(ns uikit.utils)

(defmacro source
  [fn]
  (with-out-str
    (clojure.repl/source fn)))

