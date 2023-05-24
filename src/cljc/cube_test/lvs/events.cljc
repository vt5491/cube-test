(ns cube-test.lvs.events
  (:require
   [re-frame.core :refer [dispatch dispatch-sync reg-event-db reg-event-fx reg-fx after ] :as rf]
   [cube-test.main-scene :as main-scene]
   [cube-test.lvs.game :as lvs.game]
   [cube-test.lvs.db :as lvs.db]
   [cube-test.lvs.scenes.reflect-scene :as reflect-scene]))

;;
;; db
;;
(reg-event-db
 ::init-db
 (fn [db [_]]
   (lvs.db/init-db db)))
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
;; (reg-event-fx
;;   :init-lvs-scene
;;   (fn [cofx _]
;;     {:fx [(lvs-scene/init)]}))

;; (reg-event-fx
;;   :run-lvs-scene
;;   (fn [cofx _]
;;     {:fx [(lvs-scene/run-scene)]})) 