(ns cube-test.msg-cube.msg-cube-game
  (:require
   [re-frame.core :as re-frame]
   [cube-test.msg-cube.msg-cube-scene :as msg-cube-scene]))

;; TODO: add to db
; (def msgs [])
(def dummy 7)
(defn df []
  dummy)

; (defn gen [{:keys [id level text] :as msg}])
; (defn add-msg [text db])
(defn add-msg [{:keys [text] :as msg} db]
  (println "msg-cube-game: add-msg: msg=" msg)
  (let [new-id (+ (db :max-id) 1)
        new-msg {:id new-id :text msg.text}
        old-msgs (db :msgs)]
    (println "add-msg: old-msgs=" old-msgs)
    (assoc db
           :msgs (conj old-msgs new-msg)
           :max-id new-id)))

(defn init [db]
  (println "msg-cube-game.init: entered")
  ; (js-debugger)
  (re-frame/dispatch [:init-msg-cube-scene])
  (assoc db :msgs [] :max-id 0))

(defn run []
  (println "msg-cube-game.run: entered")
  (re-frame/dispatch [:run-msg-cube-scene]))
  ; (msg-cube-scene/run-scene))
