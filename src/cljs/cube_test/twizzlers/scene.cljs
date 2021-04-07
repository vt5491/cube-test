(ns cube-test.twizzlers.scene
  (:require
   [re-frame.core :as re-frame]
   [cube-test.subs :as subs]
   [babylonjs :as bjs]
   [babylonjs-gui :as bjs-gui]
   [cube-test.main-scene :as main-scene]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.utils.fps-panel :as fps-panel]
   [cube-test.msg-cube.data.msg :as msg]))

(defn init []
  (println "twizzlers.scene.init: entered")
  (let [scene main-scene/scene]
      (let [light1 (bjs/HemisphericLight. "light1" (bjs/Vector3. 1 1 0) scene)
            light2 (bjs/PointLight. "light2" (bjs/Vector3. 0 1 -1) scene)])))
            ; sph (bjs/MeshBuilder.CreateSphere "sphere" (js-obj "diameter" 1) scene)]
        ; (set! sphere sph))))
