(ns cube-test.tmp-scene.scene
  (:require
   [re-frame.core :as rf]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [babylonjs-gui :as bjs-gui]
   [cube-test.controller-xr :as controller-xr]
   [babylonjs-loaders :as bjs-l]
   [cube-test.utils :as utils]))

(defn bedroom-loaded [meshes particle-systems skeletons anim-groups name user-cb]
  (println "bedroom-loaded ")
  (doall (map #(do
                 (when (re-matches #"__root__" (.-id %1))
                     (set! (.-name %1) name)
                     (set! (.-id %1) name)
                     ; (set! (.-scaling %1) (bjs/Vector3. 0.01 0.01 0.01))
                     ;; the neg. on the x is needed otherwise the materials are "flipped"
                     (set! (.-position %1)(bjs/Vector3. 0 0 0))))
              meshes)))

(defn init []
  (prn "tmp-scene.scene.init: entered")
  (let [scene main-scene/scene])
    ; (bjs/MeshBuilder.CreateBox "dummy" (js-obj "width" 2, "height" 2 "depth" 2) scene))
  (utils/load-model "models/tmp_scene/" "bedroom_scene.glb" "bedroom" bedroom-loaded))

(defn render-loop []
  ; (if (= main-scene/xr-mode "vr")
  ;   (controller/tick)
  (controller-xr/tick)
  ; (fps-panel/tick main-scene/engine)
  (main-scene/tick)
  (.render main-scene/scene))

(defn run-scene []
  (.runRenderLoop main-scene/engine (fn [] (render-loop))))
