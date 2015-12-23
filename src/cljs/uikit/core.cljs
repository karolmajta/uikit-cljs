(ns uikit.core
  (:require [reagent.core :as r]
            [uikit.config :as config]
            [uikit.components.accordion :refer [accordion]])
  (:require-macros [devcards.core :refer [defcard defcard-doc]]))


(defcard-doc
   "`accordion` component on it's own is just a very simple and opaque container for
    other reagent components. It accepts a single map with keys:

    - `:data` - a list of accordion children (discussed below)
    - `:header-el` - a symbol defining container element for headers of accordion items (defaults to `:div`)
    - `:container-el` - a symbol defining container element for contents of accordion items (defaults to `:div`)
    - `:on-click` - handler function that gets called with single element from `:data` when appropriate
                    header is clicked

    `:data` should be a list of maps containing `:header` `:content` and `:collapsed` keys (all of which
    are actually optional and have sane defaults).

    - `:collapsed` is a boolean deciding if content for this single data entry should be collapsed or not
      (defaults to `true`)
    - `:header` will be contents of the header container for this data entry. It can be any proper sablono
      form or reagent component.
    - `:content` will be contents of the content container for this data entry. It can be any proper sablono
      form or reagent component.
    - `:header-transition` defines transition of header for this data entry (optional)
    - `:content-transition` defines transition of content for this data entry (optional)

    Of course user is allowed to add any keys of his own, that would for example allow him to properly
    identify what was clicked (since `:content`, `:header`, `:collapsed` triple is not necessarily unique).
    I use extra key `:id` throughout examples. We will also see how to use custom keys to our benefit."
   )

(def static-accordion-simple
  (let [accordion-state [{:id 1 :header "Header1" :content "Content1" :collapsed false}
                         {:id 2 :header "Header2" :content "Content2" :collapsed false}
                         {:id 3 :header "Header3" :content "Content3" :collapsed true}
                         {:id 4 :header "Header4" :content "Content4" :collapsed false}]]
    [accordion {:data accordion-state
                :on-click #(js/alert (.toString %))}]))

(defcard static-accordion-simple-example
 "A static accordion rendered like this:

 ```clojure
 (defn static-accordion-simple []
   (let [accordion-state [{:id 1 :header \"Header1\" :content \"Content1\" :collapsed false}
                          {:id 2 :header \"Header2\" :content \"Content2\" :collapsed false}
                          {:id 3 :header \"Header3\" :content \"Content3\" :collapsed true}
                          {:id 4 :header \"Header4\" :content \"Content4\" :collapsed false}]]
     [accordion {:data accordion-state
                 :on-click #(js/alert (.toString %))}]))
 ```

 will become a component like below (click on the header bar to see what `:on-click` gets called with):
 "
 (r/as-element static-accordion-simple))

(def accordion-sablono-children
  (let [h1 [:span "Example of " [:em "emphasis"] ", contains some paragraphs"]
        h2 [:span "Example of " [:strong "strong"] ", contains some other stuff"]
        c1 [:div [:p "First paragraph..."] [:p "second paragraph"] [:p "Third paragraph..."]]
        c2 [:div [:h1 "A header..."] [:hr] [:p "And some content..."]]
        initial-accordion-state {1 {:id 1 :header h1 :content c1 :collapsed false}
                                 2 {:id 2 :header h2 :content c2 :collapsed true}}
        accordion-state (r/atom initial-accordion-state)
        handle-click (fn [datum] (swap! accordion-state update-in [(:id datum) :collapsed] not))]
    (fn []
      (let [accordion-data (vals @accordion-state)]
        [accordion {:data accordion-data
                    :on-click handle-click}]))))

(defcard accordion-sablono-children-example
         "Of course accordion `:header` and `:content` can be any valid sablono.

         Also, please note how `accordion` itself is an absolutely static thing. It does not track any
         state, nor makes decision about it's behavior, however it's trivial for the user implement
         this behaviors of his own choice. What we did here emulates `{collapse: false}` option as described
         in [UIKit accordion docs](http://getuikit.com/docs/accordion.html)


         ```clojure
         (def accordion-sablono-children
           (let [h1 [:span \"Example of \" [:em \"emphasis\"] \", contains some paragraphs\"]
                 h2 [:span \"Example of \" [:strong \"strong\"] \", contains some other stuff\"]
                 c1 [:div [:p \"First paragraph...\"] [:p \"second paragraph\"] [:p \"Third paragraph...\"]]
                 c2 [:div [:h1 \"A header...\"] [:hr] [:p \"And some content...\"]]
                 initial-accordion-state {1 {:id 1 :header h1 :content c1 :collapsed false}
                                          2 {:id 2 :header h2 :content c2 :collapsed true}}
                 accordion-state (r/atom initial-accordion-state)
                 handle-click (fn [datum] (swap! accordion-state update-in [(:id datum) :collapsed] not))]
             (fn []
               (let [accordion-data (vals @accordion-state)]
                 [accordion {:data accordion-data
                             :on-click handle-click}]))))
         ```

         will become a component like below (click on the header bar to see what `:on-click` gets called with):
         "
         (r/as-element [accordion-sablono-children]))

(defcard-doc
  "I think it's a good idea to keep `accordion` that simple and dumb, and leave all of the
   configuration to the user because it allows reuse in multiple use cases. Now let's see how we can
   use reagent components as accordion children, and how we can implement what would emulate
   `{collapse: true}` in UIKit's accordion.")

(defn counter
  [{:keys [on-click] :or {on-click #()}}]
  (let [internal-value (r/atom 0)
        handle-click #(do (on-click) (swap! internal-value inc))]
    (fn [{:keys [value]}]
      [:div
        [:div
          "This counter increments some external state: "
          value
          " "
          [:button {:on-click handle-click} "Increment!"]]
        [:div
          "But also tracks some internal state after mounting. Number of clicks since mount: "
          @internal-value]])))

(def accordion-reagent-children
  (let [counters-state (r/atom {1 0 2 0})
        initial-accordion-state {1 {:id 1 :header "Counter 1" :collapsed true}
                                 2 {:id 2 :header "Counter 2" :collapsed false}}
        accordion-state (r/atom initial-accordion-state)
        collapse-all (fn [data] (into {} (map (fn [[k v]] (vector k (assoc v :collapsed true)))) data))
        collapse-all-except-one (fn [data excluded] (-> data collapse-all (assoc-in [(:id excluded) :collapsed] false)))
        handle-click (fn [datum] (swap! accordion-state collapse-all-except-one datum))]
    (fn []
      (let [counters [[counter {:value (get @counters-state 1) :on-click #(swap! counters-state update 1 inc)}]
                      [counter {:value (get @counters-state 2) :on-click #(swap! counters-state update 2 inc)}]]
            incomplete-accordion-data (vals @accordion-state)
            accordion-data (map #(assoc %1 :content %2) incomplete-accordion-data counters)]
        [accordion {:data accordion-data
                    :on-click handle-click}]))))

(defcard accordion-reagent-children-example
         "This takes some configuration, but allows us to treat `accordion` as an *opaque* container
         so, basically we can \"pass down\" any components we want, and they will get rendered as
         accordion children.

         ```clojure
         (def accordion-reagent-children
           (let [counters-state (r/atom {1 0 2 0})
                 initial-accordion-state {1 {:id 1 :header \"Counter 1\" :collapsed true}
                                          2 {:id 2 :header \"Counter 2\" :collapsed false}}
                 accordion-state (r/atom initial-accordion-state)
                 collapse-all (fn [data] (into {} (map (fn [[k v]] (vector k (assoc v :collapsed true)))) data))
                 collapse-all-except-one (fn [data excluded] (-> data collapse-all (assoc-in [(:id excluded) :collapsed] false)))
                 handle-click (fn [datum] (swap! accordion-state collapse-all-except-one datum))]
             (fn []
               (let [counters [[counter {:value (get @counters-state 1) :on-click #(swap! counters-state update 1 inc)}]
                               [counter {:value (get @counters-state 2) :on-click #(swap! counters-state update 2 inc)}]]
                     incomplete-accordion-data (vals @accordion-state)
                     accordion-data (map #(assoc %1 :content %2) incomplete-accordion-data counters)]
                 [accordion {:data accordion-data
                             :on-click handle-click}]))))
         ```

         will become a component like below (click on the header bar to see what `:on-click` gets called with):
         "
         (r/as-element [accordion-reagent-children]))

(defcard-doc
  "Ok, so now let's try something that would be impossible with UIKit (or at least very hard).
   We will have one component that is *important* and when clicked will open/close all others,
   and all others will just toggle themselves. This would be hard to get in plain js UIKit,
   since the `collapse` option in configuration object always relates to all accordion children.")

(def accordion-important-child
  (let [h1 [:span "I am " [:em "important"] ", when I'm clicked I collapse/uncollapse all others"]
        initial-accordion-state {1 {:id 1 :header h1 :content "Content 1" :collapsed false :important true}
                                 2 {:id 2 :header "Header 2" :content "Content 2" :collapsed false}
                                 3 {:id 3 :header "Header 3" :content "Content 3" :collapsed false}}
        accordion-state (r/atom initial-accordion-state)
        collapse-all (fn [data] (into {} (map (fn [[k v]] (vector k (assoc v :collapsed true)))) data))
        uncollapse-all (fn [data] (into {} (map (fn [[k v]] (vector k (assoc v :collapsed false)))) data))
        toggle-all-except-one (fn [data important] (if (:collapsed important)
                                                       (uncollapse-all data)
                                                       (collapse-all data)))
        toggle-unimportant (fn [data datum] (update-in data [(:id datum) :collapsed] not))
        toggle (fn [data datum] (if (:important datum)
                                    (toggle-all-except-one data datum)
                                    (toggle-unimportant data datum)))
        handle-click (fn [datum] (swap! accordion-state toggle datum))]
    (fn []
      (let [accordion-data (vals @accordion-state)]
        [accordion {:data accordion-data
                    :on-click handle-click}]))))

(defcard accordion-imporant-child-example
  "Again this also required some upfront configuration...

  ```clojure
  (def accordion-important-child
    (let [h1 [:span \"I am \" [:em \"important\"] \", when I'm clicked I collapse/uncollapse all others\"]
          initial-accordion-state {1 {:id 1 :header h1 :content \"Content 1\" :collapsed false :important true}
                                   2 {:id 2 :header \"Header 2\" :content \"Content 2\" :collapsed true}
                                   3 {:id 3 :header \"Header 3\" :content \"Content 3\" :collapsed true}}
          accordion-state (r/atom initial-accordion-state)
          collapse-all (fn [data] (into {} (map (fn [[k v]] (vector k (assoc v :collapsed true)))) data))
          uncollapse-all (fn [data] (into {} (map (fn [[k v]] (vector k (assoc v :collapsed false)))) data))
          toggle-all-except-one (fn [data important] (if (:collapsed important)
                                                         (uncollapse-all data)
                                                         (collapse-all data)))
          toggle-unimportant (fn [data datum] (update-in data [(:id datum) :collapsed] not))
          toggle (fn [data datum] (if (:important datum)
                                      (toggle-all-except-one data datum)
                                      (toggle-unimportant data datum)))
          handle-click (fn [datum] (swap! accordion-state toggle datum))]
      (fn []
        (let [accordion-data (vals @accordion-state)]
          [accordion {:data accordion-data
                      :on-click handle-click}]))))(
  ```"
  (r/as-element [accordion-important-child]))

(defcard-doc
  "Another thing that would be non-trivial with UIKit is control of the accordion from it's children.
  When `accordion` is a plain simple render function this is trivial, but again - requires some upfront
  configuration")

(def accordion-control-from-children
  (let [initial-accordion-state {1 {:id 1 :header "Header 1" :text "Content 1" :collapsed false :important true}
                                 2 {:id 2 :header "Header 2" :text "Content 2" :collapsed false}
                                 3 {:id 3 :header "Header 3" :text "Content 3" :collapsed false}}
        accordion-state (r/atom initial-accordion-state)
        remove-datum #(swap! accordion-state dissoc (:id %))
        toggle (fn [data datum] (update-in data [(:id datum) :collapsed] not))
        handle-click (fn [datum] (swap! accordion-state toggle datum))]
    (fn []
      (let [incomplete-accordion-data (vals @accordion-state)
            buttons (map (fn [datum] [:button {:on-click #(remove-datum datum)} "remove me"]) incomplete-accordion-data)
            accordion-data (map
                             (fn [datum button] (assoc datum :content [:p (:text datum) " " button]))
                             incomplete-accordion-data
                             buttons)]
        [accordion {:data accordion-data
                    :on-click handle-click}]))))

(defcard accordion-control-from-children-example
   "This time instead of \"passing down\" a reframe component we will just use a sablono
   `button` form to save ourselves some typing...

   ```clojure
   (def accordion-control-from-children
     (let [initial-accordion-state {1 {:id 1 :header \"Header 1\" :text \"Content 1\" :collapsed false :important true}
                                    2 {:id 2 :header \"Header 2\" :text \"Content 2\" :collapsed false}
                                    3 {:id 3 :header \"Header 3\" :text \"Content 3\" :collapsed false}}
           accordion-state (r/atom initial-accordion-state)
           remove-datum #(swap! accordion-state dissoc (:id %))
           toggle (fn [data datum] (update-in data [(:id datum) :collapsed] not))
           handle-click (fn [datum] (swap! accordion-state toggle datum))]
       (fn []
         (let [incomplete-accordion-data (vals @accordion-state)
               buttons (map (fn [datum] [:button {:on-click #(remove-datum datum)} \"remove me\"]) incomplete-accordion-data)
               accordion-data (map
                                (fn [datum button] (assoc datum :content [:p (:text datum) \" \" button]))
                                incomplete-accordion-data
                                buttons)]
           [accordion {:data accordion-data
                       :on-click handle-click}]))))
   ```"

   (r/as-element [accordion-control-from-children]))


(def accordion-css-transition
  (let [h1 "Animating opacity... (foo class)"
        h2 "Animating color... (bar class)"
        c1 [:div [:p "First paragraph..."] [:p "second paragraph"] [:p "Third paragraph..."]]
        c2 [:div [:p "First paragraph..."] [:p "second paragraph"] [:p "Third paragraph..."]]
        initial-accordion-state {1 {:id 1 :header h1 :content c1 :collapsed false :content-transition [:foo 1000]}
                                 2 {:id 2 :header h2 :content c2 :collapsed true :content-transition [:bar 1000]}}
        accordion-state (r/atom initial-accordion-state)
        handle-click (fn [datum] (swap! accordion-state update-in [(:id datum) :collapsed] not))]
    (fn []
      (let [accordion-data (vals @accordion-state)]
        [accordion {:data accordion-data
                    :on-click handle-click}]))))

(defcard accordion-css-transition-example
  "Using transitions is quite nice with react's CSSTransitionGroup. Please note
  that by binding transition data under `:content-transition` to accordion entries
  rather than accordion itself it is possible to provide quite complex custom behaviors.
  For example we can use different transitions for different accordion entries.
  This would be not possible with JS version of UIKit, since there we could only define
  a single `animate` property for all of accordion elements.
  
  ```clojure
  (def accordion-css-transition
    (let [h1 \"Animating opacity... (foo class)\"
          h2 \"Animating color... (bar class)\"
          c1 [:div [:p \"First paragraph...\"] [:p \"second paragraph\"] [:p \"Third paragraph...\"]]
          c2 [:div [:p \"First paragraph...\"] [:p \"second paragraph\"] [:p \"Third paragraph...\"]]
          initial-accordion-state {1 {:id 1 :header h1 :content c1 :collapsed false :content-transition [:foo 1000]}
                                   2 {:id 2 :header h2 :content c2 :collapsed true :content-transition [:bar 1000]}}
          accordion-state (r/atom initial-accordion-state)
          handle-click (fn [datum] (swap! accordion-state update-in [(:id datum) :collapsed] not))]
      (fn []
        (let [accordion-data (vals @accordion-state)]
          [accordion {:data accordion-data
                      :on-click handle-click}]))))
  ```"
  (r/as-element [accordion-css-transition]))