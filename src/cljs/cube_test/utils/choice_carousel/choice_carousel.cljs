;; This is the front-end and bjs facing code for the
;; gui widget 'choice-carousel'
(ns cube-test.utils.choice-carousel.choice-carousel
  (:require
   [re-frame.core :as rf]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [babylonjs-gui :as bjs-gui]
   [babylonjs-materials :as bjs-m]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.utils.fps-panel :as fps-panel]
   ; [cube-test.utils.choice-carousel.events :as cc-events]
   [cube-test.base :as base]
   [cube-test.utils :as utils]
   [cube-test.utils.common :as common]
   [babylonjs-loaders :as bjs-l]
   [promesa.core :as p]))
   ; [cube-test.utils.choice-carousel.subs :as cc-subs]))

(declare choice-model-container-loaded)
(declare get-choice-idx-by-id)
(def cc-rot-snd)
(def focused-app-idx 1)

(defn init-snd [{:keys [rot-snd-file] :as parms}]
  (set! cc-rot-snd (bjs/Sound.
                    "cc-rot-snd"
                    rot-snd-file
                    main-scene/scene)))

(defn play-rot-snd []
  (.play cc-rot-snd))

(defn create-carousel-plane [{:keys [radius theta color] :as parms} idx scene]
  (let [x-quat-90 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.X (* base/ONE-DEG 90)))
        ; z-quat-theta (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Z (+ theta (/ js/Math.PI 1))))
        z-quat-theta (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Z theta))
        id (name (:id parms))
        plane (bjs/MeshBuilder.CreatePlane.
                id
                (clj->js {:width 3 :height 3
                           :sideOrientation bjs/Mesh.DOUBLESIDE})
               scene)
        plane-mat (bjs/StandardMaterial. (str id "-mat") scene)
        cyl (bjs/MeshBuilder.CreateCylinder (str id "-cyl") (clj->js {:width 0.5 :height 3}) scene)
        cyl-mat (bjs/StandardMaterial. (str id "-cyl" "-mat") scene)
        diffuse-num-text (bjs/Texture. "imgs/top_scene/number_mat.png" scene true true bjs/Texture.BILINEAR_SAMPLINGMODE nil nil nil true)]
      ;; this is basically the same things as an "apply rotate" in blender.
      (set! (.-rotationQuaternion plane) (.multiply x-quat-90 z-quat-theta))
      (set! (.-isPickable plane) true)
      (set! (.-position plane) (bjs/Vector3.
                                 (* radius (js/Math.cos theta))
                                 0
                                 (+ (* radius (js/Math.sin theta)) radius)))
      (set! (.-diffuseTexture plane-mat) diffuse-num-text)
      (set! (.-material plane) plane-mat)
      (set! (.-position cyl) (bjs/Vector3.
                                       (* (+ radius 2) (js/Math.cos theta))
                                       0
                                       (+ (* (+ radius 2) (js/Math.sin theta)) radius)))
      (set! (.-diffuseColor cyl-mat) color)
      (set! (.-material cyl) cyl-mat)))


;; called by a subscribe (reg-sub choice-carousels-changed)
;  defn init-meshes [radius choices colors]
(defn init-meshes [radius choices colors focused-app-idx]
  (let [scene main-scene/scene
        n-choices (count choices)
        theta-delta (/ (* 360 base/ONE-DEG) n-choices)]
      (prn "cc.init-meshes: focused-app-idx=" focused-app-idx)
      (doall
        (map-indexed
          (fn [i choice]
              ; (let [shifted-idx (mod (+ i 6 focused-app-idx) n-choices)])
              ;; the -2 is a total hack here.  The focused app is the left not the front
              ;; the -2 is a magic number that works with 7 apps, but can't say for sure it will
              ;; work with all values of n.
              ; (let [shifted-idx (mod (+ i focused-app-idx -2) n-choices)])
              (let [shifted-idx (mod (+ i focused-app-idx 1) n-choices)
                    theta (* theta-delta shifted-idx)
                    ; theta-2 (- (* theta-delta i) (* 45 base/ONE-DEG))
                    theta-2 (- (* theta-delta i) (* (+ 2 focused-app-idx) theta-delta))
                    _ (prn "init-meshes: shifted-idx=" shifted-idx ",theta(rad)=" theta ",theta(deg)" (/ theta base/ONE-DEG))
                    _ (prn "init-meshes: choice.top-level-scene=" (:top-level-scene choice))
                    _ (prn "init-meshes: theta-2=" (/ theta-2 base/ONE-DEG))]
                (prn "cc.init-meshes: shifted-idx=" shifted-idx)
                (create-carousel-plane
                 {:id (:id choice)
                  :radius radius
                  ; :theta (* theta-delta i)
                  ; :theta (* theta-delta shifted-idx)
                  :theta theta-2
                  ; :theta (* theta-delta shifted-idx -1)
                  ; :color (nth colors i)} i scene
                  :color (nth colors i)}
                 i scene)))
          choices))))

; (defn choice-model-loaded [user-parms meshes particle-systems skeletons anim-groups transform-nodes geometries lights user-cb]
;   ;; promote into a re-frame call so we can get access to the rf db.
;   (rf/dispatch [:cube-test.utils.choice-carousel.events/choice-model-loaded-rf user-parms meshes particle-systems skeletons anim-groups transform-nodes geometries lights user-cb]))

;; same as choice-model-loaded except we also have a db.
; (defn choice-model-loaded-rf [db user-parms meshes particle-systems skeletons anim-groups name user-cb])
; (defn choice-model-loaded-rf [db user-parms meshes particle-systems skeletons anim-groups transform-nodes geometries lights user-cb])
;; Note: works, but defunct (replaced by choice-model-container-loaded)
; (defn choice-model-loaded [user-parms meshes particle-systems skeletons anim-groups transform-nodes geometries lights user-cb]
;   (let [scene main-scene/scene
;         root-mesh (first meshes)
;         root-mesh-id (.-id root-mesh)
;         choice (:choice user-parms)
;         choice-id (name (:id choice))
;         new-id (str choice-id "-root")
;         scale (:scale choice)
;         plane-mesh (.getMeshByID scene choice-id)
;         cyl-mesh (.getMeshByID scene (str choice-id "-cyl"))
;         x-quat-neg-90 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.X (* base/ONE-DEG -90)))]
;     (when (= root-mesh-id "__root__")
;       (set! (.-name root-mesh) new-id)
;       (set! (.-id root-mesh) new-id)
;       (when scale
;         (.scaleInPlace (.-scaling root-mesh) scale))
;       (set! (.-position root-mesh) (.-position plane-mesh))
;       ;;TODO: parameterize this to allow for non-rotating models as well.
;       ;; undo the plane "flat" rot, so model will display properly.
;       (set! (.-rotationQuaternion plane-mesh) (.multiply (.-rotationQuaternion plane-mesh) x-quat-neg-90))
;       (set! (.-rotationQuaternion root-mesh) (.-rotationQuaternion plane-mesh))
;       ;; hide the default meshes used to denote this choice (since we now have a full model loaded)
;       (.setEnabled plane-mesh false)
;       (.setEnabled cyl-mesh false))
;     (prn "count meshes=" (count meshes))
;     (when (= choice-id "face-slot")
;       (doall (map #(do
;                      (prn "mesh-id=" (.-id %1))
;                      (when (not (re-matches #".*root.*" (.-id %1)))
;                        (.push (.-meshes cube-test.top-scene.top-scene.keep-assets) %1)))
;                 meshes)))))

;; called by a subscription, upon insertion into the db of ':choice-carousels'.
;; Note: works, but defunct (replaced by init-model-containers)
; (defn init-models [choices]
;   (let [scene main-scene/scene]
;     (doall
;       (map
;         (fn [choice]
;           (prn "choice" choice)
;           (when-let [model-file (:model-file choice)]
;             (let [path-file (common/extract-path-file model-file)
;                   path (:path path-file)
;                   file (:file path-file)]
;               (if (or (= (:id choice) :face-slot) (= (:id choice) :geb-cube))
;                 (.LoadAssetContainer bjs/SceneLoader
;                                      path file scene
;                                      (partial choice-model-container-loaded {:choice choice}))
;                 (utils/load-model path file main-scene/scene (partial choice-model-loaded {:choice choice}))))))
;         choices))))

(defn choice-model-container-loaded [user-parms ac]
  (let [scene main-scene/scene
        choice (:choice user-parms)
        choices (:choices user-parms)
        choice-id (:id choice)
        choice-id-name (name choice-id)
        idx (get-choice-idx-by-id choices choice-id)]
    (.createRootMesh ac)
    (.addAllToScene ac)
    (rf/dispatch [:cube-test.utils.choice-carousel.events/update-choice idx (assoc choice :asset-container ac)])
    (let [root (.getMeshByName scene "assetContainerRootMesh")
          scale (:scale choice)
          plane-mesh (.getMeshByID scene choice-id-name)
          cyl-mesh (.getMeshByID scene (str choice-id-name "-cyl"))
          x-quat-neg-90 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.X (* base/ONE-DEG -90)))]
      (set! (.-name root) (str (name choice-id) "-root"))
      (set! (.-id root) (str (name choice-id) "-root"))
      (when scale
        (.scaleInPlace (.-scaling root) scale))
      (set! (.-position root) (.-position plane-mesh))
      (set! (.-rotationQuaternion plane-mesh) (.multiply (.-rotationQuaternion plane-mesh) x-quat-neg-90))
      (set! (.-rotationQuaternion root) (.-rotationQuaternion plane-mesh))
      (.setEnabled plane-mesh false)
      (.setEnabled cyl-mesh false))))

;;TODO: pretty sure I don't need db here e.g can be a direct call (bypassing re-frame)
(defn init-model-containers [db]
  (let [scene main-scene/scene
        ;; TODO parameterize cc array index
        choices (get-in db [:choice-carousels 0 :choices])]
    (doall
      (map
        (fn [choice]
          (when-let [model-file (:model-file choice)]
            (let [path-file (common/extract-path-file model-file)
                  path (:path path-file)
                  file (:file path-file)]
              (.LoadAssetContainer bjs/SceneLoader
                                   path file scene
                                   (partial choice-model-container-loaded {:choice choice :choices choices})))))
        choices)))
  db)


(defn choice-carousel-gui-loaded [adv-text left-evt right-evt select-evt delta-theta]
  (let [select-btn (.getControlByName adv-text "select_btn")
        left-arrow (.getControlByName adv-text "left_arrow_img")
        left-arrow-dbg (.getControlByName adv-text "left_arrow_img_dbg")
        right-arrow (.getControlByName adv-text "right_arrow_img")
        right-arrow-dbg (.getControlByName adv-text "right_arrow_img_dbg")]
    (when select-btn
      (prn "now setting up select-btn")
      (-> select-btn (.-onPointerClickObservable)
        (.add select-evt))
      (-> left-arrow (.-onPointerClickObservable)
        (.add left-evt))
      ; (-> left-arrow-dbg (.-onPointerClickObservable)
      ;   (.add #(rf/dispatch [left-evt (/ delta-theta 10)])))
      (-> right-arrow (.-onPointerClickObservable)
        (.add right-evt)))))
      ; (-> right-arrow-dbg (.-onPointerClickObservable)
      ;   (.add #(rf/dispatch [right-evt (/ delta-theta 10)]))))))


(defn load-choice-carousel-gui [left-evt right-evt select-evt delta-theta]
  (let [scene main-scene/scene
        ; plane (bjs/MeshBuilder.CreatePlane "gui-plane" (js-obj "width" 4, "height" 4) scene)
        plane (bjs/MeshBuilder.CreatePlane "gui-plane" (js-obj "width" 6, "height" 3) scene)
        ; _ (set! (.-position plane) (bjs/Vector3. 0 3 0))
        ; _ (.enableEdgesRendering plane)
        ; _ (set! (.-edgesWidth plane) 1.0)
        adv-text (bjs-gui/AdvancedDynamicTexture.CreateForMesh plane 1920 1080)]
    (set! (.-position plane) (bjs/Vector3. 0 2 0))
    (-> adv-text
     ; (.parseFromURLAsync "guis/top_scene/choice_carousel_gui.json")
     (.parseFromURLAsync "guis/top_scene/choice_carousel_gui_2.json")
     (p/then #(choice-carousel-gui-loaded adv-text left-evt right-evt select-evt delta-theta)))))

(defn rot-meshes [mesh-ids dir theta origin]
  (let [scene main-scene/scene]
      (doall (map #(let [mesh (.getMeshByID scene (name %))
                         mesh-cyl (.getMeshByID scene (str (name %) "-cyl"))
                         x-quat-theta (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.X (* base/ONE-DEG theta)))
                         y-quat-theta (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG theta)))
                         z-quat-theta (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Z (* base/ONE-DEG theta)))
                         y-quat-0 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 0)))
                         z-quat-0 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Z (* base/ONE-DEG 0)))
                         z-quat-90 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Z (* base/ONE-DEG 90)))]
                      (.rotateAround mesh origin bjs/Axis.Y theta)
                      (.rotateAround mesh-cyl origin bjs/Axis.Y theta))
                  mesh-ids))))

(defn switch-app [top-level-scene]
  (let [scene main-scene/scene
        engine main-scene/engine]
   (.stopRenderLoop engine)
   (.dispose scene)
   (cube-test.core.init top-level-scene)))

;; Return the index into the choice array by id.
(defn get-choice-idx-by-id [choices id]
  (->> choices
       (map-indexed (fn [i choice]
                      (if (= (:id choice) id)
                        i
                        nil)))
      (filter #(not (nil? %)))
      (first)))

;; this is the opposite of 'init'.  Clean up and release any resources as we
;; switch to a new logical scene.
(defn release []
  (cube-test.utils.choice-carousel.subs.release))

;; simple insertion into the re-frame db.  Most of the work occurs on the subscribe.
;; (see :choice-carousel.subs:choice-carousel-changed) .hint: it calls cc/init-meshes
(defn init [{:keys [id radius choices colors] :as parms} db]
  (init-snd {:rot-snd-file "sounds/top_scene/plastic_swipe.ogg"})
  (let [db-2 (if (:choice-carousels db)
               db
               (assoc db :choice-carousels []))
        ccs (conj (:choice-carousels db-2) {:id id :radius radius :choices choices :colors colors})]
    (assoc db :choice-carousels ccs)))