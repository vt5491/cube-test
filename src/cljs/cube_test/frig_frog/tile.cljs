(ns cube-test.frig-frog.tile
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [cube-test.base :as base]))

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

(defn draw [grid-pos-x grid-pos-y]
  (prn "tile.draw: entered")
  (when main-scene/scene
    (let [scene main-scene/scene
          tmp-2 (prn "scene=" scene)
          ; board {}
          ; tmp (js-debugger)
          tile (bjs/MeshBuilder.CreatePlane
                "tile"
                (js-obj "width" 1 "height" 1 "sideOrientation" bjs/Mesh.DOUBLESIDE)
                scene)]
          ; tmp-db db
          ; board (:board db)]
      (set! (.-position tile) (bjs/Vector3. 0 0.5 7))
      (set! (.-rotationQuaternion tile) base/X-QUAT-NEG-90))))
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
