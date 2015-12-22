(ns uikit.components.accordion
  (:require [uikit.utils :refer [with-classes]]))


(defn- accordion-title
  [{:keys [content el on-click] :or {el :div}}]
  (let [el (with-classes el [:uk-accordion-title])
        attrs {:on-click on-click}]
    [el attrs content]))


(defn- accordion-content
  [{:keys [content el] :or {el :div}}]
  (let [el (with-classes el [:uk-accordion-content])]
    [el content]))


(defn accordion [{:keys [data header-el content-el on-click] :or {header-el :div  content-el :div}}]
  (let [accordion-container :div.uk-accordion
        title-els (map #(accordion-title {:content (:header % "") :on-click (partial on-click %)}) data)
        content-els (map #(when-not (:collapsed % false) (accordion-content {:content (:content % "")})) data)
        children (map-indexed #(with-meta %2 {:key %1}) (interleave title-els content-els)) ]
    [accordion-container children]))
