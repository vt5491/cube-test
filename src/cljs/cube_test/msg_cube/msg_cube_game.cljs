(ns cube-test.msg-cube.msg-cube-game
  (:require
   [re-frame.core :as re-frame]))
   ;; Note: since msg-cube-game is a pure "model", we should never explicitily or direclty access
   ;; the "view", thus there should never be a require for 'msg-cube-scene' here.
   ; [cube-test.msg-cube.msg-cube-scene :as msg-cube-scene]))

;; TODO: add to db
; (def msgs [])
(def dummy 7)
(defn df []
  dummy)


(defn inc-max-id [db]
  ; (js-debugger)
  (assoc db :max-id (+ (db :max-id) 1)))

; (defn gen [{:keys [id level text] :as msg}])
; (defn add-msg [text db])
;; TODO: move into msg.cljs
(defn add-msg [{:keys [text level] :as msg} db]
  (println "msg-cube-game: add-msg: msg=" msg ", msg.text" (:text msg) ", text=" text ", level=" level)
  (println "msg-cube-game: add-msg: db=" db)
  (let [new-id (+ (db :max-id) 1)
        new-msg {:id new-id :text text :level level}
        old-msgs (db :msgs)]
    (println "add-msg: old-msgs=" old-msgs)
    ;;TODO: add into default-db in 'db.cljs'
    (assoc db
           :msgs (conj old-msgs new-msg)
           :max-id new-id)))

(defn add-msg-2 [{:keys [text level] :as msg} db]
  (println "msg-cube-game: add-msg-2: msg=" msg ", msg.text" (:text msg) ", text=" text ", level=" level)
  (println "msg-cube-game: add-msg-2: db=" db)
  (let [new-id (+ (db :max-id) 1)
        new-msg {:id new-id :text text :level level}
        old-msgs (db :msgs-2)]
    (println "add-msg: old-msgs=" old-msgs)
    ;;TODO: add into default-db in 'db.cljs'
    (assoc db
           :msgs-2 (conj old-msgs new-msg)
           :max-id new-id)))
           
(defn init [db]
  (println "msg-cube-game.init: entered")
  ; (js-debugger)
  ; (re-frame/dispatch [:init-msg-cube-scene])
  (assoc db :msgs [] :max-id 0))

(defn run []
  (println "msg-cube-game.run: entered")
  (re-frame/dispatch [:run-msg-cube-scene]))
  ; (msg-cube-scene/run-scene))
