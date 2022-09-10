;; events is refer to many
(ns cube-test.top-scene.events
  (:require
   [re-frame.core :refer [dispatch dispatch-sync reg-event-db reg-event-fx reg-fx after ] :as rf]
   ; [cube-test.frig-frog.game :as ff.game]
   [cube-test.main-scene :as main-scene]
   [cube-test.utils :as utils]
   [cube-test.top-scene.top-scene :as top-scene]))
   ; [cube-test.game :as game]))

;;
;; game level events
;;
(reg-event-db
 ::init-db
 (fn [db [_ game-db]]
   ; (let [default-db top-scene/default-db]
   ;   default-db)
   (utils/merge-dbs db game-db)))

(rf/reg-event-fx
 ::set-globals
 (fn [{:keys [db] :as cofx}]
   (prn "set-globals: db=" db)
   (let [n-choices (count (get-in db [:choice-carousels 0 :choices]))]
     (set! top-scene/n-choices n-choices))
   {
    :db db}))

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
(rf/reg-event-db
 ::init-scene
 (fn [db [_]]
   (top-scene/init db)))

(rf/reg-event-fx
 ::run-scene
 (fn [cofx _]
   (top-scene/run-scene)
   {
    :db (:db cofx)}))

(rf/reg-event-db
 ::init-scene-carousel
 (fn [db [_]]
   (top-scene/init-scene-carousel db)))

; (rf/reg-event-fx
;  ::app-selected
;  (fn  [cofx _]
;    ; (top-scene/run-scene)
;    (prn "top-scene.events: app-selected, idx=" top-scene/app-cc-idx)
;    (cube-test.utils.choice-carousel.choice-carousel/switch-app (nth (:top-level-scene top-scene/app-carousel-parms) top-scene/app-cc-idx))
;    {
;     :db (:db cofx)}))
;;
;; gui
;;
(rf/reg-event-fx
 ::app-left
 (fn [cofx [_ delta-theta]]
   ; (top-scene/run-scene)
   (prn "top-scene.events: app-left")
   ; (top-scene/rot-app-carousel :left delta-theta)
   (top-scene/animate-app-carousel :left)
   {
    :db (:db cofx)}))

(rf/reg-event-fx
 ::app-right
 (fn [cofx [_ delta-theta]]
   ; (top-scene/run-scene)
   (prn "top-scene.events: app-right")
   ; (set! top-scene/app-carousel-is-animating true)
   ; (top-scene/rot-app-carousel :right delta-theta)
   (top-scene/animate-app-carousel :right)
   {
    :db (:db cofx)}))

;;
;; db access
;;
(rf/reg-event-db
 ::add-asset-containers
 (fn [db [_]]
   (let [choices (get-in db [:choice-carousels 0 :choices])]
     ; (prn "ts-events: choices=" choices)
     (when choices
       (top-scene/add-asset-containers choices)))
  db))

(rf/reg-event-db
 ::remove-asset-containers
 (fn [db [_]]
   (let [choices (get-in db [:choice-carousels 0 :choices])]
     ; (prn "ts-events: choices=" choices)
     (when choices
       (top-scene/remove-asset-containers choices)))
  db))

(rf/reg-event-db
 ::app-selected
 (fn [db [_ val]]
   (let [n-choices (count (get-in db [:choice-carousels 0 :choices]))]
     (top-scene/app-selected top-scene/app-cc-idx-shift-factor n-choices))
   db))

;; debug/development
(rf/reg-event-db
 ::tmp
 (fn [db [_]]
   (top-scene/tmp db val)))
