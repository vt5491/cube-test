(ns cube-test.beat-club.game
  (:require
   [re-frame.core :as re-frame]
   ; [cube-test.msg-cube.spec.db :as msg-cube.spec]
   [cube-test.utils :as utils]
   [cube-test.main-scene :as main-scene]
   [cube-test.beat-club.scene :as beat-club-scene]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.utils.fps-panel :as fps-panel]))

(defn render-loop []
  (if (= main-scene/xr-mode "vr")
    (controller/tick)
    (controller-xr/tick))
  (if fps-panel/fps-pnl
    (fps-panel/tick main-scene/engine))
  ; (beat-club-scene/tick)
  (.render main-scene/scene))

(defn run-game []
  (.runRenderLoop main-scene/engine (fn [] (render-loop))))

(defn init []
  (println "beat-club.game.init: entered")
  (re-frame/console :warn "console msg from beat-club.game.init"))
