;; game is refer to many, referred by few.
(ns cube-test.frig-frog.game
  (:require
   [re-frame.core :as re-frame]
   ; [babylonjs :as bjs]
   ; [cube-test.msg-cube.spec.db :as msg-cube.spec]
   [cube-test.utils :as utils]
   [cube-test.main-scene :as main-scene]
   [cube-test.frig-frog.scene-l1 :as ff.scene-l1]
   [cube-test.frig-frog.frog :as frog]
   [cube-test.frig-frog.player :as player]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.utils.fps-panel :as fps-panel]
   ; [cube-test.base :as base]
   [cube-test.utils.common :as common]))

(def default-game-db
  {:game-abc 7
   :n-cols 8 :n-rows 8
   :btm-board {}
   :top-board {}
   :active-scene :ff-l1
   :dev-mode false
   :quanta-width 1.2})

(def active-scene)
(def quanta-width nil)

; (defn change-abc [new-val db]
(defn change-abc [db new-val]
  (prn "game.change-abc: new-val=" new-val)
  (prn "game.change-abc: db=" db)
  (prn "game.change-abc: db.abc=" (db :abc))
  (assoc db :abc new-val))

(defn main-btn-handler [stateObject]
  (when (.-pressed stateObject)))

(defn secondary-btn-handler [stateObject]
  (when (.-pressed stateObject)))
    ; (prn "frig-frog.game: secondary-btn-handler: stateObject=" stateObject)))

(defn a-btn-handler [stateObject]
  (when (.-pressed stateObject)
    (re-frame/dispatch [:cube-test.frig-frog.events/reset-view])))

(defn b-btn-handler [stateObject]
  (when (.-pressed stateObject)
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
  (-> webVRController (.-onMainButtonStateChangedObservable) (.add main-btn-handler))
  (-> webVRController (.-onAButtonStateChangedObservable) (.add a-btn-handler))
  (-> webVRController (.-onBButtonStateChangedObservable) (.add b-btn-handler)))

(defn init-ctrl []
  (prn "frig-frog.game: init-ctrl entered"))

(defn init []
  ; (re-frame/console :warn "console msg from frig-frog.game.init")
  (set! active-scene (:active-scene default-game-db))
  (when-let [vrHelper main-scene/vrHelper]
    (-> vrHelper .-onAfterEnteringVRObservable (.add init-ctrl))
    (-> vrHelper .-onControllerMeshLoadedObservable (.add ctrl-mesh-loaded-handler)))
  (re-frame/dispatch [:cube-test.frig-frog.events/init-worker]) ;; necessary?
  (re-frame/dispatch [:cube-test.frig-frog.events/ff-worker-start])
  (re-frame/dispatch [:cube-test.frig-frog.events/init-boards])
  (re-frame/dispatch [:cube-test.frig-frog.events/init-btm-board])
  (re-frame/dispatch [:cube-test.frig-frog.events/init-top-board])
  (re-frame/dispatch [:cube-test.frig-frog.events/init-player]))

;; This is the top-level tick for the (sub) game
(defn render-loop []
  (if (= main-scene/xr-mode "vr")
    (controller/tick)
    (controller-xr/tick))
  (if fps-panel/fps-pnl
    (fps-panel/tick main-scene/engine))
  ; (frog/tick)
  (player/tick)
  (.render main-scene/scene)
  (condp = active-scene
    ; :scene-l1  (ff.scene-l1/tick)
    :ff-l1  (ff.scene-l1/tick)))

(defn run-game []
  (.runRenderLoop main-scene/engine (fn [] (render-loop))))
