;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns demo.todomvc.db-test
  (:require
    [clojure.test           :refer :all]
    [asphalt.core           :as asphalt]
    [demo.todomvc.test-util :as tu]
    [demo.todomvc.global    :as global]
    [demo.todomvc.db        :as db]
    [demo.todomvc.util      :as util]))


(asphalt/defsql sql-wipe-todos    "DELETE FROM todos")
(asphalt/defsql sql-count-todos   "SELECT COUNT(*) FROM todos"                     {:make-conn-worker
                                                                                    (constantly
                                                                                      (partial asphalt/query
                                                                                        asphalt/fetch-single-value))})
(asphalt/defsql sql-find-content  "SELECT content FROM todos WHERE todo_id = $id"  {:make-conn-worker
                                                                                    (constantly
                                                                                      (partial asphalt/query
                                                                                        asphalt/fetch-single-value))})
(asphalt/defsql sql-find-complete "SELECT complete FROM todos WHERE todo_id = $id" {:make-conn-worker
                                                                                    (constantly
                                                                                      (partial asphalt/query
                                                                                        asphalt/fetch-single-value))})


(defn db-test-wrap
  "Wipe tables"
  [f]
  (sql-wipe-todos global/db [])
  (f)
  (sql-wipe-todos global/db []))


(use-fixtures :each db-test-wrap)


(deftest test-insert-item
  (is (= 0 (sql-count-todos global/db [])))
  (is (= 1 (db/insert-item "foo")))
  (is (= 1 (sql-count-todos global/db []))))


(deftest test-retrieve-all
  (let [id1 (util/uuid)
        id2 (util/uuid)]
    (db/insert-item id1 "foo")
    (db/insert-item id2 "bar")
    (let [[{i1 :id
            c1 :content}
           {i2 :id
            c2 :content} :as rows] (db/find-all)]
      (is (= 2 (count rows)))
      (is (= [id1 "foo"] [i1 c1]))
      (is (= [id2 "bar"] [i2 c2])))))


(deftest test-update-item
  (let [id (util/uuid)]
    (db/insert-item id "foo")
    (db/update-content id "bar")
    (is (= "bar" (sql-find-content global/db {:id id})))
    (db/update-complete id true)
    (is (= true (sql-find-complete global/db {:id id})))))


(deftest test-toggle-items
  (let [id1 (util/uuid)
        id2 (util/uuid)
        id3 (util/uuid)]
    (testing "fresh inserts"
      (db/insert-item id1 "foo")
      (db/insert-item id2 "bar")
      (db/insert-item id3 "baz")
      (is (= [false false false] (mapv :complete? (db/find-all)))))
    (testing "all-incomplete -> all-complete"
      (db/toggle-complete true)
      (is (= [true true true] (mapv :complete? (db/find-all)))))
    (testing "all-complete -> all-incomplete"
      (db/toggle-complete false)
      (is (= [false false false] (mapv :complete? (db/find-all)))))
    (testing "some-complete -> all-complete"
      (db/update-complete id1 true)
      (db/toggle-complete true)
      (is (= [true true true] (mapv :complete? (db/find-all)))))))


(deftest test-delete-item
  (let [id1 (util/uuid)
        id2 (util/uuid)]
    (db/insert-item id1 "foo")
    (db/insert-item id2 "bar")
    (db/delete-item id1)
    (let [[{i2 :id
            c2 :content} :as rows] (db/find-all)]
      (is (= 1 (count rows)))
      (is (= [id2 "bar"] [i2 c2])))))


(deftest test-delete-complete
  (let [id1 (util/uuid)
        id2 (util/uuid)
        id3 (util/uuid)]
    (db/insert-item id1 "foo")
    (db/insert-item id2 "bar")
    (db/insert-item id3 "baz")
    (db/update-complete id1 true)
    (db/update-complete id3 true)
    (db/delete-complete)
    (let [[{i2 :id
            c2 :content} :as rows] (db/find-all)]
      (is (= 1 (count rows)))
      (is (= [id2 "bar"] [i2 c2])))))
