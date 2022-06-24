;; frog-2 uses the remote worker db to do reactive vs frog which uses local re-frame db.
(ns cube-test.frig-frog.frog-2
   (:require
     [re-frame.core :as re-frame]
     [babylonjs :as bjs]
     [cube-test.main-scene :as main-scene]
     [cube-test.base :as base]))

(defn init-frog-2 [db])
  ; (when (= main-scene/xr-mode "xr")))
  ; (assoc-in db :frog {:row 0 :col 5}))

  ; BABYLON.Mesh.CreatePolyhedron("shape" + i, {type: i, size: 3}, scene))
(defn draw-frog-2 [frog-2]
  (let [row (:row frog-2)
        col (:col frog-2)
        scene main-scene/scene
        mesh (bjs/Mesh.CreatePolyhedron. "frog-2" (js-obj "type" 2 "size" 0.5) scene)]
    (prn "draw.frog-2: row=" row ", col=" col)
    (set! (.-position mesh) (bjs/Vector3. (* col 1.2) 1.5 (* row 1.2)))))
  ;       frog-mesh (.getMeshByID scene "frog")]
  ;     (when frog-mesh
  ;       (.dispose frog-mesh))
  ;     (let [frog (bjs/Mesh.CreateBox "frog" 1 scene)]
  ;       (set! (.-position frog) (bjs/Vector3. (* col 1.2) 1 (* row 1.2))))))
