(ns cube-test.frig-frog.tile
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [cube-test.frig-frog.board :as ff.board]
   [cube-test.base :as base]))

(def hot-tile-color (bjs/Color3. 0.396 0.576 0.961))
(def orange-tile-color (bjs/Color3. 0.788 0.356 0.047))
(def std-tile-color (bjs/Color3. 0.569 0.482 0.310))
(def hot-tile-mat)
(def std-tile-mat)
(def dbg-tile-mat)

(defn x-y-to-row-col
  "Convert pixel-based x-y coords to row-col based coords"
  [x y]
  (let [row (int (+ (/ y ff.board/tile-width) 0.6))
        col (int (+ (/ x ff.board/tile-height) 0.6))]
    {:row row :col col}))
;; re-frame based methods

; still used but basically defunct since we don't have a state keyword
; in a tile anymore, using a flattened structure instead.
(defn state-update-fn [tile]
  (let [state (if (:state tile)
                (let [old-state (:state tile)]
                  (assoc {} :state (+ old-state 1)))
               (assoc {} :state 0))
        abc (if (:abc tile)
                (let [old-abc (:abc tile)]
                  (assoc {} :abc (- old-abc 1)))
               (assoc {} :abc 0))]
      (conj state abc)))

(defn draw [prfx row col]
  (let [scene main-scene/scene
        tile-prfx (str (name prfx) "-" "tile-")
        board-height (prfx ff.board/board-heights)]
    (when scene
      ;; delete any prior tile
      (when-let [prior-tile (.getMeshByID scene (str tile-prfx row "-" col))]
        (.dispose prior-tile))
      (let [ tile (bjs/MeshBuilder.CreatePlane
                    ; (str "tile-" row "-" col)
                    (str tile-prfx row "-" col)
                    (js-obj "width" 1.0 "height" 1.0 "sideOrientation" bjs/Mesh.DOUBLESIDE)
                    scene)]
        (set! (.-position tile) (bjs/Vector3. (* col ff.board/tile-width) board-height (* row ff.board/tile-height)))
        (set! (.-rotationQuaternion tile) base/X-QUAT-NEG-90)
        (set! (.-material tile) std-tile-mat)
        ;note: use something like "cube_test.main_scene.scene.getMeshesByID('tile')" to access.
        (bjs/Tags.AddTagsTo tile "tile")))))

(defn update-tile [row-num col-num update-fn db]
  (let [b (:board db)
        row (nth b row-num)
        row-kw (keyword (str "row-" row-num))
        tile (-> (row-kw row) (nth col-num))
        new-tile (update-fn tile)]
      (assoc-in db [:board row-num row-kw col-num] new-tile)))

;; rules-based methods
;btm-tile-1-3
(defn get-mesh [prfx x y]
  (let [scene main-scene/scene
        tile-pos (x-y-to-row-col x y)
        x (:row tile-pos)
        y (:col tile-pos)
        mesh-id (str prfx "-tile-" x "-" y)]
    (.getMeshByID scene mesh-id)))

(defn update-tile-mesh
  "Update the mesh associated with the tile"
  [prfx x y mat]
  (let [mesh (get-mesh prfx x y)]
    (when mesh
      (set! (.-material mesh) mat))))

(defn init []
  (let [scene main-scene/scene]
    (set! std-tile-mat (bjs/StandardMaterial. "std-tile-mat" scene))
    (set! hot-tile-mat (bjs/StandardMaterial. "hot-tile-mat" scene))
    (set! dbg-tile-mat (bjs/StandardMaterial. "dbg-tile-mat" scene))
    (set! (.-diffuseColor std-tile-mat) std-tile-color)
    (set! (.-diffuseColor hot-tile-mat) hot-tile-color)
    (set! (.-diffuseColor dbg-tile-mat) orange-tile-color)))
