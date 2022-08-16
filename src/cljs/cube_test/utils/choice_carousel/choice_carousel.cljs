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

(defn init [{:keys [id radius choices] :as parms} db]
  (let [db-2 (if (:choice-carousels db)
               db
               (assoc db :choice-carousels []))
        ; _ (prn "cc: db-2=" db-2)
        ccs (conj (:choice-carousels db-2) {:id id :radius radius :choices choices})]
        ; _ (prn "cc: ccs=" ccs)]
    (assoc db :choice-carousels ccs)))

; :choices [{:id :ff} {:id :cube-spin} {:id :face-slot}]
; (defn create-carousel-plane [parms idx scene])
(defn create-carousel-plane [{:keys [radius theta] :as parms} idx scene]
  (let [x-quat-90 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.X (* base/ONE-DEG 90)))
        plane (bjs/MeshBuilder.CreatePlane.
                (name (:id parms))
                (clj->js {:width 3 :height 3
                           :sideOrientation bjs/Mesh.DOUBLESIDE})
               scene)]
      (set! (.-rotationQuaternion plane) x-quat-90)
      (set! (.-isPickable plane) true)
      (set! (.-position plane) (bjs/Vector3.
                                 (* radius (js/Math.cos (* idx theta)))
                                 0
                                 (+ (* radius (js/Math.sin (* idx theta))) radius)))))

(defn init-meshes [radius choices]
  (let [scene main-scene/scene
        n-choices (count choices)
        ; x-quat(.normalize (bjs/Quaternion.RotationAxis bjs/Axis.X (* base/ONE-DEG (/ 360 n-choices))))
        theta (/ (* 360 base/ONE-DEG) n-choices)]
      (doall
        (map-indexed
          (fn [i choice]
            (create-carousel-plane
               {:id (:id choice)
                :radius radius
                :theta theta} i scene))
          choices))))

(defn choice-carousel-gui-loaded [adv-text left-evt right-evt select-evt]
  (prn "choice-carousel-gui-loaded: adv-text=" adv-text)
  ; (prn "choice-carousel-gui-loaded: tmp-text=" tmp-text)
  ; (js-debugger)
  (let [select-btn (.getControlByName adv-text "select_btn")
        left-arrow (.getControlByName adv-text "left_arrow_img")
        right-arrow (.getControlByName adv-text "right_arrow_img")
        ; _ (js-debugger)
        _ (prn "select-btn=" select-btn)
        _ (prn "left-arrow-img=" left-arrow)]
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
        (.add #(rf/dispatch [left-evt])))
      (-> right-arrow (.-onPointerClickObservable)
        (.add #(rf/dispatch [right-evt]))))))


(defn load-choice-carousel-gui [left-evt right-evt select-evt]
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
     (p/then #(choice-carousel-gui-loaded adv-text left-evt right-evt select-evt)))))
     ; (p/then partial(choice-carousel-gui-loaded adv-text)))))
