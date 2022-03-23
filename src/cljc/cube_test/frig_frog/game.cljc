;; game is refer to many, referred by few.
(ns cube-test.frig-frog.game
  (:require
   [re-frame.core :as re-frame]
   ; [babylonjs :as bjs]
   ; [cube-test.msg-cube.spec.db :as msg-cube.spec]
   ; [cube-test.utils :as utils]
   [cube-test.main-scene :as main-scene]
   [cube-test.frig-frog.scene-l1 :as ff.scene-l1]
   [cube-test.frig-frog.frog :as frog]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.utils.fps-panel :as fps-panel]
   ; [cube-test.base :as base]
   [cube-test.utils.common :as common]))

(def default-game-db
  {:game-abc 7
   ; :n-rows 3 :n-cols 4
   :n-cols 8 :n-rows 8
   ; :n-cols 10 :n-rows 10
   :board {}
   :active-scene :ff-l1
   :dev-mode false
   :quanta-width 1.2})

; (defn dmy []
;   8)
(def active-scene)
; (def quanta-width 1.2)
(def quanta-width nil)

; (defn change-abc [new-val db]
(defn change-abc [db new-val]
  (prn "game.change-abc: new-val=" new-val)
  (prn "game.change-abc: db=" db)
  (prn "game.change-abc: db.abc=" (db :abc))
  (assoc db :abc new-val))

; (defn init-game-db [game-db]
;   ; (re-frame/dispatch [:common/init-game-db d])
;   (re-frame/dispatch [:cube-test.events/init-game-db default-game-db]))
;   ; default-game-db)

(defn init-ctrl-2 [webVRController]
  (prn "frig-frog.game: init-ctrl-2 entered: webVRController=" webVRController))

(defn main-btn-handler [stateObject]
  (when (.-pressed stateObject)
    (prn "frig-frog.game: main-btn-handler: pressed stateObject=" stateObject)))

(defn secondary-btn-handler [stateObject]
  (when (.-pressed stateObject)
    (prn "frig-frog.game: secondary-btn-handler: stateObject=" stateObject)))

(defn a-btn-handler [stateObject]
  (when (.-pressed stateObject)
    (prn "frig-frog.game: b-btn-handler: stateObject=" stateObject)
    (re-frame/dispatch [:cube-test.frig-frog.events/reset-view])))

(defn b-btn-handler [stateObject]
  (when (.-pressed stateObject)
    (prn "frig-frog.game: a-btn-handler: stateObject=" stateObject)
    (re-frame/dispatch [:cube-test.frig-frog.events/toggle-dev-mode])))

(defn start-btn-handler [stateObject]
  (prn "frig-frog.game: start-btn-handler: stateObject=" stateObject))

(defn thumb-btn-handler [stateObject]
  (prn "frig-frog.game: thumb-btn-handler: stateObject=" stateObject))

(defn pad-btn-handler [stateObject]
  (prn "frig-frog.game: pad-btn-handler: stateObject=" stateObject))

(defn btn-handler [arg]
  (prn "frig-frog.game: btn-handler: arg=" arg))

(defn ctrl-mesh-loaded-handler [webVRController]
  ;; x and a btns
  (prn "frig-frog.game: ctrl-mesh-loaded-handler: webVRController=" webVRController)
  (-> webVRController (.-onMainButtonStateChangedObservable) (.add main-btn-handler))
  ; (-> webVRController (.-onPadValuesChangedObservable) (.add start-btn-handler))
  ; (-> webVRController (.-onPadStateChangedObservable) (.add pad-btn-handler))
  ; (-> webVRController (.-onThumbRestChangedObservable) (.add thumb-btn-handler))
  ; (-> webVRController (.-onSecondaryButtonStateChangedObservable) (.add secondary-btn-handler))
  (-> webVRController (.-onAButtonStateChangedObservable) (.add a-btn-handler))
  (-> webVRController (.-onBButtonStateChangedObservable) (.add b-btn-handler))
  ; (-> webVRController (.-onMenuButtonStateChangedObservable) (.add pad-btn-handler))
  ; (-> webVRController (.-onSecondaryTriggerStateChangedObservable) (.add start-btn-handler))
  ; (-> webVRController (.-onTriggerStateChangedObservable) (.add start-btn-handler))
  ; (-> webVRController (.onButtonStateChange btn-handler)))
  ; onSecondaryTriggerStateChangedObservable
  (prn "frig-frog: done setting hooks"))
  ; (js-debugger))

(defn init-ctrl []
  (prn "frig-frog.game: init-ctrl entered"))
  ; (prn "frig-frog.game: init-ctrl: left-ctrl=" (.-leftController main-scene/camera)))
  ; (when-let [vrHelper main-scene/vrHelper]
  ;   (prn "frig-frog.game.init-ctrl: adding mesh loaded observable to vrHelper")
  ;   (-> vrHelper .-onControllerMeshLoadedObservable (.add ctrl-mesh-loaded-handler))))
    ; (-> vrHelper (.-onMainButtonStateChangedObservable) (.add ctrl-mesh-loaded-handler))
    ; (js-debugger)
    ; (-> vrHelper (.-onMainButtonStateChangedObservable) (.add main-btn-handler))))

;; init game level overrides of the default controller actions.
; (defn init-ctrl-handlers [vrHelper]
;   (println "game.init-ctrl-handlers: entered")
;   (-> vrHelper (.-onControllerMeshLoaded) (.add ctrl-mesh-loaded-handler)))

(defn init []
  (println "frig-frog.game.init: entered")
  (re-frame/console :warn "console msg from frig-frog.game.init")
  ; (re-frame/dispatch [:frig-frog-db/init-game-level-db])
  ; (re-frame/dispatch [:cube-test.frig-frog.events/init-game-db])
  (set! active-scene (:active-scene default-game-db))
  (when-let [vrHelper main-scene/vrHelper]
    (prn "frig-frog.game: adding observable to vrHelper")
    (-> vrHelper .-onAfterEnteringVRObservable (.add init-ctrl))
    (-> vrHelper .-onControllerMeshLoadedObservable (.add ctrl-mesh-loaded-handler)))
    ; (-> vrHelper .-onControllerMeshLoaded (.add init-ctrl-2))
    ; (-> vrHelper .-onControllerMeshLoadedObservable (.add ctrl-mesh-loaded-handler)))
    ; (-> vrHelper .-onControllerMeshLoaded (.add ctrl-mesh-loaded-handler))
    ; (-> vrHelper .-onAfterEnteringVRObservable (.add ctrl-mesh-loaded-handler)))
  (re-frame/dispatch [:cube-test.frig-frog.events/init-worker])
  ; (prn "game.init: pre-init-board db=" (re-frame/dispatch [:db-hook]))
  (re-frame/dispatch [:cube-test.frig-frog.events/init-board])
  ; (prn "game.init: post-init-board db=" (re-frame/dispatch [:db-hook]))
  ; (re-frame/dispatch [:cube-test.events/init-game-db default-game-db]))
  (re-frame/dispatch [:cube-test.frig-frog.events/init-frog 0 0]))

;; This is the top-level tick for the (sub) game
(defn render-loop []
  (if (= main-scene/xr-mode "vr")
    (controller/tick)
    (controller-xr/tick))
  (if fps-panel/fps-pnl
    (fps-panel/tick main-scene/engine))
  (frog/tick)
  (.render main-scene/scene)
  (condp = active-scene
    ; :scene-l1  (ff.scene-l1/tick)
    :ff-l1  (ff.scene-l1/tick)))

(defn run-game []
  (.runRenderLoop main-scene/engine (fn [] (render-loop))))
