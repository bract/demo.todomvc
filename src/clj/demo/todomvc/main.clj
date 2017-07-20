;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns demo.todomvc.main
  (:require
    [clojure.edn       :as edn]
    [clojure.java.io   :as io]
    [bract.cli.keydef  :as cli-kdef]
    [bract.cli.main    :as cli-main]
    [bract.core.keydef :as core-kdef])
  (:gen-class))


(defn print-version
  "Print application version from the project.edn file in classpath."
  [context]
  (-> (io/resource "project.edn")
    slurp
    edn/read-string
    :version
    println)
  context)


(defn -main
  "This function becomes the Java main() method entry point."
  [& args]
  (cli-main/trigger {(key core-kdef/ctx-cli-args)    (vec args)
                     (key cli-kdef/ctx-app-commands) (assoc cli-kdef/default-commands
                                                       "version" {:doc "Print application version"
                                                                  :handler #'print-version})}))
