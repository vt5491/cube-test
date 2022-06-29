(ns cube-test.frig-frog.ball
   (:require
     [re-frame.core :as rf]
     [babylonjs :as bjs]
     [cube-test.main-scene :as main-scene]
     [cube-test.base :as base]))

; (defn draw-ball [ball]
;   (let  [row (:row ball)
;         col (:col ball)
;         scene main-scene/scene
;         mesh (bjs/Mesh.CreatePolyhedron. "frog-2" (js-obj "type" 0 "size" 0.5) scene)]
;     (set! (.-position mesh) (bjs/Vector3. (* col 1.2) 1.5 (* row 1.2)))))
(defn get-mesh-pos [id]
  (.-position (.getMeshByID main-scene/scene id)))

(defn draw-ball
  ([id x y]  (draw-ball id x cube-test.frig-frog.board/board-height y))
  ;; note: with 3-d we use the bjs actual x,y,z not pretend "y" coordinates when only using 2
  ([id x y z]
   ; (prn "ball.rules: draw-ball: x=" x ",y=" y ",z=" z)
   (let [scene main-scene/scene
         mesh (or (.getMeshByID scene id) (bjs/Mesh.CreatePolyhedron. id (js-obj "type" 3 "size" 0.5) scene))]
     (set! (.-position mesh) (bjs/Vector3. x y z)))))
     ; (js-debugger))))

; (defn draw-ball-2
;   ([id x y] (draw-ball id x cube-test.frig-frog.board/board-height y))
;   ([id x y z]
;    (let [scene main-scene/scene
;          mesh (or (.getMeshByID scene id) (bjs/Mesh.CreatePolyhedron. id (js-obj "type" 3 "size" 0.5) scene))]
;      (set! (.-position mesh) (bjs/Vector3. x 1.5 y)))))

; (defn move-ball [id dx dy]
;   (let [scene main-scene/scene
;         mesh (.getMeshByID scene id)
;         pos (.-position mesh)]
;       (draw-ball id (+ (.-x pos) dx) (.-y pos) (+ (.-z pos) dy))))

(defn move-ball [id dx dy]
  (let [scene main-scene/scene
        mesh (.getMeshByID scene id)]
      (when mesh
         (let [pos (.-position mesh)
               new-x (+ (.-x pos) dx)
               new-y (+ (.-z pos) dy)]
            (draw-ball id new-x (.-y pos) new-y)))))
