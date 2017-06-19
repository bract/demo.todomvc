;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns demo.todomvc.app
  "ClojureScript code for the browser end of the TodoMVC application.
  ## References
  * The Cljs-ajax library:        https://github.com/JulianBirch/cljs-ajax
  * CLJS DOM manipulation sample: http://davedellacosta.com/cljs-dom-survey
  * Handling Enter key press:     http://byatool.com/uncategorized/google-closure-an-enter-key-press/
  * Google Closure cheatsheet:    http://anton.shevchuk.name/wp-demo/closure-tutorials/cheatsheet.html
  * Google Closure docs below:    https://google.github.io/closure-library/
    * https://google.github.io/closure-library/api/goog.dom.html
    * https://google.github.io/closure-library/api/goog.events.EventType.html
    * https://google.github.io/closure-library/api/goog.dom.query.html"
  (:import
    [goog.dom query])
  (:require
    [goog.dom    :as gdom]
    [goog.dom.classes :as gclasses]
    [goog.events :as gevents]
    [goog.string :as gstring]
    [goog.string.format]
    [goog.style  :as gstyle]
    [ajax.core :refer [GET POST PUT DELETE]]))


;; ----- utility -----


(defn logf
  "Given a format text and arguments, log the generated text to console."
  [fmt & args]
  (.log js/console (apply gstring/format fmt args)))


(defn urif "Given URI-format text and a DOM node having `data-id` attribute, return the URI."
  [uri-fmt node]
  (gstring/format uri-fmt (.getAttribute node "data-id")))


(defn ajax-error-when
  "Given the failure use case text, return an AJAX error handler."
  [usecase-text]
  (fn [{:keys [status status-text]}]
    (js/alert (gstring/format "Error %s: [%s] %s" usecase-text status status-text))))


(defn bind-event-handler
  "Given nodes, event-type (goog.events.EventType.xxx) and `(fn [node]) -> event-handler` bind the given event type to
  the handler for every node in the list."
  [nodes event-type handler-maker]
  (dotimes [idx (alength nodes)]
    (let [each (aget nodes idx)]
      (gevents/listen
        each
        event-type
        (handler-maker each)))))


;; ----- state -----


(def current-filter :all)  ;; possible values -> :all :active :completed


;; ----- DOM elements -----


(def dom-new-todo  (gdom/getElementByClass "new-todo"))
(def dom-todo-list (gdom/getElementByClass "todo-list"))

(defn make-todo-node [id content complete?]
  (let [div (gdom/createElement "div")
        txt (gstring/format
      "<li %s>
  <div class=\"view\">
    <input class=\"toggle\" data-id=\"%s\" type=\"checkbox\" %s>
    <label data-id=\"%s\">%s</label>
    <button class=\"destroy\" data-id=\"%s\"></button>
  </div>
  <input class=\"edit\" data-id=\"%s\" value=\"%s\">
</li>"
      (let [klass (if complete? " class=\"completed\"" "")
            dnone (case current-filter
                    :all       ""
                    :active    (if complete? "style=\"display: none\"" "")
                    :completed (if complete? "" "style=\"display: none\"")
                    "")]
        (str klass " " dnone))
      id (if complete? "checked" "") id content id id content)]
    (set! (.-innerHTML div) txt)
    div))


;; ----- event handlers -----


(declare list-todos)


(defn edit-todo-text [node]
  (fn [event]
    (let [parent-li (.-parentNode (.-parentNode node))]
      (when-not (gclasses/has parent-li "editing") ; no editing?
        (gclasses/add parent-li "editing")
        (let [edit-box (gdom/getLastElementChild parent-li)]
          (.focus edit-box))))))


(defn save-edited-todo [node]
  (fn [event]
    (when (or (not= "keypress" (.-type event))
            (= 13 (.-keyCode event)))  ; pressed Enter key?
      (let [parent-li (.-parentNode node)]
        (when (gclasses/has parent-li "editing") ; still editing?
          (gclasses/remove parent-li "editing")
          (let [uri (urif "/todos/%s/content/" node)]
            (logf "-> [PUT %s]: '%s'" uri (.-value node))
            (PUT uri
              {:body (.-value node)
               :handler (fn [response] (list-todos))
               :error-handler (ajax-error-when "updating TODO")})))))))


(defn toggle-complete-todo [node]
  (fn [event]
    (let [uri  (urif "/todos/%s/complete/" node)
          body (str (.-checked node))]
      (logf "-> [PUT %s]: '%s'" uri body)
      (PUT uri
        {:body body
         :handler (fn [response] (list-todos))
         :error-handler (ajax-error-when (str "marking TODO " (when-not (.-checked node) "in") "complete"))}))))


(defn delete-todo [node]
  (fn [event]
    (let [uri (urif "/todos/%s/" node)]
      (logf "-> [DELETE %s]" uri)
      (DELETE uri
        {:handler (fn [response] (list-todos))
         :error-handler (ajax-error-when "deleting TODO")}))))


;; ----- update components -----


(defn update-todos [todos]
  (gdom/removeChildren dom-todo-list)
  (doseq [{:strs [id content complete?]} todos]
    (let [node (make-todo-node id content complete?)]
      (gdom/appendChild dom-todo-list node)))
  (let [{:keys [incomplete-count
                complete-count]} (reduce (fn [m each] (update m (if (get each "complete?")
                                                                  :complete-count :incomplete-count) inc))
                                   {:incomplete-count 0
                                    :complete-count   0}
                                   todos)]
    (set! (.-textContent (aget (query ".todo-count strong") 0)) incomplete-count)
    (gstyle/setStyle (gdom/getElementByClass "clear-completed") "display" (if (pos? complete-count)
                                                                            "block"
                                                                            "none")))
  (bind-event-handler (query "li label") goog.events.EventType.DBLCLICK edit-todo-text)
  (bind-event-handler (query "li .edit") goog.events.EventType.FOCUSOUT save-edited-todo)
  (bind-event-handler (query "li .edit") goog.events.EventType.KEYPRESS save-edited-todo)
  (bind-event-handler (query ".toggle")  goog.events.EventType.CLICK    toggle-complete-todo)
  (bind-event-handler (query ".destroy") goog.events.EventType.CLICK    delete-todo))


(defn list-todos []
  (logf "-> [GET /todos/]")
  (GET "/todos/" {:handler (fn [response] (update-todos response))
                  :error-handler (ajax-error-when "fetching TODO items")}))


(defn setup-add-todo []
  (let [element (gdom/getElementByClass "new-todo")]
    (gevents/listen
      element
      goog.events.EventType.KEYPRESS
      (fn [event]
        (when (= 13 (.-keyCode event))  ; pressed Enter key?
          (logf "-> [POST /todos/]: '%s'" (.-value element))
          (POST "/todos/" {:body (.-value element)
                           :handler (fn [status] (set! (.-value element) "") (list-todos))
                           :error-handler (ajax-error-when "adding new TODO item")}))))))


;; ----- filter setup -----


(defn setup-filters []
  (let [nodes (query "li a")
        radio (fn [selected-node]
                (dotimes [idx (alength nodes)]
                  (let [each (aget nodes idx)]
                    (gclasses/remove each "selected")))
                (gclasses/add selected-node "selected"))]
    (dotimes [idx (alength nodes)]
      (let [each (aget nodes idx)]
        (gevents/listen
          each
          goog.events.EventType.CLICK
          (fn [event]
            (case (.-hash each)
              "#/"          (do (set! current-filter :all)       (radio each))
              "#/active"    (do (set! current-filter :active)    (radio each))
              "#/completed" (do (set! current-filter :completed) (radio each))
              (js/alert (str "Unexpected hash for filter link: " (.-hash each))))
            (list-todos)))))))


;; ----- page setup -----


(defn setup []
  (setup-add-todo)
  (list-todos)
  (setup-filters))


(setup)
