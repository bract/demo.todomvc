(defproject bract/demo.todomvc "0.6.2-0.2.0-SNAPSHOT"
  :description "Demo TodoMVC app using Clojure, ClojureScript and the Bract/Gossamer framework"
  :url "https://github.com/bract/demo.todomvc"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :resource-paths ["resources" "target/generated/resources"]  ; see :project-edn entry
  :source-paths ["src/clj"]
  :test-paths   ["test/clj"]
  :pedantic?    :warn
  :dependencies [[org.clojure/clojure "1.10.2"]
                 ;; ----- ClojureScript -----
                 [org.clojure/clojurescript "1.10.758" :exclusions [com.google.code.findbugs/jsr305]]
                 [cljs-ajax "0.7.3" :exclusions [com.fasterxml.jackson.dataformat/jackson-dataformat-smile
                                                 com.fasterxml.jackson.core/jackson-core
                                                 cheshire]]  ; for making AJAX calls from within the browser
                 [hiccups   "0.3.0"]  ; for dynamically generating HTML
                 ;; ----- server-side web -----
                 [bract/gossamer.core "0.6.1-0.2.0"]
                 [ring/ring-core      "1.8.2" :exclusions [commons-codec]]
                 [cljstache           "2.0.1"]  ; mustache templates
                 ;; ----- web servers (uncomment any one) -----
                 ;;[aleph                   "0.4.6" :exclusions [org.clojure/tools.logging]]
                 [http-kit                "2.5.1"]
                 ;;[org.immutant/immutant   "2.1.10"]
                 ;;[ring/ring-jetty-adapter "1.8.2"]
                 ;; ------ database -----
                 [com.h2database/h2 "1.4.196"]  ; the embedded H2 database
                 [cumulus  "0.1.2"]  ; for easily deriving JDBC connection params
                 [clj-dbcp "0.9.0"]  ; for making database connection pool
                 [asphalt  "0.6.5"]  ; for reading/writing databases using JDBC
                 ]
  :target-path "target/%s"
  :plugins [[lein-cljsbuild   "1.1.8" :exclusions [org.clojure/clojure]]
            [lein-figwheel    "0.5.20"]
            [lein-project-edn "0.2.0"]
            [lein-ring        "0.12.5"]]
  :hooks [leiningen.project-edn/activate]
  :clean-targets ^{:protect false} ["resources/public/js/out"
                                    "resources/public/js/app.js"
                                    "resources/public/js/app.min.js"
                                    :target-path]
  :project-edn {:output-file "target/generated/resources/project.edn"
                :verify-edn? true}
  :ring {:handler bract.ring.dev/handler
         :init    bract.ring.dev/init!
         :port    3000
         :nrepl   {:start? true :port 3001}}
  :profiles {:dev     {:dependencies [[bract/bract.dev "0.6.1-0.1.0"]
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
             :uberjar {:aot [bract.core.main]
                       :main ^:skip-aot bract.core.main
                       :hooks [leiningen.cljsbuild]
                       :cljsbuild {:builds [{:id "prod"
                                             :source-paths ["src/cljs"]
                                             :compiler {:output-to  "resources/public/js/app.min.js"
                                                        :output-dir "target/js/out"
                                                        :pretty-print  false
                                                        :optimizations :advanced}}]}
                       :pedantic? :abort}}
  :aliases {"liquibase"  ["run" "-m" "liquibase"]})

