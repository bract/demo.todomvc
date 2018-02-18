;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns demo.todomvc.init
  (:require
    [clojure.edn         :as edn]
    [clojure.java.io     :as io]
    [bract.core.keydef   :as core-kdef]
    [cambium.codec       :as codec]
    [clostache.parser    :as clostache]
    [cambium.logback.json.flat-layout :as flat]
    [org.httpkit.server  :as server]
    [demo.todomvc.keydef :as kdef]
    [demo.todomvc.global :as global]
    [cumulus.core        :as cumulus]
    [clj-dbcp.core       :as dbcp]
    [demo.todomvc.web    :as web]))


(defn log-init
  [context]
  (flat/set-decoder! codec/destringify-val)
  context)


(defn info-init
  [context]
  (let [version (-> (io/resource "project.edn")
                  slurp
                  edn/read-string
                  :version)
        rt-info (core-kdef/ctx-runtime-info context)]
    (assoc context (key core-kdef/ctx-runtime-info) (-> rt-info
                                                      vec
                                                      (conj (fn [] {:app-version version}))))))


(defn db-conn-init
  [context]
  ;; initialize DB connection pool
  ;; ideally handled with dependency injection, but we use var patching here as it is easy to understand for beginners
  (let [data-source (->> (kdef/ctx-config context)           ; extract config from the context
                      kdef/database-name                     ; extract database name from config
                      (array-map :target :filesys :database) ; create option map for cumulus
                      (cumulus/jdbc-params :h2)              ; create JDBC params for DBCP
                      dbcp/make-datasource)]
    (->> data-source
      constantly                     ; turn it into a function for alter-var-root
      (alter-var-root #'global/db))
    ;; return JDBC data source in the context; liquibase script needs it
    (assoc context (key kdef/data-source) data-source)))


(defn ring-init
  [context]
  ;; store JS minification flag and corresponding patched HTML in vars
  ;; ideally handled with dependency injection, but we use var patching here as it is easy to understand for beginners
  (when-let [minify-js? (->> (kdef/ctx-config context)
                          kdef/render-minjs?)]
    (alter-var-root #'global/minify-js? (constantly true))
    (alter-var-root #'global/index-html (constantly (web/render-homepage-html true))))
  ;; return ring handler in the context
  (assoc context (key kdef/ring-handler) web/handler))


(defn start-server
  [context]
  (let [handler (kdef/ring-handler context)  ; get ring handler from the context
        stopper (->> (kdef/ctx-config context)
                  kdef/http-kit-opts
                  (server/run-server handler))]
    (assoc context (key kdef/ctx-stopper) stopper)))
