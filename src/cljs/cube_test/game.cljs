;; game is refer to many, referred by few
(ns cube-test.game
  (:require
   [re-frame.core :as re-frame]
   [cube-test.main-scene :as main-scene]
   [cube-test.utils.fps-panel :as fps-panel]
   [cube-test.base :as base]
   [cube-test.utils :as utils]
   [cube-test.twizzlers.events :as twizzler-events]
   [cube-test.beat-club.events :as beat-club-events]
   [cube-test.frig-frog.events :as frig-frog-events]
   [cube-test.frig-frog.game :as ff.game]
   [cube-test.top-scene.top-scene :as top-scene]
   [cube-test.top-scene.events :as top-scene-events]
   [cube-test.tmp-scene.events :as tmp-scene-events]))

(declare render-loop)
(def soft-switch false)

;; Just a common method used to control if a hard switch is done, or a soft one.
(defn init-applicable-dispatch [full-dispatch support-dispatch]
  (if soft-switch
    (support-dispatch)
    (full-dispatch)))

(defn init
  ([] (init base/top-level-scene))
  ([top-level-scene]
   (println "game.init: entered, top-level-scene=" top-level-scene)
   (condp = top-level-scene
     :cube-spin-scene (do
                         (println "top-level-scene=cube-spin-scene"
                             (let [soft-init-seq #(do
                                                    (re-frame/dispatch [:init-cube-spin-scene])
                                                    (re-frame/dispatch [:init-cube-fx])
                                                    (re-frame/dispatch [:init-fps-panel main-scene/scene])
                                                    (re-frame/dispatch [:run-cube-spin-scene]))
                                   full-init-seq #(re-frame/dispatch [:init-main-scene soft-init-seq])]
                              (init-applicable-dispatch full-init-seq soft-init-seq))))
     :face-slot-scene (do
                         (println "top-level-scene= face-slot-scene")
                         (let [soft-init-seq #(do
                                                (re-frame/dispatch [:init-face-slot-scene])
                                                (re-frame/dispatch [:init-fps-panel main-scene/scene])
                                                (re-frame/dispatch [:run-face-slot-scene]))
                               full-init-seq #(re-frame/dispatch [:init-main-scene soft-init-seq])]
                          (init-applicable-dispatch full-init-seq soft-init-seq)))
     :tic-tac-attack-scene (do
                              (println "top-level-scene= tic-tac-attack-scene")
                              (re-frame/dispatch [:init-main-scene(fn [] (re-frame/dispatch [:init-tic-tac-attack-scene]))])
                              (re-frame/dispatch [:init-fps-panel main-scene/scene])
                              (re-frame/dispatch [:run-tic-tac-attack-scene]))
     :vrubik-scene (do
                      (println "top-level-scene= vrubik-scene")
                      (let [soft-init-seq #(do
                                             (re-frame/dispatch [:init-vrubik-scene])
                                             (re-frame/dispatch [:init-fps-panel main-scene/scene])
                                             (re-frame/dispatch [:run-vrubik-scene]))
                            full-init-seq #(re-frame/dispatch [:init-main-scene soft-init-seq])]
                        (init-applicable-dispatch full-init-seq soft-init-seq)))
     :geb-cube-scene (do
                        (let [soft-init-seq #(do
                                               (re-frame/dispatch [:init-geb-cube-scene])
                                               (re-frame/dispatch [:init-fps-panel main-scene/scene])
                                               (re-frame/dispatch [:run-geb-cube-scene]))
                              full-init-seq #(re-frame/dispatch [:init-main-scene soft-init-seq])]
                          (init-applicable-dispatch full-init-seq soft-init-seq)))
     :skyscrapers-scene (do
                           (println "top-level-scene= skyscrapers-scene")
                           (let [soft-init-seq #(do
                                                  ; (re-frame/dispatch [:init-main-scene(fn [] (re-frame/dispatch [:init-skyscrapers-scene]))])
                                                  (re-frame/dispatch [:init-skyscrapers-scene])
                                                  (re-frame/dispatch [:init-fps-panel main-scene/scene])
                                                  (re-frame/dispatch [:run-skyscrapers-scene]))
                                 full-init-seq #(re-frame/dispatch [:init-main-scene soft-init-seq])]
                              (init-applicable-dispatch full-init-seq soft-init-seq)))

     :ut-simp-scene (do
                       (println "top-level-scene= ut-simp-scene")
                       (re-frame/dispatch [:init-main-scene(fn [] (re-frame/dispatch [:init-ut-simp-scene]))])
                       (re-frame/dispatch [:init-fps-panel main-scene/scene])
                       (re-frame/dispatch [:run-ut-simp-scene]))
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
     :twizzlers (do
                   (println "top-level-scene=twizzlers")
                   (let [support-init-seq #(do
                                             ; (re-frame/dispatch [:cube-test.events/reset-db])
                                             (re-frame/dispatch [::twizzler-events/init-db])
                                             (re-frame/dispatch [::twizzler-events/init-game])
                                             (re-frame/dispatch [::twizzler-events/run-game])
                                             (re-frame/dispatch [:init-fps-panel main-scene/scene]))
                         full-init-seq #(re-frame/dispatch [:init-main-scene support-init-seq])]
                      (init-applicable-dispatch full-init-seq support-init-seq)))
     :beat-club (do
                  (println "top-level-scene=beat-club")
                  (let [soft-init-seq #(do
                                          (re-frame/dispatch [::beat-club-events/init-db])
                                          (re-frame/dispatch [::beat-club-events/init-game])
                                          (re-frame/dispatch [::beat-club-events/run-game])
                                          (re-frame/dispatch [:init-fps-panel main-scene/scene]))
                        full-init-seq #(re-frame/dispatch [:init-main-scene soft-init-seq])]
                    (init-applicable-dispatch full-init-seq soft-init-seq)))
     :frig-frog (do
                   (let [soft-init-seq
                            #(do
                                    (re-frame/dispatch [::frig-frog-events/init-game-db ff.game/default-game-db])
                                    (re-frame/dispatch [::frig-frog-events/init-rules])
                                    (re-frame/dispatch [::frig-frog-events/init-game])
                                    (re-frame/dispatch [:init-fps-panel main-scene/scene])
                                    (re-frame/dispatch [::frig-frog-events/run-game]))
                         full-init-seq #(re-frame/dispatch [:init-main-scene soft-init-seq])]
                      (init-applicable-dispatch full-init-seq soft-init-seq)))
     :top-scene (do
                   (println "top-level-scene=top-scene")
                   (let [support-dispatch #(do
                                               (re-frame/dispatch [::top-scene-events/init-db top-scene/default-db])
                                               ; (when (not soft-switch))
                                               (re-frame/dispatch [::top-scene-events/init-scene])
                                               (re-frame/dispatch [:init-fps-panel main-scene/scene])
                                               (re-frame/dispatch [::top-scene-events/run-scene]))
                         full-dispatch #(re-frame/dispatch [:init-main-scene support-dispatch])]
                     (init-applicable-dispatch full-dispatch support-dispatch)))
     :tmp-scene (do
                      (println "top-level-scene= tmp-scene")
                      (let [soft-init-seq #(do
                                             (cube-test.tmp-scene.scene/init)
                                             (cube-test.tmp-scene.scene/run-scene))
                            full-init-seq #(re-frame/dispatch [:init-main-scene soft-init-seq])]
                        (init-applicable-dispatch full-init-seq soft-init-seq))))))

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
