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
    * https://google.github.io/closure-library/api/goog.events.EventType.html"
  (:require-macros
    [hiccups.core :as hiccups :refer [html]])
  (:require
    [goog.dom         :as gdom]
    [goog.dom.classes :as gclasses]
    [goog.events      :as gevents]
    [goog.string      :as gstring]
    [goog.string.format]
    [goog.style       :as gstyle]
    [hiccups.runtime  :as hrt]
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


(def todo-items [])  ; TODO items data, as returned by the web service


(def current-filter :all)  ;; possible values -> :all :active :completed


;; ----- DOM elements -----


(def dom-toggle-all-input (gdom/getElementByClass "toggle-all"))
(def dom-toggle-all-label (aget (js/document.querySelectorAll ".main label") 0))
(def dom-new-todo   (gdom/getElementByClass "new-todo"))
(def dom-todo-list  (gdom/getElementByClass "todo-list"))
(def dom-footer     (gdom/getElementByClass "footer"))
(def dom-clear-btn  (gdom/getElementByClass "clear-completed"))


(defn item-html [id content complete?]
  "Generate HTML text <li> to represent a TODO item node."
  (let [he-content (hrt/escape-html content)]
    (html [:li (let [klass (if complete? {:class "completed"} {})
                     dnone (case current-filter
                             :all       {}
                             :active    (if complete? {:style "display: none"} {})
                             :completed (if complete? {} {:style "display: none"})
                             {})]
                 (merge klass dnone))
           [:div {:class "view"}
            [:input  {:class "toggle"
                      :data-id id
                      :type "checkbox"
                      :checked complete?}]
            [:label  {:data-id id} he-content]
            [:button {:class "destroy"
                      :data-id id}]]
           [:input {:class "edit"
                    :data-id id
                    :value content}]])))


(defn make-todo-node [id content complete?]
  (let [div (gdom/createElement "div")
        txt (item-html id content complete?)]
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
          (let [uri (urif "/todo/%s/content/" node)]
            (logf "-> [PUT %s]: '%s'" uri (.-value node))
            (PUT uri
              {:body (.-value node)
               :handler (fn [response] (list-todos))
               :error-handler (ajax-error-when "updating TODO")})))))))


(defn toggle-complete-todo [node]
  (fn [event]
    (let [uri  (urif "/todo/%s/complete/" node)
          body (str (.-checked node))]
      (logf "-> [PUT %s]: '%s'" uri body)
      (PUT uri
        {:body body
         :handler (fn [response] (list-todos))
         :error-handler (ajax-error-when (str "marking TODO " (when-not (.-checked node) "in") "complete"))}))))


(defn delete-todo [node]
  (fn [event]
    (let [uri (urif "/todo/%s/" node)]
      (logf "-> [DELETE %s]" uri)
      (DELETE uri
        {:handler (fn [response] (list-todos))
         :error-handler (ajax-error-when "deleting TODO")}))))


;; ----- toggle-all setup -----


(defn setup-toggle-all []
  (gevents/listen
    dom-toggle-all-label
    goog.events.EventType.CLICK
    (fn [event]
      (let [uri  "/todos/complete/all/"
            all? (every? #(get % "complete?") todo-items)
            body (-> all? not str)]
        (logf "-> [PUT %s]: '%s'" uri body)
        (PUT uri {:body body
                  :handler (fn [status] (list-todos))
                  :error-handler (ajax-error-when "deleting completed TODO items")})))))


;; ----- update components -----


(defn update-todos [todos]
  (set! todo-items (into [] todos))  ; update global state
  (let [todos? (boolean (seq todos))]
    (gstyle/setElementShown dom-toggle-all-label todos?)  ; show/hide toggle-all button
    (gstyle/setElementShown dom-footer todos?))           ; show/hide controls footer
  (->> todos
    (every? #(get % "complete?"))
    (set! (.-checked dom-toggle-all-input)))              ; highlight the toggle-all button
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
    (set! (.-textContent (aget (js/document.querySelectorAll ".todo-count strong") 0)) incomplete-count)
    ;; show element only when complete-count > 0
    (gstyle/setElementShown dom-clear-btn (pos? complete-count)))
  (bind-event-handler (js/document.querySelectorAll "li label") goog.events.EventType.DBLCLICK edit-todo-text)
  (bind-event-handler (js/document.querySelectorAll "li .edit") goog.events.EventType.FOCUSOUT save-edited-todo)
  (bind-event-handler (js/document.querySelectorAll "li .edit") goog.events.EventType.KEYPRESS save-edited-todo)
  (bind-event-handler (js/document.querySelectorAll ".toggle")  goog.events.EventType.CLICK    toggle-complete-todo)
  (bind-event-handler (js/document.querySelectorAll ".destroy") goog.events.EventType.CLICK    delete-todo))


(defn list-todos []
  (logf "-> [GET /todos/]")
  (GET "/todos/" {:handler (fn [response] (update-todos response))
                  :error-handler (ajax-error-when "fetching TODO items")}))


(defn setup-add-todo []
  (gevents/listen
    dom-new-todo
    goog.events.EventType.KEYPRESS
    (fn [event]
      (when (= 13 (.-keyCode event))  ; pressed Enter key?
        (logf "-> [POST /todos/]: '%s'" (.-value dom-new-todo))
        (POST "/todos/" {:body (.-value dom-new-todo)
                         :handler (fn [status] (set! (.-value dom-new-todo) "") (list-todos))
                         :error-handler (ajax-error-when "adding new TODO item")})))))


;; ----- filter setup -----


(defn setup-filters []
  (let [nodes (js/document.querySelectorAll "li a")
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


;; ----- clear completed setup -----


(defn setup-clear-completed []
  (gevents/listen
    dom-clear-btn
    goog.events.EventType.CLICK
    (fn [event]
      (let [uri "/todos/complete/"]
        (logf "-> [DELETE %s]" uri)
        (DELETE uri {:handler (fn [status] (list-todos))
                     :error-handler (ajax-error-when "deleting completed TODO items")})))))


;; ----- page setup -----


(defn setup []
  (setup-toggle-all)
  (setup-add-todo)
  (list-todos)
  (setup-filters)
  (setup-clear-completed))


(defn ^:export main []
  (setup))
