;; Note: this file is explicitly a '.cljc' for now, as it should be "pure" functional and
;; not have any 'js/' dependencies.
;; Note: it's a .cljs now because I do have some js deps
(ns cube-test.msg-cube.data.msg
  (:require
   [re-frame.core :as re-frame]
   [clojure.spec.alpha :as s]))

;; hookup
(comment
 (shadow.cljs.devtools.api/nrepl-select :app)
 ,)

(def dummy 10)

;; sample
{:id 1, :level :INFO, :text "hello"}

(defn gen [{:keys [id level text] :as msg}]
  {:id id, :level level, :text text})

(comment
 (+ 1 1)
 (js/parseInt "5")
 ,)
; (defn extract-msg-box-num [msg-box-id]
;   (-> (re-find #"msg-box-(\d+)" "msg-box-0") (get  1) (js/parseInt)))
(defn extract-id [mesh-id]
  "Extract the id from the meshid e.g 'mc-5' or 'mc-15'"
  (-> (re-find #"-(\d+)$" mesh-id) (get 1) (js/parseInt)))

(defn up-msg-severity [db id]
  (println "msg.up-msg-severity: id=" id)
  (println "current sev=" (db :msgs))
  db)

(defn get-by-id [id msgs]
  "Return the first occurence from :msgs vector that has the given id"
  ; (println "hi from get-by-id")
  (first (filterv #(= (%1 :id) id) msgs)))

(defn set-level [id level msgs]
  "Set the level for the id, returing a new :msgs vector"
  ; (println "***msg.set-level: id=" id ", level=" level ", msgs=" msgs)
  ; (first
  (let [new-msgs
        (->
         (map #(do
                 (let [r-id (%1 :id)]
                   (if (= r-id id)
                     (assoc %1 :level level)
                     %1)))
              msgs)
         vec)]
    (println "new-msgs=" new-msgs)
    new-msgs))

(defn inc-level [id msgs]
  "increment the severity level to the next level, capping
   - at :SEVERE"
  (let [msg (get-by-id id msgs)
        msg-level (msg :level)]
    (println "inc-level: msg-level=" msg-level)
    (case msg-level
        :INFO (set-level id :WARN msgs)
        :WARN (set-level id :SEVERE msgs)
        ;; don't do anything if already at highest level
        :SEVERE msgs)))
