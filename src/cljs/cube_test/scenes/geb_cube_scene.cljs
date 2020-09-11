;; reference few, accessible by many.
(ns cube-test.scenes.geb-cube-scene
  ; (:require-macros [cube-test.macros :as macros])
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [babylonjs-gui :as bjs-gui]
   ; [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.utils.fps-panel :as fps-panel]
   [cube-test.base :as base]
   [cube-test.utils :as utils]))

; (def x-fwd-shadow-gen)
(def x-fwd-shadow-gen)
(def y-fwd-shadow-gen)
(def y-bwd-shadow-gen)
(def z-fwd-shadow-gen)
(def x-fwd-light)
(def y-fwd-light)
(def y-bwd-light)
(def z-fwd-light)

;; loads
(defn geb-cube-loaded [meshes particle-systems skeletons anim-groups user-cb]
  (println "geb-cube-loaded")
  (doall (map #(do
                 (when (re-matches #"__root__" (.-id %1))
                     ; (set! (.-name %1) "rubiks-cube")
                     (set! (.-name %1) "geb-cube")
                     (set! (.-id %1) "geb-cube")
                     ;; the neg. on the x is needed otherwise the materials are "flipped"
                     ; (set! (.-scaling %1)(bjs/Vector3. -0.3 0.3 0.3))
                     (set! (.-position %1)(bjs/Vector3. 0 2 0))))
                     ; (set! shadow-generator (bjs/ShadowGenerator. 1024 x-fwd-light))
                     ; (.addShadowCaster shadow-generator %1)))
              meshes))
  (when user-cb (user-cb)))

(defn load-geb-cube [path file user-cb]
  (.ImportMesh bjs/SceneLoader ""
               path
               file
               main-scene/scene
               #(geb-cube-loaded %1 %2 %3 %4 user-cb)))

;; inits
; var light = new BABYLON.DirectionalLight("DirectionalLight",)
; new BABYLON.Vector3(0, -1, 0), scene;
; var plane = BABYLON.MeshBuilder.CreatePlane("plane",
; {height:1, width: 0.665, sideOrientation: BABYLON.Mesh.DOUBLESIDE, frontUVs: f, backUVs: b}, scene;
(defn init-screens []
  (let [scene main-scene/scene
        rs-parms (js-obj "height" 4 "width" 4 "depth" 0.1)
        right-screen (bjs/MeshBuilder.CreateBox.
                      "right-screen"
                      (js-obj "height" 4 "width" 4 "depth" 0.1)
                      main-scene/scene)
        ; top-screen (bjs/MeshBuilder.CreateBox.
        ;               "top-screen"
        ;               (js-obj "height" 4 "width" 4 "depth" 0.1)
        ;               main-scene/scene)
        bottom-screen (bjs/MeshBuilder.CreateBox.
                       "bottom-screen"
                       (js-obj "height" 4 "width" 4 "depth" 0.1)
                       main-scene/scene)
        rear-screen (bjs/MeshBuilder.CreateBox.
                      "rear-screen"
                      (js-obj "height" 4 "width" 4 "depth" 0.1)
                      main-scene/scene)
        ; right-screen (bjs/MeshBuilder.CreateBox. "right-screen" rs-parms main-scene/scene)
        x-quat90 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.X (* base/ONE-DEG 90)))
        y-quat90 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 90)))]
    (set! (.-material right-screen) main-scene/black-mat)
    ; (set! (.-position right-screen) (bjs/Vector3. 4 1.5 0.4))
    (set! (.-position right-screen) (bjs/Vector3. 4 1.5 0.4))
    (set! (.-rotationQuaternion right-screen) y-quat90)
    (set! (.-receiveShadows right-screen) true)
    ; (set! (.-sideOrientation right-screen) bjs/Mesh.DOUBLESIDE)
    ; (set! (.-backfaceCulling (.-material right-screen)) false)
    (println "screen.receive shadows=" (.-receiveShadows right-screen))
    ;; top screen
    ; (set! (.-material top-screen) main-scene/black-mat)
    ; (set! (.-position top-screen) (bjs/Vector3. 0 4 0))
    ; (set! (.-rotationQuaternion top-screen) x-quat90)
    ; (set! (.-receiveShadows top-screen) true)
    ; ; (set! (.-enabled top-screen) false)
    ; (set! (.-visibility top-screen) 0)
    ;; bottom screen
    (set! (.-material bottom-screen) main-scene/black-mat)
    (set! (.-position bottom-screen) (bjs/Vector3. 0 0 0))
    (set! (.-rotationQuaternion bottom-screen) x-quat90)
    (set! (.-receiveShadows bottom-screen) true)
    ; (set! (.-visibility top-screen) 0)
    ;; rear screen
    (set! (.-material rear-screen) main-scene/black-mat)
    (set! (.-position rear-screen) (bjs/Vector3. 0 1.5 4))
    (set! (.-rotationQuaternion right-screen) y-quat90)
    (set! (.-receiveShadows rear-screen) true)))

(defn init-lights []
  (let [xfl (bjs/DirectionalLight.
                     "x-fwd-light"
                     (bjs/Vector3. 1 0 0)
                     main-scene/scene)
        yfl (bjs/DirectionalLight.
                     "y-fwd-light"
                     (bjs/Vector3. 0 1 0)
                     main-scene/scene)
        ybl (bjs/DirectionalLight.
                     "y-bwd-light"
                     (bjs/Vector3. 0 -1 0)
                     main-scene/scene)
        zfl (bjs/DirectionalLight.
                     "z-fwd-light"
                     (bjs/Vector3. 0 0 1)
                     main-scene/scene)]
    (set! (.-intensity xfl) 1)
    (set! x-fwd-light xfl)
    (set! (.-intensity yfl) 1)
    (set! y-fwd-light yfl)
    (set! (.-intensity zfl) 1)
    (set! z-fwd-light zfl)
    (set! (.-intensity ybl) 1)
    (set! (.-position ybl) (bjs/Vector3. 0 10 0))
    (set! y-bwd-light ybl)))

; var shadowGenerator2 = new BABYLON.ShadowGenerator(1024, light2);
(defn init []
  (println "geb-cube-scene.init: entered")
  ; (set! shadow-generator (bjs/ShadowGenerator. 1024))
  (let [light (bjs/PointLight. "pointLight" (bjs/Vector3. 0 5 -3) main-scene/scene)]
    (.setEnabled light true))
  (load-geb-cube
   "models/geb_cube/"
   "geb_cube.glb"
   (fn [] (do
            (println "now in load cb")
            (let [geb-cube (.getNodeByName main-scene/scene "geb-cube")]
              (set! x-fwd-shadow-gen (bjs/ShadowGenerator. 1024 x-fwd-light))
              (.addShadowCaster x-fwd-shadow-gen geb-cube)
              (set! y-fwd-shadow-gen (bjs/ShadowGenerator. 1024 y-fwd-light))
              (.addShadowCaster y-fwd-shadow-gen geb-cube)
              (set! y-bwd-shadow-gen (bjs/ShadowGenerator. 1024 y-bwd-light))
              (.addShadowCaster y-bwd-shadow-gen geb-cube)
              (set! z-fwd-shadow-gen (bjs/ShadowGenerator. 1024 z-fwd-light))
              (.addShadowCaster z-fwd-shadow-gen geb-cube)))))
  (init-screens)
  (init-lights))

;; render
(defn render-loop []
  ; (println "face-slot-scene: render-loop")
  ; (if (= main-scene/xr-mode "vr")
  ;   (controller/tick)
  ;   (controller-xr/tick))
  (controller-xr/tick)
  (fps-panel/tick main-scene/engine)
  ; (when @*cell-action-pending*
  ;   ; (rot-cells)
  ;   (re-frame/dispatch [:vrubik-rot-cells-combo]))
  ; (let [action-cells @*action-cells*]
  ;   (when (and action-cells (> (count action-cells) 0) (nth action-cells 0))
  ;     (swap! *action-cells* rot-cells)))
  (.render main-scene/scene))

(defn run-scene []
  (println "geb-cube-scene.run-scene: entered")
  (.runRenderLoop main-scene/engine (fn [] (render-loop))))
