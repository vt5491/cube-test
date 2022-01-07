(ns cube-test.frig-frog.frog
   (:require
     [re-frame.core :as re-frame]
     [babylonjs :as bjs]
     [cube-test.main-scene :as main-scene]
     [cube-test.base :as base]))

(defn dummy [x y]
  7)

(defn init-frog [row col db]
  ; (assoc db :frog {:row row})
  (let [tmp-db (assoc db :frog {})
        tmp-db-2 (assoc-in tmp-db [:frog :row] row)
        tmp-db-3 (assoc-in tmp-db-2 [:frog :col] col)
        tmp-db-4 (assoc-in tmp-db-3 [:frog :mode] 0)]
      tmp-db-4))

(defn draw-frog [row col]
  (prn "draw.frog: row=" row ", col=" col)
  (let [scene main-scene/scene
        frog-mesh (.getMeshByID scene "frog")]
      (when frog-mesh
        (.dispose frog-mesh))
      (let [frog (bjs/Mesh.CreateBox "frog" 1 scene)]
        (set! (.-position frog) (bjs/Vector3. (* col 1.2) 1 (* row 1.2))))))

; (defn move-frog [row col db]
;   (let [scene main-scene/scene
;         frog (:frog db)]
;         ; frog-mesh (.getMeshByID scene "frog")]
;       ; (when frog-mesh
;       ;   (.dispose frog-mesh))
;       ; (draw-frog row col)
;       (prn "move-frog row=" row ", col=" col)
;       (-> (assoc-in db [:frog :row] row)
;           (assoc-in [:frog :col] col))))
(defn ^:export tick []
  ;; Note: accessing the vr/xr controller has to be "on the tick".  It's simply not
  ;; available if you're not in full vr mode (hit the vr button *and* have the headset on) 
  (prn "frig-frog.game.tick: left-ctrl=" (.-leftController main-scene/camera)))
  ; (println "tick2: entry: camera.pos=" (.-position re-con.core/camera))
  ; (when camera
  ;   (set! leftController (.-leftController camera)))
  ; ; (println "tick: camera=" camera ",leftController=" leftController)
  ; (when (and camera leftController)))
