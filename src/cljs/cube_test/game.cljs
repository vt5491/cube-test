;; game is refer to many, referred by few
(ns cube-test.game
  (:require
   [re-frame.core :as re-frame]
   [cube-test.main-scene :as main-scene]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.cube-fx :as cube-fx]
   [cube-test.utils.fps-panel :as fps-panel]))

(declare render-loop)

(defn init []
  (println "game.init: entered")
  (re-frame/dispatch [:init-main-scene])
  (re-frame/dispatch [:init-cube-fx])
  (re-frame/dispatch [:init-fps-panel])
  (re-frame/dispatch [:run-main-scene]))

; (defn run-scene [render-loop]
;   (.runRenderLoop engine (fn [] (render-loop))))
(defn run-scene []
  (.runRenderLoop main-scene/engine (fn [] (render-loop))))
;;
;;
;; main tick handler best placed in game.cljs (refer to many, referred by few)
;; instead of main_scene (refer to few, referred by many) since we will
;; need to potentially call all other namespaces.
(defn render-loop []
  (if (= main-scene/xr-mode "vr")
    (controller/tick)
    (controller-xr/tick))
  (cube-fx/tick)
  (fps-panel/tick main-scene/engine)
  (.render main-scene/scene))
