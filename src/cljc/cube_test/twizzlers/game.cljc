(ns cube-test.twizzlers.game
  (:require
   [re-frame.core :as re-frame]
   ; [cube-test.msg-cube.spec.db :as msg-cube.spec]
   [cube-test.utils :as utils]
   [cube-test.main-scene :as main-scene]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.utils.fps-panel :as fps-panel]))
   ; [cube-test.twizzlers.events :as twizzler-events]))

(defn render-loop []
  (if (= main-scene/xr-mode "vr")
    (controller/tick)
    (controller-xr/tick))
  (if fps-panel/fps-pnl
    (fps-panel/tick main-scene/engine))
  (.render main-scene/scene))

(defn run-game []
  ; (run-render-loop)
  (.runRenderLoop main-scene/engine (fn [] (render-loop))))

(defn init []
  (println "twizzlers.game.init: entered")
  ; (re-frame/dispatch [::twizzler-events/init-scene])
  ; (re-frame/dispatch [:cube-test.twizzlers.events/init-scene])
  (re-frame/dispatch [:cube-test.twizzlers.events/init-scene]))
