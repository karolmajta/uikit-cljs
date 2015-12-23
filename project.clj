(defproject uikit "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [devcards "0.2.1" :exclusions [cljsjs/react cljsjs/react-dom sablono]]
                 [sablono "0.5.3" :exclusions [cljsjs/react cljsjs/react-dom cljsjs/react-dom-server]]
                 [reagent "0.5.1" :exclusions [cljsjs/react cljsjs/react-dom cljsjs/react-dom-server]]
                 [cljsjs/react-dom "0.14.3-1" :exclusions [cljsjs/react]]
                 [cljsjs/react-dom-server "0.14.3-0" :exclusions [cljsjs/react]]
                 [cljsjs/react-with-addons "0.14.3-0"]]

  :plugins [[lein-cljsbuild "1.1.1"]
            [lein-figwheel "0.5.0-1"]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"]

  :source-paths ["src/cljs"
                 "src/clj"]

  :cljsbuild {
              :builds [{:id "devcards"
                        :source-paths ["src"]
                        :figwheel {:devcards true } ;; <- note this
                        :compiler {:main "uikit.core"
                                   :asset-path "js/compiled/out"
                                   :output-to  "resources/public/js/compiled/app.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :source-map-timestamp true }}]}

  :figwheel { :css-dirs ["resources/public/css"] })

