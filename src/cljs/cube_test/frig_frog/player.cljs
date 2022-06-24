(ns cube-test.frig-frog.player
   (:require
     [re-frame.core :as rf]
     [babylonjs :as bjs]
     [cube-test.main-scene :as main-scene]
     [cube-test.base :as base]))

(def jumped)

(defn draw-player
  ([id x y]  (draw-player id x cube-test.frig-frog.board/board-height y))
  ;; note: with 3-d we use the bjs actual x,y,z not pretend "y" coordinates when only using 2
  ([id x y z]
   (prn "draw-player: id=" id)
   (let [scene main-scene/scene
         ; mesh (or (.getMeshByID scene id) (bjs/Mesh.CreatePolyhedron. id (js-obj "type" 7 "size" 0.5) scene))
         ; mesh (or (.getMeshByID scene id) (bjs/Mesh.CreateCylinder. id (js-obj "tessellation" 8 "height" 0.5) scene))
         ; mesh (or (.getMeshByID scene id) (bjs/MeshBuilder.CreateCylinder. id (js-obj "tessellation" 6 "height" 0.7) scene))
         mesh (or (.getMeshByID scene "player") (bjs/MeshBuilder.CreateCylinder. "player" (js-obj "tessellation" 6 "height" 0.7) scene))]
     (set! (.-position mesh) (bjs/Vector3. x y z)))))

(defn move-player [id dx dy]
  (prn "move-player: dx=" dx ",dy=" dy)
  (let [scene main-scene/scene
        mesh (.getMeshByID scene id)
        pos (.-position mesh)]
      (draw-player id (+ (.-x pos) dx) (.-y pos) (+ (.-z pos) dy))))

; (defn jump-player-ctrl [x-val y-val]
;     (cond
;       (and (> y-val 0.5) (not jumped))
;       (do
;        (prn "player: jump bwd")
;        (set! jumped true)
;        (re-frame/dispatch [:cube-test.frig-frog.events/jump-frog -1 0]))
;       (and (< y-val -0.5) (not jumped))
;       (do
;         (prn "player: jump fwd")
;         (set! jumped true)
;         (re-frame/dispatch [:cube-test.frig-frog.events/jump-frog 1 0]))))
