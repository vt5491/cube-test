;; events is refer to many
(ns cube-test.events
  (:require
   [re-frame.core :refer [reg-event-db reg-event-fx reg-fx after] :as re-frame]
   [cljs.spec.alpha :as s]
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
   [cube-test.scenes.geb-cube-scene :as geb-cube-scene]
   [cube-test.scenes.skyscrapers-scene :as skyscrapers-scene]
   [cube-test.scenes.simp-scene :as simp-scene]
   [cube-test.ut-simp.ut-simp-scene :as ut-simp-scene]
   [cube-test.ut-simp.msg :as msg]
   [cube-test.ut-simp.msg-box-phys :as msg-box-phys]
   [cube-test.face-slot.rotor :as rotor]
   [cube-test.tic-tac-attack.box-grid :as box-grid]
   [cube-test.tic-tac-attack.cell :as cell]
   [cube-test.msg-cube.msg-cube-game :as msg-cube.game]
   [cube-test.msg-cube.msg-cube-scene :as msg-cube.scene]
   [cube-test.msg-cube.data.msg :as msg-cube.msg]
   [cube-test.msg-cube.spec.db :as msg-cube.spec]
   [cube-test.twizzlers.game :as twizzlers.game]
   [cube-test.utils.common :as common]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(reg-event-db
 ::init-game-db
 (fn [db [_ default-game-db]]
   (println ":events-main: now running init-game-db")
   (prn "db=" db)
   (prn "default-game-db=" default-game-db)
   (common/merge-dbs db default-game-db)))

;; method from the 'todomvc' example, to allow checking of the db spec as an interceptor
(defn check-and-throw
  "Throws an exception if `db` doesn't match the Spec `a-spec`."
  [a-spec db]
  (println "check-and-throw: a-spec=" a-spec ", db=" db)
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))

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
   ; (let [init-db db]
   ;   (when (not (db :vrubik-state))
   ;     (assoc init-db :vrubik-state {}))
   ;side effect
   (vrubik-scene/init)
   db))

; (reg-event-fx
;  :geb-cube-scene-init
;  (fn [{:keys []} [_ a]]
;    (geb-cube-scene/init)
;    {:http {:method :get}
;     :url    "http://json.my-endpoint.com/blah"
;     :on-success  [:process-blah-response]
;     :on-fail     [:failed-blah]}))
;     ; :db   (assoc db :flag true)}))

; (reg-fx         ;; <-- registration function
;   :geb-cube-scene-init
;   (fn [value]  ;;  <2> effect handler
;     (geb-cube-scene/init)))


; (re-frame/reg-event-db
;  :init-vrubik-scene
;  (fn [db _]
;    ;side effect
;    (vrubik-scene/init)
;    db))

(re-frame/reg-event-db
 :init-geb-cube-scene
 (fn [db _]
   ;side effect
   (geb-cube-scene/init)
   db))

(re-frame/reg-event-db
 :init-ut-simp-scene
 (fn [db _]
   ;side effect
   (ut-simp-scene/init)
   db))

; (re-frame/reg-event-db)
 ; :init-simp-scene
 ; (fn [db _]
 ;   ;side effect
 ;   ; (simp-scene/init)
 ;   (println "events: now in :init-simp-scene")
 ;   (simp-scene/init-once)
 ;   db))

; (re-frame/reg-event-db
;  :init-ut-simp-scene-2
;  (fn [db _]
;    ;side effect
;    (ut-simp-scene/init-2)
;    db))

(re-frame/reg-fx
 :init-simp-scene-fx
 (fn [_]
  ; (simp-scene/init)
  (simp-scene/init-once)))

(re-frame/reg-event-fx
  :init-simp-scene
  (fn [cofx _]
     {:init-simp-scene-fx nil}))

; (re-frame/reg-event-fx
;   :init-simp-scene
;   (fn [cofx _]
;      {:init-msg-cube-game-fx nil}))

(re-frame/reg-fx
 :init-msg-cube-scene-fx
 (fn [_]
  ; (simp-scene/init-once)
  (msg-cube.scene/init)))

; (re-frame/reg-event-fx
;   :init-msg-cube-scene
;   (fn [cofx _]
;      {:init-msg-cube-scene-fx nil}))

; (re-frame/reg-event-db
;  :init-msg-cube-scene
;  (fn [db _]
;    ;side effect
;    (msg-cube.scene/init)
;    db))

(re-frame/reg-fx
 :init-msg-cube-scene
 (fn [_]
   (msg-cube.scene/init)))

; (re-frame/reg-fx
;  :init-msg-cube-game-fx
;  (fn [_]
;   ; (simp-scene/init-once)
;   (msg-cube.game/init)))

(re-frame/reg-event-fx
  :init-msg-cube-game
  (fn [cofx _]
     {:db (msg-cube.game/init (:db cofx))
      ; :init-msg-cube-game-fx nil
      :init-msg-cube-scene nil}))

; (re-frame/reg-event-db
;   :init-msg-cube-game
;   (fn [db _]
;      ; {:init-msg-cube-game-fx nil
;      ;  :db}
;      ;; note: init is db effect and a side-effect
;      (msg-cube.game/init db)))
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

;; start defunct
; (re-frame/reg-event-db
;  :init-rubiks-cube
;  (fn [db [_]]
;    (vrubik-scene/init-rubiks-cube)
;    db))

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

; (re-frame/reg-event-db
;  :vrubik-left-side-rot-grid
;  (fn [db [_]]
;    (assoc db :rubiks-grid (vrubik-scene/rubiks-cube-left-side-rot-grid (db :rubiks-grid)))))

(re-frame/reg-event-db
 :vrubik-print-rubiks-grid
 (fn [db [_]]
   (vrubik-scene/print-rubiks-grid (db :rubiks-grid))
   db))
;; end defunct

(re-frame/reg-event-db
 :vrubik-create-left-side-anim-fwd
 (fn [db [_]]
   (vrubik-scene/create-left-side-anim-fwd (db :vrubik-grid) (db :vrubik-game-state))
   db))

(re-frame/reg-event-db
 :vrubik-run-left-side-anim-fwd
 (fn [db [_]]
   (vrubik-scene/run-left-side-anim-fwd (db :vrubik-grid))
   db))

(re-frame/reg-event-db
 :vrubik-left-side-anim-fwd
 (fn [db [_]]
   (vrubik-scene/left-side-anim-fwd)
   db))

(re-frame/reg-event-db
 :vrubik-update-grid
 (fn [db [_]]
   (assoc db :vrubik-grid (vrubik-scene/update-grid (db :vrubik-grid)))))

(re-frame/reg-event-db
 :vrubik-update-grid-2
 (fn [db [_ side]]
   (assoc db :vrubik-grid (vrubik-scene/update-grid-2 (db :vrubik-grid) side))))

(re-frame/reg-event-db
 :pretty-print-grid
 (fn [db _]
   ;side effect
   (vrubik-scene/pretty-print-vrubik-grid (db :vrubik-grid))
   db))

(re-frame/reg-event-db
 :vrubik-init-game-state
 (fn [db [_]]
   ; (assoc db :vrubik-state (vrubik-scene/init-game-state (db :vrubik-state)))
   ; (println "events.vrubik-init-game-state db=" db)
   (vrubik-scene/init-game-state db)))

(re-frame/reg-event-db
 :vrubik-set-side-rot
 (fn [db [_ side val]]
   (assoc-in db [:vrubik-game-state :rots](vrubik-scene/set-side-rot side val (get db :rots)))))
   ; db))

(re-frame/reg-event-db
 :vrubik-init-cells
 (fn [db [_]]
   ; (println "events.print-db: db=" db)
   (let [result (vrubik-scene/init-cells (db :vrubik-grid))]
     ; (println "init-cells result=" result)
     (assoc db :vrubik-grid result))))
   ; db))
;;
;; vrubik/box-grid
;;

;; not currently used
; (re-frame/reg-event-db
;  :init-grid
;  (fn [db [_ grid-key]]
;    (box-grid/init-grid db grid-key)
;    db))

; (re-frame/reg-event-db
;  :init-rubiks-grid
;  (fn [db [_]]
;    ; (let [r (box-grid/init-rubiks-grid db)]
;    ;   (println "events.init-rubiks-grid: result=" db))
;    ; db
;    ; (box-grid/init-rubiks-grid db)
;    ; (assoc db :rubiks-grid (box-grid/init-rubiks-grid db))
;    ; (let [r (assoc db :abc 7)]
;    ;   (println "events: r=" r)
;    ;   r)
;    (let [r (assoc db :rubiks-grid (box-grid/init-rubiks-grid))]
;      ; (println "events: r=" r)
;      r)))
;      ; (doall r))))
;      ; (js-debugger))))
;    ; (assoc db :abc 7)))
;; TODO prefix all these calls with 'vrubik-'
(re-frame/reg-event-db
 :init-vrubik-grid
 (fn [db [_]]
   (let [r (assoc db :vrubik-grid (box-grid/init-vrubik-grid))]
     r)))

(re-frame/reg-event-db
  :print-vrubik-grid
  (fn [db [_]]
    (box-grid/print-grid (db :vrubik-grid))
    db))

; (re-frame/reg-event-fx
;   :print-vrubik-grid
;   (fn [db [_]]
;     (box-grid/print-grid (db :vrubik-grid))))
;     ; db))

(re-frame/reg-event-db
  :vrubik-user-action
  (fn [db [_]]
    ; (assoc db :vrubik-grid (vrubik-scene/left-side-fwd (db :vrubik-grid)))))
    (assoc db :vrubik-grid (vrubik-scene/side-fwd (db :vrubik-grid) :left))))
    ; (assoc db :vrubik-grid (vrubik-scene/side-fwd (db :vrubik-grid) :top))))

(re-frame/reg-event-db
  :vrubik-user-action-2
  (fn [db [_]]
    ; (assoc db :vrubik-grid (vrubik-scene/left-side-fwd (db :vrubik-grid)))))
    ; (assoc db :vrubik-grid (vrubik-scene/side-fwd (db :vrubik-grid) :left))
    (assoc db :vrubik-grid (vrubik-scene/side-fwd (db :vrubik-grid) :top))))

(re-frame/reg-event-db
  :vrubik-left-side-fwd
  (fn [db [_]]
    ; (assoc db :vrubik-grid (vrubik-scene/left-side-fwd (db :vrubik-grid)))
    ; (assoc db :vrubik-grid (vrubik-scene/side-fwd (db :vrubik-grid) :left))
    (assoc db :vrubik-grid (vrubik-scene/side-fwd (db :vrubik-grid) :top))))

(re-frame/reg-event-db
  :vrubik-side-fwd
  (fn [db [_ side]]
    ; (assoc db :vrubik-grid (vrubik-scene/left-side-fwd (db :vrubik-grid)))
    ; (assoc db :vrubik-grid (vrubik-scene/side-fwd (db :vrubik-grid) :left))
    (assoc db :vrubik-grid (vrubik-scene/side-fwd (db :vrubik-grid) side))))
; (re-frame/reg-event-db
;   :vrubik-rot-cells
;   (fn [db [_]]
;     (vrubik-scene/rot-cells)
;     db))

;;localize-action-pending-cells
(re-frame/reg-event-db
  :vrubik-localize-action-pending-cells
  (fn [db [_]]
    (println "events.vrubik-localize-action-pending-cells")
    (vrubik-scene/localize-action-pending-cells (db :vrubik-grid))
    db))

(re-frame/reg-event-db
  :vrubik-toggle-cell-action-pending
  (fn [db [_]]
    (println "events.vrubik-toggle-cell-action-pending")
    (vrubik-scene/toggle-cell-action-pending)
    db))

;; combo call
(re-frame/reg-event-fx
  :vrubik-rot-cells-combo
  (fn [db [_]]
    ; (println "events.vrubik-rot-cells-combo")
    ; {dispatch-n (list [vrubik-localize-action-pending-cells (db :vrubik-grid)]
    ;                   [vrubik-toggle-cell-action-pending])}
    {:dispatch-n [[:vrubik-localize-action-pending-cells (db :vrubik-grid)]
                  [:vrubik-toggle-cell-action-pending]]}))
    ; (dispatch-n [[vrubik-localize-action-pending-cells (db :vrubik-grid)
    ;               [vrubik-toggle-cell-action-pending]]])
                      ; [vrubik-rot-cells])}
    ; (println "events.vrubik-rot-cells-combo")
    ; (vrubik-scene/localize-action-pending-cells (db :vrubik-grid))
    ; db))

;;
;; geb-cube-scene
;;
(re-frame/reg-event-db
  :run-geb-cube-scene
  (fn [db [_]]
    (geb-cube-scene/run-scene)
    db))

;;
;; ut-simp
;;
(re-frame/reg-event-db
  :run-ut-simp-scene
  (fn [db [_]]
    (ut-simp-scene/run-scene)
    db))

; (re-frame/reg-event-db
;   :run-ut-simp-scene-2
;   (fn [db [_]]
;     (ut-simp-scene/run-scene-2)
;     db))

; (re-frame/reg-event-db
;   :add-msg-box
;   (fn [db [_ msg-boxes-atom]]
;     (msg-box/add-msg-box msg-boxes-atom)
;     db))

(re-frame/reg-event-db
  :add-msg-box-phys
  (fn [db [_ msg-box]]
    (msg-box-phys/add-msg-box-phys msg-box)
    db))

; (reg-event-fx                              ;; <1>
;    :my-event
;    (fn [{:keys [db]} [_ a]]                ;; <2>
;       {:db  (assoc db :flag true)          ;; <3>
;        :dispatch [:do-something-else 3]}))
(re-frame/reg-event-fx
 :dummy
 (fn [db [_]]
   (println "**DUMMY***")
   db))

; (re-frame/reg-event-fx
;    :add-msg-box
;    ; (fn [{:keys [db]} [_ msg-box]])
;    (fn [{:keys [db]} [_ msg-box]]
;       ; {:db  (assoc db :msg-boxes-2 (conj (db :msg-boxes-2) msg-box))}
;       ; {:db  (assoc-in db [::msg/msgs ::msg/msg-boxes] (conj (get-in db [::msg/msgs ::msg/msg-boxes]) msg-box))}
;       ; {:db  (assoc-in db [:msgs ::msg/msg-boxes] (conj (get-in db [:msgs ::msg/msg-boxes]) msg-box))}
;       {:db (let [db-change-1 (assoc-in db [:msgs ::msg/msg-boxes] (conj (get-in db [:msgs ::msg/msg-boxes]) msg-box))
;                  db-change-2 (let [max-id (get-in db [:msgs ::msg/max-id])]
;                                (println "***hello, max-id=" max-id)
;                                (assoc-in db-change-1 [:msgs ::msg/max-id] (+ max-id 1)))]
;              db-change-2)
;        ; :db (let [max-id (get-in db [:msgs ::msg/max-id])])
;        :dispatch [:add-msg-box-phys msg-box]}))
;        ; :dispatch-2 [:dummy]}))

(re-frame/reg-event-fx
   :add-msg
   ; (fn [{:keys [db]} [_ ::msg/text ::msg/msg-level]])
   (fn [{:keys [db]} [_ text msg-level]]
     (let [max-id (get-in db [:msgs ::msg/max-id])
           msg-boxes (get-in db [:msgs ::msg/msg-boxes])
           new-msg-box {::msg/box-id max-id ::msg/msg {::msg/text text ::msg/msg-level msg-level}}]
       ; (println "***add-msg: msg-boxes=" msg-boxes)
       {:db
        (let [
              ; msg-boxes (db [:msgs ::msg/msg-boxes])
              db-change-1 (assoc-in db [:msgs ::msg/msg-boxes]
                                    (conj msg-boxes new-msg-box))
              db-change-2 (assoc-in db-change-1 [:msgs ::msg/max-id] (+ max-id 1))]
          ; (println "***add-msg: db-change-1=" db-change-1)
          db-change-2)
        :dispatch [:add-msg-box-phys new-msg-box]})))


; (re-frame/reg-event-db
;   :add-msg-cube-ph-2
;   (fn [db [_ msg-box]]
;     (msg-cube-ph/add-msg-cube-ph msg-box)
;     db))

; (re-frame/reg-event-db
;  :set-msg-boxes-atom
;  (fn [db [_ msg-boxes-atom]]
;    (assoc db :msg-boxes-atom msg-boxes-atom)))

(re-frame/reg-event-db
 ; :init-msg-boxes
 :init-msgs
 (fn [db [_]]
   ; (assoc db :msg-boxes-atom-2 (atom []))
   ; (assoc db :msg-boxes-2 [])
   (assoc db :msgs {::msg/max-id 0 ::msg/msg-boxes []})))
   ; (assoc db :msgs ::msg/msgs)))

(re-frame/reg-event-db
  :simp-ut-action-1
  (fn [db [_]]
    (msg/print-msg-boxes db)
    db))

(re-frame/reg-event-db
  :simp-ut-action-2
  (fn [db [_]]
    ; (let [max-id (get-in db [:msgs ::msg/max-id])]
    ;   (println "simp-ut-action-2: max-id=" max-id)
    ;   (msg-box-phys/add-msg-box-phys {::msg/box-id (+ max-id 0), ::msg/msg {}}))
    (re-frame/dispatch [:add-msg "new" :INFO])
    db))

;; msg-cube
;; I think an effect handler is user to indicate an effect that may (e.g dispatch)
;; other effects.  If it's just an "ordinary" effect, then dipatch an event
(re-frame/reg-fx
 :run-msg-cube-scene-fx
 (fn [_]
  (msg-cube.scene/run-scene)))

;; Since this is a single effect, we specify it as an event handler.
(re-frame/reg-event-fx
 :run-msg-cube-scene-evt
 (fn [_]
  (msg-cube.scene/run-scene)))

(re-frame/reg-event-fx
  :run-msg-cube-scene
  (fn [cofx _]
     ;; following works
     ; {:run-msg-cube-scene-fx nil}))
     ;; ':fx' works, but need to be on a current version
     ; {:db nil}))
     ; {:fx [[:dispatch [:run-msg-cube-scene-evt]]]}))
     {:fx [[:run-msg-cube-scene-fx]]}))
     ;; Note: definitely need double vectors on :fx calls
     ; {:fx [:run-msg-cube-scene-fx]}))
     ;; following works
     ; {:dispatch [:run-msg-cube-scene-evt]}))

(re-frame/reg-fx
 :run-msg-cube-game-fx
 (fn [_]
  (msg-cube.game/run)))

(re-frame/reg-event-fx
  :run-msg-cube-game
  (fn [cofx _]
     ; {:run-msg-cube-game-fx nil}
     {:fx [[:run-msg-cube-game-fx nil]]}))

;; simp-scene
(re-frame/reg-event-db
  :run-simp-scene
  (fn [db [_]]
    (simp-scene/run-scene)
    db))

;; event utils
(re-frame/reg-event-db
 :print-db
 (fn [db [_]]
   (println "events.print-db: db=" db)
   db))

;;
;; cell
;;
; (re-frame/reg-event-db
;  :cell-init
;  (fn [db [_]]
;    ; (println "events.print-db: db=" db)
;    (cell/init)
;    db))

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

;; skyscrapers-scene
(re-frame/reg-event-db
 :init-skyscrapers-scene
 (fn [db _]
   ;side effect
   (skyscrapers-scene/init)
   db))

(re-frame/reg-event-db
  :run-skyscrapers-scene
  (fn [db [_]]
    (skyscrapers-scene/run-scene)
    db))

;; msg-cube.data.msg events
; (def msg-cube-check-spec-interceptor (after (partial check-and-throw :msg-cube.spec/db-spec)));
(def msg-cube-check-spec-interceptor (after (partial check-and-throw ::msg-cube.spec/db-spec)));

(re-frame/reg-event-db
 ::initialize-msg-cube-db
 (fn [db _]
   db/default-db))

(re-frame/reg-event-db
  :gen-msg
  (fn [db [_]]
    ; (msg-cube.msg/gen {:id 1, :text "hi"})
    (msg-cube.msg/gen {:id 1, :text "hi"})
    db))

(re-frame/reg-event-db
  :msg-cube.add-msg
  (fn [db [_ msg]]
    (msg-cube.game/add-msg msg db)))

(re-frame/reg-event-db
  :msg-cube.add-msg-2
  (fn [db [_ msg]]
    (msg-cube.game/add-msg-2 msg db)))

(re-frame/reg-event-db
  :msg-cube.inc-max-id
  (fn [db [_ msg]]
    (msg-cube.game/inc-max-id db)))

;; add a physical cube to the bjs scene
(re-frame/reg-event-fx
   :add-msg-cube
   (fn [cofx [_ msg]]
     ; (println ":add-msg-cube: id=" id)
     (println ":add-msg-cube: msg=" msg)
     ; {:fx [[msg-cube.scene/add-msg-cube (-> (cofx :db) :max-id)]]}
     ; {:fx [[msg-cube.scene/add-msg-cube (get-in cofx [:db :max-id])]]}
     ; {:fx [[msg-cube.scene/add-msg-cube id]]}
     ; {:fx [(msg-cube.scene/add-msg-cube id)]}
     {:fx [(msg-cube.scene/add-msg-cube msg)]}))

(re-frame/reg-event-fx
   :add-msg-cube-2
   (fn [cofx [_ msg]]
     (println ":add-msg-cube-2: msg=" msg)
     {:fx [(msg-cube.scene/add-msg-cube-2 msg)]}))

(reg-event-db
 :msg-cube.inc-level
 ; (fn [db [_ id msgs]])
 (fn [db [_ id]]
  ; (println "events: msg-cube.up-msg-severity id=" id)
  ; (msg-cube.msg/inc-level id (db :msgs))
  (assoc db :msgs (msg-cube.msg/inc-level id (db :msgs)))))

(reg-event-db
 :msg-cube.inc-level-2
 (fn [db [_ id]]
  (assoc db :msgs-2 (msg-cube.msg/inc-level-2 id (db :msgs-2)))))

(reg-event-db
 :msg-cube.set-level
 ; (fn [db [_ id level msgs]])
 (fn [db [_ id level]]
  ; (println "events: msg-cube.up-msg-severity id=" id)
  (assoc db :msgs (msg-cube.msg/set-level id level (db :msgs)))))

(reg-event-fx
 :msg-cube.update-msg-cube
 (fn [cofx [_ id level text]]
   (println "events.msg-cube.update-msg-cube: id=" id)
   ; {:fx [[:dispatch]]}
   ; {:fx [[(msg-cube.scene/update-msg-cube id level text)]]}
   {:fx [(msg-cube.scene/update-msg-cube id level text)]}))

(reg-event-fx
 :msg-cube.scene.add-msg-cube
 (fn [cofx [_ msg]]
   (println "events.msg-cube.scene.update-msg-cube: msg=" msg)
   ; {:fx [[:dispatch]]}
   ; {:fx [[(msg-cube.scene/update-msg-cube id level text)]]}
   {:fx [(msg-cube.scene/add-msg-cube msg)]}))
;; add a dummy element to div 'msg-box-proxies'
; (reg-event-fx
;  :msg-cube.add-element
;  (fn [cofx [_]]
;    (println "events.msg-cube.add-element:")
;    {:fx [(msg-cube.msg/add-element)]}))

(reg-event-db
 :msg-cube.add-ints
 [msg-cube-check-spec-interceptor]
 (fn [db [_]]
   (println "events.msg-cube.add-ints:")
   (msg-cube.msg/add-ints db)))

(reg-event-db
 :msg-cube.update-input-id
 (fn [db [_ id]]
   (println "events.update-input-id: id=" id)
   (assoc db :input-id id)))

(reg-event-db
 :msg-cube.init-db
 (fn [db [_ id]]
   (println ":msg-cube.init-db: now running")
   (msg-cube.game/init-db db)))


;;; now we create an interceptor using `after`
; (def check-spec-interceptor (after (partial check-and-throw :todomvc.db/db)));

; (reg-event-db
;  :init-twizzlers-game
;  (fn [db [_ id]]
;    (println ":msg-cube.init-db: now running")
;    db))
;    ; (msg-cube.game/init-db db)))
;
; (reg-event-fx
;  :init-twizzlers-game-fx
;  (fn [_]
;    (twizzlers.game/init)))
;
; (reg-event-fx
;  :msg-cube.abc
;  (fn [db [_ id]]
;    (println ":msg-cube.abc: now running")
;    db))
;    ; (msg-cube.game/init-db db)))
; (println "at end of events")
