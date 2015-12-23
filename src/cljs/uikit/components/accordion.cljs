(ns uikit.components.accordion
  (:require [reagent.core :as r]
            [uikit.utils :refer [with-classes]]))

(def css-transition-group
  (r/adapt-react-class js/React.addons.CSSTransitionGroup))

(defn- transition-attrs [transition]
  (let [[transition-name timeout] transition]
    {:transition-name transition-name
     :transition-appear (get transition 2 false)
     :transition-appear-timeout timeout
     :transition-enter-timeout timeout
     :transition-leave-timeout timeout}))

(defn- accordion-header
  [{:keys [header header-el on-click] :or {header-el :div}}]
  (let [el (with-classes header-el [:uk-accordion-title])
        attrs {:on-click on-click}]
    [el attrs header]))


(defn- accordion-content
  [{:keys [content content-el collapsed] :or {content-el :div}}]
  (let [el (with-classes content-el [:uk-accordion-content])]
    (when-not collapsed
      [el content])))

(defn- wrapped-accordion-header [d]
  (let [transition (:header-transition d)
        inner-element (accordion-header d)]
    (if-not transition
      inner-element
      [css-transition-group (transition-attrs transition)
       inner-element])))

(defn- wrapped-accordion-content [d]
  (let [transition (:content-transition d)
        inner-element (accordion-content d)]
    (if-not transition
      inner-element
      [css-transition-group (transition-attrs transition)
        inner-element])))


(defn accordion [{:keys [data
                         header-el
                         content-el
                         on-click] :or {header-el :div  content-el :div}}]
  (let [accordion-container :div.uk-accordion
        header-els (map #(wrapped-accordion-header (merge % {:on-click (partial on-click %)})) data)
        content-els (map wrapped-accordion-content data)
        children (map-indexed #(with-meta %2 {:key %1}) (interleave header-els content-els)) ]
    [accordion-container children]))