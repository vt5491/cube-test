;; events is refer to many
(ns cube-test.frig-frog.events
  (:require
   [re-frame.core :refer [dispatch reg-event-db reg-event-fx reg-fx after ] :as re-frame]
   [cube-test.frig-frog.game :as frig-frog.game]
   [cube-test.frig-frog.scene :as frig-frog.scene]
   [cube-test.frig-frog.db :as frig-frog.db]
   ; [cube-test.beat-club.db :as beat-club.db]
   ; [cube-test.beat-club.twitch-stream :as twitch-stream]
   ; [cube-test.twizzlers.twizzler :as twizzlers.twizzler]
   ; [cube-test.twizzlers.rules :as twizzlers.rules]
   [cube-test.utils :as utils]))

(reg-event-db
 ::init-game-level-db
 (fn [db [_ id]]
   (println ":frig-frog.init-game-level-db: now running")
   (frig-frog.db/init-game-level-db db)))

(re-frame/reg-event-fx
 ::init-scene
 (fn [cofx _]
   (frig-frog.scene/init (:db cofx))
   {
    :db (:db cofx)}))

(re-frame/reg-event-fx
 ::init-game
 (fn [cofx _]
   (frig-frog.game/init)
   {
    ; :fx [[:dispatch [::init-scene]]]
    :fx [[:dispatch [::init-scene]]]}))
    ; :db (:db cofx)}))

(re-frame/reg-event-fx
 ::run-game
 (fn [cofx _]
   (frig-frog.game/run-game)
   {
    :db (:db cofx)}))
