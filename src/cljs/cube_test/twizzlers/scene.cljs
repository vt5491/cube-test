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
   [cube-test.msg-cube.data.msg :as msg]
   [cube-test.base :as base]
   [cube-test.utils :as utils]))
   ; [cube-test.twizzlers.events :as twizzler-events]))

(declare init-mirror-sub-scene)
(def bldg-cube)
; (def bldg-cube-rot-speed (* base/ONE-DEG 0.1))
(def bldg-cube-ang-delta (* base/ONE-DEG 0.01))
(def bldg-cube-ang-vel (bjs/Vector3. 0 0 0))

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
  (swap! main-scene/*camera-init-pos* (fn [x] {:x 46.37 :y 0.96 :z 45.53}))
  (println "new-init-pos=" @main-scene/*camera-init-pos*))

(defn update-bldg-cube-ang-vel [delta-y-vel]
  (let [old-y-vel (.-y bldg-cube-ang-vel)
        new-y-vel (+ old-y-vel delta-y-vel)]
    (set! bldg-cube-ang-vel (bjs/Vector3. 0 new-y-vel 0))))

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
(defn append-portal-loaded [loaded-scene]
  (println "append-portal-loaded: loaded-scene=" loaded-scene)
  (println "Point light=" (.getLightByName loaded-scene "Point"))
  ; (move-camera)
  ;; turn off environmentTexture (ibl) so that only blender lights are in effect.
  (set! (.-environmentTexture main-scene/scene) nil)
  (let [light (.getLightByName loaded-scene "Point")
        glow-cube (.getMeshByID loaded-scene "glow_cube")
        shadow-gen (bjs/ShadowGenerator. 1024 light)
        room (.getMeshByID loaded-scene "room")]
    (.addShadowCaster shadow-gen glow-cube)
    (set! (.-useExponentialShadowMap shadow-gen) true)
    (set! (.-receiveShadows room) true))
  ;; hide the inner skybox since it blocks our view
  (set! (.-isVisible (.getMeshByID main-scene/scene "BackgroundSkybox")) false)
  (let [gl (bjs/GlowLayer. "glow" main-scene/scene)]
    (set! (.-intensity gl) 0.5)))


(defn append-space-portal [path file]
  (.Append bjs/SceneLoader path file main-scene/scene
           #(do
               (re-frame/dispatch [:cube-test.twizzlers.events/scene-loaded :space-port])
               (append-portal-loaded %1))))

(defn init-exp-gui []
  (prn "init-exp-gui: entered")
  (let [
        top-plane (bjs/MeshBuilder.CreatePlane "top-plane" (js-obj "width" 5 "height" 3))
        top-adv-texture (bjs-gui/AdvancedDynamicTexture.CreateForMesh. top-plane 2048 1024)
        top-pnl (bjs-gui/Grid.)
        top-hdr (bjs-gui/TextBlock.)
        rot-btn (bjs-gui/Button.CreateSimpleButton. "rot-btn" "rotate")
        rot-btn-2 (bjs-gui/Button.CreateSimpleButton. "rot-btn-2" "rotate2")
        left-side-rot-btn (bjs-gui/Button.CreateSimpleButton. "left-side-rot-btn" "left side rot")
        right-side-rot-btn (bjs-gui/Button.CreateSimpleButton. "rigth-side-rot-btn" "right side rot")
        scene main-scene/scene
        bldg-cube (.getMeshByID scene "bldg-cube")
        bldg-cube-pos (.-position bldg-cube)
        y-quat-neg-90 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG -90)))]
    (set! (.-position top-plane) (.add bldg-cube-pos (bjs/Vector3. 0 0.5 7)))
    (set! (.-rotationQuaternion top-plane) y-quat-neg-90)
    (.enableEdgesRendering top-plane)

    (.addControl top-adv-texture top-pnl)
    (set! (.-text top-hdr) "commands")
    (set! (.-height top-hdr) "500px")
    (set! (.-fontSize top-hdr) "160")
    (set! (.-color top-hdr) "white")
    ;; create 5 rows and 2 cols
    (.addRowDefinition top-pnl 0.20 false)
    (.addRowDefinition top-pnl 0.20)
    (.addRowDefinition top-pnl 0.20)
    (.addRowDefinition top-pnl 0.20)
    (.addRowDefinition top-pnl 0.20)
    (.addColumnDefinition top-pnl 0.5)
    (.addColumnDefinition top-pnl 0.5)
    (.addControl top-pnl top-hdr 0 0)
    ;; rot-btn
    (set! (.-autoScale rot-btn) true)
    (set! (.-fontSize rot-btn) "100")
    (set! (.-color rot-btn) "red")

    ;; left-side-rot-btn
    (set! (.-autoScale left-side-rot-btn) true)
    (set! (.-fontSize left-side-rot-btn) "100")
    (set! (.-color left-side-rot-btn) "white")
    (-> left-side-rot-btn .-onPointerUpObservable
        (.add (fn [value]
                (println "left-side-rot-btn pressed")
                (update-bldg-cube-ang-vel (* -1 bldg-cube-ang-delta)))))
    (.addControl top-pnl left-side-rot-btn 4 0)
    ;; right-side-rot-btn
    (set! (.-autoScale right-side-rot-btn) true)
    (set! (.-fontSize right-side-rot-btn) "100")
    (set! (.-color right-side-rot-btn) "white")
    (-> right-side-rot-btn .-onPointerUpObservable
        (.add (fn [value]
                 (println "right-side-rot-btn pressed")
                 (update-bldg-cube-ang-vel (* 1 bldg-cube-ang-delta)))))
    ;                                                       (re-frame/dispatch [:vrubik-side-fwd :top]))))
    (.addControl top-pnl right-side-rot-btn 4 1)))

(defn hemisferic-loaded [loaded-scene]
  ; (js-debugger)
  (println "hemisferic-loaded: loaded-scene=" loaded-scene)
  ; (move-camera)
  (set! (.-isVisible (.getMeshByID main-scene/scene "BackgroundSkybox")) false)
  ;; turn off environmentTexture (ibl) so that only blender lights are in effect.
  ;; if you don't change this you'll see a weird building in the pool reflection.
  (set! (.-environmentTexture main-scene/scene) nil)
  ;; disable one of the blender point lights so we can see reflections better.
  (-> main-scene/scene (.getLightByID "Point.002") (.setEnabled false))
  (comment)
  (let [scene main-scene/scene
        exp-plane (.getMeshByID scene "exp_plane")
        exp-plane-mat (bjs/PBRMaterial. "exp-baked-diffuse" scene)
        diffuse-text (bjs/Texture. "imgs/textures/exp_circ_out.png" scene true true bjs/Texture.BILINEAR_SAMPLINGMODE nil nil nil true)
        bump-text (bjs/Texture. "imgs/textures/exp_circ_out.png" scene true true bjs/Texture.BILINEAR_SAMPLINGMODE nil nil nil true)]
    (set! (.-bumpTexture exp-plane-mat) bump-text)
    (set! (.-backFaceCulling exp-plane-mat) false)
    (set! (.-material exp-plane) exp-plane-mat))
  (comment)
  (prn "part 0")
  (let [scene main-scene/scene
        probe (bjs/ReflectionProbe. "ref-probe" 512 scene)
        rList (.-renderList probe)
        suzanne (.getMeshByID scene "Suzanne")
        suzanne-pos (.-position suzanne)
        red-sphere (.getMeshByID scene "Sphere")
        tmp-rs (set! (.-position red-sphere) (bjs/Vector3. 2 1 0))
        tmp-sph (set! (.-material red-sphere) main-scene/red-mat)
        pool (.getMeshByID scene "pool")
        bldg-cube-tmp (bjs/MeshBuilder.CreateBox "bldg-cube" (js-obj "height" 5 "width" 5 "depth" 5) scene)
        mirror-plane (bjs/MeshBuilder.CreatePlane "mirror-plane" (js-obj "height" 5 "width" 4) scene)
        ;; Note: very important that you set the plane's position before calling 'computeWorldMatrix'
        ;; if you set position afterward, the reflection may be on the "wrong"
        tmp-mp-2 (set! (.-position mirror-plane) (bjs/Vector3. 0 2 5))
        tmp-mp (.computeWorldMatrix mirror-plane true)
        mirror-plane-world-matrix (.getWorldMatrix mirror-plane)
        mirror-plane-vertex-data (.getVerticesData mirror-plane "normal")
        mirror-plane-normal-vec (bjs/Vector3. (aget mirror-plane-vertex-data 0) (* 1 (aget mirror-plane-vertex-data 1)) (* 1 (aget mirror-plane-vertex-data 2)))
        mirror-plane-normal (bjs/Vector3.TransformNormal mirror-plane-normal-vec mirror-plane-world-matrix)
        reflector-mp (bjs/Plane.FromPositionAndNormal. (.-position mirror-plane) (.scale mirror-plane-normal -1))
        mirror-mp-mat (bjs/StandardMaterial. "mirror-mp-mat" scene)
        pool-baked-mat (bjs/StandardMaterial. "pool-baked" scene)
        pool-mat (.-material pool)
        tmp (.computeWorldMatrix pool true)
        pool-world-matrix (.getWorldMatrix pool)
        pool-vertex-data (.getVerticesData pool "normal")
        pool-normal (bjs/Vector3. (aget pool-vertex-data 0) (aget pool-vertex-data 1) (aget pool-vertex-data 2))
        pool-normal-2 (bjs/Vector3.TransformNormal. pool-normal pool-world-matrix)
        reflector (bjs/Plane.FromPositionAndNormal. (.-position pool) (.scale pool-normal-2 -1))
        tmp4 (prn "tmp4")
        mirror-mat (bjs/StandardMaterial. "mirror" scene)
        box-mat (bjs/StandardMaterial. "box-mat" scene)
        quat180 (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 180))]

    (prn "hello")
    ; (set! (.-position mirror-plane) (bjs/Vector3. 0 2 5))
    (set! (.-rotation mirror-plane) (bjs/Vector3. 0 (* base/ONE-DEG 0) 0))
    (prn "mirror-plane.position=" (.-position mirror-plane))
    (prn "mirror-plane-normal-2=" mirror-plane-normal)
    (set! bldg-cube bldg-cube-tmp)

    (prn "parta")
    ; (.computeWorldMatrix mirror-plane true)
    (set! (.-reflectionTexture mirror-mp-mat) (bjs/MirrorTexture. "mirror-mp-texture" 1024 scene true))
    ; (set! (.-activeCamera (.-reflectionTexture mirror-mp-mat)) main-scene/camera)
    (set! (-> mirror-mp-mat .-reflectionTexture .-mirrorPlane) reflector-mp)
    ; (set! (-> mirror-mp-mat .-reflectionTexture .-renderList) (array suzanne red-sphere pool))
    (.push (-> mirror-mp-mat .-reflectionTexture .-renderList) red-sphere)
    (set! (-> mirror-mp-mat .-reflectionTexture .-level) 1)
    (set! (.-material mirror-plane) mirror-mp-mat)
    ;; turn off backFaceCulling so we can see the backside as well.
    ; (.setBackfaceCulling mirror-mp-mat false)
    (set! (.-backFaceCulling mirror-mp-mat) false)
    (prn "partb")

    (set! (.-material pool) pool-mat)

    ;; box-mat
    (set! (.-backFaceCulling box-mat) true)
    (set! (.-reflectionTexture box-mat) (bjs/CubeTexture. "textures/cubic_texture_exp/bldg" scene))
    (set! (-> box-mat (.-reflectionTexture) (.-coordinatesMode)) bjs/Texture.CUBIC_MODE)
    (set! (.-diffuseColor box-mat) (bjs/Color3. 0 0 0))
    (set! (.-specularColor box-mat) (bjs/Color3. 0 0 0))
    ; (set! (.-material mirror-plane) box-mat)

    ;; bldg-cube
    (set! (.-position bldg-cube) (bjs/Vector3. 35 2.5 40))
    (set! (.-material bldg-cube) box-mat)

    (case main-scene/xr-mode
      "vr" (.addFloorMesh main-scene/vrHelper pool)
      "xr" (->  main-scene/xr-helper .-teleportation (.addFloorMesh pool)))
    (init-mirror-sub-scene)

    (let [cube (bjs/MeshBuilder.CreateBox "min-cube" (js-obj "width" 1 "height" 1))]
      (set! (.-position cube) (bjs/Vector3. 0 2 0)))
    (prn "hi2")))

(defn append-hemisferic [path file]
  (.Append bjs/SceneLoader path file main-scene/scene
           #(do
               (re-frame/dispatch [:cube-test.twizzlers.events/scene-loaded :hemisferic])
               (hemisferic-loaded %1)
               (init-exp-gui))))

(defn init-mirror-sub-scene []
  (let [scene main-scene/scene]
    (let [sphere (bjs/MeshBuilder.CreateSphere "sphere" (js-obj "diameter" 2, "segements" 32) scene)
          red-sphere (.getMeshByID scene "Sphere")]
      (prn "red-sphere=" red-sphere)
      (set! (.-position sphere) (bjs/Vector3. 0 1 0))
      (let [glass (bjs/MeshBuilder.CreatePlane "glass" (js-obj "width" 5, "height" 5) scene)]
        (set! (.-position glass) (bjs/Vector3. -2 0 6))
        (.computeWorldMatrix glass true)
        (let [glass-worldMatrix (.getWorldMatrix glass)
              glass-vertex-data (.getVerticesData glass "normal")
              glass-normal-vec (bjs/Vector3. (aget glass-vertex-data 0) (aget glass-vertex-data 1) (aget glass-vertex-data 2))
              glass-normal (bjs/Vector3.TransformNormal. glass-normal-vec glass-worldMatrix)
              reflector (bjs/Plane.FromPositionAndNormal (.-position glass) (.scale glass-normal -1))
              mirror-material (bjs/StandardMaterial. "mirror-works" scene)]
          (set! (.-reflectionTexture mirror-material) (bjs/MirrorTexture. "mirror-works-texture" 1024 scene true))
          (set! (-> mirror-material .-reflectionTexture .-mirrorPlane) reflector)
          (set! (-> mirror-material .-reflectionTexture .-renderList) (array sphere red-sphere))
          (set! (-> mirror-material .-reflectionTexture .-level) 1)
          (set! (.-material glass) mirror-material)

          (let [mirror-plane (.getMeshByID scene "mirror-plane")
                sphere (.getMeshByID scene "sphere")
                mirror-plane-render-list (-> mirror-plane .-material .-reflectionTexture .-renderList)
                tmp (prn "mirror-plane-render-list=" mirror-plane-render-list)
                new-render-list (conj (js->clj mirror-plane-render-list) sphere)]
            (.push (-> mirror-plane .-material .-reflectionTexture .-renderList) sphere)))))))
            ; mirrorMaterial.reflectionTexture.mirrorPlane = reflector))))));
            ; setting the refletor to the glass's reflector fixes the "opposite side" problem.
            ; (set! (-> mirror-plane .-material .-reflectionTexture .-mirrorPlane) reflector)))))))
          ; (js-debugger))))))


(defn init [db]
  (println "twizzlers.scene.init: entered db=" db)
  ; (init-mirror-sub-scene)
  (let [scene main-scene/scene]
      ;; override the initial position and rotation of the non-vr camera.
      (let [
            ; do-cam (.-deviceOrientationCamera main-scene/vrHelper)
            do-cam (utils/get-xr-camera)
            ; tmp (js-debugger)
            quat (bjs/Quaternion.FromEulerAngles (* -5.8 base/ONE-DEG) (* -3 base/ONE-DEG) 0)]
        ; (set! (.-position do-cam) (bjs/Vector3. 46.37 0.96 45.53))
        ; (set! (.-rotationQuaternion do-cam) quat)
        (set! (.-position do-cam) (bjs/Vector3. 0 5 -10)))

      (let [path (get-in db [:scenes :hemisferic :path])
            fn (get-in db [:scenes :hemisferic :fn])]
        (prn "path=" path ", fn=" fn)
        (append-hemisferic
         path
         fn))))


(defn tick []
  (let [engine main-scene/engine
        delta-time (.getDeltaTime engine)
        bldg-cube-rot-y-delta (* (.-y bldg-cube-ang-vel) delta-time)]
    (when bldg-cube
      (.rotate bldg-cube bjs/Axis.Y bldg-cube-rot-y-delta bjs/Space.LOCAL))))
