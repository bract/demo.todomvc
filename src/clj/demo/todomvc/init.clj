;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns demo.todomvc.init
  (:require
    [cambium.codec :as codec]
    [logback-bundle.json.flat-layout :as flat]
    [org.httpkit.server :as server]
    [demo.todomvc.config :as config]
    [cumulus.core        :as cumulus]
    [clj-dbcp.core       :as dbcp]
    [demo.todomvc.web    :as web]
    [demo.todomvc.util   :as util]))


(defn log-init
  [context]
  (flat/set-decoder! codec/destringify-val)
  context)


(defn db-conn-init
  [context]
  ;; initialize DB connection pool
  ;; ideally handled with dependency injection, but we use var patching here as it is easy to understand for beginners
  (let [data-source (->> (config/ctx-config context)         ; extract config from the context
                      config/database-name                   ; extract database name from config
                      (array-map :target :filesys :database) ; create option map for cumulus
                      (cumulus/jdbc-params :h2)              ; create JDBC params for DBCP
                      dbcp/make-datasource)]
    (->> data-source
      constantly                     ; turn it into a function for alter-var-root
      (alter-var-root #'config/db))
    ;; return JDBC data source in the context; liquibase script needs it
    (assoc context (key config/data-source) data-source)))


(defn ring-init
  [context]
  ;; store JS minification flag and corresponding patched HTML in vars
  ;; ideally handled with dependency injection, but we use var patching here as it is easy to understand for beginners
  (when-let [minify-js? (->> (config/ctx-config context)
                          config/render-minjs?)]
    (alter-var-root #'config/minify-js? (constantly true))
    (alter-var-root #'config/index-html (-> (util/read-index-html)
                                          (.replace "app.js" "app.min.js")
                                          constantly)))
  ;; return ring handler in the context
  (assoc context (key config/ring-handler) web/handler))


(defn start-server
  [context]
  (let [handler (config/ring-handler context)  ; get ring handler from the context
        stopper (->> (config/ctx-config context)
                  config/http-kit-opts
                  (server/run-server handler))]
    (assoc context (key config/ctx-stopper) stopper)))
