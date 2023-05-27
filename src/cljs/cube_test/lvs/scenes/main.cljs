(ns cube-test.lvs.scenes.main
  (:require
   [re-frame.core :as rf]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [cube-test.utils :as utils]))

(defn lvs-main-loaded [meshes particle-systems skeletons anim-groups name user-cb]
  ;; (println "lvs-main-loaded, length messhes= " (alength meshes))
  ;; (js-debugger)
  (let [scene main-scene/scene]
        ;; probe (bjs/ReflectionProbe. "probe" 512 scene)]
    (prn "scene=" main-scene/ground)
    (doall (map #(do
                   (when (re-matches #"__root__" (.-id %1))
                     (prn "found root") 
                    ;;  (prn "found root 2")
                     (set! (.-name %1) "blender-lvs")
                     (set! (.-id %1) "blender-lvs")))
              meshes))))

(defn init []
  (prn "lvs-main-scene: entered")
  (let [scene main-scene/scene
        grip-factor cube-test.controller-xr/grip-factor
        current-camera (.-activeCamera scene)
        sky-box (.getMeshByID scene "sky-box")
        bg-sky-box (.getMeshByID scene "BackgroundSkybox")]

    ;; (utils/load-model "models/lvs/" "ball_mirror.glb" "mirror" lvs-main-loaded))) 
    (set! cube-test.controller-xr/grip-factor (+ grip-factor 0.2))
    (prn "lvs-main.init: grip-factor=" cube-test.controller-xr/grip-factor)
    (utils/load-model "models/lvs/" "lvs_main.glb" "lvs-main" lvs-main-loaded) 
    (set! (.-position current-camera) (.add (.-position current-camera)(bjs/Vector3. -14 0 -875)))
    (when sky-box
      (.removeMesh scene sky-box))
    (when bg-sky-box
      (set! (.-isVisible bg-sky-box) false))))

(defn tick []
  (let [engine main-scene/engine]
        ;; delta-time (.getDeltaTime engine)
        ;; bldg-cube-rot-y-delta (* (.-y bldg-cube-ang-vel) delta-time)]
    (main-scene/tick)))