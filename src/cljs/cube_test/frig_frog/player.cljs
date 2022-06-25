(ns cube-test.frig-frog.player
   (:require
     [re-frame.core :as rf]
     [babylonjs :as bjs]
     [cube-test.main-scene :as main-scene]
     [cube-test.base :as base]))

(def jumped false)
(def open-for-service true)

;; absolute move.
(defn move-player-to
  ([mesh-id x y]  (move-player-to mesh-id x cube-test.frig-frog.board/board-height y))
  ;; note: with 3-d we use the bjs actual x,y,z not pretend "y" coordinates when only using 2
  ([mesh-id x y z]
   (prn "player: move-player-to: mesh-id=" mesh-id)
   (let [scene main-scene/scene
         ; mesh (or (.getMeshByID scene id) (bjs/Mesh.CreatePolyhedron. id (js-obj "type" 7 "size" 0.5) scene))
         ; mesh (or (.getMeshByID scene id) (bjs/Mesh.CreateCylinder. id (js-obj "tessellation" 8 "height" 0.5) scene))
         ; mesh (or (.getMeshByID scene id) (bjs/MeshBuilder.CreateCylinder. id (js-obj "tessellation" 6 "height" 0.7) scene))
         mesh (or (.getMeshByID scene "player") (bjs/MeshBuilder.CreateCylinder. "player" (js-obj "tessellation" 6 "height" 0.7) scene))]
     (set! (.-position mesh) (bjs/Vector3. x y z)))))

;; relative move.
(defn move-player-delta [mesh-id dx dy]
  (prn "move-player-delta: dx=" dx ",dy=" dy)
  (let [scene main-scene/scene
        mesh (.getMeshByID scene mesh-id)
        pos (.-position mesh)]
      (move-player-to mesh-id (+ (.-x pos) dx) (.-y pos) (+ (.-z pos) dy))))

(defn jump-player-ctrl [id x-val y-val]
    (prn "player.jump-player-ctrl: id=" id ",x=" x-val ",y=" y-val ",jumped=" jumped)
    (cond
      (and (> y-val 0.5) (not jumped))
      ; (and (> y-val 0.5) true)
      (do
       (prn "player: jump bwd")
       (set! jumped true)
       ; (re-frame/dispatch [:cube-test.frig-frog.events/jump-frog -1 0])
       (cube-test.frig-frog.rules.game-piece-move-tile-delta id 0 -1))
       ; (cube-test.frig-frog.rules.player-move-to id 0 -1))
      (and (< y-val -0.5) (not jumped))
      (do
        (prn "player: jump fwd")
        (set! jumped true)
        ; (re-frame/dispatch [:cube-test.frig-frog.events/jump-frog 1 0])
       (cube-test.frig-frog.rules.game-piece-move-tile-delta id 0 1))
       ; (cube-test.frig-frog.rules.player-move-to id 0 1))
      ; :else
      (and (> y-val -0.5) (< y-val 0.5) (> x-val -0.5) (< x-val 0.5))
      (do
         (set! jumped false))))

(defn player-ctrl-handler [axes]
  (prn "player.player-ctrl-handler: axes=" axes)
  (let [x (.-x axes)
        y (.-y axes)]
    ; (when (and (or (> x 0.5) (> y 0.5)) open-for-service))
    (when (or (> x 0.5) (> y 0.5))
      (prn "player.player-ctrl-handler: x=" x ",y=" y ",open-for-service=" open-for-service)
      ; (set! open-for-service false)
      (cube-test.frig-frog.rules.update-left-ctrl-thumbstick x y true))))
