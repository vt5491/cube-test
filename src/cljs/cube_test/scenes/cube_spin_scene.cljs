;; Developed between approx. 06/2020 & 07/2020.
(ns cube-test.scenes.cube-spin-scene
  (:require-macros [cube-test.macros :as macros])
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [babylonjs-gui :as bjs-gui]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.cube-fx :as cube-fx]
   [cube-test.utils.fps-panel :as fps-panel]
   [cube-test.utils :as utils]))

(def light1)
(def cube)

(declare init-action-pnl)
(declare init-gui)
(declare init-gui-2)
(declare pointer-handler)

(defn release []
  (prn "cube-spin.release: entered")
  (utils/release-common-scene-assets))

(defn init []
  (println "cube-spin-scene.init: entered")
  (set! light1 (bjs/PointLight. "pointLight" (bjs/Vector3. 0 5 -3) main-scene/scene))
  (.setEnabled light1 true)
  (bjs/HemisphericLight. "hemiLight" (bjs/Vector3. 0 1 0) main-scene/scene)
  (.attachControl main-scene/camera main-scene/canvas false)
  (init-action-pnl)
  (if (= main-scene/xr-mode "xr")
    (-> (.-onPointerObservable main-scene/scene) (.add pointer-handler)))
  (init-gui)
  (init-gui-2)
  (main-scene/load-main-gui release)
  (let [grnd (.getMeshByID main-scene/scene "ground")]
   (when grnd
     (.setEnabled grnd true)
     (set! (.-isVisible grnd) true))))

(defn init-cube[]
  (set! cube (bjs/MeshBuilder.CreateBox. "cube"
                                                (js-obj "height" 2 "width" 2 "depth" 0.5)
                                                main-scene/scene))
  (set! (.-position cube)(bjs/Vector3. 0 0 3))
  (set! (.-material cube) main-scene/red-mat))

(defn pointer-handler [pointer-info]
  (macros/when-let* [type (.-type pointer-info)
                     picked-mesh (-> pointer-info (.-pickInfo) (.-pickedMesh))]
                    (when (re-matches #"action-pnl" (.-name picked-mesh))
                      (cond
                        (= type js/BABYLON.PointerEventTypes.POINTERDOWN)
                        (do
                          (re-frame/dispatch [:toggle-pause-projectiles])
                          (prn "action-pnl pointerdown"))))))

(defn init-action-pnl []
  (let [action-pnl
        (js/BABYLON.MeshBuilder.CreateBox.
         "action-pnl"
         (js-obj "height" 1
                 "width"  1
                 "depth" 0.1)
         main-scene/scene)
        mat (js/BABYLON.StandardMaterial. "action-pnl-mat" main-scene/scene)]
    (set! (.-position action-pnl) (bjs/Vector3. -4 3 5))
    (set! (.-diffuseColor mat) (js/BABYLON.Color3. 1 1 0))
    (set! (.-material action-pnl) mat)))

(defn init-gui []
 (let [scene main-scene/scene
       left-plane (bjs/Mesh.CreatePlane "left-plane" 2 scene)
       left-adv-texture (bjs-gui/AdvancedDynamicTexture.CreateForMesh left-plane 1024 1024)
       left-pnl (bjs-gui/StackPanel.)
       left-hdr (bjs-gui/TextBlock.)
       bwd-btn (bjs-gui/Button.CreateImageButton "bwd-spin" "bwd" "textures/tux_tada.jpg")
       cb (bjs-gui/Checkbox.)]
   (set! (.-position left-plane) (bjs/Vector3. -1.5 1.5 0.4))
   (.addControl left-adv-texture left-pnl)
   (set! (.-text left-hdr) "Backward")
   (set! (.-height left-hdr) "100px")
   (set! (.-color left-hdr) "white")
   (set! (.-textHorizontalAlignment left-hdr) bjs-gui/Control.HORIZONTAL_ALIGNMENT_CENTER)
   (set! (.-fontSize left-hdr) "80")
   (set! (.-horizontalAlignment left-pnl) bjs-gui/StackPanel.HORIZONTAL_ALIGNMENT_CENTER)
   (set! (.-verticalAlignment left-pnl) bjs-gui/StackPanel.VERTICAL_ALIGNMENT_CENTER)
   (-> bwd-btn .-onPointerUpObservable (.add (fn [value]
                                              (prn "bwd the palace btn style")
                                              (re-frame/dispatch [:update-spin-ang-vel (bjs/Vector3. 0 0.002 0)]))))
   (.addControl left-pnl bwd-btn)
   (.addControl left-pnl left-hdr)
   ;;cb
   (set! (.-width cb) "100px")
   (set! (.-height cb) "100px")
   (.addControl left-pnl cb)))


(defn init-gui-2 []
  (init-cube)
  (let [
        scene main-scene/scene
        plane-2 (bjs/Mesh.CreatePlane "plane-2" 2)
        adv-text-2 (bjs-gui/AdvancedDynamicTexture.CreateForMesh plane-2)
        panel-2 (bjs-gui/StackPanel.)
        header-2 (bjs-gui/TextBlock.)
        cb (bjs-gui/Checkbox.)
        fwd-btn (bjs-gui/Button.CreateImageButton "fwd" "click me" "textures/tux_tada.jpg")]
    (set! (.-position plane-2) (bjs/Vector3. 1.4 1.5 0.4))
    (.addControl adv-text-2 panel-2)
    ;; btn
    (set! (.-text header-2) "Forward")
    (set! (.-height header-2) "100px")
    (set! (.-fontSize header-2) "80")
    (set! (.-color header-2) "red")
    (.addControl panel-2 header-2)
    (-> fwd-btn .-onPointerUpObservable (.add (fn [value]
                                               (prn "up the palace btn style")
                                               (re-frame/dispatch [:update-spin-ang-vel (bjs/Vector3. 0 -0.002 0)]))))
    (.addControl panel-2 fwd-btn)
    ;;cb
    (set! (.-width cb) "100px")
    (set! (.-height cb) "100px")
    (.addControl panel-2 cb)))

;;
;; run-time methods

;; main tick handler best placed in game.cljs (refer to many, referred by few)
;; instead of main_scene (refer to few, referred by many) since we will
;; need to potentially call all other namespaces.
(defn render-loop []
  (if (= main-scene/xr-mode "vr")
    (controller/tick)
    (controller-xr/tick))
  (cube-fx/tick)
  (fps-panel/tick main-scene/engine)
  (main-scene/tick)
  (.render main-scene/scene))

(defn run-scene []
  (.runRenderLoop main-scene/engine (fn [] (render-loop))))
