;; reference few, accessible by many.
(ns cube-test.scenes.skyscrapers-scene
  ; (:require-macros [cube-test.macros :as macros])
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [babylonjs-gui :as bjs-gui]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.utils.fps-panel :as fps-panel]
   [cube-test.base :as base]
   [cube-test.utils :as utils]
   [cube-test.utils.box-grid :as box-grid]))

(declare init-empire-state-bldg)
(declare init-transamerica-bldg)
(declare init-encore-las-vegas)

;; model loading
(defn empire-state-bldg-loaded [meshes particle-systems skeletons anim-groups name user-cb]
  (println "empire-state-bldg-loaded ")
  (doall (map #(do
                 (when (re-matches #"__root__" (.-id %1))
                     (set! (.-name %1) name)
                     (set! (.-id %1) name)
                     (set! (.-scaling %1) (bjs/Vector3. 0.01 0.01 0.01))
                     ;; the neg. on the x is needed otherwise the materials are "flipped"
                     (set! (.-position %1)(bjs/Vector3. 2 0 0))))
              meshes))
  (when user-cb (user-cb name)))

(defn tmp-loaded [meshes particle-systems skeletons anim-groups name user-cb]
  (println "tmp-loaded ")
  (doall (map #(do
                 (when (re-matches #"__root__" (.-id %1))
                     (set! (.-name %1) name)
                     (set! (.-id %1) name)
                     ; (set! (.-scaling %1) (bjs/Vector3. 0.01 0.01 0.01))
                     ;; the neg. on the x is needed otherwise the materials are "flipped"
                     (set! (.-position %1)(bjs/Vector3. 2 5 -1))))
              meshes)))
  ; (when user-cb (user-cb name)))

(defn transamerica-bldg-loaded [meshes particle-systems skeletons anim-groups name user-cb]
  (println "transamerica-bldg-loaded")
  (doall (map #(do
                 (when (re-matches #"__root__" (.-id %1))
                     (set! (.-name %1) name)
                     (set! (.-id %1) name)
                     (set! (.-scaling %1) (bjs/Vector3. 0.01 0.01 0.01))
                     ;; the neg. on the x is needed otherwise the materials are "flipped"
                     (set! (.-position %1)(bjs/Vector3. 0 0 0))))
              meshes))
  (when user-cb (user-cb name)))

(defn encore-las-vegas-loaded [meshes particle-systems skeletons anim-groups name user-cb]
  (println "encore-las-vegas-loaded"))

(defn model-loaded [meshes particle-systems skeletons anim-groups name pos user-cb]
  (println "generic model loaded")
  ; (js-debugger)
  (doall (map-indexed #(do
                         ; (println "map-indexed, idx=" %1)
                         (let [new-mesh (.toLeftHanded %2)
                               idx %1]
                           (when (re-matches #"__root__" (.-id %2))
                             ; (let [new-mesh (.toLeftHanded %2)])
                             (set! (.-name new-mesh) name)
                             (set! (.-id new-mesh) name)
                             ; (set! %1 (.toLeftHanded %1))
                             (set! (.-scaling new-mesh) (bjs/Vector3. 0.01 0.01 0.01))
                             ;; the neg. on the x is needed otherwise the materials are "flipped"
                             ; (set! (.-position %1)(bjs/Vector3. 0 0 0))
                             (set! (.-position new-mesh) pos)
                             (set! (.-rotationQuaternion new-mesh) (bjs/Quaternion.Zero)))
                           (aset meshes idx new-mesh)))
              meshes))
  (when user-cb (user-cb name)))

(defn load-empire-state-bldg [path file name user-cb]
  (.ImportMesh bjs/SceneLoader ""
               path
               file
               main-scene/scene
               #(empire-state-bldg-loaded %1 %2 %3 %4 name user-cb)))

; (defn load-tmp [path file name user-cb]
;   (.ImportMesh bjs/SceneLoader ""
;                path
;                file
;                main-scene/scene
;                #(tmp-loaded %1 %2 %3 %4 name user-cb)))

(defn load-transamerica-bldg [path file name user-cb]
  (.ImportMesh bjs/SceneLoader ""
               path
               file
               main-scene/scene
               #(transamerica-bldg-loaded %1 %2 %3 %4 name user-cb)))

(defn load-model
  ([path file name pos on-loaded-cb]
   (load-model path file name pos on-loaded-cb nil))
  ([path file name pos on-loaded-cb user-cb]
   (.ImportMesh bjs/SceneLoader ""
                path
                file
                main-scene/scene
                ; #(empire-state-bldg-loaded %1 %2 %3 %4 name user-cb)
                #(on-loaded-cb %1 %2 %3 %4 name pos user-cb))))


; (defn load-model [path file name loaded-cb])
(defn load-models []
  (load-model
   "models/skyscrapers/home_insurance_bldg/"
   "home_insurance_bldg.glb"
   "home_insurance_bldg"
   (bjs/Vector3. -7 0 0)
   model-loaded)
  (load-model
   "models/skyscrapers/encore_las_vegas/"
   "encore_las_vegas.glb"
   "encore_las_vegas"
   (bjs/Vector3. -5 0 3)
   ; encore-las-vegas-loaded
   model-loaded)
   ; init-encore-las-vegas))
  (load-model
   "models/skyscrapers/luxor_las_vegas/"
   "luxor_pyr.glb"
   "luxor"
   (bjs/Vector3. -5 0 0)
   model-loaded)
  (load-model
   "models/skyscrapers/great_pyramid_giza/"
   "great_pyramid_giza.glb"
   "great_pyramid_giza"
   (bjs/Vector3. -2 0 0)
   model-loaded)
  ;; vt add
  (load-model
   "models/tmp/"
   "procedural_1_plane.glb"
   "tmp"
   (bjs/Vector3. -2 0 0)
   tmp-loaded)
  ;; vt end
  (load-transamerica-bldg
   "models/skyscrapers/transamerica_bldg/"
   "transamerica_bldg.glb"
   "transamerica-bldg"
   init-transamerica-bldg)
  (load-empire-state-bldg
   "models/skyscrapers/empire_state_bldg/"
   "empire_state_bldg.glb"
   "empire-state-bldg"
   init-empire-state-bldg)
  ; (load-empire-state-bldg
  ;  "models/tmp/"
  ;  "procedural_1_plane.glb"
  ;  "tmp"
  ;  init-empire-state-bldg)
  (load-model
     "models/skyscrapers/sears_tower/"
     "sears_tower.glb"
     "sears_tower"
     (bjs/Vector3. 4 0 0)
     model-loaded))

(defn release []
  (prn "skyscrapers-release: entered")
  (utils/release-common-scene-assets))
  ;; (.removeAllFromScene hemisferic-asset-container))

;; inits
(defn init-empire-state-bldg [mesh-name]
  (println "now in init-empire-state-bldg, mesh-name=" mesh-name))

(defn init-transamerica-bldg [mesh-name]
  (println "now in init-transamerica-bldg, mesh-name=" mesh-name))

(defn init-encore-las-vegas [mesh-name]
  (println "now in init-transamerica-bldg, mesh-name=" mesh-name))

(defn init []
  (println "skyscraper.init: entered")
  (let [light (bjs/PointLight. "pointLight" (bjs/Vector3. 0 5 -3) main-scene/scene)]
    (.setEnabled light true))
  ; (swap! *active-triplet* (fn [x] "vat-cube"))
  (load-models)
  (main-scene/load-main-gui release)
  (let [grnd (.getMeshByID main-scene/scene "ground")]
    (when grnd
      (.setEnabled grnd true)
      (set! (.-isVisible grnd) true)))
  (main-scene/init-env))
  ; (load-empire-state-bldg
  ;  "models/skyscrapers/empire_state_bldg/"
  ;  "empire_state_bldg.glb"
  ;  ; "empire_state_bldg.gltf"
  ;  "empire-state-bldg"
  ;  init-empire-state-bldg))

;; render
(defn render-loop []
  ; (controller-xr/tick)
  (if (= main-scene/xr-mode "vr")
    (controller/tick)
    (controller-xr/tick))
  (fps-panel/tick main-scene/engine)
  (main-scene/tick)
  (.render main-scene/scene))

(defn run-scene []
  (println "skyscrapers-scene.run-scene: entered")
  (.runRenderLoop main-scene/engine (fn [] (render-loop))))
