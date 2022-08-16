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

(def default-db
  {:top-scene-abc 7})

(def app-carousel-parms {:radius 16.0})

(defn init-app-planes []
  (let [scene main-scene/scene
        n_apps 4
        ff-top-plane (bjs/MeshBuilder.CreatePlane "ff-top-plane"
                                        (clj->js {:width 3 :height 3
                                                  :sideOrientation bjs/Mesh.DOUBLESIDE})
                                        scene)
        cube-spin-plane (bjs/MeshBuilder.CreatePlane "cube-spin-plane"
                                              (clj->js {:width 3 :height 3
                                                        :sideOrientation bjs/Mesh.DOUBLESIDE})
                                              scene)
        face-slot-plane (bjs/MeshBuilder.CreatePlane "face-slot-plane"
                                              (clj->js {:width 3 :height 3
                                                        :sideOrientation bjs/Mesh.DOUBLESIDE})
                                              scene)
        vrubik-plane (bjs/MeshBuilder.CreatePlane "vrubik-plane"
                                  (clj->js {:width 3 :height 3
                                            :sideOrientation bjs/Mesh.DOUBLESIDE})
                                  scene)
        x-quat-90 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.X (* base/ONE-DEG 90)))]
    (set! (.-rotationQuaternion ff-top-plane) x-quat-90)
    (set! (.-rotationQuaternion cube-spin-plane) x-quat-90)
    (set! (.-rotationQuaternion face-slot-plane) x-quat-90)
    (set! (.-rotationQuaternion vrubik-plane) x-quat-90)
    (set! (.-isPickable ff-top-plane) true)
    (set! (.-isPickable cube-spin-plane) true)
    (set! (.-isPickable face-slot-plane) true)
    (set! (.-isPickable vrubik-plane) true)

    (let [r (:radius app-carousel-parms)
          theta (/ (* 360 base/ONE-DEG) n_apps)]
      (prn "theta=" theta)
      (set! (.-position cube-spin-plane) (bjs/Vector3. (* r (js/Math.cos (* 1 theta))) 0 (+ (* r (js/Math.sin (* 1 theta))) r)))
      (set! (.-position face-slot-plane) (bjs/Vector3. (* r (js/Math.cos (* 2 theta))) 0 (+ (* r (js/Math.sin (* 2 theta))) r)))
      (set! (.-position vrubik-plane) (bjs/Vector3. (* r (js/Math.cos (* 4 theta))) 0 (+ (* r (js/Math.sin (* 4 theta))) r))))))


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
(defn model-loaded [user-parms meshes particle-systems skeletons anim-groups name user-cb]
  ; (println "model-loaded")
  (let [root-prfx (:root-prfx user-parms)
        scale (:scale user-parms)
        delta-pos (:delta-pos user-parms)]
    (doall (map #(do
                   ; (prn "model-loaded: id %1=" (.-id %1) ",root-prfx=" root-prfx)
                   (when (re-matches #"__root__" (.-id %1))
                         ; (set! (.-name %1) name)
                         ; (set! (.-id %1) name)
                         ; (set! (.-name %1) "root")
                         ; (set! (.-id %1) "root")
                         (set! (.-name %1) root-prfx)
                         (set! (.-id %1) root-prfx)
                         (.scaleInPlace (.-scaling %1) scale)
                         (when delta-pos
                           (set! (.-position %1) (.add (.-position %1) delta-pos)))))
                meshes))))

(defn load-model [path file name user-cb]
  (.ImportMesh bjs/SceneLoader ""
               path
               file
               main-scene/scene
               user-cb))

(defn init-scene-carousel []
  (prn "top-scene.init-scene-carousel entered")
  (cc/load-choice-carousel-gui
   :cube-test.top-scene.events/app-left
   :cube-test.top-scene.events/app-right
   :cube-test.top-scene.events/app-selected)
  (let [choices [{:id :ff}
                 {:id :cube-spin}
                 {:id :face-slot}
                 {:id :vrubik}
                 {:id :geb-cube}
                 {:id :twizzlers}
                 {:id :beat-club}]
        parms {:id :app-cc :radius 16.0 :choices choices}]
    (rf/dispatch [:cube-test.utils.choice-carousel.events/init-choice-carousel parms])))

(defn init [db]
  ; (init-app-planes)
  (let [scene main-scene/scene
        tweak-partial (partial utils/tweak-xr-view -10 10 10)
        light1 (bjs/PointLight. "pointLight-1" (bjs/Vector3. 0 2 5) scene)
        light2 (bjs/PointLight. "pointLight-2" (bjs/Vector3. 10 2 5) scene)]
    (set! (.-isVisible (.getMeshByID main-scene/scene "ground")) false)
    (set! (.-isVisible (.getMeshByID main-scene/scene "BackgroundSkybox")) false)
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
  (.render main-scene/scene))

(defn run-scene []
  (.runRenderLoop main-scene/engine (fn [] (render-loop))))
