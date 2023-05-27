;; a simple frames-per-second (fps) panel.
(ns cube-test.utils.fps-panel
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [goog.string :as gstring]
   [cube-test.base :as base]
   [cube-test.controller-xr :as controller-xr]))
   ; [clojure.core.strint :refer [<<]]))

(def fps-pnl)
(def fps-pnl-mini-ctrl)
(def fps-spin-cube)
(def fps-pnl-ctrl-light)

(defn init-mini [scene]
  (set! fps-pnl-mini-ctrl (.clone fps-pnl))
  (set! (.-name fps-pnl-mini-ctrl) "fps-pnl-mini-ctrl")
  (set! (.-id fps-pnl-mini-ctrl) "fps-pnl-mini-ctrl")
  (set! (.-scaling fps-pnl-mini-ctrl)(bjs/Vector3. 0.03 0.03 0.03))
  (set! fps-pnl-ctrl-light (bjs/PointLight. "fps-pnl-ctrl-light" (bjs/Vector3. 0 0 0) scene))
  (set! (.-intensity fps-pnl-ctrl-light) 0.35))
;; (defn string->integer
;;   ([s] (string->integer s 10))
;;   ([s base] (Integer/parseInt s base)))
;; (defn gen-m [{:keys [id level text] :as msg}])
;; (defn my-f2 [s {:keys [a b] :as vals}]
;;   (prn "s=" s ",a=" (:a vals)))
;; (defn my-f3
;;   ([s] (my-f3 s {:a 1}))
;;   ([s {:keys [a b] :as vals}]
;;    (prn "s=" s ",a=" (:a vals))))
(defn init
  ([scene] (init scene {:add-mini false}))
  ([scene {:keys [:add-mini] :as vals}]
   (set! fps-pnl (js/BABYLON.MeshBuilder.CreateBox.
                  "fps-panel"
                  (js-obj "width" 2.50 "height" 2.50 "depth" 0.1
                   scene)))
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
     (set! (.-position fpc) (bjs/Vector3. (.-x fpp) (+ (.-y fpp) 2.0) (+ (.-z fpp) 0))))

   ;; add support for a "mini" fps panel attached to the left ctrl
   (prn "fps-panel.init: add-mini=" (:add-mini vals) ",vals=" vals)
   (when (:add-mini vals)
     (init-mini scene))))

(defn tick [engine]
  ;  (prn "fps-pnl.tick: entered, fps=" (.getFps engine))
  ;; main fps-pnl
  (.drawText
   (-> fps-pnl .-material .-diffuseTexture)
   (int (.getFps engine)) 50 50 "60px green" "white" "blue" true true
  ; (.drawText fps-pnl (int (.getFps engine) 50 50 "60px green" "white" "blue" true true)))
  ; (when-let [spin-cube (.getMeshByID main-scene/scene "spin_cube")]
   ;; spin-cube
   (let [rot (.-rotation fps-spin-cube)]
     (set! (.-rotation fps-spin-cube)(bjs/Vector3. (.-x rot) (+ (.-y rot) (* base/ONE-DEG 0.5) (.-z rot)))))

   ;; mini fps-pnl
   (when fps-pnl-mini-ctrl
     (let [left-xr-ctrl controller-xr/left-ctrl-xr]
       (when left-xr-ctrl
         (set! (.-position fps-pnl-mini-ctrl) (-> left-xr-ctrl .-pointer .-absolutePosition))
         (set! (.-rotationQuaternion fps-pnl-mini-ctrl) (-> left-xr-ctrl .-pointer .-rotationQuaternion))
         (set! (.-position fps-pnl-ctrl-light) (-> left-xr-ctrl .-grip .-position)))))))
