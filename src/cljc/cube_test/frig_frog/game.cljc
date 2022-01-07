(ns cube-test.frig-frog.game
  (:require
   [re-frame.core :as re-frame]
   ; [cube-test.msg-cube.spec.db :as msg-cube.spec]
   ; [cube-test.utils :as utils]
   [cube-test.main-scene :as main-scene]
   ; [cube-test.frig-frog.scene :as frig-frog-scene]
   [cube-test.frig-frog.frog :as frog]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.utils.fps-panel :as fps-panel]
   [cube-test.utils.common :as common]))

(def default-game-db
  {:game-abc 7
   :n-rows 3 :n-cols 4
   ; :n-cols 10 :n-rows 10
   :board {}})

(defn dmy []
  8)

; (defn change-abc [new-val db]
(defn change-abc [db new-val]
  (prn "game.change-abc: new-val=" new-val)
  (prn "game.change-abc: db=" db)
  (prn "game.change-abc: db.abc=" (db :abc))
  (assoc db :abc new-val))

;; This is the top-level tick for the (sub) game
(defn render-loop []
  (if (= main-scene/xr-mode "vr")
    (controller/tick)
    (controller-xr/tick))
  (if fps-panel/fps-pnl
    (fps-panel/tick main-scene/engine))
  (frog/tick)
  (.render main-scene/scene))

(defn run-game []
  (.runRenderLoop main-scene/engine (fn [] (render-loop))))

(defn init-game-db [game-db]
  ; (re-frame/dispatch [:common/init-game-db d])
  (re-frame/dispatch [:cube-test.events/init-game-db default-game-db]))
  ; default-game-db)

(defn init-ctrl []
  (prn "frig-frog.game: init-ctrl entered")
  (prn "frig-frog.game: init-ctrl: left-ctrl=" (.-leftController main-scene/camera)))
  ; (js-debugger))

(defn init-ctrl-2 [webVRController]
  (prn "frig-frog.game: init-ctrl-2 entered: webVRController=" webVRController))

(defn init []
  (println "frig-frog.game.init: entered")
  (re-frame/console :warn "console msg from frig-frog.game.init")
  ; (re-frame/dispatch [:frig-frog-db/init-game-level-db])
  ; (re-frame/dispatch [:cube-test.frig-frog.events/init-game-db])
  (init-game-db default-game-db)
  ; (init-ctrl)
  ; (let [
  ;       ; scene main-scene/scene
  ;       ; vrHelper main-scene/scene.vrHelper
  ;       vrHelper main-scene/vrHelper]
  ;   ; (js-debugger)
  ;   (prn "frig-frog.game: vrHelper=" vrHelper))
  ; (when vrHelper
  ;   ;; yes, we are coupling this app to vr only, since I don't have a good xr test environment
  ;   ;; at the moment.
  ;   (prn "frig-frog.game: adding observable to vrHelper")
  ;   (-> vrHelper .-onAfterEnteringVRObservable (.add init-ctrl)))
  (when-let [vrHelper main-scene/vrHelper]
    (prn "frig-frog.game: adding observable to vrHelper")
    (-> vrHelper .-onAfterEnteringVRObservable (.add init-ctrl))
    (-> vrHelper .-onControllerMeshLoaded (.add init-ctrl-2)))
  (re-frame/dispatch [:cube-test.frig-frog.events/init-board])
  ; (re-frame/dispatch [:cube-test.events/init-game-db default-game-db]))
  (re-frame/dispatch [:cube-test.frig-frog.events/init-frog 0 0]))
