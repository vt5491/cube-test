(ns cube-test.frig-frog.tile
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [cube-test.base :as base]))

(defn draw [grid-pos-x grid-pos-y]
  (prn "tile.draw: entered")
  (let [scene main-scene/scene
        ; board {}
        tmp (js-debugger)
        tile (bjs/MeshBuilder.CreatePlane
              "tile"
              (js-obj "width" 1 "height" 1 "sideOrientation" bjs/Mesh.DOUBLESIDE)
              scene)]
        ; tmp-db db
        ; board (:board db)]
    (set! (.-position tile) (bjs/Vector3. 0 0.5 7))
    (set! (.-rotationQuaternion tile) base/X-QUAT-NEG-90)))
    ; (assoc board :tile-1 {})))
  ; {:abc 2})
