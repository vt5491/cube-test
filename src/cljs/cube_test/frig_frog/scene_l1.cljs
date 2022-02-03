(ns cube-test.frig-frog.scene-l1
  ; (:require-macros [cube-test.macros :as macros])
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [babylonjs-gui :as bjs-gui]
   [babylonjs-materials :as bjs-m]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   ; [cube-test.cube-fx :as cube-fx]
   [cube-test.utils.fps-panel :as fps-panel]
   ; [cube-test.beat-club.note-twitch :as note-twitch]
   [cube-test.base :as base]
   [cube-test.utils :as utils]
   ; [cube-test.beat-club.twitch-stream :as twitch-stream]
   [babylonjs-loaders :as bjs-l]
   [cube-test.frig-frog.train :as ff.train]))


(defn init-gui []
  (prn "frig-frog.scene: init-gui entered")
  (let []))
        ; top-plane (bjs/MeshBuilder.CreatePlane "top-plane" (js-obj "width" 5 "height" 3))]))

(defn init-non-vr-view
  ([] (init-non-vr-view 180))
  ([delta-rot]
   ; (prn "ff.scene.init-view: delta-rot=" delta-rot)
   (let [camera main-scene/camera
         vrHelper main-scene/vrHelper
         non-vr-cam (.-deviceOrientationCamera vrHelper)
         quat-delta (bjs/Quaternion.RotationYawPitchRoll (* base/ONE-DEG delta-rot) 0 0)
         pos-delta (bjs/Vector3. 0 0 (if (neg? delta-rot)
                                       -1
                                       1))]
     ; (js-debugger)
     ; (set! (.-rotationQuaternion camera) (bjs/Quaternion.RotationYawPitchRoll (* base/ONE-DEG -180) 0 0))
     ; (set! (.-rotationQuaternion vr-cam) (bjs/Quaternion.RotationYawPitchRoll (* base/ONE-DEG -90) 0 0))
      (prn "init-non-vr-view: non-vr-cam=" non-vr-cam)
      ; (set! (.-rotationQuaternion non-vr-cam) (.add
      ;                                               (.-rotationQuaternion non-vr-cam)
      ;                                               quat-delta))
      ; (set! (.-position vr-cam) (bjs/Vector3. (:x ip) (:y ip) (:z ip)))
      ; (set! (.-position non-vr-cam) (bjs/Vector3. 0 1.5 -5))
      ; (set! (.-position non-vr-cam) (.add (.-position non-vr-cam) pos-delta))
      (set! (.-position vrHelper) (.add (.-position vrHelper) pos-delta)))))

(defn init-vr-view
  ([] (init-non-vr-view 180))
  ([delta-rot]
   ; (prn "ff.scene.init-vr-view: delta-rot=" delta-rot)
   (let [camera main-scene/camera
         vrHelper main-scene/vrHelper
         vr-cam (.-webVRCamera vrHelper)
         quat-delta (bjs/Quaternion.RotationYawPitchRoll (* base/ONE-DEG delta-rot) 0 0)
         pos-delta (bjs/Vector3. 0 0 (if (neg? delta-rot)
                                      -1
                                      1))]
      (prn "init-vr-view: vr-cam=" vr-cam)
      (prn "init-vr-view: pos-delta=" pos-delta)
      ; (set! (.-rotationQuaternion vr-cam) (.add
      ;                                       (.-rotationQuaternion vr-cam)
      ;                                       quat-delta))
      ; (set! (.-position vr-cam) (bjs/Vector3. 0 1.5 -10))
      (set! (.-position vrHelper) (.add (.-position vrHelper) pos-delta)))))

;; ".currentVRCamera" is supposed to return the vr or non-vr camera depending on the mode,
;; but I find it doesn't work. So you have to distinguish the use cases yourself.
;; Note: solution is to set position on the vrHelper itself
(defn init-view
  ([] (init-view 180))
  ([delta-rot]
   ; (prn "ff.scene.init-vr-view: delta-rot=" delta-rot)
   (let [
         ; camera main-scene/camera
         vrHelper main-scene/vrHelper
         ;; this is a floating value that covers both vr and non-vr cameras
         ; current-cam (.-currentVRCamera vrHelper)
         current-cam (.-activeCamera main-scene/scene)
         quat-delta (bjs/Quaternion.RotationYawPitchRoll (* base/ONE-DEG delta-rot) 0 0)
         ; ang-delta (* (* base/ONE-DEG 10) (if (neg? delta)))
         pos-delta (bjs/Vector3. 0 0 (if (neg? delta-rot)
                                      -1
                                      1))
         ; x-rot (-> current-cam (.-rotation) (.-x))
         y-rot (-> current-cam (.-rotation) (.-y))
         delta-rot-rads (-> (bjs/Angle.FromDegrees delta-rot) (.radians))
         ; unit-circ (bjs/Vector3. (js/Math.cos (+ y-rot delta-rot-rads) 0 (js/Math.sin (+ y-rot delta-rot-rads))))
         unit-circ (bjs/Vector3. (js/Math.sin (+ y-rot delta-rot-rads)) 0 (js/Math.cos (+ y-rot delta-rot-rads)))
         ; unit-circ (bjs/Vector3. (js/Math.sin 0.17) 0 (js/Math.cos 0.17))
         new-tgt (.add (.-position current-cam) unit-circ)]
         ; z-rot (-> current-cam (.-rotation) (.-z))
         ; x-pos (-> current-cam (.-position) (.-x))
         ; y-pos (-> current-cam (.-position) (.-y))
         ; z-pos (-> current-cam (.-position) (.-z))]
      ; (js-debugger)
      (prn "init-view: current-cam=" current-cam)
      (prn "init-view: current-cam.pos=" (.-position current-cam))
      (prn "init-view: current-cam.y-rot=" y-rot)
      (prn "init-view: delta-rot-rads=" delta-rot-rads)
      (prn "init-view: unit-circ" unit-circ)
      (prn "init-view: old-tgt" (.-target current-cam))
      (prn "init-view: new-tgt" new-tgt)
      ; (set! (.-rotationQuaternion cam) (.add
      ;                                   (.-rotationQuaternion cam)
      ;                                   quat-delta))
      ; (set! (.-position vr-cam) (bjs/Vector3. (:x ip) (:y ip) (:z ip)))
      ; (set! (.-position cam) (bjs/Vector3. 0 1.5 -5))
      ; (set! (.-position cam)(.add (.-position cam) pos-delta))
      ; (set! (.-position vrHelper) (.add (.-position vrHelper) pos-delta))

      ; (set! (.-rotation current-cam) (.add (.-rotation current-cam) (bjs/Vector3. 0 delta-rot 0)))
      ; (set! (.-_rotationAngle vrHelper) (+ (.-_rotationAngle vrHelper) delta-rot))
      ; (prn "new rotationAngle=" (.-_rotationAngle vrHelper))
      (prn "oldTarget=" (.-target current-cam))
      ; (.setTarget current-cam (bjs/Vector3. (.add (.-position current-cam)
      ;                                             (bjs/Vector3. (js/Math.cos y-rot) 0 (js/Math.sin y-rot)))))
      ; (.setTarget current-cam (bjs/Vector3. (.add (.-position current-cam) unit-circ)))
      ; (.setTarget current-cam (.add (.-position current-cam) unit-circ))
      (.setTarget current-cam new-tgt)
      (prn "newTarget=" (.-target current-cam)))))

      ; for (let i = 0; i < 6; i++) {
      ;      faceUV[i] = new BABYLON.Vector4(i / columns, 0, (i + 1) / columns, 1 / rows)))));

(defn create-tiled-box []
  ; BABYLON.MeshBuilder.CreateBox("box", {height: 1, width: 0.75, depth: 0.25});
  (let [scene main-scene/scene
        ; tile-box (bjs/MeshBuilder.CreateBox. "tile-box" (js-obj "height" 11 "width" 11 "depth" 11) scene)
        mat (bjs/StandardMaterial. "arrows-mat")
        cols 6
        rows 1
        face-uv (map (fn [i] (bjs/Vector4. (/ i cols) 0 (/ (+ i 1) cols) (/ 1 rows))) (range 6))
        ; opts (js-obj "pattern" bjs/Mesh.FLIP_N_ROTATE_ROW
        ;              "faceUV" face-uv
        ;              "width" 5
        ;              "height" 5
        ;              "depth" 5
        ;              "tileSize" 10
        ;              "tileWidth" 10
        ;              "sideOrientation" bjs/Mesh.DOUBLESIDE)
        opts (clj->js {:pattern "bjs/Mesh.FLIP_N_ROTATE_ROW"
                       ; :faceUV face-uv
                       :width 29
                       :height 49
                       :depth 29
                       :tileSize 5
                       :tileWidth 5
                       :sideOrientation bjs/Mesh.DOUBLESIDE})
                       ; :frontUVs face-uv
                       ; :backUVs face-uv})
        ; tile-box (bjs/MeshBuilder.CreateTiledBox "tile-box" opts)
        tile-box (bjs/MeshBuilder.CreateBox "tile-box-2" opts scene)]
      (set! (.-material tile-box) mat)
      ; (prn "face-uv=" face-uv)))
      ; (set! (.-diffuseTexture mat) (bjs/Texture. "texture/misc/arrows.jpg"))
      ; (set! (.-diffuseTexture mat) (bjs/Texture. "textures/misc/arrows.jpg"))
      (set! (.-diffuseTexture mat) (bjs/Texture. "textures/geb_cube_wood.jpg"))))

(defn create-walls []
  (prn "scene-l1: create-walls entered")
  (let [scene main-scene/scene
        rear-wall (bjs/MeshBuilder.CreatePlane "rear-wall"
                                             (clj->js {:width 10 :height 10 :subdivisions 10
                                                       :sideOrientation bjs/Mesh.DOUBLESIDE})
                                          scene)
        front-wall (bjs/MeshBuilder.CreatePlane "front-wall"
                                               (clj->js {:width 30 :height 10 :subdivisions 10
                                                         :sideOrientation bjs/Mesh.DOUBLESIDE})
                                            scene)
        left-wall (bjs/MeshBuilder.CreatePlane "left-wall"
                                             (clj->js {:width 30 :height 10 :subdivisions 10
                                                       :sideOrientation bjs/Mesh.DOUBLESIDE})
                                          scene)
        right-wall (bjs/MeshBuilder.CreatePlane "right-wall"
                                              (clj->js {:width 30 :height 10 :subdivisions 10
                                                        :sideOrientation bjs/Mesh.DOUBLESIDE})
                                          scene)
        rear-wall-mat (bjs-m/GridMaterial. "rear-wall-mat" scene)
        front-wall-mat (bjs-m/GridMaterial. "front-wall-mat" scene)
        left-wall-mat (bjs-m/GridMaterial. "left-wall-mat" scene)
        right-wall-mat (bjs-m/GridMaterial. "right-wall-mat" scene)
        ground (.getMeshByID scene "ground")]
    (set! (.-position rear-wall) (bjs/Vector3. 0 5 10))
    (set! (.-lineColor rear-wall-mat) (bjs/Color3.Blue))
    (set! (.-material rear-wall) rear-wall-mat)

    (set! (.-position front-wall) (bjs/Vector3. 0 5 -25))
    (set! (.-lineColor front-wall-mat) (bjs/Color3.Purple))
    (set! (.-material front-wall) front-wall-mat)

    (set! (.-position left-wall) (bjs/Vector3. -10 5 0))
    (.rotate left-wall bjs/Axis.Y (* base/ONE-DEG 90) bjs/Space.WORLD)
    ; (set! (.-lineColor left-wall-mat) (bjs/Color3. 0xff 0xc0 0xcb))
    ; (set! (.-lineColor left-wall-mat) (bjs/Color3. 255 192 203))
    ; (set! (.-lineColor left-wall-mat) (bjs/Color3. 1 1 1))
    (set! (.-lineColor left-wall-mat) (bjs/Color3.Yellow))
    ; (set! (.-lineColor left-wall-mat) (bjs/Color3. 0x8f 0x0c 0x0b))
    (set! (.-material left-wall) left-wall-mat)

    (set! (.-position right-wall) (bjs/Vector3. 10 5 0))
    (.rotate right-wall bjs/Axis.Y (* base/ONE-DEG -90) bjs/Space.WORLD)
    (set! (.-lineColor right-wall-mat) (bjs/Color3.Red))
    (set! (.-material right-wall) right-wall-mat)
    (when ground (set! (.-visibility ground) 1))))

(defn remove-walls []
  (prn "scene-l1.remove-walls entered")
  (let [scene main-scene/scene
        rear-wall (.getMeshByID scene "rear-wall")
        front-wall (.getMeshByID scene "front-wall")
        left-wall (.getMeshByID scene "left-wall")
        right-wall (.getMeshByID scene "right-wall")
        ground (.getMeshByID scene "ground")]
      (when rear-wall (.dispose rear-wall))
      (when front-wall (.dispose front-wall))
      (when left-wall (.dispose left-wall))
      (when right-wall (.dispose right-wall))
      (when ground (set! (.-visibility ground) 0))))
      ; (js-debugger)))

(defn reset-view []
  (let [vrHelper main-scene/vrHelper
        ; do-cam (.-deviceOrientationCamera vrHelper)
        vr-cam (.-currentVRCamera vrHelper)
        ip @main-scene/*camera-init-pos*]
    (set! (.-position vr-cam) (bjs/Vector3. (:x ip) (:y ip) (:z ip)))))

(defn secondary-btn-handler [stateObject]
  (prn "scene-l1: secondary-btn-handler: stateObject=" stateObject)
  ; (remove-walls)
  (when (.-pressed stateObject)
    (re-frame/dispatch [:cube-test.frig-frog.events/toggle-dev-mode])))

(defn init-vr-hooks [webVRController]
  (prn "scene-l1: init-vr-hooks: webVRController=" webVRController)
  ; (-> webVRController (.-onSecondaryButtonStateChangedObservable) (.add secondary-btn-handler))
  (-> webVRController (.-onBButtonStateChangedObservable) (.add secondary-btn-handler)))

(defn init [db]
  (let [scene main-scene/scene
        ; light1 (bjs/PointLight. "pointLight" (bjs/Vector3. 0 5 -3) scene)
        light1 (bjs/PointLight. "pointLight" (bjs/Vector3. 2 5 4) scene)]
    ; (when-let [vrHelper main-scene/vrHelper]
    ;   (-> vrHelper .-onControllerMeshLoadedObservable (.add init-vr-hooks)))
    ;   ; (-> vrHelper .-onBButtonStateChangedObservable (.add init-vr-hooks)))
    (init-gui)))
    ; (create-walls)))

(defn tick []
  (ff.train/tick))
  ; (let [scene main-scene/scene
  ;       engine main-scene/engine
  ;       delta-time (.getDeltaTime engine)
  ;       train-meshes (.getMeshesByTags scene "train")]
  ;   (prn "scene_l1.tick: train-mesh count=" (count train-meshes))
  ;   (doall (map #()))))
        ; bldg-cube-rot-y-delta (* (.-y bldg-cube-ang-vel) delta-time)]))
