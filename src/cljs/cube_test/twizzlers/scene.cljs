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
   [cube-test.base :as base]))
   ; [cube-test.twizzlers.events :as twizzler-events]))

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
  ; (swap! main-scene/*camera-init-pos* (fn [x] {:x -47 :y 2 :z 0}))
  ; (swap! main-scene/*camera-init-pos* (fn [x] {:x 36.585 :y 0.96 :z 37.27}))
  (swap! main-scene/*camera-init-pos* (fn [x] {:x 46.37 :y 0.96 :z 45.53}))
  (println "new-init-pos=" @main-scene/*camera-init-pos*))

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
           ; #(append-portal-loaded %1)
           #(do
               ; (re-frame/dispatch [:twizzler.events/scene-loaded])
               (re-frame/dispatch [:cube-test.twizzlers.events/scene-loaded :space-port])
               ; (re-frame/dispatch [::twizzler.events/scene-loaded])
               (append-portal-loaded %1))))

(defn init-exp-gui []
  (prn "init-exp-gui: entered")
  (let [
        ; top-plane (bjs/Mesh.CreatePlane. "top-plane" 2)
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
    ; (set! (.-position top-plane) (.add bldg-cube-pos (bjs/Vector3. 0 8 0)))
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
    ; (.addColumnDefinition top-pnl 0.33)
    (.addControl top-pnl top-hdr 0 0)
    ;; rot-btn
    (set! (.-autoScale rot-btn) true)
    (set! (.-fontSize rot-btn) "100")
    (set! (.-color rot-btn) "red")

    ;; left-side-rot-btn
    (set! (.-autoScale left-side-rot-btn) true)
    (set! (.-fontSize left-side-rot-btn) "100")
    (set! (.-color left-side-rot-btn) "white")
    ; (def tmp (.-onPointerUpObservable left-side-rot-btn))
    ; (.add tmp (fn [] (+ 1 1)))
    ; (.add (.-onPointerUpObservable left-side-rot-btn) (fn [] (+ 1 1)))
    ; (.add (.-onPointerUpObservable left-side-rot-btn) (fn [e x y] (+ 1 1)))))
    ; (js-debugger)))
    ; (-> (.-onPointerUpObservable left-side-rot-btn) (.add (fn [e x y] (+ 1 1))))))
    ; (-> left-side-rot-btn .-onPointerUpObservable (.add #(prn "hi")))))
    (-> left-side-rot-btn .-onPointerUpObservable
        (.add (fn [value]
                (println "left-side-rot-btn pressed"))))
    ; (-> left-side-rot-btn .-onPointerUpObservable
    ;   (.add (fn [value]
    ;           (println "left-side-rot-btn pressed"))))
        ; (re-frame/dispatch [:vrubik-side-fwd :left]))))))
    (.addControl top-pnl left-side-rot-btn 4 0)
    ;; right-side-rot-btn
    (set! (.-autoScale right-side-rot-btn) true)
    (set! (.-fontSize right-side-rot-btn) "100")
    (set! (.-color right-side-rot-btn) "white")
    (-> right-side-rot-btn .-onPointerUpObservable
        (.add (fn [value]
                 (println "right-side-rot-btn pressed"))))
    ;                                                       (re-frame/dispatch [:vrubik-side-fwd :top]))))
    (.addControl top-pnl right-side-rot-btn 4 1)))

(defn hemisferic-loaded [loaded-scene]
  (println "hemisferic-loaded: loaded-scene=" loaded-scene)
  (move-camera)
  ; (js-debugger)
  (set! (.-isVisible (.getMeshByID main-scene/scene "BackgroundSkybox")) false)
  ;; turn off environmentTexture (ibl) so that only blender lights are in effect.
  ; (set! (.-environmentTexture main-scene/scene) nil))
;   var probe = new BABYLON.ReflectionProbe("main", 512, scene));
; probe.renderList.push(sphere2);
  (comment)
  ; (prn "exp_plane=" (.getMeshByID))
  (let [scene main-scene/scene
        exp-plane (.getMeshByID scene "exp_plane")
        ; exp-plane-mat (bjs/StandardMaterial. "exp-baked-diffuse" scene)
        exp-plane-mat (bjs/PBRMaterial. "exp-baked-diffuse" scene)
        ; diffuse-text (bjs/Texture. "imgs/textures/exp_baked_diffuse.png" scene true true bjs/Texture.BILINEAR_SAMPLINGMODE nil nil nil true)]
        diffuse-text (bjs/Texture. "imgs/textures/exp_circ_out.png" scene true true bjs/Texture.BILINEAR_SAMPLINGMODE nil nil nil true)
        ; diffuse-text (bjs/Texture. "imgs/textures/exp_circ_out_2.png" scene true true bjs/Texture.BILINEAR_SAMPLINGMODE nil nil nil true)
        ; diffuse-text (bjs/Texture. "imgs/textures/exp_circ_out_5.png" scene true true bjs/Texture.BILINEAR_SAMPLINGMODE nil nil nil true)]
        ; diffuse-text (bjs/Texture. "imgs/textures/exp_circ_out_6.png" scene true true bjs/Texture.BILINEAR_SAMPLINGMODE nil nil nil true)
        ; diffuse-text (bjs/Texture. "imgs/textures/exp_circ_out_7.png" scene true true bjs/Texture.BILINEAR_SAMPLINGMODE nil nil nil true)]
        ; bump-text (bjs/Texture. "imgs/textures/exp_baked_bump_2.png" scene true true bjs/Texture.BILINEAR_SAMPLINGMODE nil nil nil true)]
        ; bump-text (bjs/Texture. "imgs/textures/exp_bump.png" scene true true bjs/Texture.BILINEAR_SAMPLINGMODE nil nil nil true)
        ; bump-text (bjs/Texture. "imgs/textures/exp_circ_bump.png" scene true true bjs/Texture.BILINEAR_SAMPLINGMODE nil nil nil true)]
        bump-text (bjs/Texture. "imgs/textures/exp_circ_out.png" scene true true bjs/Texture.BILINEAR_SAMPLINGMODE nil nil nil true)]
    ; (js-debugger))
    ; (set! (.-sideOrientation exp-plane) bjs/Mesh.DOUBLESIDE)
    ; (set! (.-sideOrientation exp-plane-mat) bjs/Mesh.DOUBLESIDE)
    ; (set! (.-diffuseTexture exp-plane-mat) diffuse-text)
    ; (set! (.-diffuseColor exp-plane-mat) (bjs/Color3. 0 1 0))
    (set! (.-bumpTexture exp-plane-mat) bump-text)
    (set! (.-backFaceCulling exp-plane-mat) false)
    (set! (.-material exp-plane) exp-plane-mat))
  (comment)
  ; var reflector = new BABYLON.Plane.FromPositionAndNormal(glass.position, glassNormal.scale(-1));
  ; glass.computeWorldMatrix(true);
  ; var glass_worldMatrix = glass.getWorldMatrix();
  ; glassNormal = new BABYLON.Vector3.TransformNormal(glassNormal, glass_worldMatrix)
  ; var reflector = new BABYLON.Plane.FromPositionAndNormal(glass.position, glassNormal.scale(-1));
  (let [scene main-scene/scene
        probe (bjs/ReflectionProbe. "ref-probe" 512 scene)
        rList (.-renderList probe)
        suzanne (.getMeshByID scene "Suzanne")
        suzanne-pos (.-position suzanne)
        sphere (.getMeshByID scene "Sphere")
        tmp-sph (set! (.-material sphere) main-scene/red-mat)
        pool (.getMeshByID scene "pool")
        mirror-plane (bjs/MeshBuilder.CreatePlane "mirror_plane" (js-obj "height" 5 "width" 5) scene)
        bldg-cube (bjs/MeshBuilder.CreateBox "bldg-cube" (js-obj "height" 5 "width" 5 "depth" 5) scene)
        tmp-mp (.computeWorldMatrix mirror-plane true)
        mirror-plane-world-matrix (.getWorldMatrix mirror-plane)
        mirror-plane-vertex-data (.getVerticesData mirror-plane "normal")
        ; tmp-mp-1 (set! (.-position mirror-plane) (bjs/Vector3. 49.27 0.155 34.7))
        tmp-mp-1 (set! (.-position mirror-plane) (bjs/Vector3. 40 0.155 30))
        tmp-mp-2 (.rotate mirror-plane bjs/Axis.Y (* base/ONE-DEG 180))
        mirror-plane-normal (bjs/Vector3. (aget mirror-plane-vertex-data 0) (aget mirror-plane-vertex-data 1) (aget mirror-plane-vertex-data 2))
        mirror-plane-normal-2 (bjs/Vector3.TransformNormal. mirror-plane-normal mirror-plane-world-matrix)
        pool-baked-mat (bjs/StandardMaterial. "pool-baked" scene)
        pool-mat (.-material pool)
        tmp (.computeWorldMatrix pool true)
        pool-world-matrix (.getWorldMatrix pool)
        pool-vertex-data (.getVerticesData pool "normal")
        ; tmp2 (prn "aget=" (aget pool-vertex-data 2))
        pool-normal (bjs/Vector3. (aget pool-vertex-data 0) (aget pool-vertex-data 1) (aget pool-vertex-data 2))
        pool-normal-2 (bjs/Vector3.TransformNormal. pool-normal pool-world-matrix)
        reflector (bjs/Plane.FromPositionAndNormal. (.-position pool) (.scale pool-normal-2 -1))
        reflector-mp (bjs/Plane.FromPositionAndNormal. (.-position mirror-plane) (.scale mirror-plane-normal-2 -1))
        tmp4 (prn "tmp4")
        mirror-mat (bjs/StandardMaterial. "mirror" scene)
        mirror-mp-mat (bjs/StandardMaterial. "mirror-mp-mat" scene)
        box-mat (bjs/StandardMaterial. "box-mat" scene)
        quat180 (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 180))]

    (prn "hello")
    ; (set! (.-position mirror) (.add suzanne-pos (bjs/Vector3. 0 0 -5)))
    (set! (.-reflectionTexture mirror-mat) (bjs/MirrorTexture. "mirror" 1024 scene true))
    (set! (-> mirror-mat (.-reflectionTexture) (.-mirrorPlane)) reflector)
    ; (set! (-> mirror-mat (.-reflectionTexture) (.-renderList)) #js [suzanne])
    (set! (-> mirror-mat (.-reflectionTexture) (.-renderList)) (array suzanne sphere))
    (set! (-> mirror-mat (.-reflectionTexture) (.-level)) 1)
    ; (set! (-> mirror-mat (.-reflectionTexture) (.-refreshRate)) 1)
    (set! (.-refreshRate (-> mirror-mat (.-reflectionTexture))) 1)
    (set! (.-material pool) mirror-mat)

    ; (set! (.-reflectionTexture mirror-mp-mat) (bjs/MirrorTexture. "mirror-mat" 1024 scene true))
    ; (set! (.-activeCamera (.-reflectionTexture mirror-mp-mat)) main-scene/camera)
    ; (prn "mirror-mat: reflectionTexture.activeCamera=" (-> mirror-mp-mat (.-reflectionTexture) (.-activeCamera)))
    ; (set! (-> mirror-mp-mat (.-reflectionTexture) (.-mirrorPlane)) reflector-mp)
    ; (set! (-> mirror-mp-mat (.-reflectionTexture) (.-renderList)) (array suzanne sphere pool))
    ; (set! (-> mirror-mp-mat (.-reflectionTexture) (.-level)) 1)
    ; ; (set! (.refreshRate (-> mirror-mp-mat (.-reflectionTexture))) 1)
    ; ; (set! (.-refreshRate (-> mirror-mp-mat (.-reflectionTexture))) 0)
    ; (set! (.-material mirror-plane) mirror-mp-mat)

    ;; box-mat
    (set! (.-backFaceCulling box-mat) true)
    (set! (.-reflectionTexture box-mat) (bjs/CubeTexture. "textures/cubic_texture_exp/bldg" scene))
    (set! (-> box-mat (.-reflectionTexture) (.-coordinatesMode)) bjs/Texture.CUBIC_MODE)
    (set! (.-diffuseColor box-mat) (bjs/Color3. 0 0 0))
    (set! (.-specularColor box-mat) (bjs/Color3. 0 0 0))
    (set! (.-material mirror-plane) box-mat)

    ;; bldg-cube
    (set! (.-position bldg-cube) (bjs/Vector3. 35 2.5 40))
    (set! (.-material bldg-cube) box-mat)

    ; (js-debugger)
    ; (-> probe (.renderList) (.push suzanne))
    ; (.push rList suzanne)
    ; (.push rList pool)
    ; (set! (-> pool (.-material) (.-reflectionTexture)) (.-cubeTexture probe))
    ; shapeMaterial.reflectionTexture.coordinatesMode = BABYLON.Texture.PLANAR_MODE;
    ; new BABYLON.CubeTexture("textures/skybox", scene);
    ; (set! (-> pool (.-material) (.-reflectionTexture)) (bjs/MirrorTexture. "pool-mirror" 1024 scene true))
    ; (set! (-> pool-mat (.-reflectionTexture)) (bjs/CubeTexture. "textures/skybox/skybox" scene))
    ; (set! (-> pool (.-material) (.-reflectionTexture) (.-mirrorPlane)) (bjs/Plane. 0 -1.0 0 -2.0))
    ; (set! (-> pool-mat (.-reflectionTexture) (.-coordinatesMode)) bjs/Texture.PLANAR_MODE)
    ; (.addFloorMesh main-scene/vrHelper (-> scene (.getMeshByName "pool")))
    ;; add teloporation to the pool.
    ; (js-debugger)
    ; (prn "main-scene/xr-mode=" main-scene/xr-mode)
    (when (= main-scene/xr-mode "vr")
      ; (prn "setting up teleportation")
      (.addFloorMesh main-scene/vrHelper pool))
    ; sphere.material.reflectionTexture = probe.cubeTexture));
    ; var mat = new BABYLON.StandardMaterial("wood", scene);
    ; mat.diffuseTexture = new BABYLON.Texture('data:Laerche.jpg', scene, true, true, BABYLON.Texture.BILINEAR_SAMPLINGMODE, null, null, image, true)));
    ; (set! (.-diffuseTexture pool-baked-mat)
    ;   (bjs/Texture. "imgs/pool_baked.png" scene true true bjs/Texture.BILINEAR_SAMPLINGMODE nil nil nil true))
    ; (set! (.-material pool) pool-baked-mat))
    (prn "hi2")))

    ; (-> (.renderList probe) (.push suzanne))))
    ; (.rend)))
 ;    mirror.material.reflectionTexture = new BABYLON.MirrorTexture("mirror", 1024, scene, true));
 ; mirror.material.reflectionTexture.mirrorPlane = new BABYLON.Plane(0, -1.0, 0, -2.0);
 ; mirror.material.reflectionTexture.renderList = [greenSphere, yellowSphere, blueSphere, knot]);

(defn append-hemisferic [path file]
  (.Append bjs/SceneLoader path file main-scene/scene
           #(do
               (re-frame/dispatch [:cube-test.twizzlers.events/scene-loaded :hemisferic])
               (hemisferic-loaded %1)
               (init-exp-gui))))
               ; (re-frame/dispatch [:cube-test.twizzlers.events/hemisferic-loaded %1]))))
               ;; Note: get weird "nth not supported" warning when calling init-exp-gui
               ;; with re-frame.  Note: get into trouble if not consistently callining
               ;; natively or with re-frame.
               ; (re-frame/dispatch [:cube-test.twizzlers.events/init-exp-gui]))))

; (defn vr-init []
;   (println "twizzlers/scene.vr-init entered")
;   (let [scene main-scene/scene]
;     (.addFloorMesh scene.vrHelper (-> scene (.getMeshByName "pool")))))

(defn init [db]
  (println "twizzlers.scene.init: entered db=" db)
  (let [scene main-scene/scene]
      ;; override the initial position and rotation of the non-vr camera.
      (let [do-cam (.-deviceOrientationCamera main-scene/vrHelper)
            quat (bjs/Quaternion.FromEulerAngles (* -5.8 base/ONE-DEG) (* -93 base/ONE-DEG) 0)]
        (set! (.-position do-cam) (bjs/Vector3. 46.37 0.96 45.53))
        (set! (.-rotationQuaternion do-cam) quat))
      ; enable the pool for teleportation
      ; (.addFloorMesh scene.vrHelper (-> scene (.getMeshByName "pool")))
      ;; vrHelper is only accessible upon entry to vr, so set up a listener.
      ; (-> main-scene/vrHelper .-onAfterEnteringVRObservable (.add vr-init))
      ; (.addFloorMesh main-scene/vrHelper (-> scene (.getMeshByName "pool")))
      ; (.enableTeleportation main-scene/vrHelper (js-obj "floorMeshes" [(-> scene (.getMeshByName "pool"))]))
      ; (let [light1 (bjs/HemisphericLight. "light1" (bjs/Vector3. 1 1 0) scene)
      ;       light2 (bjs/PointLight. "light2" (bjs/Vector3. 0 1 -1) scene)])
      ; (load-space-portal
      ;  "models/tmp/"
      ;  "space_portal.glb"
      ;  "space_portal"
      ;   move-camera)))

      ; (let [path (get-in db [:scenes :space-port :path])
      ;       fn (get-in db [:scenes :space-port :fn])]
      ;   (prn "path=" path ", fn=" fn)
      ;   (append-space-portal
      ;    path
      ;    fn))))

      (let [path (get-in db [:scenes :hemisferic :path])
            fn (get-in db [:scenes :hemisferic :fn])]
        (prn "path=" path ", fn=" fn)
        (append-hemisferic
         path
         fn))))

                ; sph (bjs/MeshBuilder.CreateSphere "sphere" (js-obj "diameter" 1) scene)]
              ; (set! sphere sph))))
