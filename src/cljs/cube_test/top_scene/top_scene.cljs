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
   ; [cube-test.events :as events]
   [cube-test.base :as base]
   [cube-test.utils :as utils]
   [babylonjs-loaders :as bjs-l]))

(def default-db
  {:top-scene-abc 7})

(defn init-app-planes []
  (let [scene main-scene/scene
        ff-top-plane (bjs/MeshBuilder.CreatePlane "ff-top-plane"
                                        (clj->js {:width 3 :height 3
                                                  :sideOrientation bjs/Mesh.DOUBLESIDE})
                                        scene)
        x-quat-90 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.X (* base/ONE-DEG 90)))]
    (set! (.-rotationQuaternion ff-top-plane) x-quat-90)
    (set! (.-isPickable ff-top-plane) true)))

       ; scene.onPointerDown = function (evt, pickResult) {}
       ;  // We try to pick an object
       ;  if (pickResult.hit) {}
       ;      header.textContent = pickResult.pickedMesh.name)));

(defn ff-cube-loaded [meshes particle-systems skeletons anim-groups name user-cb]
  (println "ff-cube-loaded")
  (doall (map #(do
                 (when (re-matches #"__root__" (.-id %1))
                     (set! (.-name %1) name)
                     (set! (.-id %1) name)))
                     ;; the neg. on the x is needed otherwise the materials are "flipped"
                     ; (set! (.-scaling %1)(bjs/Vector3. -0.3 0.3 0.3))
                     ; (set! (.-position %1)(bjs/Vector3. 0 2 0))))
              meshes)))

(defn load-ff-cube [path file name user-cb]
  (.ImportMesh bjs/SceneLoader ""
               path
               file
               main-scene/scene
               #(ff-cube-loaded %1 %2 %3 %4 name user-cb)))

; (defn model-loaded [root-prfx scale meshes particle-systems skeletons anim-groups name user-cb])
(defn model-loaded [user-parms meshes particle-systems skeletons anim-groups name user-cb]
  ; (println "model-loaded")
  (let [root-prfx (:root-prfx user-parms)
        scale (:scale user-parms)
        delta-pos (:delta-pos user-parms)]
    (doall (map #(do
                   (prn "model-loaded: id %1=" (.-id %1) ",root-prfx=" root-prfx)
                   (when (re-matches #"__root__" (.-id %1))
                         ; (set! (.-name %1) name)
                         ; (set! (.-id %1) name)
                         ; (set! (.-name %1) "root")
                         ; (set! (.-id %1) "root")
                         (set! (.-name %1) root-prfx)
                         (set! (.-id %1) root-prfx)
                         (.scaleInPlace (.-scaling %1) scale)
                         (when delta-pos
                           (set! (.-position %1) (.add (.-position %1) delta-pos)))))
                meshes))))

(defn load-model [path file name user-cb]
  (.ImportMesh bjs/SceneLoader ""
               path
               file
               main-scene/scene
               user-cb))

(defn init []
  (init-app-planes)
  (let [scene main-scene/scene
        ; ff-top-plane (bjs/MeshBuilder.CreatePlane "ff-top-plane"
        ;                                 (clj->js {:width 3 :height 3
        ;                                           :sideOrientation bjs/Mesh.DOUBLESIDE})
        ;                                 scene)
        tweak-partial (partial utils/tweak-xr-view -10 10 10)
        ; x-quat-91 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.X (* base/ONE-DEG 90)))
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
    ; (set! (.-rotationQuaternion ff-top-plane) x-quat-90)))
    (set! (.-onPointerDown scene)
      (fn [e pickResult]
        (when (.-hit pickResult)
          (case (-> pickResult (.-pickedMesh) (.-name))
          ; (prn "mesh hit on mesh" (-> pickResult (.-pickedMesh) (.-name)))
            ; "ff-top-plane" (cube-test.events.switch-app :frig-frog)
            "ff-top-plane" (cube-test.events.soft-switch-app :frig-frog)
            "default"))))
    ; (load-ff-cube "models/top_scene/" "ff_cube.glb" "ff-cube" nil)
    ; (load-model "models/top_scene/sub_scenes/" "ff_scene.glb" "ff-scene" model-loaded)
    ; (load-model "models/top_scene/sub_scenes/" "ff_scene.glb" "ff-scene" (partial model-loaded "ff-scene" 0.3))
    (load-model "models/top_scene/sub_scenes/" "ff_scene.glb" "ff-scene"
                (partial model-loaded
                         {:root-prfx "ff-scene"
                          :scale  0.3
                          :delta-pos (bjs/Vector3. 0 0.2 0)}))))

(defn render-loop []
  (if (= main-scene/xr-mode "vr")
    (controller/tick)
    (controller-xr/tick))
  (if fps-panel/fps-pnl
    (fps-panel/tick main-scene/engine))
  (.render main-scene/scene))

(defn run-scene []
  (.runRenderLoop main-scene/engine (fn [] (render-loop))))
