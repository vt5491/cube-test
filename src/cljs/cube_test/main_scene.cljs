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
   [cannon :as cannon]
   [oimo :as oimo]
   [promesa.core :as p]
   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer-macros [<p!]]
   [webxr-polyfill :as xr-pf]))
(defn dummy [])

(def canvas)
(def engine)
(def engine-webgpu)
; (def scene)
; (defonce scene)
(def env)
(def camera)
(def camera-rig)
;(def camera-init-pos (js/BABYLON.Vector3. 0 4 -15))
(def camera-init-pos (js/BABYLON.Vector3. 0 0 -5))
(def ^:dynamic *camera-init-pos* (atom {:x 0, :y 4, :z -7}))
; (def ^:dynamic *camera-init-pos* (atom {:x 58.6, :y 7.9, :z 12.7}))
; (def ^:dynamic *camera-init-rot* (atom {:x 0, :y 0, :z -5}))
(def ^:dynamic *camera-init-rot* (atom {:x 0, :y 0, :z 0}))
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
(def a-btn)
(def b-btn)
(def main-gui-adv-text)

;; we have to pre-declare scene because it's defined with 'init' as a defonce.
(declare scene)
(declare init)
(declare init-main)
(declare init-part-2)
(declare init-cube)
(declare setup-skybox)
(declare init-action-pnl)
(declare init-gui)
(declare init-gui-2)
(declare pointer-handler)
(declare enter-xr-handler)
(declare enter-vr-handler)
(declare ctrl-added)
(declare load-main-gui)

(defn init-ground []
  (let [grnd (bjs/MeshBuilder.CreateGround "ground" (js-obj "width" 10 "height" 30 "subdivisions" 10))]
    (set! (.-material grnd) (bjs-m/GridMaterial. "ground-mat" scene))
    (set! (.-physicsImpostor grnd)
      (bjs/PhysicsImpostor. grnd bjs/PhysicsImpostor.PlaneImposter
                            (js-obj "mass" 0 "restitution" 0.9) scene))
    (set! (.-ground env) grnd)))

(defn init-env []
  ; (prn "top-scene.tmp-2: entered")
  (set! env (bjs/EnvironmentHelper.
             (js-obj
              "createGround" false
              "skyboxSize" 90)
             scene)))

(defn init-env-2[]
  ; (prn "top-scene.tmp-2: entered")
  (set! env (bjs/EnvironmentHelper.
             (js-obj
              "createGround" true
              "skyboxSize" 90)
             scene)))

(defn pre-init [init-main use-webgpu]
  ; (go
    (set! canvas (-> js/document (.getElementById "renderCanvas")))
    ; (<p! (.initAsync engine))
    ; (prn "init-engine: engine=" engine)
    (if use-webgpu
      (do
        (set! engine (bjs/WebGPUEngine. canvas))
        (-> (.initAsync engine)
            (p/then init-main)))
      (do
        (set! engine (bjs/Engine. canvas true))
        (prn "pre-init: init-main=" init-main)
        ; #(init-main)
        (init-main))))

(defn init [dispatch-seq]
  ; (pre-init #(init-main dispatch-seq) true)
  (pre-init #(init-main dispatch-seq) false))

; (defn init [top-level-scene-initializer])
(defn init-main [top-level-scene-initializer]
  (set! top-level-scene-init top-level-scene-initializer)

  ; (<p! (.launch puppeteer))
  ;; following line necessary for mixamo animations.
  (set! bjs/Animation.AllowMatricesInterpolation true)
  ; (set! canvas (-> js/document (.getElementById "renderCanvas")))
  ; (set! engine (bjs/Engine. canvas true))
  ; (init-engine)
  ; (go
  ;   (set! engine (bjs/WebGPUEngine. canvas))
  ;   (<p! (.initAsync engine)))
   ; (set! engine (<p! (bjs/WebGPUEngine. canvas))))
  ; (-> (bjs/WebGPUEngine. canvas) (p/then #(do
  ;                                           (prn "web-gpu promose: arg=" %)
  ;                                           (set! engine-webgpu %))))
  ;; Note: even though scene is defined inside 'init' it has file level scope.
  (prn "point a: engine=" engine)
  (defonce scene (bjs/Scene. engine))
  ; (def scene (bjs/Scene. engine))
  (set! scene (bjs/Scene. engine))
  (re-frame/dispatch [:set-main-scene scene])
  ;; Note: we have to manually create our own ground in order to
  ;; properly wrap it in a physicsImposter.
  (set! physics-plugin (bjs/OimoJSPlugin.))
  (.enablePhysics scene (bjs/Vector3. 0 -0.81 0) physics-plugin)

  (init-env)
  ; (set! env (bjs/EnvironmentHelper.
  ;            (js-obj
  ;             "createGround" false
  ;             "skyboxSize" 90)
  ;            scene))
  ;; manually create a ground with a physicsImposter.
  (init-ground)
  ; (let [grnd (bjs/MeshBuilder.CreateGround "ground" (js-obj "width" 10 "height" 30 "subdivisions" 10))]
  ;   (set! (.-material grnd) (bjs-m/GridMaterial. "ground-mat" scene))
  ;   (set! (.-physicsImpostor grnd)
  ;     (bjs/PhysicsImpostor. grnd bjs/PhysicsImpostor.PlaneImposter
  ;                           (js-obj "mass" 0 "restitution" 0.9) scene))
  ;   (set! (.-ground env) grnd))

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
  (set! (.-diffuseColor black-mat) (bjs/Color3. 0 0 0))
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
      (set! xr-mode "xr"))
      ; (set! xr-mode "vr"))
    true
    (set! xr-mode "xr"))
  (prn "xr-mode=" xr-mode)
  ;; note: this needs to be called by the client scene, since the cleanup-fn is different
  ;; for each scene.
  ;; Here we set it up with a null release so we can get access to buttons like 'reset camera'
  ;; Note: now done in top-scene, so it gets called upon soft restarts.
  ; (load-main-gui #())
  (if (= xr-mode "vr")
    (do
      (println "now setting up vr")
      (set! vrHelper (.createDefaultVRExperience scene
                          (js-obj "useXR" false)))
                                  ; "floorMeshesCollection" (array))))
      (set! camera (.-webVRCamera vrHelper))
      (let [do-cam (.-deviceOrientationCamera vrHelper)
            ip @*camera-init-pos*]
        (set! (.-position do-cam) (bjs/Vector3. (:x ip) (:y ip) (:z ip))))
      (controller/init scene vrHelper camera)
      (controller/setup-controller-handlers vrHelper)
      (.enableTeleportation vrHelper (js-obj "floorMeshName" "ground"))
      (.enableInteractions vrHelper)
      (-> vrHelper .-onAfterEnteringVRObservable (.add enter-vr-handler))
      (prn "main-scene: about to call top-level-scene-initializer")
      (top-level-scene-initializer))
    (do
      ;; set up xr
        ;; definitely where you set the non-xr and xr camera position.
        ; camera.attachControl(canvas, true)
        (set! camera (bjs/UniversalCamera. "uni-cam" (bjs/Vector3. 0 4 -15) scene))
        (.attachControl camera canvas true)
        (.setTarget camera (bjs/Vector3.Zero))
        ;; Note: the promise for this is called during init and does *not*
        ;; require that xr be entered.
        (-> (.createDefaultXRExperienceAsync scene
                                             (js-obj
                                              "floorMeshes"
                                              (array (.getMeshByID scene "ground"))))
            (p/then
             (fn [xr-default-exp]
               (prn "createDefaultXRExperienceAsync promise-handler: xr-default-exp=" xr-default-exp)
               (set! xr-helper xr-default-exp)
               ; (re-frame/dispatch [:setup-xr-ctrl-cbs xr-default-exp])
               (re-frame/dispatch [:cube-test.events/setup-xr-ctrl-cbs xr-default-exp])
               ;; Note: baseExperience is of type WebXRExperienceHelper
               (set! features-manager (-> xr-default-exp (.-baseExperience) (.-featuresManager)))
               ;;Note: setting rotations on the xr camera here have no effect.  You have to do it
               ;; on the pre-xr camera (any rotations on that *will* propagate to the xr camera)
               (re-frame/dispatch [:init-xr xr-default-exp])
               ;; 'enter-xr-handler' is only called upon clicking the 'enter xr' button.
               ;; note: baseExperience is type 'WebXRDefaultExperience'
               (-> xr-default-exp (.-baseExperience)
                   (.-onStateChangedObservable)
                   (.add enter-xr-handler))
                   ;; Note: set xr camera rotation in 'enter-xr-handler'
               ; (js-debugger)
               ;; this is an attempt to reset the camera rotation upon entering XR.  Can't say if it works
               ;; because somehow during testing my Rift automatically reset somehow.
               (-> xr-default-exp (.-baseExperience)
                   (.-onInitialXRPoseSetObservable)
                   (.addOnce #(do)))
               (-> xr-helper (.-input ) (.-onControllerAddedObservable) (.add ctrl-added))

               (top-level-scene-initializer)))))))

;; This gets control when the "enter vr" button is clicked.
(defn enter-vr-handler []
  (prn "entered vr")
  ;; note: this will tilt the view because it forces the camera to look at the origin
  ;(.setTarget camera (bjs/Vector3. 0 0 0))
  (prn "cam-rot a=" (.-rotation camera))
  ;; need a different rot when in vr
  ; (swap! *camera-init-rot* (fn [x] {:x 0 :y (* base/ONE-DEG -90) :z 0}))
  ; (set! (.-rotationQuaternion camera) (bjs/Quaternion.RotationYawPitchRoll (/ js/Math.PI 1.0) 0 0))
  ;; scruz adjustment
  ; (set! (.-rotationQuaternion camera) (bjs/Quaternion.RotationYawPitchRoll (* base/ONE-DEG -180) 0 0))
  ; (set! (.-rotationQuaternion camera) (bjs/Quaternion.RotationYawPitchRoll (* base/ONE-DEG 0) 0 0))
  (.resetToCurrentRotation camera)
  (prn "cam-rot a=" (.-rotation camera))
  ; (prn "dispatch-sync=" (re-frame/dispatch-sync [:cube-test.utils.events/get-xr-camera]))
  (comment
   (let [cam-pos (.-position camera)
         ; xr-cam (re-frame/dispatch-sync [:cube-test.utils.events/get-xr-camera])
         vr-cam (.-deviceOrientationCamera vrHelper)
         x (.-x cam-pos)
         y (.-y cam-pos)
         z (.-z cam-pos)
         ip @*camera-init-pos*
         ir @*camera-init-rot*
         quat (bjs/Quaternion.FromEulerAngles (:x ir) (+ (:y ir) (* 70 base/ONE-DEG)) (:z ir))]

       (prn "vr-cam=" vr-cam)
       ; (prn "xr-cam pre.pos=" xr-cam)
       ; (set! (.-position camera) (bjs/Vector3. (:x ip) (:y ip) (:z ip)))
       ; (set! (.-rotationQuaternion camera) quat)
       ; (prn "cam-rot c=" (.-rotation camera))
       (set! (.-position vr-cam) (bjs/Vector3. (:x ip) (:y ip) (:z ip)))
       (set! (.-rotationQuaternion vr-cam) quat)
       (prn "cam-rot c=" (.-rotation vr-cam)))))

;; this gets control when the "enter xr" button is clicked.
(defn enter-xr-handler [state]
  (when (= state bjs/WebXRState.IN_XR)
    ;; important that we only assign camera to the xr-camera *after* entring
    ;; full xr, so that downwind scenes can alter the non-xr camera as needed. The xr
    ;; camera, will always start with wherever the non-xr is currently positioned.
    (set! camera (-> xr-helper (.-baseExperience) (.-camera)))
    (.setTransformationFromNonVRCamera camera)
    ;; Do camera rotation adjustments (upon entering xr) here.
    (let [quat (-> camera .-rotationQuaternion)
          cur-angles (.toEulerAngles quat)
          new-quat (bjs/Quaternion. (.-x quat) 0.785 (.-z quat) (.-w quat))]
             ; (.setTarget camera (bjs/Vector3. 0 (.-y camera) 0))
             ; (prn "camera.target=" (.-target camera))
             ;; Note: do runtime vr camera rotation here
             ;; svale backyard
             ; (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles 0 (* -90 base/ONE-DEG) 0)))
             ;; svale living room
             ; (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles 0 (* -135 base/ONE-DEG) 0))
             ; (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles 0 (* -45 base/ONE-DEG) 0)))
             ; (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles (* -35 base/ONE-DEG) (* 135 base/ONE-DEG) (* 5 base/ONE-DEG)))
        (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles 0 (* 135 base/ONE-DEG) (* 5 base/ONE-DEG))))))
        ;; TODO this shouldn't be here. a total hack
        ; (cube-test.frig-frog.scene-l1.init-view 30))))
          ; (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles 0 (* 180 base/ONE-DEG) 0))
          ;; scruz
          ; (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles 0 (* -90 base/ONE-DEG) 0)))))
          ; (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles 0 (* -0 base/ONE-DEG) 0)))))
          ; (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles 0 0 (* 75 base/ONE-DEG))))))
          ; BABYLON.Quaternion.RotationAxis(BABYLON.Axis.Y, -rot));
          ; (set! (.-rotationQuaternion camera) (bjs/Quaternion.RotationAxis bjs/Axis.Y (* 45 base/ONE-DEG)))
          ; (set! (-> (.-rotationQuaternion camera) (.-y)) 0.785)
          ; (set! (.-rotationQuaternion camera) new-quat)
          ; (-> (.-rotationQuaternion camera) (.multiplyInPlace (bjs/Quaternion.FromEulerAngles 0 (* 45 base/ONE-DEG) 0)))
          ;; Note sleeping to do a rotation does make a difference.  Search in this file for "onInitialXRPoseSetObservable"
          ;; as an attempt to achieve the same thing in a non-ad-hoc fashioin.
          ; (cube-test.utils.sleep
          ;   #(do
          ;       (prn "hi from sleep")
          ;       ; (.setTarget camera (bjs/Vector3. 0 (.-y camera) 0))
          ;       (prn "camera.target=" (.-target camera))
          ;       (-> (.-rotationQuaternion camera)
          ;           (.multiplyInPlace (bjs/Quaternion.FromEulerAngles 0 (* 90 base/ONE-DEG) 0))))
          ;   6000)
           ; (prn "new-angles=" (.toEulerAngles (.-rotationQuaternion camera)))))))

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
    ; (set! (-> skybox-mat .-diffuseColor) (bjs/Color3.Red))
    (set! (-> skybox-mat .-specularColor) (bjs/Color3. 0 0 0))
    (set! (.-material skybox) skybox-mat)))

(defn motion-ctrl-added [motion-ctrl]
  (when (= (.-handedness motion-ctrl) "right")
    ; (js-debugger)
    ; (set! left-thumbrest (.getComponent motion-ctrl "thumbrest"))
    (set! a-btn (.getComponent motion-ctrl "a-button"))
    (set! b-btn (.getComponent motion-ctrl "b-button"))))

(defn ctrl-added [xr-ctrl]
  (-> xr-ctrl .-onMotionControllerInitObservable (.add motion-ctrl-added)))

(defn a-btn-handler []
  (prn "main-scene.a-btn-handler: a-btn pressed hasChanges=" (.-hasChanges a-btn))
  (when (.-hasChanges a-btn)
    (let [main-gui-plane (.getMeshByID scene "main-gui-plane")
          cam (cube-test.utils.get-xr-camera)
          cam-quat (.-rotationQuaternion cam)
          cam-rot (.toEulerAngles cam-quat)
          cam-rot-y (.-y cam-rot)
          gui-radius 4.0
          gui-delta-x (* gui-radius (js/Math.cos (.-x cam-rot)))
          gui-delta-z (* gui-radius (js/Math.sin (.-z cam-rot)))
          ; gui-delta (bjs/Vector3. gui-delta-x 0 gui-delta-z)
          ; gui-delta (bjs/Vector3. 0 0 8)
          gui-delta (bjs/Vector3. (* gui-radius (js/Math.sin cam-rot-y)) 0 (* gui-radius (js/Math.cos cam-rot-y)))]
      (prn "a-btn-handler: cam-rot=" cam-rot)
      (set! (.-position main-gui-plane) (.add (.-position cam) gui-delta))
      (set! (.-rotation main-gui-plane) (bjs/Vector3. 0 cam-rot-y 0))
      ; (.addInPlace (.-position main-gui-plane) gui-delta)
      ;; and toggle the visibility
      ; (.setEnabled main-gui-plane true)
      (if (not (.isEnabled main-gui-plane))
        (.setEnabled main-gui-plane true)
        (.setEnabled main-gui-plane false)))))

(defn b-btn-handler []
  (when (.-hasChanges b-btn)
    (set! (.-position camera) camera-init-pos)))

(defn main-gui-loaded [cleanup-fn]
  (prn "main-gui-loaded: main-gui-adv-text=" main-gui-adv-text)
  ;; (js-debugger)
  (let [yes-btn (.getControlByName main-gui-adv-text "yes-btn")
        no-btn (.getControlByName main-gui-adv-text "no-btn")
        cancel-btn (.getControlByName main-gui-adv-text "cancel-btn")
        reset-cam-btn (.getControlByName main-gui-adv-text "reset-cam-btn")]
    (when yes-btn
      (prn "main-scene: now initing buttons, no-btn=" no-btn)
      (-> yes-btn (.-onPointerClickObservable)
        ; (.add #(re-frame/dispatch [::cube-test.events.soft-switch-app :top-level-scene]))
        (.add #(do
                 (prn "yes")
                 (cube-test.events.soft-switch-app :top-scene cleanup-fn))))
    ; (when (or no-btn cancel-btn)
      (-> no-btn (.-onPointerClickObservable)
            (.add #(do
                     (prn "main-scene: you clicked no")
                     (.setEnabled (.getMeshByID scene "main-gui-plane") false))))
      (-> cancel-btn (.-onPointerClickObservable)
            (.add #(do
                     (prn "main-scene: you clicked cancel")
                     (.setEnabled (.getMeshByID scene "main-gui-plane") false)))))
    (when reset-cam-btn
      (-> reset-cam-btn (.-onPointerClickObservable)
          (.add #(do
                   (prn "camera reset clicked")
                   (set! (.-position camera) camera-init-pos)))))))

(defn load-main-gui [cleanup-fn]
  ; (prn "main-scene: load-main-gui: cleanup-fn=" cleanup-fn)
  (let [;;scene main-scene/scene
        plane (bjs/MeshBuilder.CreatePlane "main-gui-plane" (js-obj "width" 4, "height" 4) scene)
        ; plane (bjs/Mesh.CreatePlane "main-gui-plane" (js-obj "width" 4, "height" 4) scene)
        ; _ (set! (.-position plane) (bjs/Vector3. 0 5 10))
        _ (set! (.-position plane) (bjs/Vector3. 0 10 10))
        _ (.enableEdgesRendering plane)
        _ (set! (.-edgesWidth plane) 1.0)
        adv-text (bjs-gui/AdvancedDynamicTexture.CreateForMesh plane 1024 1024)]
    (.setEnabled plane false)
    (set! main-gui-adv-text adv-text)
    (-> adv-text
     (.parseFromURLAsync "guis/main_scene/main_scene_gui_a_btn.json")
     (p/then #(main-gui-loaded cleanup-fn)))))
;; Note: do not want render loop in main scene, since it needs to call the tick of other
;; namespaces, thus violating the "refer to few, referenced by many" principle.  We can normally
;; use a re-frame event dispatch to get around this, but you do not want to call re-frame on a game
;; tick for performance reasons.  Therefore, it's better to put into a module like 'game'.
;; Note: ok, to call main-scene native methods.  You can also refer to other namespaces without
;; a :require by saying something like 'cube-test.top-scene.top-scene/do-it'.  However, it's best
;; to have "local" ticks and not one huge global tick.  If you want to call this main-scene tick
;; don't forget to call from your subscene:  (main-scene/tick)

(defn tick []
  (when (= xr-mode "xr")
    (when (and a-btn (.-pressed a-btn))
      (a-btn-handler))
    (when (and b-btn (.-pressed b-btn))
      (b-btn-handler))))
