(ns cube-test.lvs.scenes.main
  (:require
   [re-frame.core :as rf]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]))

(defn init []
  (prn "lvs-main-scene: entered"))

(defn tick []
  (let [engine main-scene/engine]
        ;; delta-time (.getDeltaTime engine)
        ;; bldg-cube-rot-y-delta (* (.-y bldg-cube-ang-vel) delta-time)]
    (main-scene/tick)))