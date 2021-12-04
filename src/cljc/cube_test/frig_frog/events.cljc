;; events is refer to many
(ns cube-test.frig-frog.events
  (:require
   [re-frame.core :refer [dispatch reg-event-db reg-event-fx reg-fx after ] :as re-frame]
   [cube-test.frig-frog.game :as ff.game]
   [cube-test.frig-frog.scene :as frig-frog.scene]
   [cube-test.frig-frog.db :as frig-frog.db]
   ; [cube-test.beat-club.db :as beat-club.db]
   ; [cube-test.beat-club.twitch-stream :as twitch-stream]
   ; [cube-test.twizzlers.twizzler :as twizzlers.twizzler]
   ; [cube-test.twizzlers.rules :as twizzlers.rules]
   [cube-test.utils :as utils]))

(reg-event-db
 ::init-game-db
 (fn [db [_ id]]
   (println ":frig-frog.init-game-db: now running")
   (frig-frog.db/init-game-db db)))

(re-frame/reg-event-fx
 ::init-scene
 (fn [cofx _]
   (frig-frog.scene/init (:db cofx))
   {
    :db (:db cofx)}))

(re-frame/reg-event-fx
 ::init-game
 (fn [cofx _]
   (ff.game/init)
   {
    ; :fx [[:dispatch [::init-scene]]]
    :fx [[:dispatch [::init-scene]]]}))
    ; :db (:db cofx)}))

(re-frame/reg-event-fx
 ::run-game
 (fn [cofx _]
   (ff.game/run-game)
   {
    :db (:db cofx)}))

;; change-abc is for testing
(reg-event-db
  ::change-abc
  (fn [db [_ val]]
    ; (ff.game/change-abc db val)
    (let [r (ff.game/change-abc db val)]
      (prn "events.change-abc: r=" r)
      r)))
    ; (prn "events: returned db=")
    ; db))
