(ns cube-test.frig-frog.scene
  ; (:require-macros [cube-test.macros :as macros])
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [babylonjs-gui :as bjs-gui]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   ; [cube-test.cube-fx :as cube-fx]
   [cube-test.utils.fps-panel :as fps-panel]
   ; [cube-test.beat-club.note-twitch :as note-twitch]
   [cube-test.base :as base]
   [cube-test.utils :as utils]
   ; [cube-test.beat-club.twitch-stream :as twitch-stream]
   [babylonjs-loaders :as bjs-l]))

(defn init-gui []
  (prn "frig-frog.scene: init-gui entered")
  (let []))
        ; top-plane (bjs/MeshBuilder.CreatePlane "top-plane" (js-obj "width" 5 "height" 3))]))

(defn init [db]
  (let [scene main-scene/scene
        light1 (bjs/PointLight. "pointLight" (bjs/Vector3. 0 5 -3) scene)]
        ; light1 (bjs/PointLight. "pointLight" (bjs/Vector3. 0 5 -3) main-scene/scene)
        ; camera main-scene/camera
        ; camera-pos (.-position camera)]
    (init-gui)))
