(ns cube-test.scenes.face-slot-scene
  (:require-macros [cube-test.macros :as macros])
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [babylonjs-gui :as bjs-gui]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.utils.fps-panel :as fps-panel]))

; (def ^:dynamic *top-face* (atom 5))
(def ^:dynamic *top-rotor-face* (atom 5))
(def ^:dynamic *mid-rotor-face* (atom 5))
(def ^:dynamic *bottom-rotor-face* (atom 5))
(def top-rotor-uniq-id)
(def mid-rotor-uniq-id)
(def bottom-rotor-uniq-id)

(defn init-gui []
  (let [left-plane (bjs/Mesh.CreatePlane. "left-plane" 2)
        left-adv-texture (bjs-gui/AdvancedDynamicTexture.CreateForMesh. left-plane 1024 1024)
        left-pnl (bjs-gui/StackPanel.)
        left-hdr (bjs-gui/TextBlock.)
        bwd-btn (bjs-gui/Button.CreateImageButton. "bwd-spin" "bwd" "textures/tux_tada.jpg")
        cb-top (bjs-gui/Checkbox.)
        cb-mid (bjs-gui/Checkbox.)
        cb-bottom (bjs-gui/Checkbox.)]
    (set! (.-position left-plane) (bjs/Vector3. -1.5 1.5 0.4))
    (.addControl left-adv-texture left-pnl)
    (set! (.-text left-hdr) "Backward")
    (set! (.-height left-hdr) "100px")
    (set! (.-color left-hdr) "white")
    (set! (.-textHorizontalAlignment left-hdr) bjs-gui/Control.HORIZONTAL_ALIGNMENT_CENTER)
    (set! (.-fontSize left-hdr) "80")
    (set! (.-horizontalAlignment left-pnl) bjs-gui/StackPanel.HORIZONTAL_ALIGNMENT_CENTER)
    (set! (.-verticalAlignment left-pnl) bjs-gui/StackPanel.VERTICAL_ALIGNMENT_CENTER)
    (.addControl left-pnl left-hdr)
    ;;cb-top
    (set! (.-width cb-top) "100px")
    (set! (.-height cb-top) "100px")
    (-> cb-top .-onPointerUpObservable (.add (fn [value]
                                               (re-frame/dispatch [:face-slot-anim-bwd :top]))))
    (.addControl left-pnl cb-top)
    ;;cb-mid
    (set! (.-width cb-mid) "100px")
    (set! (.-height cb-mid) "100px")
    (-> cb-mid .-onPointerUpObservable (.add (fn [value]
                                               (re-frame/dispatch [:face-slot-anim-bwd :mid]))))
    (.addControl left-pnl cb-mid)
    ;;cb-bottom
    (set! (.-width cb-bottom) "100px")
    (set! (.-height cb-bottom) "100px")
    (-> cb-bottom .-onPointerUpObservable (.add (fn [value]
                                                  (re-frame/dispatch [:face-slot-anim-bwd :bottom]))))
    (.addControl left-pnl cb-bottom)))

(defn anim-bwd [hlq]
  ; (println "face-slot-scene: anim-bwd entered")
  (re-frame/dispatch [:rotor-anim-bwd hlq @*top-rotor-face*])
  (swap! *top-rotor-face* dec)
  (when (< @*top-rotor-face* 0)
    (swap! *top-rotor-face* (fn [x] 7)))
  (println "*top-rotor-face=" *top-rotor-face*))

;;
;; run-time methods
;;
(defn render-loop []
  ; (println "face-slot-scene: render-loop")
  (if (= main-scene/xr-mode "vr")
    (controller/tick)
    (controller-xr/tick))
  ; (cube-fx/tick)
  (fps-panel/tick main-scene/engine)
  (.render main-scene/scene))

(defn run-scene []
  (.runRenderLoop main-scene/engine (fn [] (render-loop))))

;;
;; init
;;
(defn init-top-rotor []
  (println "init-top-rotor entered")
  ; (let [rotor (-> main-scene/scene (.getMeshByName "top-rotor"))])
  (prn "top-rotor-unique-id=" top-rotor-uniq-id)
  (let [rotor (-> main-scene/scene (.getMeshByUniqueID top-rotor-uniq-id))
        rotor-pos (.-position rotor)]
    (set! (.-position rotor) (.add rotor-pos (bjs/Vector3. 0 2 0)))))

(defn init-mid-rotor []
  (println "init-mid-rotor entered")
  (let [rotor (-> main-scene/scene (.getMeshByName "mid-rotor"))
        rotor-pos (.-position rotor)]
    (set! (.-position rotor) (.add rotor-pos (bjs/Vector3. 0 1 0)))))

(defn init-bottom-rotor []
  (println "init-bottom-rotor entered")
  (let [rotor (-> main-scene/scene (.getMeshByName "bottom-rotor"))
        rotor-pos (.-position rotor)]
    (set! (.-position rotor) (.add rotor-pos (bjs/Vector3. 0 0 0)))))

(defn init []
  (println "face-slot-scene.init: entered")
  (let [light (bjs/PointLight. "pointLight" (bjs/Vector3. 0 5 -3) main-scene/scene)]
    (.setEnabled light true))
  (bjs/HemisphericLight. "hemiLight" (bjs/Vector3. 0 1 0) main-scene/scene)

  ; (load-rotor "models/slot_rotor/" "slot_rotor.glb"))
  ; (load-rotor "models/slot_rotor/" "slot_rotor.gltf")
  (re-frame/dispatch [:load-rotor
                      "models/slot_rotor/"
                      ; "slot_rotor.gltf"
                      "eyes_rotor.gltf"
                      :top
                      (fn []
                        ; (println "hi"))])
                        (re-frame/dispatch [:init-top-rotor]))])
  (re-frame/dispatch [:load-rotor
                      "models/slot_rotor/"
                      "slot_rotor.gltf"
                      :mid
                      (fn []
                        ; (println "hi"))])
                        (re-frame/dispatch [:init-mid-rotor]))])
  (re-frame/dispatch [:load-rotor
                      "models/slot_rotor/"
                      "slot_rotor.gltf"
                      :bottom
                      (fn []
                        ; (println "hi"))])
                        (re-frame/dispatch [:init-bottom-rotor]))])
  (init-gui))
