(ns cube-test.frig-frog.scene-l1
  (:require
   [re-frame.core :as rf]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [babylonjs-gui :as bjs-gui]
   [babylonjs-materials :as bjs-m]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.utils.fps-panel :as fps-panel]
   [cube-test.base :as base]
   [cube-test.utils :as utils]
   [babylonjs-loaders :as bjs-l]
   [cube-test.frig-frog.train :as ff.train]
   [cube-test.frig-frog.ff-worker :as ff.worker]
   [cube-test.frig-frog.rules :as ff.rules]
   [cube-test.frig-frog.tile :as ff.tile]
   [promesa.core :as p]))

;; constants
;; note: some of these may arguably be placed at the object level, but we
;; aggregate some global constants at the scene level to minimize cross-domain
;; referencing.
(def quanta-width 1.2)
(def scene-initialized false)
(def reflector)
(def cmd-gui-adv-text)
(def cam-gui-adv-text)
(def ball-moving false)

(defn init-non-vr-view
  ([] (init-non-vr-view 180))
  ([delta-rot]
   (let [camera main-scene/camera
         vrHelper main-scene/vrHelper
         non-vr-cam (.-deviceOrientationCamera vrHelper)
         quat-delta (bjs/Quaternion.RotationYawPitchRoll (* base/ONE-DEG delta-rot) 0 0)
         pos-delta (bjs/Vector3. 0 0 (if (neg? delta-rot)
                                       -1
                                       1))]
      (set! (.-position vrHelper) (.add (.-position vrHelper) pos-delta)))))

(defn init-vr-view
  ([] (init-non-vr-view 180))
  ([delta-rot]
   (let [camera main-scene/camera
         vrHelper main-scene/vrHelper
         vr-cam (.-webVRCamera vrHelper)
         quat-delta (bjs/Quaternion.RotationYawPitchRoll (* base/ONE-DEG delta-rot) 0 0)
         pos-delta (bjs/Vector3. 0 0 (if (neg? delta-rot)
                                      -1
                                      1))]
      (set! (.-position vrHelper) (.add (.-position vrHelper) pos-delta)))))

;; ".currentVRCamera" is supposed to return the vr or non-vr camera depending on the mode,
;; but I find it doesn't work. So you have to distinguish the use cases yourself.
;; Note: solution is to set position on the vrHelper itself
(defn init-view
  ([] (init-view 180))
  ([delta-rot]
   (let [
         ; camera main-scene/camera
         ; vrHelper main-scene/vrHelper
         ; xrHelper main-scene/xr-helper
         ;; this is a floating value that covers both vr and non-vr cameras
         current-cam (.-activeCamera main-scene/scene)
         quat-delta (bjs/Quaternion.RotationYawPitchRoll (* base/ONE-DEG delta-rot) 0 0)
         pos-delta (bjs/Vector3. 0 0 (if (neg? delta-rot)
                                      -1
                                      1))
         x-rot (-> current-cam (.-rotation) (.-x))
         y-rot (-> current-cam (.-rotation) (.-y))
         delta-rot-rads (-> (bjs/Angle.FromDegrees delta-rot) (.radians))
         unit-circ (bjs/Vector3. (js/Math.sin (+ y-rot delta-rot-rads)) 0 (js/Math.cos (+ y-rot delta-rot-rads)))
         new-tgt (.add (.-position current-cam) unit-circ)]
      (.setTarget current-cam new-tgt)
      (set! (.-rotation current-cam) (bjs/Vector3. (* 20 base/ONE-DEG) 0 0)))))

(defn create-tiled-box []
  (let [scene main-scene/scene
        mat (bjs/StandardMaterial. "arrows-mat")
        cols 6
        rows 1
        face-uv (map (fn [i] (bjs/Vector4. (/ i cols) 0 (/ (+ i 1) cols) (/ 1 rows))) (range 6))
        opts (clj->js {:pattern "bjs/Mesh.FLIP_N_ROTATE_ROW"
                       ; :faceUV face-uv
                       :width 29
                       :height 49
                       :depth 29
                       :tileSize 5
                       :tileWidth 5
                       :sideOrientation bjs/Mesh.DOUBLESIDE})
        tile-box (bjs/MeshBuilder.CreateBox "tile-box-2" opts scene)]
      (set! (.-material tile-box) mat)
      (set! (.-diffuseTexture mat) (bjs/Texture. "textures/geb_cube_wood.jpg"))))

(defn create-walls []
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
    (set! (.-lineColor left-wall-mat) (bjs/Color3.Yellow))
    (set! (.-material left-wall) left-wall-mat)

    (set! (.-position right-wall) (bjs/Vector3. 10 5 0))
    (.rotate right-wall bjs/Axis.Y (* base/ONE-DEG -90) bjs/Space.WORLD)
    (set! (.-lineColor right-wall-mat) (bjs/Color3.Red))
    (set! (.-material right-wall) right-wall-mat)
    (when ground (set! (.-visibility ground) 1))))

(defn remove-walls []
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

(defn reset-view []
  (let [vrHelper main-scene/vrHelper
        vr-cam (.-currentVRCamera vrHelper)
        ip @main-scene/*camera-init-pos*]
        ; ip (bjs/Vector3. 2.2 2.2 -3.6)]
    (set! (.-position vr-cam) (bjs/Vector3. (:x ip) (:y ip) (:z ip)))))

(defn secondary-btn-handler [stateObject]
  (when (.-pressed stateObject)
    (rf/dispatch [:cube-test.frig-frog.events/toggle-dev-mode])))

(defn init-vr-hooks [webVRController]
  (-> webVRController (.-onBButtonStateChangedObservable) (.add secondary-btn-handler)))

(defn init-start-gui-2 []
  (let [
        top-plane (bjs/MeshBuilder.CreatePlane "top-plane" (js-obj "width" 5 "height" 3))
        top-adv-texture (bjs-gui/AdvancedDynamicTexture.CreateForMesh top-plane 2048 1024)
        top-pnl (bjs-gui/Grid.)
        top-hdr (bjs-gui/TextBlock.)
        rot-cam-btn (bjs-gui/Button.CreateSimpleButton "rot-cam" "rot-cam")
        add-train-btn (bjs-gui/Button.CreateSimpleButton "add-train-btn" "add train")
        toggle-dancer-btn (bjs-gui/Button.CreateSimpleButton "toggle-dancer-btn" "toggle dancer")
        firework-btn (bjs-gui/Button.CreateSimpleButton "firework-btn" "firework")]
    (set! (.-position top-plane) (bjs/Vector3. 0 5 10))
    (.enableEdgesRendering top-plane)

    (.addControl top-adv-texture top-pnl)
    (set! (.-text top-hdr) "commands")
    (set! (.-height top-hdr) "500px")
    (set! (.-fontSize top-hdr) "160")
    (set! (.-color top-hdr) "white")

    ;; create 4 rows and 2 cols
    (.addRowDefinition top-pnl 0.25 false)
    (.addRowDefinition top-pnl 0.25)
    (.addRowDefinition top-pnl 0.25)
    (.addRowDefinition top-pnl 0.25)
    (.addColumnDefinition top-pnl 0.5)
    (.addColumnDefinition top-pnl 0.5)
    (.addControl top-pnl top-hdr 0 0)

    ;; rot-cam-btn
    (set! (.-autoScale rot-cam-btn) true)
    (set! (.-fontSize rot-cam-btn) "100")
    (set! (.-color rot-cam-btn) "white")
    (-> rot-cam-btn .-onPointerUpObservable
        (.add (fn [value]
                (println "rot-cam-btn pressed")
                (let [cam main-scene/camera]
                  (-> (.-rotationQuaternion cam) (.multiplyInPlace (bjs/Quaternion.FromEulerAngles 0 (* 10 base/ONE-DEG) 0)))))))
    (.addControl top-pnl rot-cam-btn 1 0)

    ;; add-train-btn
    (set! (.-autoScale add-train-btn) true)
    (set! (.-fontSize add-train-btn) "100")
    (set! (.-color add-train-btn) "white")
    (-> add-train-btn .-onPointerUpObservable
        (.add (fn [value]
                (println "add-train-btn pressed")
                ;; note: calling an rf event causes a noticiable lag in all action.
                (ff.worker/post-add-train {:id-stem "tr-1", :length 3,
                                           :init-col 7, :init-row 2,
                                           :vx -1, :vy 0}))))
    (.addControl top-pnl add-train-btn 1 1)

    ;; toggle-dancer-btn
    (set! (.-autoScale toggle-dancer-btn) true)
    (set! (.-fontSize toggle-dancer-btn) "100")
    (set! (.-color toggle-dancer-btn) "white")
    (-> toggle-dancer-btn .-onPointerUpObservable
        (.add (fn [value]
                (println "toggle-dancer-btn pressed"))))
    (.addControl top-pnl toggle-dancer-btn 2 0)

    ;; firework-btn
    (set! (.-autoScale firework-btn) true)
    (set! (.-fontSize firework-btn) "100")
    (set! (.-color firework-btn) "white")
    (-> firework-btn .-onPointerUpObservable
        (.add (fn [value]
                (println "firework-btn pressed"))))
                ; (re-frame/dispatch [:cube-test.beat-club.events/firework]))))
    (.addControl top-pnl firework-btn 3 0)))

(defn init-gui []
  (init-start-gui-2))

(defn cmd-gui-loaded []
  (let [add-train-btn (.getControlByName cmd-gui-adv-text "add_train_btn")
        init-top-ball-btn (.getControlByName cmd-gui-adv-text "init_top_ball_btn")
        init-btm-ball-btn (.getControlByName cmd-gui-adv-text "init_btm_ball_btn")
        toggle-btm-ball-btn (.getControlByName cmd-gui-adv-text "toggle_btm_ball_btn")
        toggle-top-ball-btn (.getControlByName cmd-gui-adv-text "toggle_top_ball_btn")]
    (-> add-train-btn (.-onPointerClickObservable)
        (.add #(rf/dispatch [:cube-test.frig-frog.events/post-add-train
                              {:id-stem :tr-1 :vx -1 :vy 0 :length 5 :init-row 2 :init-col 4}])))
    (-> init-top-ball-btn (.-onPointerClickObservable)
        (.add #(do
                 (set! ball-moving true)
                 (ff.rules/init-ball-pos  :id :cube-test.frig-frog.rules/top-ball
                                          :sub-id 1
                                          :x 8 :y 3
                                          :vx -1 :vy 0
                                          :anim true))))
    (-> init-btm-ball-btn (.-onPointerClickObservable)
        (.add #(do
                 (set! ball-moving true)
                 (ff.rules/init-ball-pos  :id :cube-test.frig-frog.rules/btm-ball
                                          :sub-id 1
                                          :x 8 :y 3
                                          :vx -1 :vy 0
                                          :anim true))))
    (-> toggle-btm-ball-btn (.-onPointerClickObservable)
        (.add #(do
                 (ff.rules/ball-toggle-anim :cube-test.frig-frog.rules/btm-ball 1))))
    (-> toggle-top-ball-btn (.-onPointerClickObservable)
        (.add #(do
                 (ff.rules/ball-toggle-anim :cube-test.frig-frog.rules/top-ball 1))))))

(defn load-cmd-gui []
  (let [scene main-scene/scene
        plane (bjs/MeshBuilder.CreatePlane "gui-plane" (js-obj "width" 4, "height" 4) scene)
        ; _ (set! (.-position plane) (bjs/Vector3. 0 5 10))
        _ (set! (.-position plane) (bjs/Vector3. 0 3 10))
        _ (.enableEdgesRendering plane)
        _ (set! (.-edgesWidth plane) 1.0)
        adv-text (bjs-gui/AdvancedDynamicTexture.CreateForMesh plane 768 768)
        _ (set! cmd-gui-adv-text adv-text)]
    (-> adv-text
     (.parseFromURLAsync "models/frig_frog/scene_l1_cmd_pnl.json")
     (p/then #(cmd-gui-loaded)))))

(defn cam-gui-loaded []
  (let [x-radio-up-btn (.getControlByName cam-gui-adv-text "x_radio_up_btn")
        x-radio-down-btn (.getControlByName cam-gui-adv-text "x_radio_down_btn")
        y-radio-up-btn (.getControlByName cam-gui-adv-text "y_radio_up_btn")
        y-radio-down-btn (.getControlByName cam-gui-adv-text "y_radio_down_btn")
        z-radio-up-btn (.getControlByName cam-gui-adv-text "z_radio_up_btn")
        z-radio-down-btn (.getControlByName cam-gui-adv-text "z_radio_down_btn")
        ; cam-rot-axis-group (bjs/RadioGroup. "cam_rot_axis")
        ; _ (.addRadio cam-rot-axis-group)
        rot-cam-btn (.getControlByName cam-gui-adv-text "rot_cam_btn")]
    (-> x-radio-up-btn (.-onPointerClickObservable)
        (.add #(do
                 (prn "x-radio-up-btn pressed"))))
    (-> x-radio-down-btn (.-onPointerClickObservable)
        (.add #(do
                 (prn "x-radio-down-btn pressed"))))
    (-> rot-cam-btn (.-onPointerClickObservable)
        ; (.add #(do))
        (.add (fn [value]
                (let [cam main-scene/camera
                      quat (.-rotationQuaternion cam)
                      inc (* 10 base/ONE-DEG)]
                  (when (.-isChecked x-radio-up-btn)
                    (prn "rot x axis up")
                    (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles  (* -1 inc) 0 0)))
                  (when (.-isChecked x-radio-down-btn)
                    (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles inc 0 0))
                    (prn "rot x axis down"))
                  (when (.-isChecked y-radio-up-btn)
                    (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles  0 (* -1 inc) 0)))
                  (when (.-isChecked y-radio-down-btn)
                    (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles 0 inc 0)))
                  (when (.-isChecked z-radio-up-btn)
                    (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles  0 0 (* -1 inc))))
                  (when (.-isChecked z-radio-down-btn)
                    (.multiplyInPlace quat (bjs/Quaternion.FromEulerAngles 0 0 inc)))))))))


(defn load-cam-gui []
  (let [scene main-scene/scene
        plane (bjs/MeshBuilder.CreatePlane "cam-plane" (js-obj "width" 6, "height" 6) scene)
        _ (set! (.-position plane) (bjs/Vector3. 9 5 10))
        _ (.enableEdgesRendering plane)
        _ (set! (.-edgesWidth plane) 1.0)
        adv-text (bjs-gui/AdvancedDynamicTexture.CreateForMesh plane 1024 1024)
        _ (set! cam-gui-adv-text adv-text)]
    (-> adv-text
     (.parseFromURLAsync "models/frig_frog/scene_l1_cam_pnl.json")
     (p/then #(cam-gui-loaded)))))

(defn start-btn-handler [])

(defn toggle-animation []
  (rf/dispatch [:cube-test.frig-frog.events/train-toggle-animation]))

(defn init-reflector []
  (set! reflector (bjs/Reflector. main-scene/scene "localhost" 1234)))

(defn init-balls []
  (ff.rules/init-ball-pos
    :id :cube-test.frig-frog.rules/top-ball
    :sub-id 2
    :x 8 :y 4
    :vx -1.5 :vy 0
    :anim true)
  (ff.rules/init-ball-pos
    :id :cube-test.frig-frog.rules/btm-ball
    :sub-id 2
    :x 8 :y 4
    :vx -1.2 :vy 0
    :anim true))

(defn init [db]
  (let [scene main-scene/scene
        ; light1 (bjs/PointLight. "pointLight" (bjs/Vector3. 2 5 4) scene)
        light1 (bjs/PointLight. "pointLight-1" (bjs/Vector3. 0 2 5) scene)
        light2 (bjs/PointLight. "pointLight-2" (bjs/Vector3. 10 2 5) scene)
        camera main-scene/camera
        ; cam-quat (bjs/Quaternion.FromEulerAngles (* -90 base/ONE-DEG) 0 0)
        cam-rot (bjs/Vector3. (* -15 base/ONE-DEG) 0 0)
        spin-cube (bjs/MeshBuilder.CreateBox. "spin_cube" (js-obj "height" 1 "width" 1 "depth" 1) scene)]
    ; (set! (.-position camera) (bjs/Vector3. 1.54 4.77 -7.82))
    (set! (.-position camera) (bjs/Vector3. 2.2 2.2 -7.82))
    (set! (.-rotation camera) (.add (.-rotation camera) cam-rot))
    ; (prn "cam quat=" (.-rotationQuaternion camera))
    ; (js-debugger)
    ; (.multiplyInPlace (.-rotationQuaternion camera) cam-quat)

    (set! cube-test.frig-frog.board/board-width (* (:n-cols db) (:quanta-width db)))
    (set! cube-test.frig-frog.board/board-length (* (:n-rows db) (:quanta-width db)))
    ; (init-gui)
    (load-cmd-gui)
    (load-cam-gui)
    (set! (.-position spin-cube) (bjs/Vector3. 4 6 10))
    ;; seed the initial train id
    (ff.rules/update-train-id-cnt 1)
    ; (ff.rules/init-player)
    ; (ff.rules/init-game-piece "player" 0 5 0 0)
    ; (ff.rules/init-game-piece ff.rules/player 0 5 0 0)
    ; (ff.rules/init-game-piece :cube-test.frig-frog.rules/player 0 5 0 0)
    (ff.rules/init-player :cube-test.frig-frog.rules/btm-player 0 5)
    ; (ff.rules/init-top-player :cube-test.frig-frog.rules/top-player 1 6)
    (ff.rules/init-player :cube-test.frig-frog.rules/top-player 0 5)
    (init-balls)
    (ff.tile/init)))

(defn tick []
  (ff.train/tick)
  (when-let [spin-cube (.getMeshByID main-scene/scene "spin_cube")]
    (let [rot (.-rotation spin-cube)]
      (set! (.-rotation spin-cube)(bjs/Vector3. (.-x rot) (+ (.-y rot) (* base/ONE-DEG 0.5) (.-z rot))))))
  (ff.rules/tick))
