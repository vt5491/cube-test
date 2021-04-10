;; events is refer to many
(ns cube-test.twizzlers.events
  (:require
   [re-frame.core :refer [reg-event-db reg-event-fx reg-fx after] :as re-frame]
   ; [cube-test.events :as events]
   [cube-test.twizzlers.game :as twizzlers.game]
   [cube-test.twizzlers.scene :as twizzlers.scene]
   [cube-test.twizzlers.db :as twizzlers.db]
   [cube-test.twizzlers.twizzler :as twizzlers.twizzler]
   [cube-test.twizzlers.rules :as twizzlers.rules]
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

(reg-event-db
 ::add-twizzler
 (fn [db [_ id]]
   (println ":twizzlers.init-db: now running")
   (twizzlers.twizzler/add-twizzler db)))

;; first odoyle rule
(re-frame/reg-event-fx
 ::update-time
 ; [twizzlers-check-spec-interceptor]
 (fn [_]
   (prn "twizzler.events.update-time: entered")
   (twizzlers.rules/update-time)))

;; odoyle rule
(re-frame/reg-event-fx
 ::update-twiz-cnt
 (fn [_ [_ new-cnt]]
   (prn "twizzler.events.update-twiz-cnt: entered, new-cnt=" new-cnt)
   (twizzlers.rules/update-twiz-cnt new-cnt)))

(re-frame/reg-event-fx
 ::update-dmy-atom
 (fn [_]
   (prn "twizzler.events.update-dmy-atom: entered")
   (twizzlers.game/update-atom)))

(reg-event-fx
 :add-twiz-cube
 (fn [cofx [_ twiz]]
   (println "events.add-twiz-cube: twiz=" twiz)
   {:fx [(twizzlers.scene/add-twiz-cube twiz)]}))
