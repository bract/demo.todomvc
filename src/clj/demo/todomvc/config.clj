;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns demo.todomvc.config
  (:require
    [clojure.java.io :as io]
    [keypin.core  :as keypin]
    [keypin.util  :as kputil]
    [cambium.core :as log]
    [demo.todomvc.util :as util]))


(keypin/defkey
  ctx-config   [:bract.core/config]
  ctx-stopper  [:bract.core/stopper]
  ring-handler [:bract.ring/ring-handler]
  data-source  [:app/data-source])


(keypin/defkey
  database-name ["database.name"  string?      "Database name"]
  http-kit-opts ["http-kit.opts"  map?         "Options for HTTP-Kit"]
  render-minjs? ["minify.js.file" kputil/bool? "Whether render minified JavaScript file"])


(def ^:redef db nil)


(def ^:redef minify-js? false)


(def ^:redef index-html nil)


(log/deflogger metrics "METRICS")
