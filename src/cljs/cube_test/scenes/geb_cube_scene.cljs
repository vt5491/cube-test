;; reference few, accessible by many.
(ns cube-test.scenes.geb-cube-scene
  ; (:require-macros [cube-test.macros :as macros])
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [babylonjs-gui :as bjs-gui]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.utils.fps-panel :as fps-panel]
   [cube-test.base :as base]
   [cube-test.utils :as utils]))

; (def x-fwd-shadow-gen)
(def x-fwd-shadow-gen)
(def y-fwd-shadow-gen)
(def y-bwd-shadow-gen)
(def z-fwd-shadow-gen)
(def x-fwd-light)
(def y-fwd-light)
(def y-bwd-light)
(def z-fwd-light)
(def shadow-light-intensity 0.5)
(def ^:dynamic *active-triplet* (atom (fn [x] nil)))

(declare init-triplet-cube)
;; loads
(defn triplet-cube-loaded [meshes particle-systems skeletons anim-groups name user-cb]
  (println "triplet-cube-loaded")
  (doall (map #(do
                 (when (re-matches #"__root__" (.-id %1))
                     ; (set! (.-name %1) "rubiks-cube")
                     ; (set! (.-name %1) "geb-cube")
                     ; (set! (.-id %1) "geb-cube")
                     (set! (.-name %1) name)
                     (set! (.-id %1) name)
                     ;; the neg. on the x is needed otherwise the materials are "flipped"
                     ; (set! (.-scaling %1)(bjs/Vector3. -0.3 0.3 0.3))
                     (set! (.-position %1)(bjs/Vector3. 0 2 0))))
                     ; (set! shadow-generator (bjs/ShadowGenerator. 1024 x-fwd-light))
                     ; (.addShadowCaster shadow-generator %1)))
              meshes))
  (when user-cb (user-cb name)))

(defn load-triplet-cube [path file name user-cb]
  (.ImportMesh bjs/SceneLoader ""
               path
               file
               main-scene/scene
               #(triplet-cube-loaded %1 %2 %3 %4 name user-cb)))

;; inits
; var light = new BABYLON.DirectionalLight("DirectionalLight",)
; new BABYLON.Vector3(0, -1, 0), scene;
; var plane = BABYLON.MeshBuilder.CreatePlane("plane",
; {height:1, width: 0.665, sideOrientation: BABYLON.Mesh.DOUBLESIDE, frontUVs: f, backUVs: b}, scene;
(defn init-screens []
  (let [scene main-scene/scene
        rs-parms (js-obj "height" 4 "width" 4 "depth" 0.1)
        right-screen (bjs/MeshBuilder.CreateBox.
                      "right-screen"
                      (js-obj "height" 4 "width" 4 "depth" 0.1)
                      main-scene/scene)
        ; top-screen (bjs/MeshBuilder.CreateBox.
        ;               "top-screen"
        ;               (js-obj "height" 4 "width" 4 "depth" 0.1)
        ;               main-scene/scene)
        bottom-screen (bjs/MeshBuilder.CreateBox.
                       "bottom-screen"
                       (js-obj "height" 4 "width" 4 "depth" 0.1)
                       main-scene/scene)
        rear-screen (bjs/MeshBuilder.CreateBox.
                      "rear-screen"
                      (js-obj "height" 4 "width" 4 "depth" 0.1)
                      main-scene/scene)
        ; right-screen (bjs/MeshBuilder.CreateBox. "right-screen" rs-parms main-scene/scene)
        x-quat90 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.X (* base/ONE-DEG 90)))
        y-quat90 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 90)))]
    (set! (.-material right-screen) main-scene/black-mat)
    ; (set! (.-position right-screen) (bjs/Vector3. 4 1.5 0.4))
    (set! (.-position right-screen) (bjs/Vector3. 4 1.5 0.4))
    (set! (.-rotationQuaternion right-screen) y-quat90)
    (set! (.-receiveShadows right-screen) true)
    ; (set! (.-sideOrientation right-screen) bjs/Mesh.DOUBLESIDE)
    ; (set! (.-backfaceCulling (.-material right-screen)) false)
    (println "screen.receive shadows=" (.-receiveShadows right-screen))
    ;; top screen
    ; (set! (.-material top-screen) main-scene/black-mat)
    ; (set! (.-position top-screen) (bjs/Vector3. 0 4 0))
    ; (set! (.-rotationQuaternion top-screen) x-quat90)
    ; (set! (.-receiveShadows top-screen) true)
    ; ; (set! (.-enabled top-screen) false)
    ; (set! (.-visibility top-screen) 0)
    ;; bottom screen
    (set! (.-material bottom-screen) main-scene/black-mat)
    (set! (.-position bottom-screen) (bjs/Vector3. 0 0 0))
    (set! (.-rotationQuaternion bottom-screen) x-quat90)
    (set! (.-receiveShadows bottom-screen) true)
    ; (set! (.-visibility top-screen) 0)
    ;; rear screen
    (set! (.-material rear-screen) main-scene/black-mat)
    (set! (.-position rear-screen) (bjs/Vector3. 0 1.5 4))
    (set! (.-rotationQuaternion right-screen) y-quat90)
    (set! (.-receiveShadows rear-screen) true)))

(defn init-lights []
  (let [xfl (bjs/DirectionalLight.
                     "x-fwd-light"
                     (bjs/Vector3. 1 0 0)
                     main-scene/scene)
        yfl (bjs/DirectionalLight.
                     "y-fwd-light"
                     (bjs/Vector3. 0 1 0)
                     main-scene/scene)
        ybl (bjs/DirectionalLight.
                     "y-bwd-light"
                     (bjs/Vector3. 0 -1 0)
                     main-scene/scene)
        zfl (bjs/DirectionalLight.
                     "z-fwd-light"
                     (bjs/Vector3. 0 0 1)
                     main-scene/scene)]
    (set! (.-intensity xfl) shadow-light-intensity)
    (set! x-fwd-light xfl)
    (set! (.-intensity yfl) shadow-light-intensity)
    (set! y-fwd-light yfl)
    (set! (.-intensity zfl) shadow-light-intensity)
    (set! z-fwd-light zfl)
    (set! (.-intensity ybl) shadow-light-intensity)
    (set! (.-position ybl) (bjs/Vector3. 0 10 0))
    (set! y-bwd-light ybl)
    (set! (.-position xfl) (bjs/Vector3. -2 0 0))
    (set! (.-position zfl) (bjs/Vector3. 0 0 -2))))

; (defn add-radio [text-blk text parent]
;   (println "hi from add-radio, text=" text)
;   (let [btn (bjs-gui/RadioButton.)
;         text-blk (bjs-gui/TextBlock.)]
;     (set! (.-width btn) "20px")
;     (set! (.-height btn) "20px")
;     (set! (.-color btn) "white")
;     (set! (.-autoScale btn) true)
;     ; (set! (.-fontSize btn) "100")
;     (-> btn
;         .-onIsCheckedChangedObservable
;         (.add
;          (fn [state]
;            (when state
;              (set! (.-text text-blk) (str "you selected " text))))))
;     (set! (.-height text-blk) "50px")))

; var header = BABYLON.GUI.Control.AddHeader(button, text, "100px", { isHorizontal: true, controlFirst: true});
; header.height = "30px";
(defn triplet-btn-handler [text]
  (let [triplet-name (str text "-cube")
        triplet-fn (str text "_cube.glb")
        triplet-cube (.getNodeByName main-scene/scene triplet-name)]
    (println "triplet-btn-handler: triplet-name=" triplet-name ", triplet-cube=" triplet-cube)
    ;; hide current triplet
    (let [current-triplet (.getNodeByName main-scene/scene @*active-triplet*)]
      (.setEnabled current-triplet false)
      (swap! *active-triplet* (fn [x] triplet-name))
      (println "triplet-btn-handler: active-triplet=" @*active-triplet*))
    (if-not triplet-cube
      (do
        (println "now loading " triplet-name)
        (load-triplet-cube
         "models/geb_cube/"
         triplet-fn
         triplet-name
         init-triplet-cube))
      (do
        (init-triplet-cube triplet-name)
        ;; and make new visible if not already
        (.setEnabled triplet-cube true)))))

(defn init-gui []
  (let [gui-plane (bjs/Mesh.CreatePlane. "gui-plane" 2)
        gui-adv-texture (bjs-gui/AdvancedDynamicTexture.CreateForMesh gui-plane 1024 1024)
        ; gui-adv-texture (bjs-gui/AdvancedDynamicTexture.CreateForMesh gui-plane 512 512)
        gui-pnl (bjs-gui/Grid.)
        gui-hdr (bjs-gui/TextBlock.)
        cube-radio-text-blk (bjs-gui/TextBlock.)
        add-radio (fn f
                    ([text parent row col]
                     (f text parent row col false))
                    ([text parent row col is-checked]
                     (let [btn (bjs-gui/RadioButton.)
                           ; text-blk (bjs-gui/TextBlock.)
                           header (bjs-gui/Control.AddHeader
                                   btn
                                   text
                                   "120px"
                                   (js-obj "isHorizontal" "true"
                                           "controlFirst" "true"))]
                       ; (set! (.-font header) "50px")
                       (set! (.-width btn) "40px")
                       (set! (.-height btn) "40px")
                       (set! (.-color btn) "white")
                       (when is-checked
                         (set! (.-isChecked btn) true))
                       (-> btn
                           .-onIsCheckedChangedObservable
                           (.add
                            (fn [state]
                              (when state
                                (println "state=" state ",text=" text)
                                ; (set! (.-text cube-radio-text-blk) (str "you selected " text))
                                ; (triplet-btn-handler (.-text cube-radio-text-blk))
                                (triplet-btn-handler text)))))
                       ; (set! (.-height header) "30px")
                       (set! (.-height header) "100px")
                       (set! (.-width header) "250px")
                       (set! (.-fontSize header) "50px")
                       ; (set! (.-autoScale header) true)
                       (set! (.-color header) "white")
                       (.addControl parent header row col))))]

        ; cube-radio-btn (bjs-gui/RadioButton.)]
    ; (set! (.-position gui-plane)(bjs/Vector3. 0 6 -1))
    (set! (.-position gui-plane)(bjs/Vector3. 0 4 -9))
    (.addControl gui-adv-texture gui-pnl)
    ;; gui-pnl
    ; (set! (.-height gui-pnl) "100px")
    ;; gui-hdr
    (set! (.-text gui-hdr) "box type")
    (set! (.-height gui-hdr) "100px")
    (set! (.-fontSize gui-hdr) "80")
    (set! (.-color gui-hdr) "white")
    ;; create 4 rows and 3 cols
    (.addRowDefinition gui-pnl 0.25 false)
    (.addRowDefinition gui-pnl 0.25)
    (.addRowDefinition gui-pnl 0.25)
    (.addRowDefinition gui-pnl 0.25)
    (.addColumnDefinition gui-pnl 0.33)
    (.addColumnDefinition gui-pnl 0.33)
    (.addColumnDefinition gui-pnl 0.33)
    (.addControl gui-pnl gui-hdr 0 0)
    ;; text-block
    (set! (.-height cube-radio-text-blk) "50px")
    (.addControl gui-pnl cube-radio-text-blk  1 0)
    ;; cube radio btn
    ; (add-radio cube-radio-text-blk "geb" gui-pnl)
    (add-radio "geb" gui-pnl 1 0 false)
    (add-radio "iwp" gui-pnl 1 1 false)
    (add-radio "vat" gui-pnl 1 2 true)))

   ; addRadio("option 1", panel)));
; var shadowGenerator2 = new BABYLON.ShadowGenerator(1024, light2);
(defn init-triplet-cube [triplet-name]
  (println "now in init-triplet-cube, triplet-name=" triplet-name)
  ; (let [triplet-cube (.getNodeByName main-scene/scene "geb-cube")])
  (let [triplet-cube (.getNodeByName main-scene/scene triplet-name)]
    (set! x-fwd-shadow-gen (bjs/ShadowGenerator. 1024 x-fwd-light))
    (.addShadowCaster x-fwd-shadow-gen triplet-cube)
    (set! y-fwd-shadow-gen (bjs/ShadowGenerator. 1024 y-fwd-light))
    (.addShadowCaster y-fwd-shadow-gen triplet-cube)
    (set! y-bwd-shadow-gen (bjs/ShadowGenerator. 1024 y-bwd-light))
    (.addShadowCaster y-bwd-shadow-gen triplet-cube)
    (set! z-fwd-shadow-gen (bjs/ShadowGenerator. 1024 z-fwd-light))
    (.addShadowCaster z-fwd-shadow-gen triplet-cube)))

(defn init []
  (println "geb-cube-scene.init: entered")
  ; (set! shadow-generator (bjs/ShadowGenerator. 1024))
  (let [light (bjs/PointLight. "pointLight" (bjs/Vector3. 0 5 -3) main-scene/scene)]
    (.setEnabled light false))
  (swap! *active-triplet* (fn [x] "vat-cube"))
  (load-triplet-cube
   "models/geb_cube/"
   ; "geb_cube.glb"
   "vat_cube.glb"
   ; "geb-cube"
   "vat-cube"
   init-triplet-cube)
   ; (fn [] (do
   ;          (println "now in load cb")
   ;          (let [triplet-cube (.getNodeByName main-scene/scene "geb-cube")]
   ;            (set! x-fwd-shadow-gen (bjs/ShadowGenerator. 1024 x-fwd-light))
   ;            (.addShadowCaster x-fwd-shadow-gen triplet-cube)
   ;            (set! y-fwd-shadow-gen (bjs/ShadowGenerator. 1024 y-fwd-light))
   ;            (.addShadowCaster y-fwd-shadow-gen triplet-cube)
   ;            (set! y-bwd-shadow-gen (bjs/ShadowGenerator. 1024 y-bwd-light))
   ;            (.addShadowCaster y-bwd-shadow-gen triplet-cube)
   ;            (set! z-fwd-shadow-gen (bjs/ShadowGenerator. 1024 z-fwd-light))
   ;            (.addShadowCaster z-fwd-shadow-gen triplet-cube)))))
  (init-screens)
  (init-lights)
  (init-gui))

;; render
(defn render-loop []
  ; (println "face-slot-scene: render-loop")
  (if (= main-scene/xr-mode "vr")
    (controller/tick)
    (controller-xr/tick))
  ; (controller-xr/tick)
  (fps-panel/tick main-scene/engine)
  ; (when @*cell-action-pending*
  ;   ; (rot-cells)
  ;   (re-frame/dispatch [:vrubik-rot-cells-combo]))
  ; (let [action-cells @*action-cells*]
  ;   (when (and action-cells (> (count action-cells) 0) (nth action-cells 0))
  ;     (swap! *action-cells* rot-cells)))
  (.render main-scene/scene))

(defn run-scene []
  (println "triplet-cube-scene.run-scene: entered")
  (.runRenderLoop main-scene/engine (fn [] (render-loop))))
