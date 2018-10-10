;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns demo.todomvc.global
  (:require
    [cambium.core :as log]
    [keypin.core :as keypin]))


(defonce runtime (atom {}))


(keypin/defkey
  {:source runtime}
  dbconnpool [:dbconnpool some?    "DB connection pool"]
  minify-js? [:minify-js? boolean? "Whether minify JS?"]
  index-html [:index-html string?  "HTML for the index page"])


(log/deflogger metrics "METRICS")
