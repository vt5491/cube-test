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
      (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles 0 (* 90 base/ONE-DEG) 0)))))
      ; (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles 0 (* 180 base/ONE-DEG) 0)))))
      ; (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles 0 (* -0 base/ONE-DEG) 0)))))

; (defn init-part-2 []
;   (println "now in init-part-2")
;   (set! light1 (bjs/PointLight. "pointLight" (bjs/Vector3. 0 5 -3) scene))
;   (.setEnabled light1 true)
;   (bjs/HemisphericLight. "hemiLight" (bjs/Vector3. 0 1 0) scene)
;   (.attachControl camera canvas false)
;   ; (init-cube)
;   (init-action-pnl)
;   (re-frame/dispatch [:setup-btn])
;   ; (re-frame/dispatch [:init-fps-panel scene])
;   (if (= xr-mode "xr")
;     (-> (.-onPointerObservable scene) (.add pointer-handler)))
;   (init-gui)
;   (init-gui-2))

  ;; init the 3d gui
;   var plane = BABYLON.Mesh.CreatePlane("plane", 1));
; plane.position = new BABYLON.Vector3(3.4, 1.5, 0.4)
; var advancedTexture = BABYLON.GUI.AdvancedDynamicTexture.CreateForMesh(plane);
; var panel = new BABYLON.GUI.StackPanel();
; advancedTexture.addControl(panel);
  ; var manager = new BABYLON.GUI.GUI3DManager(scene));
  ; (set! gui-mgr (bjs/GUI.GUI3DManager. scene))

  ; // Let's add a button
  ;  var button = new BABYLON.GUI.Button3D("reset");
  ;  manager.addControl(button);
  ;  button.linkToTransformNode(anchor);
  ;  button.position.z = -1.5);
 ;  var text1 = new BABYLON.GUI.TextBlock();
 ; text1.text = "reset";
 ; text1.color = "white";
 ; text1.fontSize = 24;
 ; button.content = text1);
;  button.onPointerUpObservable.add(function(){})
;     donut.rotation.x = 0);
; ;
; (defn init-cube[]
;   (set! cube (bjs/MeshBuilder.CreateBox. "cube"
;                                                 (js-obj "height" 2 "width" 2 "depth" 0.5)
;                                                 scene))
;   (set! (.-position cube)(bjs/Vector3. 0 0 3))
;   (set! (.-material cube) red-mat))
;
; (defn init-action-pnl []
;   (let [action-pnl
;         (js/BABYLON.MeshBuilder.CreateBox.
;          "action-pnl"
;          (js-obj "height" 1
;                  "width"  1
;                  "depth" 0.1)
;          scene)
;         mat (js/BABYLON.StandardMaterial. "action-pnl-mat" scene)]
;     (set! (.-position action-pnl) (bjs/Vector3. -4 3 5))
;     (set! (.-diffuseColor mat) (js/BABYLON.Color3. 1 1 0))
;     (set! (.-material action-pnl) mat)))

; (defn init-gui []
;   (set! gui-mgr (bjs-gui/GUI3DManager. scene))
;   (set! gui-anchor (bjs/AbstractMesh. "gui-anchor" scene))
;   (set! plane (bjs/Mesh.CreatePlane. "plane" 1))
;   (set! plane2 (bjs/Mesh.CreatePlane. "plane2" 2 scene))
;   ; (set! spin-fwd-btn (bjs-gui/Button3D. "fwd"))
;   (set! spin-fwd-btn (bjs-gui/MeshButton3D. plane2 "fwd"))
;   (set! spin-bwd-btn (bjs-gui/Button3D. "bwd"))
;   ; (set! gui-pnl (bjs-gui/StackPanel.))
;   (set! gui-pnl (bjs-gui/StackPanel3D.))
;   (set! adv-texture (bjs-gui/AdvancedDynamicTexture.CreateForMesh. plane))
;   ; (.addControl adv-texture gui-pnl)
;   ; (let [ adv-txt (bjs-gui/AdvancedDynamicTexture.CreateForMesh plane)]
;   ;   (js-debugger)
;   ;   (.addControl adv-text gui-pnl))
;   ; (.addControl gui-mgr spin-fwd-btn)
;   ; (.addControl gui-pnl spin-fwd-btn)
;   (.addControl gui-mgr spin-bwd-btn)
;   (.linkToTransformNode spin-fwd-btn gui-anchor)
;   (.linkToTransformNode spin-bwd-btn gui-anchor)
;   (set! (.-x (.-position spin-fwd-btn)) 1.5)
;   (set! (.-x (.-position spin-bwd-btn)) -1.5)
;   ; (js-debugger)
;   ; (-> plane .-onPointerUpObservable (.add (fn []
;   ;                                           (prn "plane upped"))))
;   (-> spin-fwd-btn .-onPointerUpObservable (.add (fn [])
;                                                  (prn "fwd btn pressed")))
;   (-> spin-fwd-btn .-onPointerClickObservable (.add (fn [])
;                                                  (prn "fwd btn clicked^"))))
;   ; (-> spin-fwd-btn .-onValueStateChangedObservable (.add (fn []))
;   ;                                                (prn "fwd btn state change"))
;   ; (set! (-> spin-fwd-btn (.-onPointerObservable) (.add (fn [])))
;   ;   (prn "fwd btn pressed"))
;   ; (let [text (bjs-gui/TextBlock.)]
;   ;   (set! (.-text text) ">>\nfwd")
;   ;   (set! (.-color text) "white")
;   ;   (set! (.-fontSize text) 48)
;   ;   (set! (.-content spin-fwd-btn) text)))

; (defn pointer-handler [pointer-info]
;   (macros/when-let* [type (.-type pointer-info)
;                      picked-mesh (-> pointer-info (.-pickInfo) (.-pickedMesh))]
;                     ; (prn "pointer-handler: picked-mesh.name=" (.-name picked-mesh))
;                     (when (re-matches #"action-pnl" (.-name picked-mesh))
;                       (cond
;                         (= type js/BABYLON.PointerEventTypes.POINTERDOWN)
;                         (do
;                           ; (re-frame/dispatch [:reset-spin-projectile 1])
;                           ; (re-frame/dispatch [:reset-projectiles])
;                           (re-frame/dispatch [:toggle-pause-projectiles])
;                           (prn "action-pnl pointerdown"))))))
;                       ; (cond
;                       ;   (= type js/BABYLON.PointerEventTypes.POINTERUP)
;                       ;   (do
;                       ;     (prn "action-pnl pointerup"))))))



; const sessionManager = await xrHelper.enterXRAsync("immersive-vr", "local-floor" /*, optionalRenderTarget */);
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


         ; b.setAttribute('class', 'btn');
         ; b.innerHTML = 'test value'))));
; (defn setup-btn []
;   (prn "setup-btn: entered")
;   ; (let [el1 (-> js/document (.getElementById "vt-div"))
;   ;       el2 (-> js/document (.getElementById "app"))
;   ;       btn1 (bjs/WebXREnterExitUIButton. el1 "immersive-vr" "local-floor")
;   ;       btn2 (.-element btn1)]
;   ;   (js-debugger)
;   ;   (.appendChild el2 btn2)))
;   (let [vt-div (-> js/document (.getElementById "vt-div"))
;         btn (js/document.createElement "button")]
;     (set! (.-name btn) "abc-name")
;     (set! (.-value btn) "abc-value")
;     (.setAttribute btn "id" "vt-btn")
;     (set! (.-innerHTML btn) "vt-btn")
;     (.addEventListener btn "click" (fn []
;                                      (prn "you click vt-btn")
;                                      (enter-vr)))
;     (.appendChild vt-div btn)))

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
;   ; (if base/use-xr
;   ;   (ctrl-xr/tick)
;   ;   (controller/tick))
;   ; (prn "fps=" (.getFps main-scene/engine))
;   ; (.drawText (-> main-scene/fps-pnl .-material .-diffuseTexture) (int (.getFps main-scene/engine)) 50 50 "60px green" "white" "blue" true true)
;   (controller/tick)
;   (.render scene))

; // GUI
;  var plane = BABYLON.Mesh.CreatePlane("plane", 1);
;  plane.position = new BABYLON.Vector3(1.4, 1.5, 0.4)
;  var advancedTexture = BABYLON.GUI.AdvancedDynamicTexture.CreateForMesh(plane);
;  var panel = new BABYLON.GUI.StackPanel();
;  advancedTexture.addControl(panel);
;  var header = new BABYLON.GUI.TextBlock();
;  header.text = "Color GUI";
;  header.height = "100px";
;  header.color = "white";
;  header.textHorizontalAlignment = BABYLON.GUI.Control.HORIZONTAL_ALIGNMENT_CENTER;
;  header.fontSize = "120"
;  panel.addControl(header);
;  var picker = new BABYLON.GUI.ColorPicker();
;  picker.value = sphere.material.diffuseColor;
;  picker.horizontalAlignment = BABYLON.GUI.Control.HORIZONTAL_ALIGNMENT_CENTER;
;  picker.height = "350px";
;  picker.width = "350px";
;  picker.onValueChangedObservable.add(function(value) {})
;      sphere.material.diffuseColor.copyFrom(value);
;  ;
;  panel.addControl(picker);
; (defn init-gui-2 []
;   (init-cube)
;   (let [plane (bjs/Mesh.CreatePlane. "plane" 1)
;         adv-text (bjs-gui/AdvancedDynamicTexture.CreateForMesh. plane)
;         panel (bjs-gui/StackPanel.)
;         header (bjs-gui/TextBlock.)
;         picker (bjs-gui/ColorPicker.)]
;     (set! (.-position plane) (bjs/Vector3. 1.4 1.5 0.4))
;     (.addControl adv-text panel)
;     (set! (.-text header) "Color GUI")
;     (set! (.-height header) "100px")
;     (set! (.-color header) "white")
;     (set! (.-textHorizontalAlignment header) bjs-gui/Control.HORIZONTAL_ALIGNMENT_CENTER)
;     (set! (.-fontSize header) "120")
;     (.addControl panel header)
;     (set! (.-value picker) (-> cube .-material .-diffuseColor))
;     (set! (.-horizontalAlignment picker) bjs-gui/Control.HORIZONTAL_ALIGNMENT_CENTER)
;     (set! (.-height picker) "350px")
;     (set! (.-width picker) "350px")
;     (-> picker .-onValueChangedObservable (.add (fn [value]
;                                                   (-> cube .-material .-diffuseColor (.copyFrom value)))))
;     (.addControl panel picker)))

; (defn init-gui []
;   (let [left-plane (bjs/Mesh.CreatePlane. "left-plane" 2)
;         left-adv-texture (bjs-gui/AdvancedDynamicTexture.CreateForMesh. left-plane 1024 1024)
;         left-pnl (bjs-gui/StackPanel.)
;         left-hdr (bjs-gui/TextBlock.)
;         bwd-btn (bjs-gui/Button.CreateImageButton. "bwd-spin" "bwd" "textures/tux_tada.jpg")
;         cb (bjs-gui/Checkbox.)]
;     (set! (.-position left-plane) (bjs/Vector3. -1.5 1.5 0.4))
;     (.addControl left-adv-texture left-pnl)
;     (set! (.-text left-hdr) "Backward")
;     (set! (.-height left-hdr) "100px")
;     (set! (.-color left-hdr) "white")
;     (set! (.-textHorizontalAlignment left-hdr) bjs-gui/Control.HORIZONTAL_ALIGNMENT_CENTER)
;     (set! (.-fontSize left-hdr) "80")
;     ; (.addControl left-pnl left-hdr)
;     ; (set! (.-horizontalAlignment bwd-btn) bjs-gui/Button.HORIZONTAL_ALIGNMENT_CENTER)
;     ; (set! (.-verticalAlignment bwd-btn) bjs-gui/Button.VERTICAL_ALIGNMENT_CENTER)
;     (set! (.-horizontalAlignment left-pnl) bjs-gui/StackPanel.HORIZONTAL_ALIGNMENT_CENTER)
;     (set! (.-verticalAlignment left-pnl) bjs-gui/StackPanel.VERTICAL_ALIGNMENT_CENTER)
;     (-> bwd-btn .-onPointerUpObservable (.add (fn [value]
;                                                (prn "bwd the palace btn style")
;                                                (re-frame/dispatch [:update-spin-ang-vel (bjs/Vector3. 0 0.002 0)]))))
;     (.addControl left-pnl bwd-btn)
;     (.addControl left-pnl left-hdr)
;     ;;cb
;     (set! (.-width cb) "100px")
;     (set! (.-height cb) "100px")
;     (.addControl left-pnl cb)))
;     ; (.addControl left-pnl left-hdr)))


; (defn init-gui-2 []
;   (init-cube)
;   (let [plane (bjs/Mesh.CreatePlane. "plane" 1)
;         adv-text (bjs-gui/AdvancedDynamicTexture.CreateForMesh. plane)
;         plane-2 (bjs/Mesh.CreatePlane. "plane-2" 2)
;         adv-text-2 (bjs-gui/AdvancedDynamicTexture.CreateForMesh. plane-2)
;         panel (bjs-gui/StackPanel.)
;         panel-2 (bjs-gui/StackPanel.)
;         header (bjs-gui/TextBlock.)
;         header-2 (bjs-gui/TextBlock.)
;         picker (bjs-gui/ColorPicker.)
;         ; fwd-btn (bjs-gui/Button3D. "fwd")
;         cb (bjs-gui/Checkbox.)
;         ; fwd-btn (bjs-gui/Button.CreateSimpleButton. "fwd" "click me")]
;         fwd-btn (bjs-gui/Button.CreateImageButton. "fwd" "click me" "textures/tux_tada.jpg")]
;         ; fwd-btn (bjs-gui/HolographicButton. "fwd")]
;     (set! (.-position plane) (bjs/Vector3. -3.4 1.5 0.4))
;     (set! (.-position plane-2) (bjs/Vector3. 1.4 1.5 0.4))
;     ; (set! (.-material plane) green-mat)
;     ; (set! (.-material plane-2) green-mat)
;     (.addControl adv-text panel)
;     (.addControl adv-text-2 panel-2)
;     (set! (.-text header) "Color GUI")
;     (set! (.-height header) "100px")
;     (set! (.-color header) "white")
;     (set! (.-textHorizontalAlignment header) bjs-gui/Control.HORIZONTAL_ALIGNMENT_CENTER)
;     (set! (.-fontSize header) "120")
;     (.addControl panel header)
;     ; (.addControl panel-2 header)
;     (set! (.-value picker) (-> cube .-material .-diffuseColor))
;     (set! (.-horizontalAlignment picker) bjs-gui/Control.HORIZONTAL_ALIGNMENT_CENTER)
;     (set! (.-height picker) "350px")
;     (set! (.-width picker) "350px")
;     (-> picker .-onValueChangedObservable (.add (fn [value]
;                                                   (-> cube .-material .-diffuseColor (.copyFrom value)))))
;     (-> picker .-onPointerUpObservable (.add (fn [value]
;                                                (prn "up the palace"))))
;     (-> picker .-onPointerClickObservable (.add (fn [value])
;                                                (prn "click the palace")))
;     (.addControl panel picker)
;     ;; btn
;     (set! (.-text header-2) "Forward")
;     (set! (.-height header-2) "100px")
;     (set! (.-fontSize header-2) "80")
;     (set! (.-color header-2) "red")
;     (.addControl panel-2 header-2)
;     ; (set! (.-x (.-position fwd-btn)) 1.5)
;     (-> fwd-btn .-onPointerUpObservable (.add (fn [value]
;                                                (prn "up the palace btn style")
;                                                (re-frame/dispatch [:update-spin-ang-vel (bjs/Vector3. 0 -0.002 0)]))))
;     (.addControl panel-2 fwd-btn)
;     ;;cb
;     (set! (.-width cb) "100px")
;     (set! (.-height cb) "100px")
;     (.addControl panel-2 cb)))

(defn tick [])
