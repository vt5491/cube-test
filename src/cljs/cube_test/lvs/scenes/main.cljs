(ns cube-test.lvs.scenes.main
  (:require
   [re-frame.core :as rf]
   [babylonjs :as bjs]
   [babylonjs-materials :as bjs-m]
   [cube-test.main-scene :as main-scene]
   [cube-test.utils :as utils]
   [babylonjs-gui :as bjs-gui]
   [promesa.core :as p]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.base :as base]))
  ;;  [cube-test.lvs.events :as lvs-events]))

(def cam-far-plane 50000)
;; (def camera-init-pos (bjs/Vector3. 9 2.2 625))
;; (def camera-init-pos (bjs/Vector3. 15.5 21.5 608))
(def camera-init-pos (bjs/Vector3. 8.6 6.7 550))
;; (def camera-init-rot)
(def left-ctrl)
(def x-btn)
(def y-btn)
(def dest-gui)
(def settings-gui)
(def tmp)
(def x-btn-handled false)
(def y-btn-handled false)
;; (def base-grip-factor 2.0)
(def base-grip-factor 1.8)
(def lvs-grnd)

(defn enter-js-debugger []
  (js-debugger))

(defn update-lankmarks [site pos db]
  ;; (prn "update-landmarks: site=" site ", pos=" pos)
  ;; Note: we switch y and z, since blender's z (up) is bjs's y .
  ;; (assoc-in db [:landmarks site :pos] {:x (.-x pos) :y (.-z pos) :z (.-y pos)})
  ;; Note2: While it's true that blenders y and z are different, when we get the coordinates
  ;; from glb, they are properly set. However, we do need to flip the x-coord for some reason.
  (assoc-in db [:landmarks site :pos] {:x (* -1 (.-x pos)) :y (.-y pos) :z (.-z pos)}))

(defn jump-to-landmark [site db]
  (let [pos (get-in db [:landmarks site :pos])
        _ (prn "jump-to-landmark. pos=" pos)
        pos-vec (bjs/Vector3. (:x pos) (:y pos) (:z pos))]
    (prn "jump-to-landmark:  goto:" pos-vec
      (let [scene main-scene/scene
            camera main-scene/camera]
        (set! (.-position camera) pos-vec)))))

(defn lvs-main-loaded [meshes particle-systems skeletons anim-groups transform-nodes name user-cb]
  ;; (println " lvs-main-loaded, length messhes= " (alength meshes))
  ;; (js-debugger)
  (let [scene main-scene/scene]
        ;; probe (bjs/ReflectionProbe. "probe" 512 scene)]
    ;; (prn "scene=" main-scene/ground)
    ;; (prn "lvs-main-loaded: transform-nodes=" transform-nodes)
    ;; set grnd-zero manually, since it's not "driven" by something in the .glb
                  ;;  (when (or 
                  ;;           ;; (re-matches #"Plane\.003" (.-id %1))
                  ;;           (re-matches #"plane_vt" (.-id %1))
                  ;;           false)
                            ;; (re-matches #"lvs_median_ph.004" (.-id %1))) 
                  ;; (when (re-matches #"Plane.003" (.-id %1)) 
                    ;;  (prn "hi")
                    ;;  (prn "found Plane.003")
                    ;;  (.applyDisplacementMap %1 "textures/lvs_strip/Grass004_2K-JPG/Grass004_2K_Displacement.jpg" 0 0.3 nil nil nil true))))
                 ;;  (when (re-matches #"^aria_bldg.*" (.-id %1))
                 ;;    (prn "aria-pos=" (.-position %1))
                 ;;    (set! tmp %1)))
                      ;;  (js-debugger)))
    (rf/dispatch [:cube-test.lvs.events/update-landmarks :grnd-zero (bjs/Vector3. 0 0 0)])
    (doall (map #(do
                  ;; (prn "id=" (.-id %1) "matches plane_vt=" (re-matches #"plane_vt" (.-id %1)))
                  (when (re-matches #"__root__" (.-id %1))
                    (prn "found root") 
                   ;;  (prn "found root 2")
                    (set! (.-name %1) "blender-lvs")
                    (set! (.-id %1) "blender-lvs"))
                  (when (or (re-matches #"LasVegasBlvd.*" (.-id %1))
                            (re-matches #"EHarmonAve.*" (.-id %1))
                            (re-matches #"EFlamingoRd.*" (.-id %1))
                            (re-matches #"AriaEntryWay.*" (.-id %1)))
                    (->  main-scene/xr-helper .-teleportation (.addFloorMesh %1)))
                  (when (re-matches #"walkway\.001.*" (.-id %1))
                    ;; walkway.001 is a weird artifact of the first walkway, so delete it
                    (prn "now deleting walkway.001")
                    (.dispose %1))
                    ;; (when (or 
                    ;;        (re-matches #"plane_vt" (.-id %1))
                    ;;        (re-matches #"abc-def" (.-id %1)))
                    ;;     (prn "hi")
                    ;;     (.applyDisplacementMap %1 "textures/lvs_strip/Grass004_2K-JPG/Grass004_2K_Displacement.jpg" 0 0.3 nil nil nil true)))
                  ;; (when (re-matches #"walkway_primitive3" (.-id %1))
                  ;;   (prn "now applying disp map walkway_primitive3")
                  ;;   (.applyDisplacementMap %1 "textures/lvs_strip/Grass004_2K-JPG/Grass004_2K_Displacement.jpg" 0 0.2 nil nil nil true))
                  (when (or 
                          (re-matches #"Plane\.014" (.-id %1))
                          (re-matches #"Plane\.015" (.-id %1)))
                          ;; (re-matches #"BezierCircle.001" (.-id %1)))
                     (prn "found Plane.014")
                           ;; mesh.isUpdatable = true;
                           ;; (set! (.-isUpdatable %1) true)
                           ;; (.applyDisplacementMap %1 "textures/lvs_strip/Grass004_2K-JPG/Grass004_2K_Displacement.jpg" 0 1)
                           ;; mesh.applyDisplacementMap  ("/textures/amiga.jpg", 0, 1, null, null, null, true); 
                           ;; (.applyDisplacementMap %1 "textures/lvs_strip/Grass004_2K-JPG/Grass004_2K_Displacement.jpg")
                     (.applyDisplacementMap %1 "textures/lvs_strip/Grass004_2K-JPG/Grass004_2K_Displacement.jpg" 0 0.2 nil nil nil true))
                  ;; (when (re-matches #"Landscape.009" (.-id %1)))
                  ;; (when (or (re-matches #"Landscape.009" (.-id %1)) (re-matches #"abc-def" (.-id %1))))
                  ;; (when (and (re-matches #"Landscape.009" (.-id %1)) true)
                  ;; (when (and true true)
                  ;;  (prn "found Landscape.009"))
                     ;; (.applyDisplacementMap %1 "textures/lvs_strip/Grass004_2K-JPG/Grass004_2K_Displacement.jpg" 0 1.1 nil nil nil true)))
                  (when (re-matches #"lvs_median_ph.003_primitive0" (.-id %1))
                    (prn "found lvs_median_ph.003_primitive0")
                    (.applyDisplacementMap %1 "textures/lvs_strip/Grass004_2K-JPG/Grass004_2K_Displacement.jpg" 0 1.0 nil nil nil true))
                  (when (re-matches #"lvs_median_ph.003_primitive1" (.-id %1))
                    (prn "found lvs_median_ph.003_primitive1")
                    (.applyDisplacementMap %1 "textures/lvs_strip/Grass004_2K-JPG/Grass004_2K_Displacement.jpg" 0 1.0 nil nil nil true))
                  ;; (when (re-matches #"BezierCircle.001" (.-id %1))
                  ;;   (prn "found BezierCircle.001")
                  ;;   (.applyDisplacementMap %1 "textures/lvs_strip/Grass004_2K-JPG/Grass004_2K_Displacement.jpg" 0 0.6 nil nil nil true))
                  (when (or 
                          (re-matches #"BezierCircle.001" (.-id %1)) 
                          (re-matches #"BezierCircle.004" (.-id %1)))
                    (prn "found BezierCircle.001 or 004")
                    (.applyDisplacementMap %1 "textures/lvs_strip/Grass004_2K-JPG/Grass004_2K_Displacement.jpg" 0 0.6 nil nil nil true))
                  (when (re-matches #"lvs_median_ph.003" (.-id %1))
                    (prn "found lvs_median_ph.003")
                    (.forceSharedVertices %1)
                    (.applyDisplacementMap %1 "textures/lvs_strip/Grass004_2K-JPG/Grass004_2K_Displacement.jpg" 0 1.0 nil nil nil true))
                  (when (or 
                          (re-matches #"Landscape.009" (.-id %1)) 
                          (re-matches #"plane_vt" (.-id %1))
                          ;; (re-matches #"BezierCircle.001" (.-id %1))
                          (re-matches #"Plane.003" (.-id %1))
                          (re-matches #"Plane.003.primitive0" (.-id %1)))
                    (prn "hi" ",id %1=" (.-id %1))
                    (.applyDisplacementMap %1 "textures/lvs_strip/Grass004_2K-JPG/Grass004_2K_Displacement.jpg" 0 0.25 nil nil nil true)))
             meshes))
    (doall (map #(do
                   (when (re-matches #"^aria_bldg" (.-id %1))
                     (rf/dispatch [:cube-test.lvs.events/update-landmarks :aria (.-position %1)])) 
                  ;;  (when (re-matches #"^pool_circle" (.-id %1)))
                   (when (re-matches #"^column.001" (.-id %1))
                     (rf/dispatch [:cube-test.lvs.events/update-landmarks :caesars-pool (.-position %1)])) 
                   (when (re-matches #"^RightTower.001" (.-id %1))
                     (rf/dispatch [:cube-test.lvs.events/update-landmarks :mirage (.-position %1)])) 
                   (when (re-matches #"^LEDScreen.002" (.-id %1))
                     (rf/dispatch [:cube-test.lvs.events/update-landmarks :aria-sign (.-position %1)])) 
                   (when (re-matches #"^EncoreTower.010" (.-id %1))
                     (rf/dispatch [:cube-test.lvs.events/update-landmarks :wynn (.-position %1)])) 
                   (when (re-matches #"^combined_tower" (.-id %1))
                     (rf/dispatch [:cube-test.lvs.events/update-landmarks :wynn-aria (.-position %1)])) 
                   (when (re-matches #"^tower.002" (.-id %1))
                     (rf/dispatch [:cube-test.lvs.events/update-landmarks :burj-al-arab (.-position %1)]))) 
                transform-nodes))))    

(defn enter-xr-handler []
  (let [xr-camera (.-activeCamera main-scene/scene)]
    (set! (.-maxZ xr-camera) cam-far-plane)))

(defn dest-gui-loaded [adv-text gui-plane]
  (let []
        ;; select-btn (.getControlByName adv-text "select_btn")
        ;; left-arrow (.getControlByName adv-text "left_arrow_img")]))
    ;; (prn "dest-gui-loaded")
    (set! dest-gui gui-plane)
    (.setEnabled dest-gui false)
    (let [camera main-scene/camera
          aria-btn (.getControlByName adv-text "aria_btn")
          aria-sign-btn (.getControlByName adv-text "aria_sign_btn")
          caesars-pool-btn (.getControlByName adv-text "caesars_pool_btn")
          mirage-btn (.getControlByName adv-text "mirage_btn")
          wynn-btn (.getControlByName adv-text "wynn_btn")
          wynn-aria-btn (.getControlByName adv-text "wynn_aria_btn")
          burj-al-arab-btn (.getControlByName adv-text "burj_al_arab_btn")
          grnd-zero-btn (.getControlByName adv-text "grnd_zero_btn")]
      (when aria-btn
        (-> aria-btn (.-onPointerClickObservable)
          (.add #(do
                   (prn "go to aria")
                   (rf/dispatch [:jump-to-landmark :aria])))))
                  ;;  (set! (.-position camera) camera-init-pos))))))))
      (when aria-sign-btn
        (-> aria-sign-btn (.-onPointerClickObservable)
          (.add #(do
                   (prn "go to aria-sign")
                   (rf/dispatch [:jump-to-landmark :aria-sign])))))
      (when caesars-pool-btn
        (-> caesars-pool-btn (.-onPointerClickObservable)
          (.add #(do
                   (prn "go to caesars-pool")
                   (rf/dispatch [:jump-to-landmark :caesars-pool])))))
      (when mirage-btn
        (-> mirage-btn (.-onPointerClickObservable)
          (.add #(do
                   (prn "go to mirage")
                   (rf/dispatch [:jump-to-landmark :mirage])))))
      (when wynn-btn
        (-> wynn-btn (.-onPointerClickObservable)
          (.add #(do
                   (prn "go to wynn")
                   (rf/dispatch [:jump-to-landmark :wynn])))))
      (when wynn-aria-btn
        (-> wynn-aria-btn (.-onPointerClickObservable)
          (.add #(do
                   (prn "go to wynn-aria")
                   (rf/dispatch [:jump-to-landmark :wynn-aria])))))
      (when burj-al-arab-btn
        (-> burj-al-arab-btn (.-onPointerClickObservable)
          (.add #(do
                   (prn "go to burj-al-arab")
                   (rf/dispatch [:jump-to-landmark :burj-al-arab])))))
      (when grnd-zero-btn
        (-> grnd-zero-btn (.-onPointerClickObservable)
          (.add #(do
                  ;;  (prn "go to burj-al-arab")
                   (rf/dispatch [:jump-to-landmark :grnd-zero]))))))))

(defn load-dest-gui []
  (let [scene main-scene/scene
        plane (bjs/MeshBuilder.CreatePlane "dest-gui-plane" (js-obj "width" 6, "height" 3) scene)
        ; _ (set! (.-position plane) (bjs/Vector3. 0 3 0))
        ; _ (.enableEdgesRendering plane)
        ; _ (set! (.-edgesWidth plane) 1.0)
        left-xr-ctrl controller-xr/left-ctrl-xr
        adv-text (bjs-gui/AdvancedDynamicTexture.CreateForMesh plane 1920 1080)]
    ;; (set! (.-position plane) (bjs/Vector3. 0 2 0))
    (.enableEdgesRendering plane)
    (set! (.-edgesWidth plane) 1.0)
    (when left-xr-ctrl
      (set! (.-position plane) (-> left-xr-ctrl .-grip .-position)))
    (-> adv-text
     ; (.parseFromURLAsync "guis/top_scene/choice_carousel_gui.json")
        (.parseFromURLAsync "guis/lvs/select_dest_gui.json")
        (p/then #(dest-gui-loaded adv-text plane)))))

(defn settings-gui-loaded [adv-text gui-plane]
  (set! settings-gui gui-plane)
  (.setEnabled settings-gui false)
  (let [gf-slider (.getControlByName adv-text "gf-slider")] 
    (when gf-slider
      ;; (-> gf-slider (.-onPointerClickObservable))
      ;; (-> gf-slider (.-onDirtyObservable))
      (-> gf-slider (.-onValueChangedObservable)
        (.add (fn [scale-fac]
                ;; (prn "you pressed the slider, scale-fac" scale-fac)
                ;; (let [gf cube-test.controller-xr/grip-factor]
                (set! cube-test.controller-xr/grip-factor (* base-grip-factor scale-fac))))))))
                ;; (prn "new grip factor=" cube-test.controller-xr/grip-factor)))))))
                 
  

(defn load-settings-gui []
  (let [scene main-scene/scene
        plane (bjs/MeshBuilder.CreatePlane "settings-gui-plane" (js-obj "width" 6, "height" 4) scene)
        ;; left-xr-ctrl controller-xr/left-ctrl-xr
        adv-text (bjs-gui/AdvancedDynamicTexture.CreateForMesh plane 1920 1080)]
    (.enableEdgesRendering plane)
    (-> adv-text
        (.parseFromURLAsync "guis/lvs/settings_gui.json")
        (p/then #(settings-gui-loaded adv-text plane)))))

;; (defn ctrl-mesh-loaded-handler [webVRController]
;;   ;; x and a btns
;;   (-> webVRController (.-onMainButtonStateChangedObservable) (.add main-btn-handler))
;;   (-> webVRController (.-onAButtonStateChangedObservable) (.add a-btn-handler))
;;   (-> webVRController (.-onBButtonStateChangedObservable) (.add b-btn-handler)))

(defn player-motion-ctrl-added [motion-ctrl]
  ;; (prn "lvs.main.motion-ctrl.handednes=" (.-handedness motion-ctrl))
  (when (= (.-handedness motion-ctrl) "left")
    (set! left-ctrl motion-ctrl)
    (set! x-btn (.getComponent motion-ctrl "x-button"))
    (set! y-btn (.getComponent motion-ctrl "y-button"))))

(defn ctrl-added [xr-ctrl]
  (prn "player.ctrl-added: xr-ctrl=" xr-ctrl)
  (-> xr-ctrl .-onMotionControllerInitObservable (.add player-motion-ctrl-added)))
 
(defn x-btn-handler []
  ;; (prn "lvs-main.x-btn-handler: x-btn pressed hasChanges=" (.-hasChanges x-btn))
  (utils/gui-btn-handler x-btn dest-gui))

(defn y-btn-handler []
  ;; (prn "lvs-main.x-btn-handler: x-btn pressed hasChanges=" (.-hasChanges x-btn))
  (utils/gui-btn-handler y-btn settings-gui))

;; override the default ground as set by main-scene
(defn init-lvs-grnd []
  (let [grnd (bjs/MeshBuilder.CreateGround "lvs-grnd" (js-obj "width" 200 "height" 500 "subdivisions" 1))]
    (set! (.-scaling grnd) (bjs/Vector3. 10.0 1 10.0))
    (set! (.-material grnd) (bjs-m/GridMaterial. "lvs-grnd-mat" main-scene/scene))
    (set! (.-position grnd) (bjs/Vector3. 0 -1.0 0))
    ;; (.enableTeleportation vrHelper (js-obj "floorMeshName" "ground"))
    (->  main-scene/xr-helper .-teleportation (.addFloorMesh grnd))
    (set! lvs-grnd grnd)))

(defn init []
  (prn "lvs-main-scene: entered")
  (let [scene main-scene/scene
        ;; env main-scene/env
        ;; grnd (.-ground env)
        grnd main-scene/ground
        grip-factor cube-test.controller-xr/grip-factor
        current-camera (.-activeCamera scene)
        sky-box (.getMeshByID scene "sky-box")
        bg-sky-box (.getMeshByID scene "BackgroundSkybox")]

    ;; (utils/load-model "models/lvs/" "ball_mirror.glb" "mirror" lvs-main-loaded))) 
    (set! cube-test.controller-xr/grip-factor (+ grip-factor 0.37))
    (prn "lvs-main.init: grip-factor=" cube-test.controller-xr/grip-factor)
    (utils/load-model "models/lvs/" "lvs_main.glb" "lvs-main" lvs-main-loaded) 
    ;; (set! (.-position current-camera) (.add (.-position current-camera)(bjs/Vector3. -14 0 -875)))
    (set! (.-position current-camera) camera-init-pos)
    ;; (set! (.-rotationQuaternion current-camera) (bjs/Quaternion.RotationYawPitchRoll (* base/ONE-DEG 32) (* base/ONE-DEG 198) 0))
    (set! (.-rotationQuaternion current-camera)(bjs/Quaternion.FromEulerAngles (* base/ONE-DEG 32) (* base/ONE-DEG 198) 0))
    ;; (set! (.-position grnd) (bjs/Vector3. 0 -5 0))
    ;; (js-debugger)
    (init-lvs-grnd)
    (.setEnabled grnd false)

    (when sky-box
      ;; (.removeMesh scene sky-box))
      (set! (-> sky-box .-scaling) (bjs/Vector3. 25 25 25)))
    (when bg-sky-box
      (set! (.-isVisible bg-sky-box) false))
    ;; (let [av (.createInstance bjs/Debug.AxesViewer)]
    ;;   ;; (.update bjs/Debug.AxesViewer (bjs/Vector3. -30.0 38.5 -855.0))
    ;;   (.update av (bjs/Vector3. -30.0 38.5 -855.0)))
    (let [av (bjs/Debug.AxesViewer. scene)
          av-2 (bjs/Debug.AxesViewer. scene)
          thickness 12
          scale 40
          scale-vec (bjs/Vector3. thickness thickness scale)]
      ;; (.update av (bjs/Vector3. -30.0 38.5 -855.0)(bjs/Vector3. 1 0 0) (bjs/Vector3. 0 1 0) (bjs/Vector3. 0 0 1))
      ;; (.update av (bjs/Vector3. 8.6 4.4 -907.0)(bjs/Vector3. 1 0 0) (bjs/Vector3. 0 1 0) (bjs/Vector3. 0 0 1))
      (.update av (.add camera-init-pos (bjs/Vector3. 2 5 8)) (bjs/Vector3. 1 0 0) (bjs/Vector3. 0 1 0) (bjs/Vector3. 0 0 1))
      ;; (.update av-2 bjs/Vector3.Zero (bjs/Vector3. 1 0 0) (bjs/Vector3. 0 1 0) (bjs/Vector3. 0 0 1))
      (.update av-2 (bjs/Vector3. 5 5 5) (bjs/Vector3. 1 0 0) (bjs/Vector3. 0 1 0) (bjs/Vector3. 0 0 1))
      ;; (set! (-> av .-xAxis .-scaling .-z) scale)
      (set! (-> av .-xAxis .-scaling ) (bjs/Vector3. thickness thickness scale))
      (set! (-> av .-yAxis .-scaling ) (bjs/Vector3. thickness thickness scale))
      (set! (-> av .-zAxis .-scaling ) (bjs/Vector3. thickness thickness scale))
      (set! (-> av-2 .-xAxis .-scaling ) scale-vec)
      ;; (prn "av2.xAxis.scaling=" (-> av-2 .-xAxis .-scaling))
      (set! (-> av-2 .-yAxis .-scaling ) scale-vec)
      (set! (-> av-2 .-zAxis .-scaling ) scale-vec))
    (set! (.-maxZ current-camera) cam-far-plane)

    (-> main-scene/xr-helper (.-baseExperience)
                  (.-onStateChangedObservable)
                  (.add enter-xr-handler))
    ;; (set! (.-environmentTexture scene) nil)))
    ;; var hdrTexture = new BABYLON.CubeTexture("textures/country.env", scene)));
    (let [ 
          ;; hdr-text] (bjs/CubeTexture. "textures/envs/country.env" scene)
          ;; dark. not very good
          ;; hdr-text (bjs/CubeTexture. "textures/envs/night.env" scene)
          ;; kind of pink
          ;; hdr-text (bjs/CubeTexture. "textures/envs/Runyon_Canyon_A_2k_cube_specular.env" scene)
          ;; bright
          hdr-text (bjs/CubeTexture. "textures/envs/SpecularHDR.env" scene)]
          ;; very bright.. pretty neutral
          ;; hdr-text (bjs/CubeTexture. "textures/envs/parking.env" scene)
          ;; not bad
          ;; hdr-text (bjs/CubeTexture. "textures/envs/Studio_Softbox_2Umbrellas_cube_specular.env" scene)
          ;; kind of has a sunsetty feel
          ;; hdr-text (bjs/CubeTexture. "textures/envs/room.env" scene)]
      (set! (.-environmentTexture scene) hdr-text))
    (let [xr-helper main-scene/xr-helper]
      (-> xr-helper (.-input ) (.-onControllerAddedObservable) (.add ctrl-added)))
    (load-dest-gui)
    (load-settings-gui)))

(defn tick []
  (let [engine main-scene/engine]
        ;; delta-time (.getDeltaTime engine)
        ;; bldg-cube-rot-y-delta (* (.-y bldg-cube-ang-vel) delta-time)]
    (when (and x-btn (.-pressed x-btn))
      (x-btn-handler))
    (when (and y-btn (.-pressed y-btn))
      (y-btn-handler))
    (main-scene/tick)))