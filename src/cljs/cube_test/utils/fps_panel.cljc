;; a simple frames-per-second (fps) panel.
(ns cube-test.utils.fps-panel
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [goog.string :as gstring]
   [cube-test.base :as base]))
   ; [clojure.core.strint :refer [<<]]))

(def fps-pnl)
(def fps-spin-cube)

(defn init [scene]
  (set! fps-pnl (js/BABYLON.MeshBuilder.CreateBox.
                 "fps-panel"
                 (js-obj "width" 2.50 "height" 2.50 "depth" 0.1)
                 scene))
  (let [dyn-texture (js/BABYLON.DynamicTexture. "fps-pnl-texture" (js-obj "width" 256 "height" 60) scene)
        fps-pnl-mat (js/BABYLON.StandardMaterial. "fps-panel-mat" scene)]
    (set! (.-position fps-pnl) (js/BABYLON.Vector3. -8 10 7))
    (set! (.-material fps-pnl) fps-pnl-mat)
    (set! (.-diffuseTexture fps-pnl-mat) dyn-texture)
    ; (.drawText (-> fps-pnl .-material .-diffuseTexture) "60" 50 50 "60px green" "white" "blue" true true)))
    (set! fps-spin-cube (bjs/MeshBuilder.CreateBox. "fps_spin_cube" (js-obj "height" 0.5 "width" 0.5 "depth" 0.5) scene)))

  ;; for some reason we need to fully qualify our references to fps-pnl top level vars.
  (let [fp cube-test.utils.fps-panel/fps-pnl
        fpp (.-position fp)
        fpc cube-test.utils.fps-panel/fps-spin-cube]
    ; (js-debugger)
    (set! (.-position fpc) (bjs/Vector3. (.-x fpp) (+ (.-y fpp) 2.0) (.-z fpp)))))

(defn tick [engine]
  ;  (prn "fps-pnl.tick: entered, fps=" (.getFps engine))
  (.drawText
   (-> fps-pnl .-material .-diffuseTexture)
   (int (.getFps engine)) 50 50 "60px green" "white" "blue" true true
  ; (.drawText fps-pnl (int (.getFps engine) 50 50 "60px green" "white" "blue" true true)))
  ; (when-let [spin-cube (.getMeshByID main-scene/scene "spin_cube")]
   (let [rot (.-rotation fps-spin-cube)]
     (set! (.-rotation fps-spin-cube)(bjs/Vector3. (.-x rot) (+ (.-y rot) (* base/ONE-DEG 0.5) (.-z rot)))))))
