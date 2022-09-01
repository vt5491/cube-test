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
(def keep-assets (bjs/KeepAssets.))
(def top-scene-assets (bjs/AssetContainer. main-scene/scene))
(def face-slot-assets (bjs/AssetContainer. main-scene/scene))
(def geb-cube-assets (bjs/AssetContainer. main-scene/scene))
(def app-cc-asset-containers {})

(def default-db
  {:top-scene-abc 7})

(def app-cc-idx 6)
(def app-carousel-parms {:radius 16.0
                         :app-ids [:ff
                                   :cube-spin
                                   :face-slot
                                   :vrubik
                                   :skyscraper
                                   :geb-cube
                                   :twizzlers
                                   :beat-club]
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

(def app-carousel-theta-width (/ (* 2.0 js/Math.PI) (count (:app-ids app-carousel-parms)) 1))
; (def app-carousel-theta-width (* base/ONE-DEG 5))
(def app-carousel-origin (bjs/Vector3. 0 0 (:radius app-carousel-parms)))
(def app-carousel-is-animating false)
(def app-carousel-rot-remaining 0)
(def app-carousel-rot-dir nil)
(def app-carousel-rot-duration 1)


(defn tmp [db hash]
  ; (assoc-in db [])
  ; (assoc [0 1 2] 1 3)
  ; (let [ccs (get-in db :choice-carousels)])
  (let [
        ; ccs (:choice-carousels db)
        ; cc (first ccs)
        ; choices (:choices cc)
        ; first-choice (first choices)
        first-choice (get-in db [:choice-carousels 0 :choices 0])
        new-choice (assoc first-choice :abc 7)]
    (prn "first-choice=" first-choice)
    (prn "new-choice=" new-choice)
    ; (assoc db)
    ; (assoc first-choice 0 new-choice)
    ; (assoc choices 0 new-choice)
    (assoc-in db [:choice-carousels 0 :choices 0] new-choice)))

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

(defn app-selected []
  (prn "top-scene:app-selected entered: app-cc-idx=" app-cc-idx)
  (let [top-level-scene (nth (:top-level-scenes app-carousel-parms) app-cc-idx)]
    (prn "top-scene. top-level-scene=" top-level-scene)
    ; (cube-test.events/switch-app top-level-scene)
    (cube-test.events/soft-switch-app top-level-scene)))

(defn init-scene-carousel []
  (prn "top-scene.init-scene-carousel entered")
  (cc/load-choice-carousel-gui
   ; :cube-test.top-scene.events/app-left
   ;; Note: calling directly is slightly less "jerky" than dispatching a (re-frame) event.
   (partial animate-app-carousel :left)
   ; :cube-test.top-scene.events/app-right
   (partial animate-app-carousel :right)
   ; :cube-test.top-scene.events/app-selected
   app-selected
   app-carousel-theta-width)
  (let [
        choices (vec (map #(hash-map :id %1 :model-file %2 :scale %3 :top-level-scene %4)
                          (:app-ids app-carousel-parms)
                          (:model-files app-carousel-parms)
                          (:scales app-carousel-parms)
                          (:top-level-scenes app-carousel-parms)))
        parms {:id :app-cc :radius 16.0 :choices choices :colors (:colors app-carousel-parms)}]
    (rf/dispatch [:cube-test.utils.choice-carousel.events/init-choice-carousel parms])))

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
        ; (set! app-cc-idx (mod (dec app-cc-idx) n-apps))
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
  (prn "ts.rac: choices=" choices)
  (doall
    (map #(do
            (prn "rac: choice=" %1)
            (let [ac (:asset-container %1)]
              (prn "remove-asset-containers: ac=" ac)
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


(defn init [db]
  ; (init-app-planes)
  (prn "app-carousel-theta-width=" app-carousel-theta-width)
  (let [scene main-scene/scene
        tweak-partial (partial utils/tweak-xr-view 20 10 10)
        ground (.getMeshByID scene "ground")
        sky-box (.getMeshByID scene "BackgroundSkybox")
        ; tweak-partial (partial utils/tweak-xr-view -40 0 0)
        ; tweak-partial #()
        light1 (bjs/PointLight. "pointLight-1" (bjs/Vector3. 0 2 5) scene)
        light2 (bjs/PointLight. "pointLight-2" (bjs/Vector3. 10 8 5) scene)]
    ; (set! (.-isVisible (.getMeshByID main-scene/scene "ground")) false)
    ; (set! (.-isVisible (.getMeshByID main-scene/scene "BackgroundSkybox")) false)
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

    ;; note: keep
    ; (load-model "models/top_scene/sub_scenes/" "ff_scene.glb" "ff-scene"
    ;             (partial model-loaded
    ;                      {:root-prfx "ff-scene"
    ;                       :scale  0.3
    ;                       :delta-pos (bjs/Vector3. 0 0.2 0)}))

    ; (let [choices [{:id :ff} {:id :cube-spin} {:id :face-slot}]
    ;       parms {:id :app-cc :choices choices}]
    ;   (rf/dispatch [:cube-test.utils.choice-carousel.events/init-choice-carousel parms db]))))
    ; db))
    (init-scene-carousel)))

(defn render-loop []
  (if (= main-scene/xr-mode "vr")
    (controller/tick)
    (controller-xr/tick))
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
  (.runRenderLoop main-scene/engine (fn [] (render-loop))))
