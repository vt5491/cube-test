;; This is the front-end "meta-scene" for all the sub-scenens
;; in cube-test
(ns cube-test.top-scene.top-scene
  (:require
   [re-frame.core :as rf]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [babylonjs-gui :as bjs-gui]
   [babylonjs-materials :as bjs-m]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.utils.fps-panel :as fps-panel]
   ; [cube-test.events :as events]
   ; [cube-test.top-scene.events :as top-scene-events]
   [cube-test.base :as base]
   [cube-test.utils :as utils]
   [babylonjs-loaders :as bjs-l]
   [cube-test.utils.choice-carousel.choice-carousel :as cc]))

(declare animate-app-carousel)
(declare release)
(def keep-assets (bjs/KeepAssets.))
(def top-scene-assets (bjs/AssetContainer. main-scene/scene))
(def face-slot-assets (bjs/AssetContainer. main-scene/scene))
(def geb-cube-assets (bjs/AssetContainer. main-scene/scene))
(def app-cc-asset-containers {})

(def default-db
  {:top-scene-abc 7})

;; steps to "top-scene-izing" a sub-scene:
;; 1. create a release function in the subscene.  Something like:
;;(defn release []
;; (prn "twizzlers.release: entered"))
;; (utils/release-common-scene-assets)
;; (.removeAllFromScene hemisferic-asset-container))
;; 2. add (main-scene/load-main-gui release) to the sub-scene's init
;;  -> it can't be done at the top level scene because each sub-scene's
;;     release function is potentially different and can't be known
;;     during top-scene's init.
;; 3. add (main-scene/tick) to the sub-scenes tick.
;;  -> this will set up the a-btn to say "return to top scene".
;; 4. add a soft-reset and hard-reset-path in game.cljs
;; 5. (potential): if getting dark PBR mats, may need to add '(main-scene/init-env)' to
;;   the sub-scenes init.
;; 6. (optional): make ground visible
; (let [grnd (.getMeshByID main-scene/scene "ground")]
;   (when grnd
;     (.setEnabled grnd true)
;     (set! (.-isVisible grnd) true)))

;; top-selectable scenes 7(beat-club),6(twizzlers), 5(geb), 4(skyscrapers), 3(vrubik), 2(face-slot), 1(cube-spin)
; 0=ff, 4=skyscraper, 5=geb, 6=twizzlers,
(def app-cc-idx-seed 7)
;; yes, kind of a magic number, but at least it's not hard-coded everywhere.
(def app-cc-idx-shift-factor -2)
;; Note: this will be overriden by global->top-scene->last-selected-idx upon a "soft" switch.
(def app-cc-idx app-cc-idx-seed)
; (def app-cc-idx 5)
; (def app-cc-idx 0)
(def app-carousel-parms {:radius 16.0
                         :app-ids [:ff         ;0
                                   :cube-spin  ;1
                                   :face-slot  ;2
                                   :vrubik     ;3
                                   :skyscraper ;4
                                   :geb-cube   ;5
                                   :twizzlers  ;6
                                   :beat-club] ;7
                         :model-files [
                                       ; "models/top_scene/sub_scenes/ff_scene.glb"
                                       ; "models/top_scene/sub_scenes/ff_scene_3.glb"
                                       "models/top_scene/sub_scenes/ff_scene_no_anim.glb"
                                       "models/top_scene/sub_scenes/cube_spin_scene_blender.glb"
                                       "models/top_scene/sub_scenes/face_slot_scene_blender.glb"
                                       "models/top_scene/sub_scenes/vrubik_scene_blender.glb"
                                       "models/top_scene/sub_scenes/skyscraper_scene_blender.glb"
                                       "models/top_scene/sub_scenes/geb_cube_scene_blender.glb"
                                       "models/top_scene/sub_scenes/twizzlers_scene_blender.glb"
                                       "models/top_scene/sub_scenes/beat_club_scene_blender_no_anim.glb"]
                         :scales [0.3
                                  0.2
                                  0.2
                                  0.2
                                  0.3
                                  0.3
                                  0.035
                                  0.2]
                         :colors [(bjs/Color3.Blue) (bjs/Color3.Gray) (bjs/Color3.Green) (bjs/Color3.Magenta)
                                  (bjs/Color3.Red) (bjs/Color3.Yellow) (bjs/Color3.Teal) (bjs/Color3.Purple)]
                         :top-level-scenes [:frig-frog
                                            :cube-spin-scene
                                            :face-slot-scene
                                            :vrubik-scene
                                            :skyscrapers-scene
                                            :geb-cube-scene
                                            :twizzlers
                                            :beat-club]})
;; tried to make this dynamic, but too hard-- just settle with making it static instead.
; (def n-choices (- 8 1))
(def n-choices (count (:model-files app-carousel-parms)))
(def app-carousel-theta-width (/ (* 2.0 js/Math.PI) (count (:app-ids app-carousel-parms)) 1))
; (def app-carousel-theta-width (* base/ONE-DEG 5))
(def app-carousel-origin (bjs/Vector3. 0 0 (:radius app-carousel-parms)))
(def app-carousel-is-animating false)
(def app-carousel-rot-remaining 0)
(def app-carousel-rot-dir nil)
(def app-carousel-rot-duration 1)

(def left-thumbrest)
(def y-btn)


(def tmp-container)
(def tmp-keep-assets)
(defn tmp [db val]
  (rf/dispatch [:cube-test.utils.choice-carousel.events/update-last-selected-idx [:globals :top-scene :last-selected-idx] app-cc-idx]))

;; Note: defunct
; (defn init-app-planes []
;   (let [scene main-scene/scene
;         n_apps 4
;         ff-top-plane (bjs/MeshBuilder.CreatePlane "ff-top-plane"
;                                         (clj->js {:width 3 :height 3
;                                                   :sideOrientation bjs/Mesh.DOUBLESIDE})
;                                         scene)
;         cube-spin-plane (bjs/MeshBuilder.CreatePlane "cube-spin-plane"
;                                               (clj->js {:width 3 :height 3
;                                                         :sideOrientation bjs/Mesh.DOUBLESIDE})
;                                               scene)
;         face-slot-plane (bjs/MeshBuilder.CreatePlane "face-slot-plane"
;                                               (clj->js {:width 3 :height 3
;                                                         :sideOrientation bjs/Mesh.DOUBLESIDE})
;                                               scene)
;         vrubik-plane (bjs/MeshBuilder.CreatePlane "vrubik-plane"
;                                   (clj->js {:width 3 :height 3
;                                             :sideOrientation bjs/Mesh.DOUBLESIDE})
;                                   scene)
;         x-quat-90 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.X (* base/ONE-DEG 90)))]
;     (set! (.-rotationQuaternion ff-top-plane) x-quat-90)
;     (set! (.-rotationQuaternion cube-spin-plane) x-quat-90)
;     (set! (.-rotationQuaternion face-slot-plane) x-quat-90)
;     (set! (.-rotationQuaternion vrubik-plane) x-quat-90)
;     (set! (.-isPickable ff-top-plane) true)
;     (set! (.-isPickable cube-spin-plane) true)
;     (set! (.-isPickable face-slot-plane) true)
;     (set! (.-isPickable vrubik-plane) true)
;
;     (let [r (:radius app-carousel-parms)
;           theta (/ (* 360 base/ONE-DEG) n_apps)]
;       (prn "theta=" theta)
;       (set! (.-position cube-spin-plane) (bjs/Vector3. (* r (js/Math.cos (* 1 theta))) 0 (+ (* r (js/Math.sin (* 1 theta))) r)))
;       (set! (.-position face-slot-plane) (bjs/Vector3. (* r (js/Math.cos (* 2 theta))) 0 (+ (* r (js/Math.sin (* 2 theta))) r)))
;       (set! (.-position vrubik-plane) (bjs/Vector3. (* r (js/Math.cos (* 4 theta))) 0 (+ (* r (js/Math.sin (* 4 theta))) r))))))
(defn tmp-2 [val]
  (prn "top-scene.tmp-2: entered")
  (set! main-scene/env (bjs/EnvironmentHelper.
                          (js-obj
                           "createGround" false
                           "skyboxSize" 90)
                          main-scene/scene)))

(defn ff-cube-loaded [meshes particle-systems skeletons anim-groups name user-cb]
  (println "ff-cube-loaded")
  (doall (map #(do
                 (when (re-matches #"__root__" (.-id %1))
                     (set! (.-name %1) name)
                     (set! (.-id %1) name)))
                     ;; the neg. on the x is needed otherwise the materials are "flipped"
                     ; (set! (.-scaling %1)(bjs/Vector3. -0.3 0.3 0.3))
                     ; (set! (.-position %1)(bjs/Vector3. 0 2 0))))
              meshes)))

(defn load-ff-cube [path file name user-cb]
  (.ImportMesh bjs/SceneLoader ""
               path
               file
               main-scene/scene
               #(ff-cube-loaded %1 %2 %3 %4 name user-cb)))

; (defn model-loaded [root-prfx scale meshes particle-systems skeletons anim-groups name user-cb])
;; following 2 defunct: replaced by cc/choice-model-loaded
; (defn model-loaded [user-parms meshes particle-systems skeletons anim-groups name user-cb]
;   (println "model-loaded")
;   (let [root-prfx (:root-prfx user-parms)
;         scale (:scale user-parms)
;         delta-pos (:delta-pos user-parms)]
;     (doall (map #(do
;                    ; (prn "model-loaded: id %1=" (.-id %1) ",root-prfx=" root-prfx)
;                    (when (re-matches #"__root__" (.-id %1))
;                          ; (set! (.-name %1) name)
;                          ; (set! (.-id %1) name)
;                          ; (set! (.-name %1) "root")
;                          ; (set! (.-id %1) "root")
;                          (set! (.-name %1) root-prfx)
;                          (set! (.-id %1) root-prfx)
;                          (.scaleInPlace (.-scaling %1) scale)
;                          (when delta-pos
;                            (set! (.-position %1) (.add (.-position %1) delta-pos)))))
;                 meshes))))
;
; (defn load-model [path file name user-cb]
;   (.ImportMesh bjs/SceneLoader ""
;                path
;                file
;                main-scene/scene
;                user-cb))

(defn app-selected [magic-idx-shift n-choices]
  (prn "top-scene:app-selected entered: app-cc-idx=" app-cc-idx ",magic-idx-shift=" magic-idx-shift ",n-choicse=" n-choices)
  ; (let [mod-idx (mod (+ app-cc-idx -2) 8)])
  ; (let [mod-idx (mod (+ app-cc-idx magic-idx-shift) n-choices)])
  (let [mod-idx app-cc-idx
        ; top-level-scene (nth (:top-level-scenes app-carousel-parms) app-cc-idx)
        _ (prn "app-selected: mod-idx=" mod-idx)
        top-level-scene (nth (:top-level-scenes app-carousel-parms) mod-idx)]
    (prn "top-scene. top-level-scene=" top-level-scene)
    ; (cube-test.events/switch-app top-level-scene)
    ; (rf/dispatch [:cube-test.utils.choice-carousel.events/update-last-selected-idx app-cc-idx])
    (cube-test.events/soft-switch-app top-level-scene release)))
    ; (rf/dispatch [:cube-test.events.soft-switch-app-evt top-level-scene release])
    ; (rf/dispatch [:soft-switch-app-evt top-level-scene release])))

(defn init-scene-carousel [db]
  (prn "init-scene-carousel: db=" db)
  (prn "top-scene.init-scene-carousel entered, db.choice-carousels[0]=" (get-in db [:choice-carousels 0]))
  ; (rf/dispatch [:cube-test.utils.choice-carousel.events/update-last-selected-idx app-cc-idx])
  (cc/load-choice-carousel-gui
   ; :cube-test.top-scene.events/app-left
   ;; Note: calling directly is slightly less "jerky" than dispatching a (re-frame) event.
   (partial animate-app-carousel :left)
   ; :cube-test.top-scene.events/app-right
   (partial animate-app-carousel :right)
   ; :cube-test.top-scene.events/app-selected
   ; (partial app-selected app-cc-idx-shift-factor (count (get-in db [:choice-carousels 0 :choices])))
   ; #(rf/dispatch [:cube-test.top-scene.events/app-selected])
   (partial app-selected app-cc-idx-shift-factor n-choices)
   app-carousel-theta-width)
  (let [
        choices (vec (map #(hash-map :id %1 :model-file %2 :scale %3 :top-level-scene %4)
                          (:app-ids app-carousel-parms)
                          (:model-files app-carousel-parms)
                          (:scales app-carousel-parms)
                          (:top-level-scenes app-carousel-parms)))
        parms {:id :app-cc :radius 16.0 :choices choices :colors (:colors app-carousel-parms)}]
    (rf/dispatch [:cube-test.utils.choice-carousel.events/init-choice-carousel parms]))
  (let [last-idx (get-in db [:globals :top-scene :last-selected-idx])]
    (if last-idx
      (set! app-cc-idx last-idx)
      (rf/dispatch [:cube-test.utils.choice-carousel.events/update-last-selected-idx [:globals :top-scene :last-selected-idx] app-cc-idx])))
  db)

(defn rot-app-carousel [dir delta-theta]
  (let [scene main-scene/scene
        ; m1 (.getMeshByID scene "ff")
        ; origin (bjs/Vector3.Zero.)
        ; theta (if (= dir :right))
        theta (if (= dir :left)
                  ; (* -1 app-carousel-theta-width)
                  (* -1 delta-theta)
                  ; app-carousel-theta-width
                  delta-theta)]
      ; (.rotateAround m1 app-carousel-origin bjs/Axis.Y theta)
      (cc/rot-meshes (:app-ids app-carousel-parms) dir theta app-carousel-origin)))

(defn animate-app-carousel [dir]
  (set! app-carousel-is-animating true)
  (set! app-carousel-rot-remaining app-carousel-theta-width)
  (set! app-carousel-rot-dir dir)
  (cc/play-rot-snd)
  (let [n-apps (+ (count (:app-ids app-carousel-parms)) 0)]
    (if (= dir :left)
        ; (set! app-cc-idx (mod (inc app-cc-idx) n-apps))
        ; (set! app-cc-idx (mod (dec app-cc-idx) n-apps)))))
        (set! app-cc-idx (mod (dec app-cc-idx) n-apps))
        (set! app-cc-idx (mod (inc app-cc-idx) n-apps)))))

(defn tmp-rot [dir]
  (let [scene main-scene/scene
        m (.getMeshByID scene "twizzlers")
        x-quat (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 10)))
        y-quat (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 10)))
        z-quat (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Z (* base/ONE-DEG 10)))]
      (case dir
        :x (.multiplyInPlace (.-rotationQuaternion m) x-quat)
        :y (.multiplyInPlace (.-rotationQuaternion m) y-quat)
        :z (.multiplyInPlace (.-rotationQuaternion m) z-quat))))
      ; (.multiplyInPlace (.-rotationQuaternion m) y-quat)))

(defn remove-asset-containers [choices]
  ; (prn "ts.rac: choices=" choices)
  (doall
    (map #(do
            ; (prn "rac: choice=" %1)
            (let [ac (:asset-container %1)]
              ; (prn "remove-asset-containers: ac=" ac)
              (.removeAllFromScene ac)))
       choices)))

(defn add-asset-containers [choices]
  ; (prn "ts.race: choices=" choices)
  (doall
    (map #(do
            ; (prn "rac: choice=" %1)
            (let [ac (:asset-container %1)]
              ; (prn "remove-asset-containers: ac=" ac)
              (.addAllToScene ac)))
       choices)))

(defn motion-ctrl-added [motion-ctrl]
  (when (= (.-handedness motion-ctrl) "left")
    ; (js-debugger)
    ; (set! left-thumbrest (.getComponent motion-ctrl "thumbrest"))
    (set! y-btn (.getComponent motion-ctrl "y-button"))))

(defn ctrl-added [xr-ctrl]
  (-> xr-ctrl .-onMotionControllerInitObservable (.add motion-ctrl-added)))

(defn thumbrest-handler []
  (prn "top-scene.thumbrest-handler: thumbrest pressed hasChanges=" (.-hasChanges left-thumbrest))
  (js-debugger))

(defn thumbrest-handler-2 []
  (prn "top-scene.thumbrest-handler: thumbrest touched hasChanges=" (.-hasChanges left-thumbrest)))
  ; (js-debugger))

(defn y-btn-handler []
  (prn "top-scene.y-btn-handler: y-btn pressed hasChanges=" (.-hasChanges y-btn)))
  ;  (js-debugger))

(defn release []
  (prn "top-scene.release: entered")
  ; (set! top-scene-assets (bjs/AssetContainer. main-scene/scene))
  ; (set! keep-assets (bjs/KeepAssets.))
  ; (let [scene main-scene/scene
  ;       uni-cam (.getCameraByID scene "uni-cam")
  ;       webxr (.getCameraByID scene "webxr")
  ;       sky-box (.getMeshByID scene "sky-box")
  ;       teleportation-target (.getMeshByID scene "teleportationTarget")
  ;       bg-helper (.getMeshByID scene "BackgroundHelper")]
  ;   (.push (.-cameras keep-assets) uni-cam webxr)
  ;   (.push (.-meshes keep-assets) sky-box teleportation-target bg-helper)
  ;   (.moveAllFromScene top-scene-assets keep-assets))
  (rf/dispatch [:cube-test.utils.choice-carousel.events/update-last-selected-idx [:globals :top-scene :last-selected-idx] app-cc-idx])
  (utils/release-common-scene-assets)
  (cc/release))
  ; (.stopRenderLoop main-scene/engine)
  ; (let [target (.-_currentRenderTarget main-scene/engine)]
  ;   (when target
  ;     (.unBindFramebuffer main-scene/engine target))))

(defn init [db]
  ; (init-app-planes)
  ;; we need to setup the skybox each time (.e.g. when doing a soft switch)
  (main-scene/init-env)
  (let [xr-helper main-scene/xr-helper]
    (-> xr-helper (.-input ) (.-onControllerAddedObservable) (.add ctrl-added)))
  (let [scene main-scene/scene
        tweak-partial (partial utils/tweak-xr-view 20 10 10)
        ground (.getMeshByID scene "ground")
        sky-box (.getMeshByID scene "BackgroundSkybox")
        ; tweak-partial (partial utils/tweak-xr-view -40 0 0)
        ; tweak-partial #()
        light1 (bjs/PointLight. "pointLight-1" (bjs/Vector3. 0 2 5) scene)
        light2 (bjs/PointLight. "pointLight-2" (bjs/Vector3. 10 8 5) scene)
        cam (utils/get-xr-camera)]
    ; (set! (.-isVisible (.getMeshByID main-scene/scene "ground")) false)
    ; (set!  (.-isVisible (.getMeshByID main-scene/scene "BackgroundSkybox")) false)
    (when ground
      (set! (.-isVisible ground) false))
    (when sky-box
      (set! (.-isVisible sky-box) false))
    ;; set-up scene level "enter xr" hook
    (-> main-scene/xr-helper
      (.-baseExperience)
      (.-onStateChangedObservable)
      (.add tweak-partial))
    (set! (.-onPointerDown scene)
      (fn [e pickResult]
        (when (.-hit pickResult)
          (case (-> pickResult (.-pickedMesh) (.-name))
            "ff-top-plane" (cube-test.events.soft-switch-app :frig-frog)
            "default"))))
    (bjs/TransformNode. "top-scene-root" scene)
    (set! (.-position cam) (bjs/Vector3. 0 4 -15))
    (prn "cam=" cam)
    (set! (.-rotationQuaternion cam) (bjs/Quaternion.FromEulerAngles (* 0 base/ONE-DEG) (* 0 base/ONE-DEG) 0))
    (prn "cam quat=" (.-rotationQuaternion cam))
    (prn "quat=" (bjs/Quaternion.FromEulerAngles 0.0 0.0 0.0))
    ; (init-scene-carousel)
    (rf/dispatch [:cube-test.top-scene.events/init-scene-carousel])
    ; (rf/dispatch [:cube-test.top-scene.events/set-globals])
    db))


;; the main tick
(defn render-loop []
  ; (prn "top-scene.render-loop: entered")
  (if (= main-scene/xr-mode "vr")
    (controller/tick)
    (controller-xr/tick))
  (main-scene/tick)
  ; (when (= main-scene/xr-mode "xr")
    ; (when (and left-thumbrest (.-hasChanges left-thumbrest)))
    ; (prn "ts.tick: left-thumbrest=" left-thumbrest)
    ; (when (and left-thumbrest (.-pressed left-thumbrest))
    ;   (thumbrest-handler))
    ; (when (and left-thumbrest) (.-touched left-thumbrest)
    ;   (thumbrest-handler-2))
    ; (when (and y-btn (.-pressed y-btn))
    ;   (y-btn-handler)))
      ; (let [axes (.-axes player-left-thumbstick)]
      ;   (player-ctrl-handler axes))))
  (if fps-panel/fps-pnl
    (fps-panel/tick main-scene/engine))
  (.render main-scene/scene)
  (when app-carousel-is-animating
    ;;TODO: add less than zero rounding so never go beyond theta-width
    (let [dt (/ (.getDeltaTime main-scene/engine) 1000)
          delta-theta (* (/ dt app-carousel-rot-duration) app-carousel-theta-width)]
        (rot-app-carousel app-carousel-rot-dir delta-theta)
        (set! app-carousel-rot-remaining (- app-carousel-rot-remaining delta-theta))
        ; (prn "rot-remain=" app-carousel-rot-remaining)
        (when (< app-carousel-rot-remaining 0)
          ;; stub rotate "backward" so we don't get any cumulative "angular drift".
          ; (prn "stub rotate=" (* base/ONE-DEG app-carousel-rot-remaining))
          ; (rot-app-carousel (if (= app-carousel-rot-dir :left) :right :left) app-carousel-rot-remaining)
          (rot-app-carousel app-carousel-rot-dir app-carousel-rot-remaining)
          (set! app-carousel-is-animating false)))))


(defn run-scene []
  (.stopRenderLoop main-scene/engine)
  (.runRenderLoop main-scene/engine (fn [] (render-loop))))
