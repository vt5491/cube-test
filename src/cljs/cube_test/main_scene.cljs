;; main_scene should reference few and be accessible by many
(ns cube-test.main-scene
  (:require-macros [cube-test.macros :as macros])
  (:require
   [re-frame.core :as re-frame]
   [cube-test.base :as base]
   [cube-test.controller :as controller]
   [babylonjs :as bjs]
   [babylonjs-materials :as bjs-m]
   ;; Note: you need this to get the controller models loaded
   [babylonjs-loaders :as bjs-l]
   [babylonjs-gui :as bjs-gui]
   ["@hapi/ammo" :as ammo]
   [cannon :as cannon]
   [oimo :as oimo]
   [promesa.core :as p]
   [webxr-polyfill :as xr-pf]))
; import * as GUI from 'babylonjs-gui';
(def canvas)
(def engine)
(def scene)
(def env)
(def camera)
(def camera-rig)
(def camera-init-pos (js/BABYLON.Vector3. 0 4 -15))
(def vrHelper)
(def xr)
(def ground)
(def grid-mat)
(def features-manager)
; (def light1)
(def red-mat)
(def green-mat)
(def blue-mat)
; (def cube)
(def poly-fill)
(def xr-helper)
(def session-mgr)
(def skybox-mat)
(def xr-mode)
(def physics-plugin)
(def gui-mgr)
(def gui-anchor)
(def spin-fwd-btn)
(def spin-bwd-btn)
(def plane)
(def plane2)
(def gui-pnl)
(def adv-texture)

(declare init-part-2)
(declare init-cube)
(declare setup-skybox)
(declare init-action-pnl)
(declare init-gui)
(declare init-gui-2)
(declare pointer-handler)
(declare enter-xr-handler)

(defn init [top-level-scene-initializer]
; (defn init []
; (defn init [n]
  (println "main-scene.init: entered")
  ;; following line necessary for mixamo animations.
  ; (set! js/BABYLON.Animation.AllowMatricesInterpolation true)
  (set! bjs/Animation.AllowMatricesInterpolation true)
  (set! canvas (-> js/document (.getElementById "renderCanvas")))
  (set! engine (bjs/Engine. canvas true))
  (set! scene (bjs/Scene. engine))
  (re-frame/dispatch [:set-main-scene scene])
  ; (set! env (.-createDefaultEnvironment scene))
  ;; Note: we have to manually create our own ground in order to
  ;; properly wrap it in a physicsImposter.
  (set! physics-plugin (bjs/OimoJSPlugin.))
  (.enablePhysics scene (bjs/Vector3. 0 -0.81 0) physics-plugin)

  (set! env (bjs/EnvironmentHelper.
             (js-obj
              "createGround" false
              "skyboxSize" 30)
             scene))
  ; (set! env (.createDefaultEnvironment scene))
  ;; manually create a ground with a physicsImposter.
  (let [grnd (bjs/MeshBuilder.CreateGround "ground" (js-obj "width" 10 "height" 10 "subdivisions" 10))]
    (set! (.-material grnd) (bjs-m/GridMaterial. "ground-mat" scene))
    (set! (.-physicsImpostor grnd)
      (bjs/PhysicsImpostor. grnd bjs/PhysicsImpostor.PlaneImposter
                            (js-obj "mass" 0 "restitution" 0.9) scene))
    (set! (.-ground env) grnd))

  (set! grid-mat (bjs-m/GridMaterial. "ground-mat" scene))
  ; (prn "num of textures=" (count (.-getActiveTextures grid-mat)))

  ; (set! env (.createDefaultEnvironment scene
  ;             (js-obj
  ;              ; "groundTexture" (bjs-m/GridMaterial. "ground-mat" scene)
  ;              ; "groundTexture" grid-mat
  ;              ; "groundTexture" (.-getActiveTextures grid-mat)
  ;              "skyboxSize" 30)))
  ; (js-debugger)
  ; (set! (.-physicsImpostor (.-ground env))
  ; ; (set! (.-physicsImpostor (-> env .-ground .-parent))
  ;   (bjs/PhysicsImpostor. (.-ground env) (js-obj "mass" 0 "restitution" 0.9) scene))
  ;                           (js-obj "mass" 0 "restitution" 0.9) scene))
  ; (let [grnd (.-ground env)]
  ;   (set! (.-physicsImpostor grnd)
  ;     (bjs/PhysicsImpostor. grnd bjs/PhysicsImpostor.PlaneImposter
  ;                           (js-obj "mass" 0 "restitution" 0.9) scene)))
  (set! red-mat (bjs/StandardMaterial. "red-mat" scene))
  (set! (.-diffuseColor red-mat) (bjs/Color3. 1 0 0))
  (set! green-mat (bjs/StandardMaterial. "green-mat" scene))
  (set! (.-diffuseColor green-mat) (bjs/Color3. 0 1 0))
  (set! blue-mat (bjs/StandardMaterial. "blue-mat" scene))
  (set! (.-diffuseColor blue-mat) (bjs/Color3. 0 0 1))
  ; (js/BABYLON.Debug.AxesViewer. scene)
  ; (-> (bjs/Debug.AxesViewer. scene) (.update (bjs/Vector3. 5 0 0)))
  (let [av (bjs/Debug.AxesViewer. scene)]
  ; (let [av bjs/Debug.AxesViewer.createInstance])
  ; (let [av (.createInstance bjs/Debug.AxesViewer.)]
  ;   (js-debugger)
    ; (.update av (bjs/Vector3. 0 0 0) (.-xAxis av) (.-yAxis av) (.-zAxis av))
    (.update av (bjs/Vector3. 5 0 0) (bjs/Vector3. 1 0 0) (bjs/Vector3. 0 1 0) (bjs/Vector3. 0 0 1)))
  (setup-skybox)
  ; (set! physics-plugin (bjs/AmmoJSPlugin. true "Ammo"))
  ; (set! physics-plugin (ammo/AmmoJSPlugin. true "Ammo"))
  ; (set! physics-plugin (bjs/CannonJSPlugin.))
  ; (set! physics-plugin (bjs/CannonJSPlugin. true "CANNON"))
  ; ar ground = BABYLON.Mesh.CreateGround('ground1', 6, 6, 2, scene);
  ; (bjs/Mesh.CreateGround "ground" 6 6 0.5 scene)
  ; (bjs/GroundMesh. "ground" scene)
  ; BABYLON.MeshBuilder.CreateGround(“myGround”, { width: 4, height: 4, depth: 4 }, scene);
  ; ground.material = new BABYLON.GridMaterial("groundMaterial", scene);
  ;; ground
  ; (let [grnd (bjs/MeshBuilder.CreateGround "ground" (js-obj "width" 10 "height" 10 "subdivisions" 10))]
  ;   (set! (.-material grnd) (bjs-m/GridMaterial. "ground-mat" scene))
  ;   (set! (.-physicsImpostor grnd)
  ;     (bjs/PhysicsImpostor. grnd bjs/PhysicsImpostor.PlaneImposter
  ;                           (js-obj "mass" 0 "restitution" 0.9) scene))
  ;   (set! (.-ground env) grnd))

  (cond
    (re-seq #"Chrome" js/navigator.userAgent)
    (do
      (prn "Chrome detected")
      (set! xr-mode "xr"))
    (re-seq #"Firefox" js/navigator.userAgent)
    (do
      (prn "Firefox detected")
      ; (set! xr-mode "vr")
      (set! xr-mode "xr"))
    true (set! xr-mode "xr"))
  ; (set! poly-fill (xr-pf/WebXRPolyfill.))
  ; (set! poly-fill (xr-pf/WebXRPolyfill))
  ; (set! poly-fill (xr-pf.))
  ; (set! poly-fill xr-pf/WebXRPolyfill. (js-obj "webvr" true))
  ; (prn "about to call debugger")
  ; (js-debugger)
  ; (set! camera (bjs/ArcRotateCamera. "Camera" (/ js/Math.PI 2) (/ js/Math.PI 2) 2 (bjs/Vector3. 0 0 5) scene))
  ; (if (not base/use-xr))
  (prn "xr-mode=" xr-mode)
  (if (= xr-mode "vr")
    (do
      (println "now setting up vr")
      (set! vrHelper (.createDefaultVRExperience scene (js-obj "useXR" false)))
      (set! camera (.-webVRCamera vrHelper))
      ; (set! (.-position camera) (bjs/Vector3. 0 0 -5))
      ; (set! camera-rig (bjs/TransformNode. "camera-rig"))
      ; (set! (.-rotation camera-rig) (bjs/Vector3. 0 js/Math.PI 0))
      ; (set! (.-parent camera) camera-rig)
      ; (set! (.-rotation camera-rig) (bjs/Vector3. 0 3.1 0))
      ; (set! (.-rotation camera) (bjs/Vector3. 0 (/ js/Math.PI 2) 0))
      ; (set! camera (.-deviceOrientationCamera vrHelper))
      (let [do-cam (.-deviceOrientationCamera vrHelper)]
        (set! (.-position do-cam) (bjs/Vector3. 0 4 -15)))
      ; camera.rotationQuaternion = BABYLON.Quaternion.RotationYawPitchRoll(-x, 0, 0);
      (set! (.-id camera) "main-camera")
      ; (set! (.-rotationQuaternion camera) (bjs/Quaternion.RotationYawPitchRoll 0 js/Math.Pi 0))
      (controller/init scene vrHelper camera)
      (controller/setup-controller-handlers vrHelper)
      (.enableTeleportation vrHelper (js-obj "floorMeshName" "ground"))
      (.enableInteractions vrHelper)
      ; vrHelper.onAfterEnteringVRObservable.add(() => {})
      ; disc.rotate(BABYLON.Axis.Y, Math.PI / 150, BABYLON.Space.LOCAL);
      (-> vrHelper .-onAfterEnteringVRObservable (.add
                                                  (fn []
                                                    (prn "entered vr")
                                                    (.setTarget camera (bjs/Vector3. 0 0 0))
                                                    (prn "cam-rot a=" (.-rotation camera))
                                                    ; (set! (.-rotation camera) (bjs/Vector3. 0 js/Math.PI 0))
                                                    ; (set! (.-rotation camera) (bjs/Vector3. js/Math.PI 0 0))
                                                    ;;vt-x
                                                    (set! (.-rotationQuaternion camera) (bjs/Quaternion.RotationYawPitchRoll (/ js/Math.PI 1.0) 0 0))
                                                    ; (set! (.-rotationQuaternion camera) (bjs/Quaternion.RotationYawPitchRoll 0 0 0))
                                                    (.resetToCurrentRotation camera)
                                                    (prn "cam-rot a=" (.-rotation camera))
                                                    ; (set! (.-rotation camera-rig) (bjs/Vector3. 0 js/Math.PI 0))
                                                    (let [cam-pos (.-position camera)
                                                          x (.-x cam-pos)
                                                          y (.-y cam-pos)
                                                          z (.-z cam-pos)]
                                                      (set! (.-position camera) (bjs/Vector3. x y -5)))
                                                    ; (.rotate camera-rig bjs/Axis.Y (/ js/Math.PI 2) bjs/Space.LOCAL)
                                                    ; (.rotate camera bjs/Axis.Y (/ js/Math.PI 2) bjs/Space.LOCAL)
                                                    (prn "cam-rot c=" (.-rotation camera)))))

      (top-level-scene-initializer))
      ; (init-part-2))
    (do
      ;; set up xr
      ; (js-debugger)
      (set! camera (bjs/UniversalCamera. "uni-cam" (bjs/Vector3. 0 4 -15) scene))
      (-> (.createDefaultXRExperienceAsync scene (js-obj "floorMeshes" (array (.-ground env))))
          (p/then
           (fn [xr-default-exp]
             (set! xr-helper xr-default-exp)
             ; xrHelper.enterexitui["_buttons"][0].sessionMode = "immersive-ar";
             ; (-> xr-default-exp (.-baseExperience)
             ;     (set! .-sessionMOde (-> (.-enterexitui) (get (.-_buttons) 0)) "immersive-ar"))
             ; (let [btns (-> xr-default-exp (.-enterExitUI) (.-_buttons))
             ;       btn0 (get btns 0)]
             ;   ; (js-debugger)
             ;   (.update btn0))
             ; (set! (.-sessionMode btn0) "immersive-ar"))
             (re-frame/dispatch [:setup-xr-ctrl-cbs xr-default-exp])
             ;; Note: baseExperience is of type WebXRExperienceHelper
             (set! features-manager (-> xr-default-exp (.-baseExperience) (.-featuresManager)))
             (println "xr features available=" (.GetAvailableFeatures js/BABYLON.WebXRFeaturesManager))
             (println "xr features acitve=" (-> xr-default-exp (.-baseExperience) (.-featuresManager) (.getEnabledFeatures)))
             (set! camera (-> xr-default-exp (.-baseExperience) (.-camera)))
             (set! (.-position camera) camera-init-pos)
             ;;Note: setting rotations on the xr camera here have no effect.  You have to do it
             ;; on the pre-xr camera (any rotations on that *will* propagate to the xr camera)
             (re-frame/dispatch [:init-xr xr-default-exp])
             (-> xr-default-exp (.-baseExperience)
                 (.-onStateChangedObservable)
                 (.add enter-xr-handler))
                 ;; Note: set xr camera rotation in 'enter-xr-handler'

             (println "main-scene: top-level scene=" top-level-scene-initializer)
             (top-level-scene-initializer)))))))
             ; (init-part-2)))))))

(defn enter-xr-handler [state]
  (println "enter-xr-handler: onStateChangedObservable: state=" state)
  (when (= state bjs/WebXRState.IN_XR)
    (println "onStateChangedObservable: state: in xr")
    (println "state: old camera pos=" (.-position camera) ",camera-init-pos=" camera-init-pos)
    (set! (.-position camera) (bjs/Vector3. 0 4 -10))
    ; (set! (.-rotation camera) (bjs/Vector3. (* 90 base/ONE-DEG (* 180 base/ONE-DEG 0)) 0))
    ; (let [ quat (-> scene .activeCamera .rotationQuaternion)])
    ;; Do camera rotation adjustments (upon entering xr) here.
    (let [ quat (-> camera .-rotationQuaternion)]
      ; cur-angles (.toEulerAngles quat)]
      ;; Note: do runtime vr camera rotation here
      ; (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles 0 (* 90 base/ONE-DEG) 0)))))
      ; (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles 0 (* -135 base/ONE-DEG) 0)))))
      ; (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles 0 (* 180 base/ONE-DEG) 0)))))
      (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles 0 (* -0 base/ONE-DEG) 0)))))

(defn enter-vr []
  (prn "main-scene.enter-vr: entered")
  ; (js-debugger)
  ; (-> (.enterXRAsync (.-baseExperience xr-helper)))
  ; (-> (.enterXRAsync (.baseExperience xr-helper) "immersive-vr" "local-floor"))
  (-> (.-baseExperience xr-helper) (.enterXRAsync "immersive-vr" "local-floor")
  ; (-> (.-baseExperience xr-helper) (.enterXRAsync "inline" "viewer")
  ; (-> (.enterXRAsync xr-helper)
      (p/then
       (fn []
         (prn "entered xr")))))

(defn setup-skybox []
  (let [skybox (bjs/MeshBuilder.CreateBox. "sky-box" (js-obj "size" 1000.0) scene)
        skybox-mat (bjs/StandardMaterial. "sky-box" scene)]
    (set! (.-backFaceCulling skybox-mat) false)
    (set! (.-reflectionTexture skybox-mat) (bjs/CubeTexture. "textures/skybox/skybox" scene))
    (set! (-> skybox-mat .-reflectionTexture .-coordinatesMode) (-> bjs .-Texture .-SKYBOX_MODE))
    (set! (-> skybox-mat .-diffuseColor) (bjs/Color3. 0 0 0))
    (set! (-> skybox-mat .-specularColor) (bjs/Color3. 0 0 0))
    (set! (.-material skybox) skybox-mat)))

;; Note: do not want render loop in main scene, since it needs to call the tick of other
;; namespaces, thus violating the "refer to few, referenced by many" principle.  We can normally
;; use a re-frame event dispatch to get around this, but you do not want to call re-frame on a game
;; tick for performance rease.  Therefore, it's better to put into a module like 'game'.
; (defn run-scene [render-loop]
;   (.runRenderLoop engine (fn [] (render-loop))))
; ;;
; ;;
; (defn render-loop []

(defn tick [])
