;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns demo.todomvc.db
  (:require
    [asphalt.core        :as asphalt]
    [demo.todomvc.config :as config]
    [demo.todomvc.entity :as entity]
    [demo.todomvc.util   :as util]))


(asphalt/defsql sql-insert-item     "INSERT INTO todos (todo_id, content) VALUES ($id, $content)")


(asphalt/defsql sql-find-all        "SELECT todo_id, content, complete, created, updated FROM todos")


(asphalt/defsql sql-update-content  "UPDATE todos SET content = $content, updated = now() WHERE todo_id = $id")


(asphalt/defsql sql-update-complete "UPDATE todos SET complete = $complete, updated = now() WHERE todo_id = $id")


(asphalt/defsql sql-toggle-complete "UPDATE todos SET complete = $complete, updated = now()")


(asphalt/defsql sql-delete-item     "DELETE FROM todos WHERE todo_id = $id")


(asphalt/defsql sql-delete-complete "DELETE FROM todos WHERE complete = true")


(defn insert-item
  ([content]
    (insert-item (util/uuid) content))
  ([id content]
    (sql-insert-item config/db {:id id :content content})))


(defn find-all
  []
  (->> (sql-find-all config/db [])
    (mapv #(apply entity/->Todo %))))


(defn update-content
  [id content]
  (sql-update-content config/db {:id id :content content}))


(defn update-complete
  [id complete?]
  (sql-update-complete config/db {:id id :complete complete?}))


(defn toggle-complete
  [complete?]
  (sql-toggle-complete config/db {:complete complete?}))


(defn delete-item
  [id]
  (sql-delete-item config/db {:id id}))


(defn delete-complete
  []
  (sql-delete-complete config/db []))
