;; events is refer to many
(ns cube-test.twizzlers.events
  (:require
   [re-frame.core :refer [reg-event-db reg-event-fx reg-fx after] :as re-frame]
   ; [cube-test.events :as events]
   [cube-test.twizzlers.game :as twizzlers.game]
   [cube-test.twizzlers.scene :as twizzlers.scene]
   [cube-test.twizzlers.db :as twizzlers.db]
   [cube-test.utils :as utils]))

; (re-frame/reg-fx
;  :init-msg-cube-scene-fx
;  (fn [_]
;   ; (simp-scene/init-once)
;   (msg-cube.scene/init)))
(def dummy 7)

; (def msg-cube-check-spec-interceptor (after (partial check-and-throw ::msg-cube.spec/db-spec)));
(def twizzlers-check-spec-interceptor (after (partial utils/check-and-throw ::twizzlers.db/db-spec)));

(re-frame/reg-event-fx
 :init-twizzlers-game
 (fn [_]
   (twizzlers.game/init)))

(re-frame/reg-event-fx
 ::init-game
 (fn [_]
   (twizzlers.game/init)))

(re-frame/reg-event-fx
 ::init-scene
 (fn [_]
   (twizzlers.scene/init)))

(re-frame/reg-event-fx
 ::run-game
 [twizzlers-check-spec-interceptor]
 (fn [_]
   (twizzlers.game/run-game)))

(reg-event-db
 ::init-db
 (fn [db [_ id]]
   (println ":twizzlers.init-db: now running")
   (twizzlers.db/init-db db)))
