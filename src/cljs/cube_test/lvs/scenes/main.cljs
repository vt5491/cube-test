(ns cube-test.lvs.scenes.main
  (:require
   [re-frame.core :as rf]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [cube-test.utils :as utils]
   [babylonjs-gui :as bjs-gui]
   [promesa.core :as p]
   [cube-test.controller-xr :as controller-xr]))
  ;;  [cube-test.lvs.events :as lvs-events]))

(def cam-far-plane 50000)
(def left-ctrl)
(def x-btn)
(def y-btn)
(def dest-gui)
(def tmp)

(defn enter-js-debugger []
  (js-debugger))

(defn update-lankmarks [site pos db]
  (assoc-in db [:landmarks site :pos] {:x (.-x pos) :y (.-y pos) :z (.-z pos)}))

(defn jump-to-landmark [site db]
  (let [pos (get-in db [:landmarks site :pos])
        _ (prn "jump-to-landmark. pos=" pos)
        pos-vec (bjs/Vector3. (:x pos) (:y pos) (:z pos))]
    (prn "jump-to-landmark:  goto:" pos-vec
      (let [scene main-scene/scene
            camera main-scene/camera]
        (set! (.-position camera) pos-vec)))))

(defn lvs-main-loaded [meshes particle-systems skeletons anim-groups transform-nodes name user-cb]
  ;; (println "lvs-main-loaded, length messhes= " (alength meshes))
  ;; (js-debugger)
  (let [scene main-scene/scene]
        ;; probe (bjs/ReflectionProbe. "probe" 512 scene)]
    ;; (prn "scene=" main-scene/ground)
    ;; (prn "lvs-main-loaded: transform-nodes=" transform-nodes)
    (doall (map #(do
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
                     (.dispose %1)))
                  ;;  (when (re-matches #"^aria_bldg.*" (.-id %1))
                  ;;    (prn "aria-pos=" (.-position %1))
                  ;;    (set! tmp %1)))
                    ;;  (js-debugger)))
              meshes))
    (doall (map #(do
                   (when (re-matches #"^aria_bldg" (.-id %1))
                    ;;  (re-frame/dispatch  [::lvs-events/run-game])
                     (rf/dispatch [:cube-test.lvs.events/update-landmarks :aria (.-position %1)])) 
                  ;;  (prn "transform-nodes.id: %1=" (.-id %1) ",pos=" (.-position %1)))
                    ;;  (when (re-matches #"^aria_bldg")))
                   (when (re-matches #"^pool_circle" (.-id %1))
                    ;;  (re-frame/dispatch  [::lvs-events/run-game])
                     (rf/dispatch [:cube-test.lvs.events/update-landmarks :caesar_pool (.-position %1)]))) 
                transform-nodes))))    

(defn enter-xr-handler []
  (let [xr-camera (.-activeCamera main-scene/scene)]
    (set! (.-maxZ xr-camera) cam-far-plane)))

(defn dest-gui-loaded [adv-text gui-plane]
  (let []
        ;; select-btn (.getControlByName adv-text "select_btn")
        ;; left-arrow (.getControlByName adv-text "left_arrow_img")]))
    (prn "dest-gui-loaded")
    (set! dest-gui gui-plane)
    (let [camera main-scene/camera
          aria-btn (.getControlByName adv-text "aria_btn")
          caesars-pool-btn (.getControlByName adv-text "caesars_pool_btn")
          mirage-btn (.getControlByName adv-text "mirage_btn")]
      (when aria-btn
        (-> aria-btn (.-onPointerClickObservable)
          (.add #(do
                   (prn "go to aria")
                   (rf/dispatch [:jump-to-landmark :aria]))))))))
                  ;;  (set! (.-position camera) camera-init-pos))))))))

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

;; (defn ctrl-mesh-loaded-handler [webVRController]
;;   ;; x and a btns
;;   (-> webVRController (.-onMainButtonStateChangedObservable) (.add main-btn-handler))
;;   (-> webVRController (.-onAButtonStateChangedObservable) (.add a-btn-handler))
;;   (-> webVRController (.-onBButtonStateChangedObservable) (.add b-btn-handler)))

(defn player-motion-ctrl-added [motion-ctrl]
  (prn "lvs.main.motion-ctrl.handednes=" (.-handedness motion-ctrl))
  (when (= (.-handedness motion-ctrl) "left")
    ;; (set! player-left-thumbstick (.getComponent motion-ctrl "xr-standard-thumbstick"))
    (set! left-ctrl motion-ctrl)
    (set! x-btn (.getComponent motion-ctrl "x-button"))
    (set! y-btn (.getComponent motion-ctrl "y-button"))))

(defn ctrl-added [xr-ctrl]
  (prn "player.ctrl-added: xr-ctrl=" xr-ctrl)
  (-> xr-ctrl .-onMotionControllerInitObservable (.add player-motion-ctrl-added)))
 
(defn x-btn-handler []
  (prn "lvs-main.x-btn-handler: x-btn pressed hasChanges=" (.-hasChanges x-btn))
  (utils/gui-btn-handler x-btn dest-gui))

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
      ;; (.removeMesh scene sky-box))
      (set! (-> sky-box .-scaling) (bjs/Vector3. 25 25 25)))
    (when bg-sky-box
      (set! (.-isVisible bg-sky-box) false))
    ;; (let [av (.createInstance bjs/Debug.AxesViewer)]
    ;;   ;; (.update bjs/Debug.AxesViewer (bjs/Vector3. -30.0 38.5 -855.0))
    ;;   (.update av (bjs/Vector3. -30.0 38.5 -855.0)))
    (let [av (bjs/Debug.AxesViewer. scene)
          thickness 12
          scale 40]
      ;; (.update av (bjs/Vector3. -30.0 38.5 -855.0)(bjs/Vector3. 1 0 0) (bjs/Vector3. 0 1 0) (bjs/Vector3. 0 0 1))
      (.update av (bjs/Vector3. 8.6 4.4 -907.0)(bjs/Vector3. 1 0 0) (bjs/Vector3. 0 1 0) (bjs/Vector3. 0 0 1))
      ;; (set! (-> av .-xAxis .-scaling .-z) scale)
      (set! (-> av .-xAxis .-scaling ) (bjs/Vector3. thickness thickness scale))
      (set! (-> av .-yAxis .-scaling ) (bjs/Vector3. thickness thickness scale))
      (set! (-> av .-zAxis .-scaling ) (bjs/Vector3. thickness thickness scale)))
      ;; (set! (-> av .-yAxis .-scaling .-z) scale)
      ;; (set! (-> av .-zAxis .-scaling .-z) scale))))
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
    (load-dest-gui)))

(defn tick []
  (let [engine main-scene/engine]
        ;; delta-time (.getDeltaTime engine)
        ;; bldg-cube-rot-y-delta (* (.-y bldg-cube-ang-vel) delta-time)]
    (when (and x-btn (.-pressed x-btn))
      (x-btn-handler))
    (main-scene/tick)))