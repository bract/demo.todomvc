(defproject bract/demo.todomvc "0.3.1-SNAPSHOT"
  :description "Demo TodoMVC app using Clojure, ClojureScript and the Bract framework"
  :url "https://github.com/bract/demo.todomvc"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/clj"]
  :test-paths   ["test/clj"]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [bract/bract.cli     "0.3.1-SNAPSHOT"]
                 ;; ClojureScript
                 [org.clojure/clojurescript "1.9.660"]
                 [cljs-ajax "0.6.0"]         ; for making AJAX calls from within the browser
                 [hiccups   "0.3.0"]         ; for dynamically generating HTML
                 ;; server-side web
                 [ring/ring-core                  "1.6.1"]
                 [bract/bract.ring                "0.3.1-SNAPSHOT"]  ; Ring support for Bract
                 [calfpath                        "0.4.0"]  ; server side web routing
                 [de.ubercode.clostache/clostache "1.4.0" :exclusions [org.clojure/clojure]]  ; Mustache templates
                 [http-kit                        "2.2.0"]  ; web server
                 ;; logging
                 [cambium/cambium.core           "0.9.0"]  ; for logs as data (builds on clojure/tools.logging)
                 [cambium/cambium.codec-cheshire "0.9.0"]  ; a JSON based codec for logs as data
                 [logback-bundle/json-bundle     "0.3.0"]  ; a JSON based backing implementation
                 ;; database
                 [com.h2database/h2 "1.4.196"]  ; the embedded H2 database
                 [cumulus  "0.1.2"]  ; for easily deriving JDBC connection params
                 [clj-dbcp "0.9.0"]  ; for making database connection pool
                 [asphalt  "0.6.0"]  ; for reading/writing databases using JDBC
                 ]
  :target-path "target/%s"
  :plugins [[lein-cljsbuild "1.1.6" :exclusions [org.clojure/clojure]]
            [lein-figwheel  "0.5.10"]
            [lein-ring      "0.12.0"]]
  :clean-targets ^{:protect false} ["resources/public/js/out"
                                    "resources/public/js/app.js"
                                    "resources/public/js/app.min.js"
                                    :target-path]
  :ring {:handler bract.ring.dev/handler
         :init    bract.ring.dev/init!
         :port    3000
         :nrepl   {:start? true :port 3001}}
  :profiles {:dev     {:dependencies [[bract/bract.dev "0.3.1-SNAPSHOT"]
                                      [clj-liquibase   "0.6.0"]]
                       :source-paths ["dev"]
                       :cljsbuild {:builds [{:id "dev"
                                             :source-paths ["src/cljs"]
                                             :figwheel {:on-jsload "demo.todomvc.app/main"}
                                             :compiler {:main demo.todomvc.app
                                                        :asset-path "/public/js/out"
                                                        :output-to  "resources/public/js/app.js"
                                                        :output-dir "resources/public/js/out"
                                                        :pretty-print  true
                                                        :optimizations :none
                                                        :source-map    true
                                                        :source-map-timestamp true}}]}}
             :uberjar {:aot [bract.cli.main]
                       :main ^:skip-aot bract.cli.main
                       :hooks [leiningen.cljsbuild]
                       :cljsbuild {:builds [{:id "prod"
                                             :source-paths ["src/cljs"]
                                             :compiler {:output-to  "resources/public/js/app.min.js"
                                                        :output-dir "target/js/out"
                                                        :pretty-print  false
                                                        :optimizations :advanced}}]}}}
  :aliases {"liquibase"  ["run" "-m" "liquibase"]})
