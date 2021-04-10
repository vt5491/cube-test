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

(defn add-twiz-cube [twiz]
  (println "add-twizzler: entered, twiz=" twiz)
  ; (when (and id (> id 0)))
  (when twiz
    (let [id (twiz :cube-test.twizzlers.db/id)
          scene main-scene/scene]
      (when (not (.getMeshByID scene (str "twiz-" id)))
        (when (and id (> id 0))
          (let [scene main-scene/scene
                twiz-cube (bjs/MeshBuilder.CreateBox (str "twiz-" id) (js-obj "height" 1 "width" 1) scene)
                pos (bjs/Vector3. (* id 1.1) 0 0)]
            (set! (.-position twiz-cube) pos))))))
                ; (add-mesh-pick-action msg-cube)))))
  ;; return nil because this is a pure side effect
  nil)

(defn init []
  (println "twizzlers.scene.init: entered")
  (let [scene main-scene/scene]
      (let [light1 (bjs/HemisphericLight. "light1" (bjs/Vector3. 1 1 0) scene)
            light2 (bjs/PointLight. "light2" (bjs/Vector3. 0 1 -1) scene)])))
            ; sph (bjs/MeshBuilder.CreateSphere "sphere" (js-obj "diameter" 1) scene)]
        ; (set! sphere sph))))
