;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns demo.todomvc.util
  (:require
    [cambium.core :as log])
  (:import
    [java.util UUID]))


(defn uuid
  ^String
  []
  (.toString (UUID/randomUUID)))


(defn handle-uncaught-exception
  "Callback function for handling uncaught exceptions."
  [^Thread thread ^Throwable ex]
  (log/error (Throwable->map ex) ex (format "Uncaught exception on thread ID: %d, thread name: %s - (%s) %s"
                                      (.getId thread) (.getName thread) (class ex) (.getMessage ex))))
