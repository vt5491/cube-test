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
   [babylonjs-loaders :as bjs-l]
   [promesa.core :as p]))
   ; [cube-test.utils.choice-carousel.subs :as cc-subs]))

; (def tmp-text nil)

;; simple insertion into the re-frame db.  Most of the work occurs on the subscribe.
;; (see :choice-carousel.subs:choice-carousel-changed) .hint: it calls cc/init-meshes
(defn init [{:keys [id radius choices colors] :as parms} db]
  (let [db-2 (if (:choice-carousels db)
               db
               (assoc db :choice-carousels []))
        ; _ (prn "cc: db-2=" db-2)
        ccs (conj (:choice-carousels db-2) {:id id :radius radius :choices choices :colors colors})]
        ; _ (prn "cc: ccs=" ccs)]
    (assoc db :choice-carousels ccs)))


  ; (set! green-mat (bjs/StandardMaterial. "green-mat" scene))
  ; (set! (.-diffuseColor green-mat) (bjs/Color3. 0 1 0)))
; :choices [{:id :ff} {:id :cube-spin} {:id :face-slot}]
; (defn create-carousel-plane [parms idx scene])
(defn create-carousel-plane [{:keys [radius theta color] :as parms} idx scene]
  (prn "create-carousel-plane: theta=" theta)
  (let [x-quat-90 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.X (* base/ONE-DEG 90)))
        ; y-quat-theta (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG theta)))
        y-quat-theta (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Y theta))
        ; z-quat-theta (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Z (* base/ONE-DEG theta)))
        z-quat-theta (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Z (+ theta (/ js/Math.PI 1))))
        z-quat-theta-2 (.normalize (bjs/Quaternion.FromEulerAngles 0 0 (* base/ONE-DEG theta)))
        z-quat-90 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Z (* base/ONE-DEG 90)))
        id (name (:id parms))
        plane (bjs/MeshBuilder.CreatePlane.
                id
                ; (name (:id parms))
                (clj->js {:width 3 :height 3
                           :sideOrientation bjs/Mesh.DOUBLESIDE})
               scene)
        plane-mat (bjs/StandardMaterial. (str id "-mat") scene)
        ; cube (bjs/MeshBuilder.CreateBox (str id "-cube" (clj->js {:width 0.5 :height 3}) scene))
        cyl (bjs/MeshBuilder.CreateCylinder (str id "-cyl") (clj->js {:width 0.5 :height 3}) scene)
        cyl-mat (bjs/StandardMaterial. (str id "-cyl" "-mat") scene)
        diffuse-num-text (bjs/Texture. "imgs/top_scene/number_mat.png" scene true true bjs/Texture.BILINEAR_SAMPLINGMODE nil nil nil true)
        _ (prn "z-quat-theta=" z-quat-theta)]
        ; _ (prn "green color=" (bjs/Color3. 0 1 0))]
        ; mesh.rotate(BABYLON.Axis.Z, -Math.PI, BABYLON.Space.LOCAL)];
      ; mesh.setPivotMatrix(BABYLON.Matrix.Translation(x, y, z));
      ; (set! (.-rotationQuaternion plane) x-quat-90)
      ; (.computeWorldMatrix plane true)
      ;; this is basically the same things as an "apply rotate" in blender.
      ; (.setPivotMatrix plane (bjs/Matrix.RotationX (* base/ONE-DEG 90) true))
      ; (set! (.-rotationQuaternion plane) (.multiply x-quat-90 (.scale z-quat-90 0.5)))
      ; (set! (.-rotationQuaternion plane) (.multiply x-quat-90 z-quat-theta-2))
      ; (set! (.-rotationQuaternion plane) (.multiply y-quat-theta x-quat-90))
      ; (set! (.-rotationQuaternion plane) (.multiply x-quat-90 y-quat-theta))
      (set! (.-rotationQuaternion plane) (.multiply x-quat-90 z-quat-theta))
      ; (.rotate plane bjs/Axis.X (* base/ONE-DEG 90 bjs/Space.WORLD))
      ; (.rotate plane bjs/Axis.Y (* base/ONE-DEG -45) bjs/Space.WORLD)
      ; (.rotate plane bjs/Axis.Y theta bjs/Space.WORLD)
      (set! (.-isPickable plane) true)
      ; (set! (.-position plane) (bjs/Vector3.
      ;                            (* radius (js/Math.cos (* idx theta)))
      ;                            0
      ;                            (+ (* radius (js/Math.sin (* idx theta))) radius)))
      (set! (.-position plane) (bjs/Vector3.
                                 (* radius (js/Math.cos theta))
                                 0
                                 (+ (* radius (js/Math.sin theta)) radius)))
      ; (set! (.-diffuseColor plane-mat) color)
      (set! (.-diffuseTexture plane-mat) diffuse-num-text)
      ; (set! (.-diffuseColor plane-mat) (bjs/Color3. 0 1 0))
      (set! (.-material plane) plane-mat)
      ; (set! (.-position cyl) (bjs/Vector3.
      ;                                  (* (+ radius 2) (js/Math.cos (* idx theta)))
      ;                                  0
      ;                                  (+ (* (+ radius 2) (js/Math.sin (* idx theta))) radius)))
      (set! (.-position cyl) (bjs/Vector3.
                                       (* (+ radius 2) (js/Math.cos theta))
                                       0
                                       (+ (* (+ radius 2) (js/Math.sin theta)) radius)))
      (set! (.-diffuseColor cyl-mat) color)
      (set! (.-material cyl) cyl-mat)))


(defn init-meshes [radius choices colors]
  ; (prn "init-meshes: colors=" colors)
  (let [scene main-scene/scene
        n-choices (count choices)
        ; x-quat(.normalize (bjs/Quaternion.RotationAxis bjs/Axis.X (* base/ONE-DEG (/ 360 n-choices))))
        theta-delta (/ (* 360 base/ONE-DEG) n-choices)]
      (doall
        (map-indexed
          (fn [i choice]
            (create-carousel-plane
               {:id (:id choice)
                :radius radius
                :theta (* theta-delta i)
                :color (nth colors i)} i scene))
          choices))))

(defn choice-carousel-gui-loaded [adv-text left-evt right-evt select-evt delta-theta]
  (prn "choice-carousel-gui-loaded: adv-text=" adv-text)
  ; (prn "choice-carousel-gui-loaded: tmp-text=" tmp-text)
  ; (js-debugger)
  (let [select-btn (.getControlByName adv-text "select_btn")
        left-arrow (.getControlByName adv-text "left_arrow_img")
        left-arrow-dbg (.getControlByName adv-text "left_arrow_img_dbg")
        right-arrow (.getControlByName adv-text "right_arrow_img")
        right-arrow-dbg (.getControlByName adv-text "right_arrow_img_dbg")
        ; _ (js-debugger)
        _ (prn "select-btn=" select-btn)
        _ (prn "left-arrow-img=" left-arrow)
        _ (prn "left-arrow-img.source=" (.-source left-arrow))]
        ; _ (set! (.-source right-arrow) "guis/top_scene/left_arrow.png")
        ; _ (set! (.-source right-arrow) "https://localhost:8281/guis/top_scene/left_arrow.png")
        ; _ (prn "right-arrow-img.source=" (.-source right-arrow))]
        ; btn-2 (.getControlByName tmp-text "select_btn")
        ; _ (prn "btn-2=" btn-2)]))
        ; _ (prn "add-train-btn.onPointer=" (.-onPointerClickObservable add-train-btn))
    ;     init-top-ball-btn (.getControlByName cmd-gui-adv-text "init_top_ball_btn")
    ;     init-btm-ball-btn (.getControlByName cmd-gui-adv-text "init_btm_ball_btn")
    ;     toggle-btm-ball-btn (.getControlByName cmd-gui-adv-text "toggle_btm_ball_btn")
    ;     toggle-top-ball-btn (.getControlByName cmd-gui-adv-text "toggle_top_ball_btn")]
    (when select-btn
      (prn "now setting up select-btn")
      (-> select-btn (.-onPointerClickObservable)
        (.add #(rf/dispatch [select-evt])))
      (-> left-arrow (.-onPointerClickObservable)
        (.add #(rf/dispatch [left-evt delta-theta])))
      (-> left-arrow-dbg (.-onPointerClickObservable)
        (.add #(rf/dispatch [left-evt (/ delta-theta 10)])))
      (-> right-arrow (.-onPointerClickObservable)
        (.add #(rf/dispatch [right-evt delta-theta])))
      (-> right-arrow-dbg (.-onPointerClickObservable)
        (.add #(rf/dispatch [right-evt (/ delta-theta 10)]))))))


(defn load-choice-carousel-gui [left-evt right-evt select-evt delta-theta]
  (let [scene main-scene/scene
        plane (bjs/MeshBuilder.CreatePlane "gui-plane" (js-obj "width" 4, "height" 4) scene)
        _ (set! (.-position plane) (bjs/Vector3. 0 3 0))
        _ (.enableEdgesRendering plane)
        _ (set! (.-edgesWidth plane) 1.0)
        ; adv-text (bjs-gui/AdvancedDynamicTexture.CreateForMesh plane 768 768)
        adv-text (bjs-gui/AdvancedDynamicTexture.CreateForMesh plane 1920 1080)]
        ; _ (set! tmp-text adv-text)]
    (-> adv-text
     (.parseFromURLAsync "guis/top_scene/choice_carousel_gui.json")
     (p/then #(choice-carousel-gui-loaded adv-text left-evt right-evt select-evt delta-theta)))))
     ; (p/then partial(choice-carousel-gui-loaded adv-text)))))

(defn rot-meshes [mesh-ids dir theta origin]
  (let [scene main-scene/scene]
        ; m1-id (name (first mesh-ids))
        ; _ (prn "m1-id=" m1-id)
        ; m1 (.getMeshByID scene (name (first mesh-ids)))]
        ; theta (if (= dir :left)
        ;           (* -1 app-carousel-theta-width)
        ;           app-carousel-theta-width)]
      ; (.rotateAround m1 origin bjs/Axis.Y theta)
      (doall (map #(let [mesh (.getMeshByID scene (name %))
                         mesh-cyl (.getMeshByID scene (str (name %) "-cyl"))
                         x-quat-theta (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.X (* base/ONE-DEG theta)))
                         y-quat-theta (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG theta)))
                         z-quat-theta (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Z (* base/ONE-DEG theta)))
                         y-quat-0 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 0)))
                         z-quat-0 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Z (* base/ONE-DEG 0)))
                         z-quat-90 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Z (* base/ONE-DEG 90)))]
                         ; tmp (.-rotationQuaternion mesh)
                         ; _ (prn "tmp=" tmp)]
                      ; (js-debugger)
                      (.rotateAround mesh origin bjs/Axis.Y theta)
                      (.rotateAround mesh-cyl origin bjs/Axis.Y theta))
                      ; (set! (.-rotationQuaternion mesh) y-quat-0)
                      ; (set! (.-rotationQuaternion mesh) y-quat-0)
                      ; (.multiplyInPlace (.-rotationQuaternion mesh) z-quat-theta)
                      ; (.multiplyInPlace (.-rotationQuaternion mesh) z-quat-0)
                      ; (.multiplyInPlace (.-rotationQuaternion mesh) z-quat-90))
                  mesh-ids))))
