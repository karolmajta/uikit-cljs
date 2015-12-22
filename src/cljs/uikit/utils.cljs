(ns uikit.utils)

(defn with-classes
  [el classes]
  (let [classes-as-strs (map name classes)
        el-as-str (name el)
        suffix (clojure.string/join "." classes-as-strs)]
    (keyword (str el-as-str "." suffix))))