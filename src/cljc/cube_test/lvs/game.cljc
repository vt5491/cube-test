;; game is refer to many, referred by few.
(ns cube-test.lvs.game
  (:require
   [re-frame.core :as rf]
   ; [babylonjs :as bjs]
   [cube-test.utils :as utils]
   [cube-test.main-scene :as main-scene]
   [cube-test.lvs.scenes.main :as lvs-main-scene]
   [cube-test.lvs.scenes.reflect-scene :as lvs-reflect-scene]))

(def active-scene)

(defn init [db]
  ;; (set! active-scene (:active-scene default-game-db))
  (set! active-scene :reflect-scene)
  (prn "lvs-game.init: entered, db=" db)
  (prn "lvs-game.init: default-scene=" (:default-scene db))
  (case (:default-scene db)
    :lvs-main (do
                (prn "calling lvs-main") 
                (lvs-main-scene/init))
    :lvs-reflect (do
                    (lvs-reflect-scene/init)
                    (lvs-reflect-scene/run-scene))))