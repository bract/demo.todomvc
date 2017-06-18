;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns liquibase
  (:require
    [clj-liquibase.cli   :as cli]
    [clj-liquibase.core  :refer [defparser]]
    [bract.core.dev      :as core-dev]
    [bract.core.inducer  :as core-inducer]
    [demo.todomvc.config :as config]
    [demo.todomvc.init   :as init]))


(defparser changelog "liquibase/changelog.edn")


(defn -main
  [& [cmd & args]]
  (let [data-source (-> core-dev/default-root-context
                      (core-inducer/induce [core-inducer/set-verbosity   ; set default verbosity
                                            core-inducer/read-config     ; read config file(s) and populate context
                                            init/db-conn-init            ; populate JDBC data-source
                                            ])
                      config/data-source)]
    (apply cli/entry cmd {:datasource data-source :changelog changelog} args)))
