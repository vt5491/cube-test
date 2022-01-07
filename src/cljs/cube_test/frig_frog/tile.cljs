(ns cube-test.frig-frog.tile
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [cube-test.base :as base]))

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

(defn draw [row col]
  ; (prn "tile.draw: row=" row ", col=" col)
  (let [scene main-scene/scene]
    (when scene
      ;; delete any prior tile
      ; (prn "prior tile unique=" (.getMeshByUniqueId scene (str "tile-" row "-" col)))
      ; (prn "prior tile=" (.getMeshByID scene (str "tile-" row "-" col)))
      (when-let [prior-tile (.getMeshByID scene (str "tile-" row "-" col))]
        (.dispose prior-tile))
      (let [ ;tmp-2 (prn "scene=" scene)
            ; board {}
            ; tmp (js-debugger)
            tile (bjs/MeshBuilder.CreatePlane
                  (str "tile-" row "-" col)
                  (js-obj "width" 1.0 "height" 1.0 "sideOrientation" bjs/Mesh.DOUBLESIDE)
                  scene)]
            ; tmp-db db
            ; board (:board db)]
        ; (set! (.-position tile) (bjs/Vector3. 0 0.5 7))
        (set! (.-position tile) (bjs/Vector3. (* col 1.2) 1 (* row 1.2)))
        (set! (.-rotationQuaternion tile) base/X-QUAT-NEG-90)
        ; BABYLON.Tags.AddTagsTo(o, "tile"))))
        ;note: use something like "cube_test.main_scene.scene.getMeshesByID('tile')" to access.
        (bjs/Tags.AddTagsTo tile "tile")))))

                ; (assoc board :tile-1 {})))
  ; {:abc 2})

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
