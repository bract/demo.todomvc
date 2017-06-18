;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns demo.todomvc.web
  (:require
    [clojure.java.io :as io]
    [clojure.string  :as string]
    [calfpath.core   :refer [->uri ->method ->get ->head ->options ->patch ->put ->post ->delete]]
    [cambium.core    :as log]
    [cheshire.core   :as cheshire]
    [ring.util.response  :as rur]
    [demo.todomvc.config :as config]
    [demo.todomvc.db     :as db]
    [demo.todomvc.util   :as util]))


(defn json-200
  ([]
    (json-200 {:status "success"}))
  ([data]
    (config/metrics "metrics.web.200")
    {:status 200
     :headers {"Content-type" "application/json"}
     :body (cheshire/generate-string data)}))


(defn id-404 [id]
  (config/metrics {:id id} "metrics.web.404")
  {:status 404
   :headers {"Content-type" "text/plain"}
   :body (format "Todo ID '%s' not found" id)})


(defn list-todos []
  (log/with-logging-context {:endpoint "list-todos"}
    (json-200 (db/find-all))))


(defn add-todo [content]
  (log/with-logging-context {:endpoint "add-todo"}
    (db/insert-item content)
    ;; for a POST request ideally we should return HTTP 201, but use HTTP 200 for now
    (json-200)))


(defn delete-todo [id]
  (log/with-logging-context {:endpoint "delete-todo"}
    (if (pos? (db/delete-item id))
      (json-200)
      (id-404 id))))


(defn update-content [id content]
  (log/with-logging-context {:endpoint "update-content"}
    (if (pos? (db/update-content id content))
      (json-200)
      (id-404 id))))


(defn update-complete [id complete?]
  (log/with-logging-context {:endpoint "update-complete"}
    (if (pos? (db/update-complete id complete?))
      (json-200)
      (id-404 id))))


(defn render-home []
  (log/with-logging-context {:endpoint "home"}
    (config/metrics "metrics.web.200")
    {:status 200
     :headers {"Content-type" "text/html"}
     :body (if config/minify-js?
             config/index-html
             (util/read-index-html))}))


(defn handler
  [request]
  (->uri request
    "/todos/"              []   (->method request
                                  :get  (list-todos)
                                  :post (add-todo (slurp (:body request))))
    "/todos/:id/"          [id] (->delete request (delete-todo id))
    "/todos/:id/content/"  [id] (->put request (update-content  id (slurp (:body request))))
    "/todos/:id/complete/" [id] (->put request (update-complete id (= "true" (string/lower-case
                                                                               (slurp (:body request))))))
    "/public/*"            []   (->get request
                                  (if-let [response (rur/resource-response (subs (:uri request) 1))]
                                    (do (config/metrics "metrics.web.200") response)
                                    (do (config/metrics {:uri (:uri request)} "metrics.web.404")
                                      {:status 404 :body "Not found"})))
    "/favicon.ico"         []   {:status 302 :headers {"Location" "/public/favicon.ico"}}
    "/"                    []   (->get request (render-home))))
