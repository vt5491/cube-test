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
(def ^:dynamic *top-rotor-face* (atom 0))
(def ^:dynamic *mid-rotor-face* (atom 0))
(def ^:dynamic *bottom-rotor-face* (atom 0))
(def top-rotor-uniq-id)
(def mid-rotor-uniq-id)
(def bottom-rotor-uniq-id)
(def ^:const rotor-width 0.4)
(def ^:const rotor-top-pos (bjs/Vector3. 0 2 0))

(defn init-left-gui []
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

(defn init-right-gui []
  (let [right-plane (bjs/Mesh.CreatePlane. "right-plane" 2)
        right-adv-texture (bjs-gui/AdvancedDynamicTexture.CreateForMesh. right-plane 1024 1024)
        right-pnl (bjs-gui/StackPanel.)
        right-hdr (bjs-gui/TextBlock.)
        bwd-btn (bjs-gui/Button.CreateImageButton. "bwd-spin" "bwd" "textures/tux_tada.jpg")
        cb-top (bjs-gui/Checkbox.)
        cb-mid (bjs-gui/Checkbox.)
        cb-bottom (bjs-gui/Checkbox.)]
    (set! (.-position right-plane) (bjs/Vector3. 1.5 1.5 0.4))
    (.addControl right-adv-texture right-pnl)
    (set! (.-text right-hdr) "Forward")
    (set! (.-height right-hdr) "100px")
    (set! (.-color right-hdr) "white")
    (set! (.-textHorizontalAlignment right-hdr) bjs-gui/Control.HORIZONTAL_ALIGNMENT_CENTER)
    (set! (.-fontSize right-hdr) "80")
    (set! (.-horizontalAlignment right-pnl) bjs-gui/StackPanel.HORIZONTAL_ALIGNMENT_CENTER)
    (set! (.-verticalAlignment right-pnl) bjs-gui/StackPanel.VERTICAL_ALIGNMENT_CENTER)
    (.addControl right-pnl right-hdr)
    ;;cb-top
    (set! (.-width cb-top) "100px")
    (set! (.-height cb-top) "100px")
    (-> cb-top .-onPointerUpObservable (.add (fn [value]
                                               (re-frame/dispatch [:face-slot-anim-fwd :top]))))
    (.addControl right-pnl cb-top)
    ;;cb-mid
    (set! (.-width cb-mid) "100px")
    (set! (.-height cb-mid) "100px")
    (-> cb-mid .-onPointerUpObservable (.add (fn [value]
                                               (re-frame/dispatch [:face-slot-anim-fwd :mid]))))
    (.addControl right-pnl cb-mid)
    ;;cb-bottom
    (set! (.-width cb-bottom) "100px")
    (set! (.-height cb-bottom) "100px")
    (-> cb-bottom .-onPointerUpObservable (.add (fn [value]
                                                  (re-frame/dispatch [:face-slot-anim-fwd :bottom]))))
    (.addControl right-pnl cb-bottom)))

(defn init-gui []
  (init-left-gui)
  (init-right-gui))

; (defn anim-bwd [hlq]
;   ; (println "face-slot-scene: anim-bwd entered")
;   (re-frame/dispatch [:rotor-anim-bwd hlq @*top-rotor-face*])
;   (swap! *top-rotor-face* dec)
;   (when (< @*top-rotor-face* 0)
;     (swap! *top-rotor-face* (fn [x] 7)))
;   (println "*top-rotor-face=" *top-rotor-face*))

(defn anim-bwd [hlq]
  ; (println "face-slot-scene: anim-bwd entered")
  (condp = hlq
    :top (do
           (re-frame/dispatch [:rotor-anim-bwd hlq @*top-rotor-face*])
           (swap! *top-rotor-face* dec)
           (when (< @*top-rotor-face* 0)
             (swap! *top-rotor-face* (fn [x] 7))))
    :mid (do
           (re-frame/dispatch [:rotor-anim-bwd hlq @*mid-rotor-face*])
           (swap! *mid-rotor-face* dec)
           (when (< @*mid-rotor-face* 0)
             (swap! *mid-rotor-face* (fn [x] 7))))
    :bottom (do
              (re-frame/dispatch [:rotor-anim-bwd hlq @*bottom-rotor-face*])
              (swap! *bottom-rotor-face* dec)
              (when (< @*bottom-rotor-face* 0)
                (swap! *bottom-rotor-face* (fn [x] 7))))))

(defn anim-fwd [hlq]
  ; (println "face-slot-scene: anim-bwd entered")
  (condp = hlq
    :top (do
           (println "*top-rotor-face=" *top-rotor-face*)
           (re-frame/dispatch [:rotor-anim-fwd hlq @*top-rotor-face*])
           (swap! *top-rotor-face* inc)
           (when (> @*top-rotor-face* 7)
             (swap! *top-rotor-face* (fn [x] 0))))
    :mid (do
           (re-frame/dispatch [:rotor-anim-fwd hlq @*mid-rotor-face*])
           (swap! *mid-rotor-face* inc)
           (when (> @*mid-rotor-face* 7)
             (swap! *mid-rotor-face* (fn [x] 0))))
    :bottom (do
              (re-frame/dispatch [:rotor-anim-fwd hlq @*bottom-rotor-face*])
              (swap! *bottom-rotor-face* inc)
              (when (> @*bottom-rotor-face* 7)
                (swap! *bottom-rotor-face* (fn [x] 0))))))

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
    ; (set! (.-position rotor) (.add rotor-pos (bjs/Vector3. 0 2 0)))
    (set! (.-position rotor) rotor-top-pos)))

(defn init-mid-rotor []
  (println "init-mid-rotor entered")
  ; (let [rotor (-> main-scene/scene (.getMeshByName "mid-rotor"))])
  (let [rotor (-> main-scene/scene (.getMeshByUniqueID mid-rotor-uniq-id))
        rotor-pos (.-position rotor)]
    ; (set! (.-position rotor) (.add rotor-pos (bjs/Vector3. 0 1 0)))
    (set! (.-position rotor) (.subtract rotor-top-pos (bjs/Vector3. 0 rotor-width 0)))))

(defn init-bottom-rotor []
  (println "init-bottom-rotor entered")
  ; (let [rotor (-> main-scene/scene (.getMeshByName "bottom-rotor"))])
  (let [rotor (-> main-scene/scene (.getMeshByUniqueID bottom-rotor-uniq-id))
        rotor-pos (.-position rotor)]
    ; (set! (.-position rotor) (.add rotor-pos (bjs/Vector3. 0 0 0)))
    (set! (.-position rotor) (.subtract rotor-top-pos (bjs/Vector3. 0 (* 2 rotor-width) 0)))))

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
                      ; "eyes_rotor.gltf"
                      "eyes_rotor_n1.gltf"
                      :top
                      (fn []
                        ; (println "hi"))])
                        (re-frame/dispatch [:init-top-rotor]))])
  (re-frame/dispatch [:load-rotor
                      "models/slot_rotor/"
                      ; "noses_rotor.gltf"
                      "noses_rotor_n1.gltf"
                      :mid
                      (fn []
                        ; (println "hi"))])
                        (re-frame/dispatch [:init-mid-rotor]))])
  (re-frame/dispatch [:load-rotor
                      "models/slot_rotor/"
                      ; "slot_rotor.gltf"
                      "mouths_rotor_n1.gltf"
                      :bottom
                      (fn []
                        ; (println "hi"))])
                        (re-frame/dispatch [:init-bottom-rotor]))])
  (init-gui))
