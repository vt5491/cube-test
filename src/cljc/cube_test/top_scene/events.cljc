;; events is refer to many
(ns cube-test.top-scene.events
  (:require
   [re-frame.core :refer [dispatch dispatch-sync reg-event-db reg-event-fx reg-fx after ] :as rf]
   ; [cube-test.frig-frog.game :as ff.game]
   [cube-test.main-scene :as main-scene]
   [cube-test.top-scene.top-scene :as top-scene]))

;;
;; game level events
;;
(reg-event-db
 ::init-db
 (fn [db [_ game-db]]
   (let [default-db top-scene/default-db]
     default-db)))

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
