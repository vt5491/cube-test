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
   [babylonjs-loaders :as bjs-l]))
   ; [cube-test.utils.choice-carousel.subs :as cc-subs]))


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
