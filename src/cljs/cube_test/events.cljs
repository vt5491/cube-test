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
   [cube-test.scenes.face-slot-scene :as face-slot-scene]
   [cube-test.scenes.tic-tac-attack-scene :as tta-scene]
   [cube-test.scenes.vrubik-scene :as vrubik-scene]
   [cube-test.face-slot.rotor :as rotor]
   [cube-test.tic-tac-attack.box-grid :as box-grid]))


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
;; scene initialization
;;
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

(re-frame/reg-event-db
 :init-tic-tac-attack-scene
 (fn [db _]
   ;side effect
   (tta-scene/init)
   db))

(re-frame/reg-event-db
 :init-vrubik-scene
 (fn [db _]
   ;side effect
   (vrubik-scene/init)
   db))
;;
;; face-slot scene
;;
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

(re-frame/reg-event-db
  ; :rotor-anim-bwd
  :face-slot-anim-bwd
 (fn [db [_ hlq mute]]
   ; (println "events.face-slot-anim-bwd: mute=" mute)
   ;side effect
   (face-slot-scene/anim-bwd hlq mute)
   db))

; (re-frame/reg-event-db
;   :face-slot-super-anim-bwd
;  (fn [db [_]]
;    ; (println "init-cube-fx event handler")
;    ;side effect
;    (face-slot-scene/super-anim-bwd)
;    db))

(re-frame/reg-event-db
  :face-slot-anim-fwd
 (fn [db [_ hlq mute]]
   ; (println "init-cube-fx event handler")
   ;side effect
   (face-slot-scene/anim-fwd hlq mute)
   db))
;
; (re-frame/reg-event-db
;   :face-slot-super-anim-fwd
;  (fn [db [_]]
;    ; (println "init-cube-fx event handler")
;    ;side effect
;    (face-slot-scene/super-anim-fwd)
;    db))

(re-frame/reg-event-db
 :init-top-rotor
 (fn [db [_]]
   (println "events: init-top-rotor")
   (face-slot-scene/init-top-rotor)
   db))

(re-frame/reg-event-db
 :init-mid-rotor
 (fn [db [_]]
   (println "events: init-mid-rotor")
   (face-slot-scene/init-mid-rotor)
   db))

(re-frame/reg-event-db
 :init-bottom-rotor
 (fn [db [_]]
   (println "events: init-bottom-rotor")
   (face-slot-scene/init-bottom-rotor)
   db))

(re-frame/reg-event-db
 :load-rotor-frame
 (fn [db [_ path file user-cb]]
   (face-slot-scene/load-rotor-frame path file user-cb)
   db))

(re-frame/reg-event-db
 :toggle-rotor-frame
 (fn [db [_]]
   (face-slot-scene/toggle-rotor-frame)
   db))

(re-frame/reg-event-db
 :randomize-rotor
 (fn [db [_]]
   (face-slot-scene/randomize-rotor)
   db))
;;
;; rotor
;;
(re-frame/reg-event-db
 :load-rotor
 (fn [db [_ path file hlq user-cb]]
   ;side effect
   (rotor/load-rotor path file hlq user-cb)
   db))

(re-frame/reg-event-db
 :rotor-anim-bwd
 (fn [db [_ hlq start-face mute]]
   ; (println "event: rotor-anim-bwd: mute=" mute)
   (rotor/anim-bwd hlq start-face mute)
   db))

(re-frame/reg-event-db
 :rotor-anim-fwd
 (fn [db [_ hlq start-face mute]]
   (rotor/anim-fwd hlq start-face mute)
   db))

(re-frame/reg-event-db
 :rotor-init-snd
 (fn [db [_]]
   (rotor/init-snd)
   db))

(re-frame/reg-event-db
 :rotor-play-rot-snd
 (fn [db [_]]
   (rotor/rotor-play-rot-snd)
   db))

(re-frame/reg-event-db
 :rotor-stop-rot-snd
 (fn [db [_]]
   (rotor/rotor-stop-rot-snd)
   db))

(re-frame/reg-event-db
 :rotor-auto-stop-rotor-snds
 (fn [db [_ val]]
   (rotor/auto-stop-rotor-snds val)
   db))

; (re-frame/reg-event-db
;  :load-rotor
;  (fn [db [_ path fn]]
;    ; non-rf side effect
;    (face-slot-scene/load-rotor db path fn)
;    db))
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

;;
;; tic-tac-attack scene
;;
(re-frame/reg-event-db
  :run-tic-tac-attack-scene
  (fn [db [_]]
    (tta-scene/run-scene)
    db))

(re-frame/reg-event-db
 :init-cross
 (fn [db [_]]
   (tta-scene/init-cross)
   db))

(re-frame/reg-event-db
 :init-ring-plex
 (fn [db [_]]
   (tta-scene/init-ring-plex)
   db))

(re-frame/reg-event-db
 :init-rubiks-cube
 (fn [db [_]]
   (tta-scene/init-rubiks-cube)
   db))

(re-frame/reg-event-db
 :tta-rot-cube
 (fn [db [_]]
   (tta-scene/rot-cube)
   db))

(re-frame/reg-event-db
 :tta-rot-cube-2
 (fn [db [_]]
   (tta-scene/rot-cube-2)
   db))

(re-frame/reg-event-db
 :tta-rot-cube-3
 (fn [db [_]]
   (tta-scene/rot-cube-3)
   db))

(re-frame/reg-event-db
 :tta-left-side-anim
 (fn [db [_]]
   (tta-scene/rubiks-cube-left-side-anim (db :rubiks-grid))
   db))

(re-frame/reg-event-db
 :tta-left-side-rot
 (fn [db [_]]
   (tta-scene/rubiks-cube-left-side-rot (db :rubiks-grid))
   db))

(re-frame/reg-event-db
 :tta-left-side-rot-grid
 (fn [db [_]]
   (assoc db :rubiks-grid (tta-scene/rubiks-cube-left-side-rot-grid (db :rubiks-grid)))))

(re-frame/reg-event-db
 :tta-print-rubiks-grid
 (fn [db [_]]
   (tta-scene/print-rubiks-grid (db :rubiks-grid))
   db))

;;
;; vrubik
;;
(re-frame/reg-event-db
  :run-vrubik-scene
  (fn [db [_]]
    (vrubik-scene/run-scene)
    db))

(re-frame/reg-event-db
 :init-rubiks-cube
 (fn [db [_]]
   (vrubik-scene/init-rubiks-cube)
   db))

(re-frame/reg-event-db
 :vrubik-left-side-anim
 (fn [db [_]]
   (vrubik-scene/rubiks-cube-left-side-anim (db :rubiks-grid))
   db))

(re-frame/reg-event-db
 :vrubik-left-side-rot
 (fn [db [_]]
   (vrubik-scene/rubiks-cube-left-side-rot (db :rubiks-grid))
   db))

(re-frame/reg-event-db
 :vrubik-left-side-rot-grid
 (fn [db [_]]
   (assoc db :rubiks-grid (vrubik-scene/rubiks-cube-left-side-rot-grid (db :rubiks-grid)))))

(re-frame/reg-event-db
 :vrubik-print-rubiks-grid
 (fn [db [_]]
   (vrubik-scene/print-rubiks-grid (db :rubiks-grid))
   db))

;;
;; vrubik/box-grid
;;

;; not currently used
; (re-frame/reg-event-db
;  :init-grid
;  (fn [db [_ grid-key]]
;    (box-grid/init-grid db grid-key)
;    db))

(re-frame/reg-event-db
 :init-rubiks-grid
 (fn [db [_]]
   ; (let [r (box-grid/init-rubiks-grid db)]
   ;   (println "events.init-rubiks-grid: result=" db))
   ; db
   ; (box-grid/init-rubiks-grid db)
   ; (assoc db :rubiks-grid (box-grid/init-rubiks-grid db))
   ; (let [r (assoc db :abc 7)]
   ;   (println "events: r=" r)
   ;   r)
   (let [r (assoc db :rubiks-grid (box-grid/init-rubiks-grid))]
     ; (println "events: r=" r)
     r)))
     ; (doall r))))
     ; (js-debugger))))
   ; (assoc db :abc 7)))

;; event utils
(re-frame/reg-event-db
 :print-db
 (fn [db [_]]
   (println "events.print-db: db=" db)
   db))

;; the following two methods are if something you added to the db are lazy
;; and haven't been evaluated yet.
(re-frame/reg-event-db
 :unlazy-db
 (fn [db [_]]
   ; (doall db)
   db))

(re-frame/reg-event-db
 :trampoline-db
 (fn [db [_]]
   db))

(re-frame/reg-event-db
 :call-doit-with-db
 (fn [db [_]]
   (tta-scene/do-it db)
   db))
