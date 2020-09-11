;; game is refer to many, referred by few
(ns cube-test.game
  (:require
   [re-frame.core :as re-frame]
   [cube-test.main-scene :as main-scene]
   [cube-test.utils.fps-panel :as fps-panel]
   [cube-test.base :as base]))

(declare render-loop)

(defn init []
  (println "game.init: entered")
  (condp = base/top-level-scene
    :cube-spin-scene (do
                           (println "top-level-scene=cube-spin-scene")
                           (re-frame/dispatch [:init-main-scene (fn [] (re-frame/dispatch [:init-cube-spin-scene]))])
                           (re-frame/dispatch [:init-cube-fx])
                           (re-frame/dispatch [:init-fps-panel main-scene/scene])
                           (re-frame/dispatch [:run-cube-spin-scene]))
    :face-slot-scene (do
                       (println "top-level-scene= face-slot-scene")
                       (re-frame/dispatch [:init-main-scene (fn [] (re-frame/dispatch [:init-face-slot-scene]))])
                       (re-frame/dispatch [:init-fps-panel main-scene/scene])
                       (re-frame/dispatch [:run-face-slot-scene]))
    :tic-tac-attack-scene (do
                            (println "top-level-scene= tic-tac-attack-scene")
                            (re-frame/dispatch [:init-main-scene(fn [] (re-frame/dispatch [:init-tic-tac-attack-scene]))])
                            (re-frame/dispatch [:init-fps-panel main-scene/scene])
                            (re-frame/dispatch [:run-tic-tac-attack-scene]))
    :vrubik-scene (do
                            (println "top-level-scene= vrubik-scene")
                            (re-frame/dispatch [:init-main-scene(fn [] (re-frame/dispatch [:init-vrubik-scene]))])
                            (re-frame/dispatch [:init-fps-panel main-scene/scene])
                            (re-frame/dispatch [:run-vrubik-scene]))
    :geb-cube-scene (do
                            (println "top-level-scene= geb-cube-scene")
                            (re-frame/dispatch [:init-main-scene(fn [] (re-frame/dispatch [:init-geb-cube-scene]))])
                            (re-frame/dispatch [:init-fps-panel main-scene/scene])
                            (re-frame/dispatch [:run-geb-cube-scene]))))
;;
;; main tick handler best placed in game.cljs (refer to many, referred by few)
;; instead of main_scene (refer to few, referred by many) since we will
;; need to potentially call all other namespaces.
; (defn render-loop []
;   (if (= main-scene/xr-mode "vr")
;     (controller/tick)
;     (controller-xr/tick))
;   (cube-fx/tick)
;   (fps-panel/tick main-scene/engine)
;   (.render main-scene/scene))
