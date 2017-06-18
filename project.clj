(defproject bract/demo.todomvc "0.3.0-SNAPSHOT"
  :description "Demo TodoMVC app using Clojure, ClojureScript and the Bract framework"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/clj"]
  :test-paths   ["test/clj"]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [bract/bract.cli     "0.3.0"]
                 ;; ClojureScript
                 [org.clojure/clojurescript "1.9.562"]
                 [cljs-ajax "0.6.0"]         ; for making AJAX calls from within the browser
                 ;; server-side web
                 [bract/bract.ring "0.3.0"]  ; Ring support for Bract
                 [calfpath         "0.4.0"]  ; server side web routing
                 [http-kit         "2.2.0"]  ; web server
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
  :plugins [[lein-cljsbuild "1.1.6"]
            [lein-ring      "0.12.0"]]
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljs"]
                        :compiler {:output-to  "resources/public/js/app.js"
                                   :output-dir "resources/public/js/out"
                                   :pretty-print  true
                                   :optimizations :none
                                   :source-map    true}}
                       {:id "prod"
                        :source-paths ["src/cljs"]
                        :compiler {:output-to "resources/public/js/app.min.js"
                                   :pretty-print  false
                                   :optimizations :advanced
                                   :source-map    false}}]}
  :ring {:handler bract.ring.dev/handler
         :init    bract.ring.dev/init!
         :port    3000
         :nrepl   {:start? true :port 3001}}
  :profiles {:dev {:dependencies [[bract/bract.dev "0.3.0"]
                                  [clj-liquibase   "0.6.0"]]
                   :source-paths ["dev"]}
             :uberjar {:aot [bract.cli.main]
                       :main ^:skip-aot bract.cli.main}}
  :aliases {"liquibase"  ["run" "-m" "liquibase"]
            "db:migrate" ["run" "-m" "liquibase" "update"]})
