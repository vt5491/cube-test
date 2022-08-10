;; events is refer to many
(ns cube-test.top-scene.events
  (:require
   [re-frame.core :refer [dispatch dispatch-sync reg-event-db reg-event-fx reg-fx after ] :as rf]
   ; [cube-test.frig-frog.game :as ff.game]
   [cube-test.main-scene :as main-scene]
   [cube-test.top-scene.top-scene :as top-scene]))
   ; [cube-test.game :as game]))

;;
;; game level events
;;
(reg-event-db
 ::init-db
 (fn [db [_ game-db]]
   (let [default-db top-scene/default-db]
     default-db)))

; (reg-event-db
;  ::switch-app
;  (fn [db [_ top-level-scene]]
;    (let [scene main-scene/scene
;          engine main-scene/engine]
;      (.stopRenderLoop engine)
;      (.dispose scene)
;      (cube-test.game.init top-level-scene))))

;;
;; scene level events
;;
(rf/reg-event-fx
 ::init-scene
 (fn [cofx _]
   (top-scene/init)
   {
    :db (:db cofx)}))

(rf/reg-event-fx
 ::run-scene
 (fn [cofx _]
   (top-scene/run-scene)
   {
    :db (:db cofx)}))
