;; a simple frames-per-second (fps) panel.
(ns cube-test.utils.fps-panel
  (:require
   [re-frame.core :as re-frame]
   [goog.string :as gstring]))

(def fps-pnl)

(defn init [scene]
  (set! fps-pnl (js/BABYLON.MeshBuilder.CreateBox.
                 "fps-panel"
                 (js-obj "width" 2.50 "height" 2.50 "depth" 0.1)
                 scene))
 (let [dyn-texture (js/BABYLON.DynamicTexture. "fps-pnl-texture" (js-obj "width" 256 "height" 60) scene)
       fps-pnl-mat (js/BABYLON.StandardMaterial. "fps-panel-mat" scene)]
   (set! (.-position fps-pnl) (js/BABYLON.Vector3. -8 10 7))
   (set! (.-material fps-pnl) fps-pnl-mat)
   (set! (.-diffuseTexture fps-pnl-mat) dyn-texture)))
   ; (.drawText (-> fps-pnl .-material .-diffuseTexture) "60" 50 50 "60px green" "white" "blue" true true)))

(defn tick [engine]
  ; (prn "fps-pnl.tick: entered, fps=" (.getFps engine))
  (.drawText
   (-> fps-pnl .-material .-diffuseTexture)
   (int (.getFps engine)) 50 50 "60px green" "white" "blue" true true))
  ; (.drawText fps-pnl (int (.getFps engine) 50 50 "60px green" "white" "blue" true true)))
