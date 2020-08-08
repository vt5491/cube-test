(ns cube-test.scenes.tic-tac-attack-scene
  (:require-macros [cube-test.macros :as macros])
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [babylonjs-gui :as bjs-gui]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.utils.fps-panel :as fps-panel]
   [cube-test.base :as base]))

(def cross2)
(def ring-plex2)
(def cube-anim)
;;
;; load models
;;
(defn cross-loaded [meshes particle-systems skeletons anim-groups user-cb]
  (println "tta.cross-loaded")
  (doall (map #(do
                 (prn "mesh-name=" (.-name %1))
                 (when (re-matches #"^cross.*" (.-name %1))
                   (set! (.-scaling %1) (bjs/Vector3. 0.1 0.1 0.1))
                   ; (set! (.-position %1)(bjs/Vector3. 0 1 0))
                   (set! (.-name (.-parent %1)) "cross-arch")))
              meshes))
  (when user-cb (user-cb)))

(defn load-cross [path file user-cb]
  (println "tta.load-cross: path=" path ", file=" file)
  (.ImportMesh bjs/SceneLoader ""
               path
               file
               main-scene/scene
               #(cross-loaded %1 %2 %3 %4 user-cb)))

(defn ring-plex-loaded [meshes particle-systems skeletons anim-groups user-cb]
  (println "tta.ring-plex-loaded")
  (doall (map #(do
                 (prn "mesh-name=" (.-name %1))
                 (when (re-matches #"^ringPlex_xy.*" (.-name %1))
                   (let [ring-plex-parent (.-parent %1)]
                     (set! (.-name ring-plex-parent) "ring-plex-arch")
                     (set! (.-scaling ring-plex-parent)(bjs/Vector3. 0.1 0.1 0.1)))))
                 ;   (set! (.-scaling %1) (bjs/Vector3. 0.1 0.1 0.1))
                 ;   (set! (.-name (.-parent %1)) "cross-arch")))
              meshes))
  (when user-cb (user-cb)))

(defn load-ring-plex [path file user-cb]
  (println "tta.load-ring-plex: path=" path ", file=" file)
  (.ImportMesh bjs/SceneLoader ""
               path
               file
               main-scene/scene
               #(ring-plex-loaded %1 %2 %3 %4 user-cb)))

(defn rubiks-cube-loaded [meshes particle-systems skeletons anim-groups user-cb]
  (println "tta.rubiks-cube-loaded")
  ; (js-debugger)
  (doall (map #(do
                 ; (prn "mesh-id=" (.-id %1))
                 (when (re-matches #"__root__" (.-id %1))
                     (set! (.-name %1) "rubiks-cube")
                     ;; the neg. on the x is needed otherwise the materials are "flipped"
                     (set! (.-scaling %1)(bjs/Vector3. -0.3 0.3 0.3))
                     (set! (.-position %1)(bjs/Vector3. 0 2 0))))
              meshes))
  (when user-cb (user-cb)))

(defn load-rubiks-cube [path file user-cb]
  ; (println "tta.load-rubiks-cube: path=" path ", file=" file)
  (.ImportMesh bjs/SceneLoader ""
               path
               file
               main-scene/scene
               #(rubiks-cube-loaded %1 %2 %3 %4 user-cb)))

(defn init-top-gui []
  (let [top-plane (bjs/Mesh.CreatePlane. "top-plane" 2)
        top-adv-texture (bjs-gui/AdvancedDynamicTexture.CreateForMesh. top-plane 1024 1024)
        ; top-pnl (bjs-gui/StackPanel.)
        top-pnl (bjs-gui/Grid.)
        top-hdr (bjs-gui/TextBlock.)
        rot-btn (bjs-gui/Button.CreateSimpleButton. "rot-btn" "rotate")
        rot-btn-2 (bjs-gui/Button.CreateSimpleButton. "rot-btn-2" "rotate2")
        rot-btn-3 (bjs-gui/Button.CreateSimpleButton. "rot-btn-3" "rotate3")]
    (set! (.-position top-plane)(bjs/Vector3. 0 6 -2))
    (.addControl top-adv-texture top-pnl)
    (set! (.-text top-hdr) "commands")
    (set! (.-height top-hdr) "100px")
    (set! (.-fontSize top-hdr) "80")
    (set! (.-color top-hdr) "white")
    ;; create 4 rows and 2 cols
    (.addRowDefinition top-pnl 0.25 false)
    (.addRowDefinition top-pnl 0.25)
    (.addRowDefinition top-pnl 0.25)
    (.addRowDefinition top-pnl 0.25)
    (.addColumnDefinition top-pnl 0.5)
    (.addColumnDefinition top-pnl 0.5)
    ; (.addControl top-pnl top-hdr)
    ; (set! (.-horizontalAlignment top-pnl) bjs-gui/StackPanel.HORIZONTAL_ALIGNMENT_CENTER)
    ; (set! (.-verticalAlignment top-pnl) bjs-gui/StackPanel.VERTICAL_ALIGNMENT_CENTER)
    (.addControl top-pnl top-hdr 0 0)
    ;; rot-btn
    (set! (.-autoScale rot-btn) true)
    (set! (.-fontSize rot-btn) "100")
    (set! (.-color rot-btn) "red")
    (-> rot-btn .-onPointerUpObservable (.add (fn [value]
                                                  (println "rot-btn pressed")
                                                  (re-frame/dispatch [:tta-rot-cube]))))
    ; (set! (.-horizontalAlignment rot-btn) bjs-gui/Control.HORIZONTAL_ALIGNMENT_LEFT)
    ; (set! (.-verticalAlignment rot-btn) bjs-gui/Control.VERTICAL_ALIGNMENT_TOP)
    (.addControl top-pnl rot-btn 2 0)
    ;; rot-btn-2
    (set! (.-autoScale rot-btn-2) true)
    (set! (.-fontSize rot-btn-2) "100")
    (set! (.-color rot-btn-2) "white")
    (-> rot-btn-2 .-onPointerUpObservable (.add (fn [value]
                                                  (println "rot-btn-2 pressed")
                                                  (re-frame/dispatch [:tta-rot-cube-2]))))
    (.addControl top-pnl rot-btn-2 2 1)
    ;; rot-btn-3
    (set! (.-autoScale rot-btn-3) true)
    (set! (.-fontSize rot-btn-3) "100")
    (set! (.-color rot-btn-3) "white")
    (-> rot-btn-3 .-onPointerUpObservable (.add (fn [value]
                                                  (println "rot-btn-3 pressed")
                                                  (re-frame/dispatch [:tta-rot-cube-3]))))
    (.addControl top-pnl rot-btn-3 3 0)))

(defn rot-cube []
  (println "now in rot-cube")
  ; (.beginAnimation main-scene/scene)
  (let [scene main-scene/scene
        red-cube-1 (.getMeshByID scene "red_cube_1")]
    ; (js-debugger)
    (println "pos cube=" (.-position red-cube-1) ", rot cube=" (.-rotation red-cube-1))
    (js/setTimeout
     #(do (let [rc1 (.getMeshByID main-scene/scene "red_cube_1")]
            (println "timer-pop: pos cube=" (.-position rc1) ", rot cube=" (.-rotation red-cube-1))))
     1000)
    (.beginAnimation scene red-cube-1 0 150 true)))

(defn rot-cube-2 []
  (let [scene main-scene/scene
        rc1 (.getMeshByID scene "red_cube_1")
        rot (.-rotation rc1)
        quat45 (.toQuaternion (.add rot (bjs/Vector3. 0 (/ js/Math.PI 4.0) 0)))
        new-rot (.rotateByQuaternionToRef rot quat45 (bjs/Vector3.))]
    ;; restore back to zero
    ; (.rotate rc1 (bjs/Vector3. 0 0 0) (bjs/Vector3. 0 0 0))
    (set! (.-rotationQuaternion rc1) (bjs/Quaternion.Zero))
    (set! (.-rotationQuaternion rc1) quat45)))
    ; (set! (.-rotation rc1) new-rot)
    ; (.rotate rc1 bjs/Vector3.Up new-rot)
    ; (.rotate rc1 (bjs/Vector3. 0 1 0) new-rot)))
    ; (.rotate rc1 bjs/Vector3.Up (* base/ONE-DEG 45))))

(defn rot-cube-3 []
  (let [scene main-scene/scene
        rc1 (.getMeshByID scene "red_cube_2")
        pivot (.-position rc1)
        quat45 (.toQuaternion (bjs/Vector3. 0 (* base/ONE-DEG 45.0) 0))
        cur-quat (.-rotationQuaternion rc1)]
    ; (set! (.-parent rc1) pivot)
    ; (.setPivotPoint rc1 pivot)
    ; (set! (.-rotationQuaternion pivot) quat45)
    ; (.rotate rc1 bjs/Axis.Y (* base/ONE-DEG 45) bjs/Space.WORLD)
    ; (set! (.-rotationQuaternion rc1) (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 45)))
    (set! (.-rotationQuaternion rc1) (.multiply cur-quat (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 15))))))
;;
;; run-time methods
;;
(defn render-loop []
  ; (println "face-slot-scene: render-loop")
  (if (= main-scene/xr-mode "vr")
    (controller/tick)
    (controller-xr/tick))
  ; (cube-fx/tick)
  (fps-panel/tick main-scene/engine)
  (.render main-scene/scene))

(defn run-scene []
  (.runRenderLoop main-scene/engine (fn [] (render-loop))))

;;
;; init
;;
(defn init-cross []
  (let [cross-arch (-> main-scene/scene (.getNodeByName "cross-arch"))]
    (set! (.-position cross-arch)(bjs/Vector3. -1 1 0))
    (set! cross2 (.clone cross-arch))
    (set! (.-position cross2)(bjs/Vector3. -5 1 0))
    (set! (.-name cross2) "cross2")
    (.setEnabled cross-arch false)))

(defn init-ring-plex []
  (println "now in init-ring-plex")
  (let [ring-plex-arch (-> main-scene/scene (.getNodeByName "ring-plex-arch"))]
    ; (set! (.-position ring-plex-arch)(bjs/Vector3. -1 1 0))
    (set! ring-plex2 (.clone ring-plex-arch))
    (set! (.-position ring-plex2)(bjs/Vector3. 5 1 0))
    (set! (.-name ring-plex2) "ring-plex2")
    (.setEnabled ring-plex-arch false)))

; var animationBox = new BABYLON.Animation("myAnimation", "scaling.x", 30, BABYLON.Animation.ANIMATIONTYPE_FLOAT, BABYLON.Animation.ANIMATIONLOOPMODE_CYCLE);^
(defn init-rubiks-cube []
  (println "now in init-rubiks-cube")
  ; (set! cube-anim (bjs/Animation. "cube-anim" "rotation.z" 30 bjs/Animation.ANIMATIONTYPE_FLOAT bjs/Animation.ANIMATIONLOOPMODE_CONSTANT))
  ; (set! cube-anim (bjs/Animation. "cube-anim" "rotationQuaternion.z" 30 bjs/Animation.ANIMATIONTYPE_FLOAT bjs/Animation.ANIMATIONLOOPMODE_CONSTANT))
  (set! cube-anim (bjs/Animation. "cube-anim" "rotationQuaternion" 30 bjs/Animation.ANIMATIONTYPE_QUATERNION bjs/Animation.ANIMATIONLOOPMODE_CONSTANT))
  ; (set! cube-anim (bjs/Animation. "cube-anim" "position.x" 30 bjs/Animation.ANIMATIONTYPE_FLOAT bjs/Animation.ANIMATIONLOOPMODE_CONSTANT))
  ; (set! cube-anim (bjs/Animation. "cube-anim" "rotation" 30 bjs/Animation.ANIMATIONTYPE_VECTOR3 bjs/Animation.ANIMATIONLOOPMODE_CONSTANT))
  ; (set! cube-anim (bjs/Animation. "cube-anim" "scaling.x" 30 bjs/Animation.ANIMATIONTYPE_FLOAT bjs/Animation.ANIMATIONLOOPMODE_CONSTANT))
  (let [keys (array)
        ; quat45 (.toQuaternion (bjs/Vector3. 0 (/ js/Math.PI 4.0) 0))
        ; quat45 (.toQuaternion (bjs/Vector3. 0 (* base/ONE-DEG 45) 0))
        ; quat90 (.toQuaternion (bjs/Vector3. 0 (* base/ONE-DEG 90) 0))
        quat15 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 15)))
        quat30 (.normalize (.multiply quat15 (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 15))))
        quat45 (.normalize (.multiply quat30 (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 15))))
        quat90 (.normalize (.multiply quat45 (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 45))))
        quat180 (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 180))
        quat270 (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 270))
        quat360 (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 360))]
    ; (.push keys (js-obj "frame" 0 "value" 0))
    ; (.push keys (js-obj "frame" 30 "value" (* 90 base/ONE-DEG)))
    ; (.push keys (js-obj "frame" 30 "value" (/ (* 90 base/ONE-DEG) 30)))
    ; (.push keys (js-obj "frame" 30 "value" (* 20 base/ONE-DEG)))
    ; (.push keys (js-obj "frame" 0 "value" bjs/Vector3. 0 0 0))
    ; (.push keys (js-obj "frame" 30 "value" bjs/Vector3. 0 0 (/ (* 90 base/ONE-DEG) 30)))
    ; (.push keys (js-obj "frame" 0 "value" 1))
    ; (.push keys (js-obj "frame" 30 "value" 2))
    ; (.push keys (js-obj "frame" 0 "value" (bjs/Quaternion.Zero)))
    (.push keys (js-obj "frame" 0 "value" (bjs/Quaternion.Identity)))
    ; (.push keys (js-obj "frame" 150 "value" (bjs/Quaternion.FromEulerAngles 0 0 (* 180 base/ONE-DEG))))
    ; (.push keys (js-obj "frame" 150 "value" (.rotationaxis bjs/Quaternion bjs/Vector3.Up (* 180 base/ONE-DEG))))
    ; (.push keys (js-obj "frame" 150 "value" (bjs/Quaternion.RotationAxis (bjs/Vector3.Up) (* 180 base/ONE-DEG))))
    ; (.push keys (js-obj "frame" 150 "value" (bjs/Quaternion.RotationAxis bjs/Axis.Z (* 90 base/ONE-DEG))))
    ; (.push keys (js-obj "frame" 30 "value" quat45))
    ; (.push keys (js-obj "frame" 60 "value" quat90))
    ; (.push keys (js-obj "frame" 20 "value" quat15))
    ; (.push keys (js-obj "frame" 40 "value" quat30))
    (.push keys (js-obj "frame" 30 "value" quat180))
    (.push keys (js-obj "frame" 60 "value" quat360))
    (.setKeys cube-anim keys))
  (let [red-cube-1 (.getMeshByID main-scene/scene "red_cube_1")]
    (set! (.-animations red-cube-1) (array cube-anim))))

(defn init []
  (println "tic-tac-attack.init: entered")
  (let [light (bjs/PointLight. "pointLight" (bjs/Vector3. 0 5 -3) main-scene/scene)]
    (.setEnabled light true))
  (bjs/HemisphericLight. "hemiLight" (bjs/Vector3. 0 1 0) main-scene/scene)
  (load-cross
   "models/tic_tac_attack/"
   "cross.glb"
   (fn [] (re-frame/dispatch [:init-cross])))
  (load-ring-plex
   "models/tic_tac_attack/"
   "ring_plex.glb"
   (fn [] (re-frame/dispatch [:init-ring-plex])))
  (load-rubiks-cube
   "models/rubiks_cube/"
   "rubiks_cube.glb"
   (fn [] (re-frame/dispatch [:init-rubiks-cube])))
  (init-top-gui))
