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

; (def rotor-anim)
; (def rotor-anim-0-7)
; (def rotor-anim-7-6)
; (def rotor-anim-6-5)
; (def rotor-anim-5-4)
; (def rotor-anim-4-3)
; (def rotor-anim-3-2)
; (def rotor-anim-2-1)
; (def rotor-anim-1-0)

(def ^:dynamic *top-face* (atom 5))

; (declare load-rotor)
;
; (defn slot-rotor-loaded [new-meshes particle-systems skeletons]
;   (prn "slot-rotor-loaded: new-meshes=" new-meshes)
;   (prn "count new-meshes=" (count new-meshes))
;   ; (set! rotor-anim (-> main-scene/scene (.getAnimationGroupByName "rotorAction.001")))
;   (set! rotor-anim-0-7 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.0-7")))
;   (set! rotor-anim-7-6 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.7-6")))
;   (set! rotor-anim-6-5 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.6-5")))
;   (set! rotor-anim-5-4 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.5-4")))
;   (set! rotor-anim-4-3 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.4-3")))
;   (set! rotor-anim-3-2 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.3-2")))
;   (set! rotor-anim-2-1 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.2-1")))
;   (set! rotor-anim-1-0 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.1-0")))
;   (.stop rotor-anim-0-7)
;   (.stop rotor-anim-7-6)
;   (.stop rotor-anim-6-5)
;   (.stop rotor-anim-5-4)
;   (.stop rotor-anim-4-3)
;   (.stop rotor-anim-3-2)
;   (.stop rotor-anim-2-1)
;   (.stop rotor-anim-1-0))

  ; (.stop rotor-anim)
  ; (js-debugger)
  ; (println "animatables=" (.-animatables rotor-anim)))
  ; (let [rotor (-> main-scene/scene (.getMeshByID "__root__"))]
  ;   (set! rotor-anim (.beginAnimation rotor 1 105 true))
  ;   (println "rotor-anim=" rotor-anim)))
  ; scene.beginAnimation(box1, 0, 100, true));

; (defn load-rotor [path fn]
;   (.ImportMesh bjs/SceneLoader ""
;                path
;                fn
;                main-scene/scene
;                #(slot-rotor-loaded %1 %2 %3)))

(defn init-gui []
  (let [left-plane (bjs/Mesh.CreatePlane. "left-plane" 2)
        left-adv-texture (bjs-gui/AdvancedDynamicTexture.CreateForMesh. left-plane 1024 1024)
        left-pnl (bjs-gui/StackPanel.)
        left-hdr (bjs-gui/TextBlock.)
        bwd-btn (bjs-gui/Button.CreateImageButton. "bwd-spin" "bwd" "textures/tux_tada.jpg")
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
    ; (-> bwd-btn .-onPointerUpObservable (.add (fn [value]
    ;                                            (prn "bwd the palace btn style")
    ;                                            (re-frame/dispatch [:rotor-anim-bwd]))))
    ; (.addControl left-pnl bwd-btn)
    (.addControl left-pnl left-hdr)
    ;;cb
    (set! (.-width cb) "100px")
    (set! (.-height cb) "100px")
    (-> cb .-onPointerUpObservable (.add (fn [value]
                                           (re-frame/dispatch [:face-slot-anim-bwd]))))
                                           ; (println "anim from=" (.-from rotor-anim) ", to=" (.-to rotor-anim)))))
    (.addControl left-pnl cb)))

; (defn rotor-anim-bwd []
;   (condp = @*top-face*
;     0 (.play rotor-anim-0-7)
;     7 (.play rotor-anim-7-6)
;     6 (.play rotor-anim-6-5)
;     5 (.play rotor-anim-5-4)
;     4 (.play rotor-anim-4-3)
;     3 (.play rotor-anim-3-2)
;     2 (.play rotor-anim-2-1)
;     1 (.play rotor-anim-1-0))
;   (swap! *top-face* dec)
;   (when (< @*top-face* 0)
;     (swap! *top-face* (fn [x] 7)))
;   (println "*top-face=" *top-face*))

  ; (.play rotor-anim)
  ; (.start rotor-anim false 1 0 15)
  ; (.play rotor-anim-1-2)
  ; (.play rotor-anim-0-7))

(defn anim-bwd []
  (re-frame/dispatch [:rotor-anim-bwd "top" @*top-face*])
  (swap! *top-face* dec)
  (when (< @*top-face* 0)
    (swap! *top-face* (fn [x] 7)))
  (println "*top-face=" *top-face*))

; (defn rotor-anim-fwd []
;   (condp = @*top-face*
;     0 (.play rotor-anim-0-7)
;     7 (.play rotor-anim-7-6))
;   (swap! *top-face* dec)
;   (when (< @*top-face* 0)
;     (swap! *top-face* (fn [x] 7))))
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
  (let [rotor (-> main-scene/scene (.getMeshByName "top-rotor"))
        rotor-pos (.-position rotor)]
    (set! (.-position rotor) (.add rotor-pos (bjs/Vector3. 0 2 0)))))

(defn init-mid-rotor []
  (println "init-mid-rotor entered")
  (let [rotor (-> main-scene/scene (.getMeshByName "mid-rotor"))
        rotor-pos (.-position rotor)]
    (set! (.-position rotor) (.add rotor-pos (bjs/Vector3. 0 1 0)))))

(defn init []
  (println "face-slot-scene.init: entered")
  (let [light (bjs/PointLight. "pointLight" (bjs/Vector3. 0 5 -3) main-scene/scene)]
    (.setEnabled light true))
  (bjs/HemisphericLight. "hemiLight" (bjs/Vector3. 0 1 0) main-scene/scene)

  ; (load-rotor "models/slot_rotor/" "slot_rotor.glb"))
  ; (load-rotor "models/slot_rotor/" "slot_rotor.gltf")
  (re-frame/dispatch [:load-rotor
                      "models/slot_rotor/"
                      "slot_rotor.gltf"
                      "top"
                      (fn []
                        ; (println "hi"))])
                        (re-frame/dispatch [:init-top-rotor]))])
  (re-frame/dispatch [:load-rotor
                      "models/slot_rotor/"
                      "slot_rotor.gltf"
                      "mid"
                      (fn []
                        ; (println "hi"))])
                        (re-frame/dispatch [:init-mid-rotor]))])
  (init-gui))
