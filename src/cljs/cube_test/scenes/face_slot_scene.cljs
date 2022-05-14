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
   ; [cube-test.face-slot.rotor :as rotor]))

; (def ^:dynamic *top-face* (atom 5))
(def ^:dynamic *top-rotor-face* (atom 0))
(def ^:dynamic *mid-rotor-face* (atom 0))
(def ^:dynamic *bottom-rotor-face* (atom 0))
(def top-rotor-uniq-id)
(def mid-rotor-uniq-id)
(def bottom-rotor-uniq-id)
; (def ^:const rotor-width 0.4)
(def rotor-width 0.4)
; (def ^:const rotor-top-pos (bjs/Vector3. 0 2 0))
(def rotor-top-pos (bjs/Vector3. 0 2 0))
(def rotor-rot-snd)

(defn init-left-gui []
  (let [left-plane (bjs/Mesh.CreatePlane. "left-plane" 2)
        left-adv-texture (bjs-gui/AdvancedDynamicTexture.CreateForMesh left-plane 1024 1024)
        left-pnl (bjs-gui/StackPanel.)
        left-hdr (bjs-gui/TextBlock.)
        bwd-btn (bjs-gui/Button.CreateImageButton "bwd-spin" "bwd" "textures/tux_tada.jpg")
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
        right-adv-texture (bjs-gui/AdvancedDynamicTexture.CreateForMesh right-plane 1024 1024)
        right-pnl (bjs-gui/StackPanel.)
        right-hdr (bjs-gui/TextBlock.)
        bwd-btn (bjs-gui/Button.CreateImageButton "bwd-spin" "bwd" "textures/tux_tada.jpg")
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

    ; var button = BABYLON.GUI.Button.CreateImageButton()
    ;   "but",
    ;   "Click Me",
    ;   "textures/grass.png"))
    ;
(defn init-bottom-gui []
  (println "init-bottom-gui entered")
  (let [bottom-plane (bjs/Mesh.CreatePlane. "bottom-plane" 2)
        bottom-adv-texture (bjs-gui/AdvancedDynamicTexture.CreateForMesh bottom-plane 1024 1024)
        ; bottom-pnl (bjs-gui/StackPanel.)
        bottom-pnl (bjs-gui/Grid.)
        bottom-hdr (bjs-gui/TextBlock.)
        ; bwd-btn (bjs-gui/Button.CreateImageButton "super-bwd-btn" "bwd" "imgs/left_arrow_fat.png")
        bwd-btn (bjs-gui/Button.CreateImageButton "super-bwd-btn" "bwd" "imgs/left_arrow_fat_small.png")
        bwd-btn-img (.-image bwd-btn)
        ; bwd-btn (bjs-gui/Button.CreateImageWithCenterTextButton "super-bwd-btn" "bwd" "imgs/left_arrow_fat.png")
        ; fwd-btn (bjs-gui/Button.CreateImageButton. "super-fwd-btn" "fwd" "imgs/right_arrow_fat.png")
        fwd-btn (bjs-gui/Button.CreateImageButton "super-fwd-btn" "fwd" "imgs/right_arrow_fat_small.png")
        fwd-btn-img (.-image fwd-btn)
        cb-frame (bjs-gui/Checkbox.)
        cb-rnd (bjs-gui/Checkbox.)
        cb-bottom (bjs-gui/Checkbox.)
        frame-txt (bjs-gui/TextBlock.)
        rnd-txt (bjs-gui/TextBlock.)]
    ; (set! (.-position))
    ; (set! (.-position bottom-plane) (bjs/Vector3. 0 4.0 0.4))
    (set! (.-position bottom-plane) (bjs/Vector3. 0.2 4.0 0.4))
    ; (set! (.-isVertical bottom-pnl) false)
    (.addRowDefinition bottom-pnl 0.25 false)
    (.addRowDefinition bottom-pnl 0.25)
    (.addRowDefinition bottom-pnl 0.25)
    (.addRowDefinition bottom-pnl 0.25)
    (.addColumnDefinition bottom-pnl 0.5)
    (.addColumnDefinition bottom-pnl 0.5)
    ; (.addColumnDefinition bottom-pnl 0.33)
    (.addControl bottom-adv-texture bottom-pnl)
    (set! (.-text bottom-hdr) "Super Rotate")
    (set! (.-height bottom-hdr) "100px")
    (set! (.-color bottom-hdr) "white")
    (set! (.-textHorizontalAlignment bottom-hdr) bjs-gui/Control.HORIZONTAL_ALIGNMENT_CENTER)
    (set! (.-fontSize bottom-hdr) "80")
    (set! (.-horizontalAlignment bottom-pnl) bjs-gui/StackPanel.HORIZONTAL_ALIGNMENT_CENTER)
    (set! (.-verticalAlignment bottom-pnl) bjs-gui/StackPanel.VERTICAL_ALIGNMENT_CENTER)
    (.addControl bottom-pnl bottom-hdr 0 0)
    ;; bwd-btn
    ; (set! (.-width bwd-btn) 100)
    ; (set! (.-height bwd-btn) 100)
    (set! (.-autoScale bwd-btn-img) true)
    ; (set! (.-left bwd-btn) "50px")))
    (set! (.-horizontalAlignment bwd-btn) bjs-gui/Control.HORIZONTAL_ALIGNMENT_LEFT)
    (set! (.-verticalAlignment bwd-btn) bjs-gui/Control.VERTICAL_ALIGNMENT_TOP)
    (-> bwd-btn .-onPointerUpObservable (.add (fn [value]
                                                  ; (println "super backward")
                                                  (re-frame/dispatch [:face-slot-super-anim-bwd]))))
    (.addControl bottom-pnl bwd-btn 1 0)
    ; (println "descendents=" (.getDescendents bwd-btn))
    ; (.addControl bottom-adv-texture bwd-btn)
    (set! (.-horizontalAlignment fwd-btn) bjs-gui/Control.HORIZONTAL_ALIGNMENT_RIGHT)
    (set! (.-verticalAlignment fwd-btn) bjs-gui/Control.VERTICAL_ALIGNMENT_TOP)
    (set! (.-autoScale fwd-btn-img) true)
    (-> fwd-btn .-onPointerUpObservable (.add (fn [value]
                                                  ; (println "super backward")
                                                  (re-frame/dispatch [:face-slot-super-anim-fwd]))))
    (.addControl bottom-pnl fwd-btn 1 2)
    ; (.addControl bottom-adv-texture fwd-btn)))
    ;; cb-frame
    (set! (.-width cb-frame) "100px")
    (set! (.-height cb-frame) "100px")
    (-> cb-frame .-onPointerUpObservable (.add (fn [value]
                                                  ; (println "super backward")
                                                  (re-frame/dispatch [:toggle-rotor-frame]))))
    (.addControl bottom-pnl cb-frame 2 0)
    (set! (.-text frame-txt) "Toggle Frame")
    (set! (.-fontSize frame-txt) "80")
    (set! (.-color frame-txt) "white")
    (.addControl bottom-pnl frame-txt 2 1)

    ;; cb-rnd
    (set! (.-width cb-rnd) "100px")
    (set! (.-height cb-rnd) "100px")
    (-> cb-rnd .-onPointerUpObservable (.add (fn [value]
                                               (re-frame/dispatch [:randomize-rotor]))))
    (.addControl bottom-pnl cb-rnd 3 0)
    (set! (.-text rnd-txt) "Randomize")
    (set! (.-fontSize rnd-txt) "80")
    (set! (.-color rnd-txt) "white")
    (.addControl bottom-pnl rnd-txt 3 1)))


(defn init-gui []
  (init-left-gui)
  (init-right-gui)
  (init-bottom-gui))

; (defn anim-bwd [hlq]
;   ; (println "face-slot-scene: anim-bwd entered")
;   (re-frame/dispatch [:rotor-anim-bwd hlq @*top-rotor-face*])
;   (swap! *top-rotor-face* dec)
;   (when (< @*top-rotor-face* 0)
;     (swap! *top-rotor-face* (fn [x] 7)))
;   (println "*top-rotor-face=" *top-rotor-face*))
(defn rotor-frame-loaded [new-meshes particle-systems skeletons anim-groups user-cb]
  ; (js-debugger)
  (println "slot-rotor-loaded: new-meshes=" new-meshes)
  (doall (map #(do
                 (when (= (.-name %1) "frame")
                   (set! (.-position %1) (bjs/Vector3. 0 2.55 -1))
                   (.setEnabled %1 false)))
              new-meshes)))

(defn load-rotor-frame [path file user-cb]
  (println "face-slot-scene.load-rotor-frame: path=" path ", file=" file)
  (.ImportMesh bjs/SceneLoader ""
               path
               file
               main-scene/scene
               #(rotor-frame-loaded %1 %2 %3 %4 user-cb)))

(defn toggle-rotor-frame []
  (println "now toggling rotor-frame")
  (let [frame (-> main-scene/scene (.getMeshByName "frame"))]
    (if (.isEnabled frame)
      (.setEnabled frame false)
      (.setEnabled frame true))))

(defn delayed-rot-fwd [n hlq]
  (js/setTimeout #(do (re-frame/dispatch [:face-slot-anim-fwd hlq true])) (* n 500)))

(defn delayed-rot-bwd [n hlq]
  (js/setTimeout #(do (re-frame/dispatch [:face-slot-anim-bwd hlq true])) (* n 500)))

(defn randomize-rotor []
  (println "face-slot-scene: randomize-rotor")
  (let [*max-rot* (atom 0)]
    (let [r (for [i (range (rand 8))] [i])
          rots (reduce into [] r)]
      ; (re-frame/dispatch [:rotor-play-rot-snd])
      (doall (for [i rots]
               (delayed-rot-fwd i :top)))
      (if (> (count rots) @*max-rot*) (swap! *max-rot* (fn [x] (count rots)))))

    ; (js/setTimeout #(do (re-frame/dispatch [:rotor-stop-rot-snd])) (* (count rots) 500)))
    (let [r (for [i (range (rand 8))] [i])
          rots (reduce into [] r)]
      ; (re-frame/dispatch [:rotor-play-rot-snd])
      (doall (for [i rots]
               (delayed-rot-bwd i :mid)))
      (if (> (count rots) @*max-rot*) (swap! *max-rot* (fn [x] (count rots)))))
      ; (js/setTimeout #(do (re-frame/dispatch [:rotor-stop-rot-snd])) (* (count rots) 500)))
    (let [r (for [i (range (rand 8))] [i])
          rots (reduce into [] r)]
      ; (re-frame/dispatch [:rotor-play-rot-snd])
      (doall (for [i rots]
               (delayed-rot-fwd i :bottom)))
      (if (> (count rots) @*max-rot*) (swap! *max-rot* (fn [x] (count rots)))))
    ; (js/setTimeout #(do (re-frame/dispatch [:rotor-stop-rot-snd])) (* (count rots) 500))))
    (println "randomize-rotor: *max-rot*=" @*max-rot*)
    ; (swap! rotor/*auto-stop-rotor-snds* false)
    ;; turn off auto stopping of sound on individual rotor anims.
    (re-frame/dispatch [:rotor-auto-stop-rotor-snds false])
    (re-frame/dispatch [:rotor-play-rot-snd])
    (let [timer-pop (* @*max-rot* 500)]
      (js/setTimeout #(do (re-frame/dispatch [:rotor-stop-rot-snd])) timer-pop)
      ;; turn auto-stopping back on at end of the collective animation
      (js/setTimeout #(re-frame/dispatch [:rotor-auto-stop-rotor-snds true]) timer-pop))))
  ; (re-frame/dispatch [:face-slot-anim-fwd :top])
  ; (js/setTimeout #(do (re-frame/dispatch [:face-slot-anim-fwd :top])) 500)
  ; (js/setTimeout #(do (re-frame/dispatch [:face-slot-anim-fwd :top])) 1000))
  ; (doall (for [i [0 1]]
  ;          ; (println "hi, i=" i)
  ;          (re-frame/dispatch [:face-slot-anim-fwd :top]))))

(defn anim-bwd
  ([hlq]
   (anim-bwd hlq nil))
  ([hlq mute]
   ; (println "face-slot-scene.anim-bwd: mute=" mute)
   (condp = hlq
     :top (do
            (re-frame/dispatch [:rotor-anim-bwd hlq @*top-rotor-face* mute])
            (swap! *top-rotor-face* dec)
            (when (< @*top-rotor-face* 0)
              (swap! *top-rotor-face* (fn [x] 7))))
     :mid (do
            (re-frame/dispatch [:rotor-anim-bwd hlq @*mid-rotor-face* mute])
            (swap! *mid-rotor-face* dec)
            (when (< @*mid-rotor-face* 0)
              (swap! *mid-rotor-face* (fn [x] 7))))
     :bottom (do
               (re-frame/dispatch [:rotor-anim-bwd hlq @*bottom-rotor-face* mute])
               (swap! *bottom-rotor-face* dec)
               (when (< @*bottom-rotor-face* 0)
                 (swap! *bottom-rotor-face* (fn [x] 7)))))))

(defn anim-fwd
  ([hlq]
   (anim-fwd hlq nil))
  ([hlq mute]
   (condp = hlq
     :top (do
            (println "*top-rotor-face=" *top-rotor-face* ",mute=" mute)
            (re-frame/dispatch [:rotor-anim-fwd hlq @*top-rotor-face* mute])
            (swap! *top-rotor-face* inc)
            (when (> @*top-rotor-face* 7)
              (swap! *top-rotor-face* (fn [x] 0))))
     :mid (do
            (re-frame/dispatch [:rotor-anim-fwd hlq @*mid-rotor-face* mute])
            (swap! *mid-rotor-face* inc)
            (when (> @*mid-rotor-face* 7)
              (swap! *mid-rotor-face* (fn [x] 0))))
     :bottom (do
               (re-frame/dispatch [:rotor-anim-fwd hlq @*bottom-rotor-face* mute])
               (swap! *bottom-rotor-face* inc)
               (when (> @*bottom-rotor-face* 7)
                 (swap! *bottom-rotor-face* (fn [x] 0)))))))

; (defn super-anim-bwd []
;   (println "event: super-anim-bwd")
;   (re-frame/dispatch [:face-slot-anim-bwd :top])
;   (re-frame/dispatch [:face-slot-anim-bwd :mid])
;   (re-frame/dispatch [:face-slot-anim-bwd :bottom]))
;
; (defn super-anim-fwd []
;   (println "event: super-anim-fwd")
;   (re-frame/dispatch [:face-slot-anim-fwd :top])
;   (re-frame/dispatch [:face-slot-anim-fwd :mid])
;   (re-frame/dispatch [:face-slot-anim-fwd :bottom]))

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

(defn init-snd []
  (re-frame/dispatch [:rotor-init-snd]))
  ; (set! rotor-rot-snd (bjs/Sound.
  ;                          "tile-selected"
  ;                          ; "sounds/104532__skyumori__door-open-01.wav"
  ;                          "sounds/bicycle-rotating.ogg"
  ;                          main-scene/scene)))

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
  (re-frame/dispatch [:load-rotor-frame
                      "models/slot_rotor/"
                      "rotor_frame.gltf"
                      (fn []
                        (println "hi from load-rotor-frame cb"))])
                        ; (re-frame/dispatch [:init-bottom-rotor]))])
  (init-gui)
  (init-snd))
