;; events is refer to many
(ns cube-test.lvs.events
  (:require
   [re-frame.core :refer [dispatch dispatch-sync reg-event-db reg-event-fx reg-fx after ] :as rf]
   [cube-test.main-scene :as main-scene]
   [cube-test.lvs.game :as lvs.game]
   [cube-test.lvs.scenes.main :as lvs.main]
   [cube-test.lvs.db :as lvs.db]
   [cube-test.lvs.scenes.reflect-scene :as reflect-scene]
   [cube-test.utils.fps-panel :as fps-panel]))

;;
;; db
;;
(reg-event-db
 ::init-db
 (fn [db [_]]
   (lvs.db/init-db db)))
  ;;  db))

(reg-event-db
 ::update-landmarks
 (fn [db [_ site pos]]
   (lvs.main/update-lankmarks site pos db)))

  ;;  db))

;;
;; game
;;
;; (reg-event-fx
;;   ::init-game
;;   (fn [cofx _]
;;     {:fx [(lvs.game/init)]}))

(reg-event-db
 ::init-game
 (fn [db [_]]
   (prn "lvs.events: db=" db)
   (lvs.game/init db)
   db))

(reg-event-fx
 ::run-game
 (fn [_]
   (lvs.game/run-game)))

(reg-event-fx
 ::enter-js-debugger
 (fn [_]
   (lvs.main/enter-js-debugger)))

;;
;; reflect-scene
;;

(reg-event-fx
  :init-reflect-scene
  (fn [cofx _]
    {:fx [(reflect-scene/init)]}))

;; (reg-event-fx
;;   :run-reflect-scene
;;   (fn [cofx _]
;;     {:fx [(reflect-scene/run-scene)]})) 

;;
;; lvs-scene
;;
;; (re-frame/reg-event-fx
;;  ::run-game
;;  (fn [cofx _]
;;    (beat-club.game/run-game)
;;    ; cofx
;;    {:db (:db cofx)}))
(reg-event-fx
  :jump-to-landmark
  (fn [cofx [_ site]]
    {:fx [(lvs.main/jump-to-landmark site (:db cofx))]}))
    ;; (lvs.main/jump-to-landmark site (:db cofx))
    ;; {:db (:db cofx)}))
    ;; (lvs.main/)
    ;; (lvs.main/enter-js-debugger)))

    ;; {:fx [(lvs.main.jump-to-landmark site (:db cofx))]}))
    ;; {:fx [(lvs.main/jump-to-landmark site (:db cofx))]}))

;; (reg-event-fx
;;   :run-lvs-scene
;;   (fn  [cofx _]
;;     {:fx [(lvs-scene/run-scene)]})) 

;;
;; views
;;
(reg-event-fx
  ::tmp
  (fn [cofx _]
    (prn "now handling tmp")
    (reflect-scene/move-fps-pnl)))
    ;; {:fx [(do 
    ;;         (prn "now handling tmp")
    ;;         ;; (cube-test.lvs.scenes.reflect-scene/move-fps-pnl)
    ;;         (reflect-scene/move-fps-pnl))]}))
    ;;         ;; (let [fps-pnl cube-test.lvs.scenes.reflect-scene/fps-pnl]
    ;;         ;;   (set! (.-position fps-pnl) (.add (.-position fps-pnl) (bjs/Vector3. 0.5 0 0)))))]}))

;; (reg-event-fx
;;   ::rot-cam
;;   (fn [cofx _]))
;;     ;; {:fx [(reflect-scene/init)]}
;;     ;; {:fx [(lvs.game/reset-cam-tgt)]}))
