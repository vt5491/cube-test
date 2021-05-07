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

(defn move-camera []
  (println "now moving camera for space-portal")
  ; (let [ip])
  ; (swap! *flag* (fn [x] true))
  ; (swap! main-scene/*camera-init-pos* (fn [x] {:x 47 :y 15 :z 0}))
  ; (swap! main-scene/*camera-init-pos* (fn [x] {:x 47 :y 2 :z 0}))
  (swap! main-scene/*camera-init-pos* (fn [x] {:x -47 :y 2 :z 0}))
  (println "new-init-pos=" @main-scene/*camera-init-pos*))
  ; (let [scene main-scene/scene]
  ; (let [ip main-scene/camera-init-pos])
  ; (let [ip main-scene/scene]
  ;   (set! ip (bjs/Vector3. 0 0 47))))
  ; (set! (main-scene/camera-init-pos) (bjs/Vector3. 0 0 47)))
  ; (let [scene main-scene/scene
  ;       camera scene.camera
  ;       cam-pos (.-position camera)]
  ;   (set! (.-position camera) (bjs/Vector3. (+ (.-x cam-pos) 47) (.-y cam-pos) (.-z cam-pos))))
  ; (println "invoke debugger")
  ; (js-debugger)
  ; (let [scene main-scene/scene
  ;       do-cam (.-deviceOrientationCamera scene.vrHelper)
  ;       cam-pos (.-position do-cam)
  ;       new-pos (bjs/Vector3. (+ (.-x cam-pos) 47) (.-y cam-pos) (.-z cam-pos))]
  ;   (set! (.-position do-cam) new-pos)))

(defn space-portal-loaded [meshes particle-systems skeletons anim-groups name user-cb]
  (println "space-portal-loaded")
  (doall (map #(do
                 (when (re-matches #"__root__" (.-id %1))
                     (set! (.-name %1) name)
                     (set! (.-id %1) name)))
                     ;; the neg. on the x is needed otherwise the materials are "flipped"
                     ; (set! (.-scaling %1)(bjs/Vector3. -0.3 0.3 0.3))
                     ; (set! (.-position %1)(bjs/Vector3. 0 2 0))))
              meshes))
  (when user-cb (user-cb name)))

(defn load-space-portal [path file name user-cb]
  (.ImportMesh bjs/SceneLoader ""
               path
               file
               main-scene/scene
               #(space-portal-loaded %1 %2 %3 %4 name user-cb)))
; BABYLON.SceneLoader.Append("scenes/", "skull.babylon", scene, function (loadedScene) {})

  ; // Shadows
  ; var shadowGenerator = new BABYLON.ShadowGenerator(1024, light);
  ; shadowGenerator.addShadowCaster(torus);
  ; shadowGenerator.useExponentialShadowMap = true);
(defn append-portal-loaded [loaded-scene]
  (println "append-portal-loaded: loaded-scene=" loaded-scene)
  ; (js-debugger)
  ; light01 = scene.getLightByName("Light.000"));
  (println "Point light=" (.getLightByName loaded-scene "Point"))
  (move-camera)
  ;; turn off environmentTexture (ibl) so that only blender lights are in effect.
  (set! (.-environmentTexture main-scene/scene) nil)
  (let [light (.getLightByName loaded-scene "Point")
        glow-cube (.getMeshByID loaded-scene "glow_cube")
        shadow-gen (bjs/ShadowGenerator. 1024 light)
        room (.getMeshByID loaded-scene "room")]
    ; (js-debugger)
    (.addShadowCaster shadow-gen glow-cube)
    (set! (.-useExponentialShadowMap shadow-gen) true)
    (set! (.-receiveShadows room) true))
    ; var gl = new BABYLON.GlowLayer("glow", scene)));
  ;; hide the inner skybox since it blocks our view
  (set! (.-isVisible (.getMeshByID main-scene/scene "BackgroundSkybox")) false)
  (let [gl (bjs/GlowLayer. "glow" main-scene/scene)]
    (set! (.-intensity gl) 0.5)))

  ;; Note: blender lights are added as nodes.  We have to loop over them and create
  ;; bjs equivalents.
  ; (doall (map #(do
  ;                (println "node id=" (.-id %1))))))
  ; (println "Point light=" (.getNodeByID loaded-scene "Point"))
  ; (println "Point light.pos=" (-> (.getNodeByID loaded-scene "Point") (.-position)))
  ; (println "Sun=" (.getNodeByID loaded-scene "Sun")))


(defn append-space-portal [path file]
  (.Append bjs/SceneLoader path file main-scene/scene
           #(append-portal-loaded %1)))



(defn init []
  (println "twizzlers.scene.init: entered")
  (let [scene main-scene/scene]
      ; (let [light1 (bjs/HemisphericLight. "light1" (bjs/Vector3. 1 1 0) scene)
      ;       light2 (bjs/PointLight. "light2" (bjs/Vector3. 0 1 -1) scene)])
      ; (load-space-portal
      ;  "models/tmp/"
      ;  "space_portal.glb"
      ;  "space_portal"
      ;   move-camera)))

      (append-space-portal
       ; "models/tmp/"
       "models/space_portal/"
       "space_portal.glb")))

          ; sph (bjs/MeshBuilder.CreateSphere "sphere" (js-obj "diameter" 1) scene)]
        ; (set! sphere sph))))
