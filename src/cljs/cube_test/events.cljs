;; events is refer to many
(ns cube-test.events
  (:require
   [re-frame.core :as re-frame]
   [cube-test.db :as db]
   [cube-test.game :as game]
   [cube-test.main-scene :as main-scene]
   [cube-test.controller-xr :as ctrl-xr]
   [cube-test.cube-fx :as cube-fx]
   [cube-test.projectile :as projectile]
   [cube-test.utils.fps-panel :as fps-panel]
   [cube-test.scenes.cube-spin-scene :as cube-spin-scene]
   [cube-test.scenes.face-slot-scene :as face-slot-scene]))


(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

;;
;; game
;;
; (re-frame/reg-event-db
;   :run-main-scene
;   (fn [db [_]]
;   ; (fn [db [_ top-level-scene-initializer]]
;     ; non-rf side effect
;     ; (game/run-scene game/render-loop)
;     (game/run-scene)
;     db))

;;
;;> main-scene
;;
(re-frame/reg-event-db
 :set-main-scene
 (fn [db [_ scene]]
   (assoc db :main-scene scene)))

(re-frame/reg-event-db
 :get-main-scene
 (fn [db [_ scene]]
   (assoc db :main-scene scene)))

; (re-frame/reg-event-db
;   :init-main-scene
;   (fn [db [_]]
;     ; non-rf side effect
;     (main-scene/init)
;     db))

(re-frame/reg-event-db
  :init-main-scene
  (fn [db [_ top-level-scene-initializer]]
    ; non-rf side effect
    (main-scene/init top-level-scene-initializer)
    db))

(re-frame/reg-event-db
 :enter-vr
 (fn [db [_]]
   (main-scene/enter-vr)
   db))

; (re-frame/reg-event-db
;  :setup-btn
;  (fn [db [_]]
;    (main-scene/setup-btn)
;    db))

;;> xr events
(re-frame/reg-event-db
 :setup-xr-ctrl-cbs
 (fn [db [_ xr]]
   (ctrl-xr/setup-xr-ctrl-cbs xr)
   db))

; (re-frame/reg-event-db
;  :init-xr
;  (fn [db [_ scene xr]]
;    (ctrl-xr/init scene xr)
;    db))

(re-frame/reg-event-db
 :init-xr
 (fn [db [_ xr]]
   (ctrl-xr/init xr)
   db))

;;
;; cube-spin scene
(re-frame/reg-event-db
 :init-cube-spin-scene
 (fn [db _]
   ;side effect
   (cube-spin-scene/init)
   db))

(re-frame/reg-event-db
  :run-cube-spin-scene
  (fn [db [_]]
    (cube-spin-scene/run-scene)
    db))
;;
;; face-slot scene
(re-frame/reg-event-db
 :init-face-slot-scene
 (fn [db _]
   ;side effect
   (face-slot-scene/init)
   db))

(re-frame/reg-event-db
  :run-face-slot-scene
  (fn [db [_]]
    (face-slot-scene/run-scene)
    db))
;; cube-fx
(re-frame/reg-event-db
 :init-cube-fx
 (fn [db _]
   (println "init-cube-fx event handler")
   ;side effect
   (cube-fx/init db)
   db))

(re-frame/reg-event-db
 :reset-projectiles
 (fn [db [_ idx]]
   ;side effect
   (cube-fx/reset-projectiles)
   db))

(re-frame/reg-event-db
  :update-spin-ang-vel
 (fn [db [_ delta-vec]]
   ; (println "init-cube-fx event handler")
   ;side effect
   (cube-fx/update-spin-ang-vel delta-vec)
   db))

(re-frame/reg-event-db
  :update-projectile-vel
 (fn [db [_]]
   (cube-fx/update-projectile-vel)
   db))

(re-frame/reg-event-db
 :toggle-pause-projectiles
 (fn [db [_ idx]]
   ;side effect
   (cube-fx/toggle-pause-projectiles)
   db))
; (re-frame/reg-event-db
;  :cube-fx-tick
;  (fn [db _]
;    (println "init-cube-fx event handler")
;    ;side effect
;    (cube-fx/tick db)
;    db))
;;
;; projectile
;;
; (re-frame/reg-event-db
;   :create-projectile
;  (fn [db [_ vel]]
;    ;side effect
;    (projectile/create vel)
;    db))

;; fps-panel
(re-frame/reg-event-db
 :init-fps-panel
 (fn [db [_ scene]]
   ;side effect
   (fps-panel/init scene)
   db))

;; general
(re-frame/reg-event-db
 :debug-view
 (fn [db _]
   ;side effect
   ; (js-debugger)
   (-> main-scene/scene (.-debugLayer) (.show))
   db))
