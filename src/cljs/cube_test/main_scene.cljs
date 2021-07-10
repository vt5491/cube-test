;; main_scene should reference few and be accessible by many
(ns cube-test.main-scene
  ; (:require-macros [cube-test.macros :as macros])
  (:require
   [re-frame.core :as re-frame]
   [cube-test.base :as base]
   [cube-test.controller :as controller]
   [babylonjs :as bjs]
   [babylonjs-materials :as bjs-m]
   ;; Note: you need this to get the controller models loaded
   [babylonjs-loaders :as bjs-l]
   [babylonjs-gui :as bjs-gui]
   ; ["@hapi/ammo" :as ammo]
   [cannon :as cannon]
   [oimo :as oimo]
   [promesa.core :as p]
   [webxr-polyfill :as xr-pf]))
; import * as GUI from 'babylonjs-gui';
(defn dummy [])

(def canvas)
(def engine)
; (def scene)
; (defonce scene)
(def env)
(def camera)
(def camera-rig)
; (def camera-init-pos (js/BABYLON.Vector3. 0 4 -15))
(def camera-init-pos (js/BABYLON.Vector3. 0 0 -5))
(def ^:dynamic *camera-init-pos* (atom {:x 0, :y 0, :z -5}))
; (def ^:dynamic *camera-init-pos* (atom {:x 58.6, :y 7.9, :z 12.7}))
(def ^:dynamic *camera-init-rot* (atom {:x 0, :y 0, :z -5}))
; (def non-vr-camera)
(def vrHelper)
(def xr)
(def ground)
(def grid-mat)
(def features-manager)
(def red-mat)
(def green-mat)
(def orange-mat)
(def blue-mat)
(def white-mat)
(def black-mat)
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
(def top-level-scene-init)

;; we have to pre-declare scene because it's defined with 'init' as a defonce.
(declare scene)
(declare init)
(declare init-part-2)
(declare init-cube)
(declare setup-skybox)
(declare init-action-pnl)
(declare init-gui)
(declare init-gui-2)
(declare pointer-handler)
(declare enter-xr-handler)
(declare enter-vr-handler)

(defn init [top-level-scene-initializer]
  (println "main-scene.init: entered")
  (set! top-level-scene-init top-level-scene-initializer)

  ;; following line necessary for mixamo animations.
  (set! bjs/Animation.AllowMatricesInterpolation true)
  (set! canvas (-> js/document (.getElementById "renderCanvas")))
  (set! engine (bjs/Engine. canvas true))
  ;; Note: even though scene is defined inside 'init' it has file level scope.
  (defonce scene (bjs/Scene. engine))
  (re-frame/dispatch [:set-main-scene scene])
  ;; Note: we have to manually create our own ground in order to
  ;; properly wrap it in a physicsImposter.
  (set! physics-plugin (bjs/OimoJSPlugin.))
  (.enablePhysics scene (bjs/Vector3. 0 -0.81 0) physics-plugin)

  (set! env (bjs/EnvironmentHelper.
             (js-obj
              "createGround" false
              "skyboxSize" 30)
             scene))
  ;; manually create a ground with a physicsImposter.
  (let [grnd (bjs/MeshBuilder.CreateGround "ground" (js-obj "width" 10 "height" 10 "subdivisions" 10))]
    ; (set! (.-material grnd) (bjs-m/GridMaterial. "ground-mat" scene))
    (set! (.-material grnd) (bjs-m/GridMaterial. "green-mat" scene))
    (set! (.-physicsImpostor grnd)
      (bjs/PhysicsImpostor. grnd bjs/PhysicsImpostor.PlaneImposter
                            (js-obj "mass" 0 "restitution" 0.9) scene))
    (set! (.-ground env) grnd))

  (set! grid-mat (bjs-m/GridMaterial. "ground-mat" scene))
  (set! red-mat (bjs/StandardMaterial. "red-mat" scene))
  (set! (.-diffuseColor red-mat) (bjs/Color3. 1 0 0))
  (set! green-mat (bjs/StandardMaterial. "green-mat" scene))
  (set! (.-diffuseColor green-mat) (bjs/Color3. 0 1 0))
  (set! orange-mat (bjs/StandardMaterial. "orange-mat" scene))
  (set! (.-diffuseColor orange-mat) (bjs/Color3. 1 0.5 0))
  (set! blue-mat (bjs/StandardMaterial. "blue-mat" scene))
  (set! (.-diffuseColor blue-mat) (bjs/Color3. 0 0 1))
  (set! white-mat (bjs/StandardMaterial. "white-mat" scene))
  (set! (.-diffuseColor white-mat) (bjs/Color3. 1 1 1))
  (set! black-mat (bjs/StandardMaterial. "black-mat" scene))
  (set! (.-diffuseColor white-mat) (bjs/Color3. 0 0 0))
  (let [av (bjs/Debug.AxesViewer. scene)]
    (.update av (bjs/Vector3. 4 0 0) (bjs/Vector3. 1 0 0) (bjs/Vector3. 0 1 0) (bjs/Vector3. 0 0 1)))
  (setup-skybox)

  (cond
    (re-seq #"Chrome" js/navigator.userAgent)
    (do
      (prn "Chrome detected")
      (set! xr-mode "xr"))
      ; (set! xr-mode "vr"))
    (re-seq #"Firefox" js/navigator.userAgent)
    (do
      (prn "Firefox detected")
      ; (set! xr-mode "xr"))
      (set! xr-mode "vr"))
    true (set! xr-mode "xr"))
  (prn "xr-mode=" xr-mode)
  (if (= xr-mode "vr")
    (do
      (println "now setting up vr")
      (set! vrHelper (.createDefaultVRExperience scene
                          (js-obj "useXR" false)))
                                  ; "floorMeshesCollection" (array))))
      (set! camera (.-webVRCamera vrHelper))
      (let [do-cam (.-deviceOrientationCamera vrHelper)]
        (set! (.-position do-cam) (bjs/Vector3. 0 4 -15)))
      (set! (.-id camera) "main-camera")
      (controller/init scene vrHelper camera)
      (controller/setup-controller-handlers vrHelper)
      (.enableTeleportation vrHelper (js-obj "floorMeshName" "ground"))
      (.enableInteractions vrHelper)
      (-> vrHelper .-onAfterEnteringVRObservable (.add enter-vr-handler))
      (top-level-scene-initializer))
    (do
      ;; set up xr
        ;; definitely where you set the non-xr and xr camera position.
        (set! camera (bjs/UniversalCamera. "uni-cam" (bjs/Vector3. 0 4 -15) scene))
        (.setTarget camera (bjs/Vector3.Zero))
        (-> (.createDefaultXRExperienceAsync scene
                                             (js-obj
                                              "floorMeshes"
                                              (array (.getMeshByID scene "ground"))))
            (p/then
             (fn [xr-default-exp]
               (set! xr-helper xr-default-exp)
               (re-frame/dispatch [:setup-xr-ctrl-cbs xr-default-exp])
               ;; Note: baseExperience is of type WebXRExperienceHelper
               (set! features-manager (-> xr-default-exp (.-baseExperience) (.-featuresManager)))
               ;;Note: setting rotations on the xr camera here have no effect.  You have to do it
               ;; on the pre-xr camera (any rotations on that *will* propagate to the xr camera)
               (re-frame/dispatch [:init-xr xr-default-exp])
               (-> xr-default-exp (.-baseExperience)
                   (.-onStateChangedObservable)
                   (.add enter-xr-handler))
                   ;; Note: set xr camera rotation in 'enter-xr-handler'

               (println "main-scene: top-level scene=" top-level-scene-initializer)
               (top-level-scene-initializer)))))))

;; This gets control when the "enter vr" button is clicked.
(defn enter-vr-handler []
  (prn "entered vr")
  (.setTarget camera (bjs/Vector3. 0 0 0))
  (prn "cam-rot a=" (.-rotation camera))
  ;; need a different rot when in vr
  ; (swap! *camera-init-rot* (fn [x] {:x 0 :y (* base/ONE-DEG -90) :z 0}))
  ; (set! (.-rotationQuaternion camera) (bjs/Quaternion.RotationYawPitchRoll (/ js/Math.PI 1.0) 0 0))
  ;; scruz adjustment
  ; (set! (.-rotationQuaternion camera) (bjs/Quaternion.RotationYawPitchRoll (* base/ONE-DEG -45) 0 0))
  ; (set! (.-rotationQuaternion camera) (bjs/Quaternion.RotationYawPitchRoll (* base/ONE-DEG 0) 0 0))
  (.resetToCurrentRotation camera)
  (prn "cam-rot a=" (.-rotation camera))
  ; (prn "dispatch-sync=" (re-frame/dispatch-sync [:cube-test.utils.events/get-xr-camera]))
  (let [cam-pos (.-position camera)
        ; xr-cam (re-frame/dispatch-sync [:cube-test.utils.events/get-xr-camera])
        xr-cam (.-deviceOrientationCamera vrHelper)
        x (.-x cam-pos)
        y (.-y cam-pos)
        z (.-z cam-pos)
        ip @*camera-init-pos*
        ir @*camera-init-rot*
        quat (bjs/Quaternion.FromEulerAngles (:x ir) (+ (:y ir) (* 70 base/ONE-DEG)) (:z ir))]

      (prn "xr-cam=" xr-cam)
      (set! (.-position camera) (bjs/Vector3. (:x ip) (:y ip) (:z ip)))
      (set! (.-rotationQuaternion camera) quat)
      (prn "cam-rot c=" (.-rotation camera))))

;; this gets control when the "enter xr" button is clicked.
(defn enter-xr-handler [state]
  (when (= state bjs/WebXRState.IN_XR)
    ;; important that we only assign camera to the xr-camera *after* entring
    ;; full xr, so that downwind scenes can alter the non-xr camera as needed. The xr
    ;; camera, will always start with wherever the non-xr is currently positioned.
    (set! camera (-> xr-helper (.-baseExperience) (.-camera)))
    ;; Do camera rotation adjustments (upon entering xr) here.
    (let [ quat (-> camera .-rotationQuaternion)])))
      ; cur-angles (.toEulerAngles quat)]
      ;; Note: do runtime vr camera rotation here
      ;; svale backyard
      ; (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles 0 (* 90 base/ONE-DEG) 0)))))
      ;; svale living room
      ; (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles 0 (* -135 base/ONE-DEG) 0)))))
      ; (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles 0 (* 135 base/ONE-DEG) 0)))))
      ; (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles 0 (* 180 base/ONE-DEG) 0)))))
      ;; scruz
      ; (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles 0 (* -90 base/ONE-DEG) 0)))))
      ; (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles 0 (* -0 base/ONE-DEG) 0)))))
      ; (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles 0 (* -45 base/ONE-DEG) 0)))))

(defn enter-vr []
  (prn "main-scene.enter-vr: entered")
  (-> (.-baseExperience xr-helper) (.enterXRAsync "immersive-vr" "local-floor")
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

(defn tick [])
