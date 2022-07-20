(ns cube-test.frig-frog.tile
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [cube-test.frig-frog.board :as ff.board]
   [cube-test.base :as base]))

; (def hot-tile-color (bjs/Color3. 101 147 245))
(def hot-tile-color (bjs/Color3. 0.396 0.576 0.961))
; (def std-tile-color (bjs/Color3. 0xf5 0xf5 0xf5))
(def std-tile-color (bjs/Color3. 0.961 0.961 0.961))
(def hot-tile-mat)
(def std-tile-mat)

  ; (set! blue-mat (bjs/StandardMaterial. "blue-mat" scene))
  ; (set! (.-diffuseColor blue-mat) (bjs/Color3. 0 0 1)))
(defn x-y-to-row-col
  "Convert pixel-based x-y coords to row-col based coords"
  [x y]
  (prn "x-y-to-row-col: x=" x ",y=" y)
  (let [row (int (/ y ff.board/tile-width))
        col (int (/ x ff.board/tile-height))]
    ; (js-debugger)
    (prn "x-y-to-row-col: row=" row ",col=" col)
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
      ; (prn "state-update-fn: state=" state)
      ; (prn "state-update-fn: abc=" abc)
      (conj state abc)))

(defn draw [prfx row col]
  ; (prn "tile.draw: row=" row ", col=" col)
  (let [scene main-scene/scene
        tile-prfx (str (name prfx) "-" "tile-")
        board-height (prfx ff.board/board-heights)]
        ; _ (prn "tile.draw: board-height=" board-height)]
    (when scene
      ;; delete any prior tile
      ; (when-let [prior-tile (.getMeshByID scene (str "tile-" row "-" col))])
      (when-let [prior-tile (.getMeshByID scene (str tile-prfx row "-" col))]
        (.dispose prior-tile))
      (let [ tile (bjs/MeshBuilder.CreatePlane
                    ; (str "tile-" row "-" col)
                    (str tile-prfx row "-" col)
                    (js-obj "width" 1.0 "height" 1.0 "sideOrientation" bjs/Mesh.DOUBLESIDE)
                    scene)]
        ; (set! (.-position tile) (bjs/Vector3. (* col 1.2) 1 (* row 1.2)))
        (set! (.-position tile) (bjs/Vector3. (* col ff.board/tile-width) board-height (* row ff.board/tile-height)))
        (set! (.-rotationQuaternion tile) base/X-QUAT-NEG-90)
        ;note: use something like "cube_test.main_scene.scene.getMeshesByID('tile')" to access.
        (bjs/Tags.AddTagsTo tile "tile")))))

(defn update-tile [row-num col-num update-fn db]
  (let [b (:board db)
        row (nth b row-num)
        row-kw (keyword (str "row-" row-num))
        tmp (prn "b=" b ", row=" row ", row-kw=" row-kw)
        tile (-> (row-kw row) (nth col-num))
        ; new-tile (assoc tile :state 7)
        new-tile (update-fn tile)]
      (prn "tile.update-tile: tile=" tile)
      ; (assoc (-> (nth b row-num) (row-kw) (nth col-num)) :state 7)))
      ; (assoc db (-> db :board (nth row-num) (row-kw) (nth col-num)) new-tile)))
      (assoc-in db [:board row-num row-kw col-num] new-tile)))
      ; (assoc db :board [])))
      ; (assoc db (-> :board :row-1 ) 7)
      ; (assoc-in db [:board 1 :row-1] 7)))
;; rules-based methods
;btm-tile-1-3
(defn get-mesh [prfx x y]
  (let [scene main-scene/scene
        tile-pos (x-y-to-row-col x y)
        ; _ (prn "tile-pos=" tile-pos)
        x (:row tile-pos)
        y (:col tile-pos)
        mesh-id (str prfx "-tile-" x "-" y)]
        ; mesh-id (format "%s-tile-%s-%s" prfx x y)]
    (prn "tile: mesh-id=" mesh-id)
    (.getMeshByID scene mesh-id)))

(defn update-tile-mesh
  "Update the mesh associated with the tile"
  [prfx x y mat]
  (let [mesh (get-mesh prfx x y)]
    (when mesh
      (prn "update-tile-mesh: mesh=" mesh)
      ; (set! (.-material mesh) main-scene/green-mat)
      (set! (.-material mesh) hot-tile-mat))))

(defn init []
  (let [scene main-scene/scene]
    (set! std-tile-mat (bjs/StandardMaterial. "std-tile-mat" scene))
    (set! hot-tile-mat (bjs/StandardMaterial. "hot-tile-mat" scene))
    (set! (.-diffuseColor std-tile-mat) std-tile-color)
    ; (set! (.-diffuseColor hot-tile-mat) hot-tile-color)
    (set! (.-diffuseColor hot-tile-mat) hot-tile-color)))
    ; (set! (.-diffuseColor hot-tile-mat) (bjs/Color3. 1 0 0))))
