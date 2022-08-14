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
   [cube-test.utils.fps-panel :as fps-panel]))

(def light1)
(def cube)

(declare init-action-pnl)
(declare init-gui)
(declare init-gui-2)
(declare pointer-handler)

(defn init []
  (println "cube-spin-scene.init: entered")
  (set! light1 (bjs/PointLight. "pointLight" (bjs/Vector3. 0 5 -3) main-scene/scene))
  (.setEnabled light1 true)
  (bjs/HemisphericLight. "hemiLight" (bjs/Vector3. 0 1 0) main-scene/scene)
  (.attachControl main-scene/camera main-scene/canvas false)
  ; (init-cube)
  (init-action-pnl)
  ; (re-frame/dispatch [:setup-btn])
  ; (re-frame/dispatch [:init-fps-panel scene])
  (if (= main-scene/xr-mode "xr")
    (-> (.-onPointerObservable main-scene/scene) (.add pointer-handler)))
  (init-gui)
  (init-gui-2))

(defn init-cube[]
  (set! cube (bjs/MeshBuilder.CreateBox. "cube"
                                                (js-obj "height" 2 "width" 2 "depth" 0.5)
                                                main-scene/scene))
  (set! (.-position cube)(bjs/Vector3. 0 0 3))
  (set! (.-material cube) main-scene/red-mat))

(defn pointer-handler [pointer-info]
  (macros/when-let* [type (.-type pointer-info)
                     picked-mesh (-> pointer-info (.-pickInfo) (.-pickedMesh))]
                    ; (prn "pointer-handler: picked-mesh.name=" (.-name picked-mesh))
                    (when (re-matches #"action-pnl" (.-name picked-mesh))
                      (cond
                        (= type js/BABYLON.PointerEventTypes.POINTERDOWN)
                        (do
                          ; (re-frame/dispatch [:reset-spin-projectile 1])
                          ; (re-frame/dispatch [:reset-projectiles])
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

; (defn setup-btn []
;   (prn "setup-btn: entered")
;   (let [vt-div (-> js/document (.getElementById "vt-div"))
;         btn (js/document.createElement "button")]
;     (set! (.-name btn) "abc-name")
;     (set! (.-value btn) "abc-value")
;     (.setAttribute btn "id" "vt-btn")
;     (set! (.-innerHTML btn) "vt-btn")
;     (.addEventListener btn "click" (fn []
;                                      (prn "you click vt-btn")
;                                      (enter-vr)))
;     (.appendChild vt-div btn)))

(defn init-gui []
  (let [scene main-scene/scene
        ;; left-plane (bjs/Mesh.CreatePlane. "left-plane" 2)
        left-plane (bjs/Mesh.CreatePlane "left-plane" (js-obj "width" 2, "height" 2) scene)
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
    ; (.addControl left-pnl left-hdr)))


(defn init-gui-2 []
  (init-cube)
  (let [
        ;; plane (bjs/Mesh.CreatePlane. "plane" 1)
        scene main-scene/scene
        plane (bjs/Mesh.CreatePlane "plane" (js-obj "width" 1, "height" 1) scene)
        adv-text (bjs-gui/AdvancedDynamicTexture.CreateForMesh plane)
        plane-2 (bjs/Mesh.CreatePlane "plane-2" 2)
        adv-text-2 (bjs-gui/AdvancedDynamicTexture.CreateForMesh plane-2)
        panel (bjs-gui/StackPanel.)
        panel-2 (bjs-gui/StackPanel.)
        header (bjs-gui/TextBlock.)
        header-2 (bjs-gui/TextBlock.)
        picker (bjs-gui/ColorPicker.)
        ; fwd-btn (bjs-gui/Button3D. "fwd")
        cb (bjs-gui/Checkbox.)
        ; fwd-btn (bjs-gui/Button.CreateSimpleButton. "fwd" "click me")]
        fwd-btn (bjs-gui/Button.CreateImageButton "fwd" "click me" "textures/tux_tada.jpg")]
        ; fwd-btn (bjs-gui/HolographicButton. "fwd")]
    (set! (.-position plane) (bjs/Vector3. -3.4 1.5 0.4))
    (set! (.-position plane-2) (bjs/Vector3. 1.4 1.5 0.4))
    ; (set! (.-material plane) green-mat)
    ; (set! (.-material plane-2) green-mat)
    (.addControl adv-text panel)
    (.addControl adv-text-2 panel-2)
    (set! (.-text header) "Color GUI")
    (set! (.-height header) "100px")
    (set! (.-color header) "white")
    (set! (.-textHorizontalAlignment header) bjs-gui/Control.HORIZONTAL_ALIGNMENT_CENTER)
    (set! (.-fontSize header) "120")
    (.addControl panel header)
    ; (.addControl panel-2 header)
    (set! (.-value picker) (-> cube .-material .-diffuseColor))
    (set! (.-horizontalAlignment picker) bjs-gui/Control.HORIZONTAL_ALIGNMENT_CENTER)
    (set! (.-height picker) "350px")
    (set! (.-width picker) "350px")
    (-> picker .-onValueChangedObservable (.add (fn [value]
                                                  (-> cube .-material .-diffuseColor (.copyFrom value)))))
    (-> picker .-onPointerUpObservable (.add (fn [value]
                                               (prn "up the palace"))))
    (-> picker .-onPointerClickObservable (.add (fn [value])
                                               (prn "click the palace")))
    (.addControl panel picker)
    ;; btn
    (set! (.-text header-2) "Forward")
    (set! (.-height header-2) "100px")
    (set! (.-fontSize header-2) "80")
    (set! (.-color header-2) "red")
    (.addControl panel-2 header-2)
    ; (set! (.-x (.-position fwd-btn)) 1.5)
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
  (.render main-scene/scene))

(defn run-scene []
  (.runRenderLoop main-scene/engine (fn [] (render-loop))))
