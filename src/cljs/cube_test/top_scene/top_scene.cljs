;; This is the front-end "meta-scene" for all the sub-scenens
;; in cube-test
(ns cube-test.top-scene.top-scene
  (:require
   [re-frame.core :as rf]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [babylonjs-gui :as bjs-gui]
   [babylonjs-materials :as bjs-m]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.utils.fps-panel :as fps-panel]
   [cube-test.base :as base]
   [cube-test.utils :as utils]
   [babylonjs-loaders :as bjs-l]))

(def default-db
  {:top-scene-abc 7})

(defn init []
  (let [scene main-scene/scene
        ff-top-plane (bjs/MeshBuilder.CreatePlane "ff-top-plane"
                                        (clj->js {:width 3 :height 3
                                                  :sideOrientation bjs/Mesh.DOUBLESIDE})
                                        scene)
        tweak-partial (partial utils/tweak-xr-view -10 10 10)
        x-quat-90 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.X (* base/ONE-DEG 90)))
        light1 (bjs/PointLight. "pointLight-1" (bjs/Vector3. 0 2 5) scene)
        light2 (bjs/PointLight. "pointLight-2" (bjs/Vector3. 10 2 5) scene)]
    (set! (.-isVisible (.getMeshByID main-scene/scene "ground")) false)
    ; (set! (.-isVisible (.getMeshByID main-scene/scene "sky-box")) false)
    (set! (.-isVisible (.getMeshByID main-scene/scene "BackgroundSkybox")) false)
    ;; set-up scene level "enter xr" hook
    (-> main-scene/xr-helper
      (.-baseExperience)
      (.-onStateChangedObservable)
      (.add tweak-partial))
    (set! (.-rotationQuaternion ff-top-plane) x-quat-90)))

(defn render-loop []
  (if (= main-scene/xr-mode "vr")
    (controller/tick)
    (controller-xr/tick))
  (if fps-panel/fps-pnl
    (fps-panel/tick main-scene/engine))
  (.render main-scene/scene))

(defn run-scene []
  (.runRenderLoop main-scene/engine (fn [] (render-loop))))
