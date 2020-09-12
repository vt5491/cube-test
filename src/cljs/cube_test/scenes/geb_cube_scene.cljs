;; reference few, accessible by many.
(ns cube-test.scenes.geb-cube-scene
  ; (:require-macros [cube-test.macros :as macros])
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [babylonjs-gui :as bjs-gui]
   ; [cube-test.controller :as controller]
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
;; loads
(defn geb-cube-loaded [meshes particle-systems skeletons anim-groups user-cb]
  (println "geb-cube-loaded")
  (doall (map #(do
                 (when (re-matches #"__root__" (.-id %1))
                     ; (set! (.-name %1) "rubiks-cube")
                     (set! (.-name %1) "geb-cube")
                     (set! (.-id %1) "geb-cube")
                     ;; the neg. on the x is needed otherwise the materials are "flipped"
                     ; (set! (.-scaling %1)(bjs/Vector3. -0.3 0.3 0.3))
                     (set! (.-position %1)(bjs/Vector3. 0 2 0))))
                     ; (set! shadow-generator (bjs/ShadowGenerator. 1024 x-fwd-light))
                     ; (.addShadowCaster shadow-generator %1)))
              meshes))
  (when user-cb (user-cb)))

(defn load-geb-cube [path file user-cb]
  (.ImportMesh bjs/SceneLoader ""
               path
               file
               main-scene/scene
               #(geb-cube-loaded %1 %2 %3 %4 user-cb)))

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
    (set! y-bwd-light ybl)))

    ; var button = new BABYLON.GUI.RadioButton();
    ; button.width = "20px";
    ; button.height = "20px";
    ; button.color = "white";
    ; button.background = "green";
    ;
    ; button.onIsCheckedChangedObservable.add(function(state) {})
    ;     if (state) {}
    ;         textblock.text = "You selected " + text));
    ;

    ; var textblock = new BABYLON.GUI.TextBlock();
    ;  textblock.height = "50px";
    ;  panel.addControl(textblock)));

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
(defn init-gui []
  (let [gui-plane (bjs/Mesh.CreatePlane. "gui-plane" 2)
        gui-adv-texture (bjs-gui/AdvancedDynamicTexture.CreateForMesh. gui-plane 1024 1024)
        ; gui-adv-texture (bjs-gui/AdvancedDynamicTexture.CreateForMesh. gui-plane 512 512)
        gui-pnl (bjs-gui/Grid.)
        gui-hdr (bjs-gui/TextBlock.)
        cube-radio-text-blk (bjs-gui/TextBlock.)
        add-radio (fn [text parent row col]
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
                      (-> btn
                          .-onIsCheckedChangedObservable
                          (.add
                           (fn [state]
                             (when state
                               (set! (.-text cube-radio-text-blk) (str "you selected " text))))))
                      ; (set! (.-height header) "30px")
                      (set! (.-height header) "100px")
                      (set! (.-width header) "250px")
                      (set! (.-fontSize header) "50px")
                      ; (set! (.-autoScale header) true)
                      (set! (.-color header) "white")
                      (.addControl parent header row col)))]

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
    (add-radio "geb" gui-pnl 1 0)
    (add-radio "vat" gui-pnl 1 1)))

   ; addRadio("option 1", panel)));
; var shadowGenerator2 = new BABYLON.ShadowGenerator(1024, light2);
(defn init []
  (println "geb-cube-scene.init: entered")
  ; (set! shadow-generator (bjs/ShadowGenerator. 1024))
  (let [light (bjs/PointLight. "pointLight" (bjs/Vector3. 0 5 -3) main-scene/scene)]
    (.setEnabled light false))
  (load-geb-cube
   "models/geb_cube/"
   ; "geb_cube.glb"
   "vat_cube.glb"
   (fn [] (do
            (println "now in load cb")
            (let [geb-cube (.getNodeByName main-scene/scene "geb-cube")]
              (set! x-fwd-shadow-gen (bjs/ShadowGenerator. 1024 x-fwd-light))
              (.addShadowCaster x-fwd-shadow-gen geb-cube)
              (set! y-fwd-shadow-gen (bjs/ShadowGenerator. 1024 y-fwd-light))
              (.addShadowCaster y-fwd-shadow-gen geb-cube)
              (set! y-bwd-shadow-gen (bjs/ShadowGenerator. 1024 y-bwd-light))
              (.addShadowCaster y-bwd-shadow-gen geb-cube)
              (set! z-fwd-shadow-gen (bjs/ShadowGenerator. 1024 z-fwd-light))
              (.addShadowCaster z-fwd-shadow-gen geb-cube)))))
  (init-screens)
  (init-lights)
  (init-gui))

;; render
(defn render-loop []
  ; (println "face-slot-scene: render-loop")
  ; (if (= main-scene/xr-mode "vr")
  ;   (controller/tick)
  ;   (controller-xr/tick))
  (controller-xr/tick)
  (fps-panel/tick main-scene/engine)
  ; (when @*cell-action-pending*
  ;   ; (rot-cells)
  ;   (re-frame/dispatch [:vrubik-rot-cells-combo]))
  ; (let [action-cells @*action-cells*]
  ;   (when (and action-cells (> (count action-cells) 0) (nth action-cells 0))
  ;     (swap! *action-cells* rot-cells)))
  (.render main-scene/scene))

(defn run-scene []
  (println "geb-cube-scene.run-scene: entered")
  (.runRenderLoop main-scene/engine (fn [] (render-loop))))
