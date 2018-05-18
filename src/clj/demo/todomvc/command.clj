;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns demo.todomvc.command
  (:require
    [bract.core.keydef   :as core-kdef]
    [demo.todomvc.db     :as db]
    [demo.todomvc.keydef :as kdef]))


(defn command-print-version
  "CLI command - print application version."
  [context]
  (let [config (core-kdef/ctx-config context)
        version (kdef/app-version config)]
    (println version))
  (reduced context))


(defn launcher-purge-deleted
  [context]
  (db/purge-deleted)
  (reduced context))
