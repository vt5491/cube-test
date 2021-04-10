;; game is refer to many, referred by few
(ns cube-test.game
  (:require
   [re-frame.core :as re-frame]
   [cube-test.main-scene :as main-scene]
   [cube-test.utils.fps-panel :as fps-panel]
   [cube-test.base :as base]
   [cube-test.utils :as utils]
   [cube-test.dummy-base :as dummy-base]
   ; [cube-test.dummy-base :as dummy-base]))
   [cube-test.twizzlers.events :as twizzler-events]))

(declare render-loop)

(defn init []
  (println "game.init: entered, top-level-scene=" base/top-level-scene)
  (re-frame.core/set-loggers!  {:warn utils/rf-odoyle-warn-override-logger})
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
                            (re-frame/dispatch [:run-geb-cube-scene]))
    :skyscrapers-scene (do
                            (println "top-level-scene= skyscrapers-scene")
                            (re-frame/dispatch [:init-main-scene(fn [] (re-frame/dispatch [:init-skyscrapers-scene]))])
                            (re-frame/dispatch [:init-fps-panel main-scene/scene])
                            (re-frame/dispatch [:run-skyscrapers-scene]))
    :ut-simp-scene (do
                            (println "top-level-scene= ut-simp-scene")
                            (re-frame/dispatch [:init-main-scene(fn [] (re-frame/dispatch [:init-ut-simp-scene]))])
                            ; (re-frame/dispatch [:init-ut-simp-scene-2])
                            (re-frame/dispatch [:init-fps-panel main-scene/scene])
                            (re-frame/dispatch [:run-ut-simp-scene]))
                            ; (re-frame/dispatch [:run-ut-simp-scene-2]))
    :simp-scene (do
                           (println "top-level-scene2= simp-scene")
                           (re-frame/dispatch [:init-simp-scene])
                           (re-frame/dispatch [:run-simp-scene]))
    :msg-cube (do
                           (println "top-level-scene=msg-cube")
                           (re-frame/dispatch [:init-main-scene
                                               (fn [] (do
                                                        (re-frame/dispatch [:msg-cube.init-db])
                                                        (re-frame/dispatch [:init-msg-cube-game])
                                                        (re-frame/dispatch [:run-msg-cube-game])))])
                           (re-frame/dispatch [:init-fps-panel main-scene/scene]))
                           ; (re-frame/dispatch [:run-msg-cube-game]))))
                           ; (re-frame/dispatch [:run-msg-cube-scene]))))
    :twizzlers (do
                           (println "top-level-scene=twizzlers")
                           (re-frame/dispatch [:init-main-scene
                                               (fn [] (do
                                                        (re-frame/dispatch [::twizzler-events/init-db])
                                                        ; (re-frame/dispatch [::init-twizzlers-game])
                                                        ; (re-frame/dispatch [:twizzler-events/init-twizzlers-game])
                                                        ; (re-frame/dispatch [:init-twizzlers-game])
                                                        (re-frame/dispatch [::twizzler-events/init-game])
                                                        (re-frame/dispatch [::twizzler-events/run-game])))])
                                                        ; (re-frame/dispatch [:init-twizzlers-game-fx])
                                                        ; (re-frame/dispatch [:msg-cube.abc])))])
                                                        ; (re-frame/dispatch [:run-msg-cube-game])))])
                           (re-frame/dispatch [:init-fps-panel main-scene/scene]))))
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
