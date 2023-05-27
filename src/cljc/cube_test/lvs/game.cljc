;; game is refer to many, referred by few.
(ns cube-test.lvs.game
  (:require
   [re-frame.core :as rf]
   ; [babylonjs :as bjs]
   [cube-test.utils :as utils]
   [cube-test.main-scene :as main-scene]
   [cube-test.lvs.scenes.main :as lvs-main-scene]
   [cube-test.lvs.scenes.reflect-scene :as lvs-reflect-scene]
   [cube-test.utils.fps-panel :as fps-panel]
   [cube-test.controller-xr :as controller-xr]))

(def active-scene)

(defn init [db]
  ;; (set! active-scene (:active-scene default-game-db))
  ;; (set! active-scene :reflect-scene)
  (set! active-scene (:default-scene db))
  (prn "lvs-game.init: entered, db=" db)
  (prn "lvs-game.init: default-scene=" (:default-scene db))
  (case (:default-scene db)
    :lvs-main (do
                (prn "calling lvs-main") 
                (lvs-main-scene/init))
    :lvs-reflect (do
                    (lvs-reflect-scene/init))))
                    ;; (lvs-reflect-scene/run-scene))))

(defn render-loop []
  ;; (if (= main-scene/xr-mode "vr")
  ;;   (controller/tick)
  (controller-xr/tick)
  (if fps-panel/fps-pnl
    (fps-panel/tick main-scene/engine))
  (case active-scene
     :lvs-reflect (lvs-reflect-scene/tick)
     :lvs-main (lvs-main-scene/tick))
  (.render main-scene/scene))

(defn run-game []
  ; (run-render-loop)
  (.stopRenderLoop main-scene/engine)
  (.runRenderLoop main-scene/engine (fn [] (render-loop))))
