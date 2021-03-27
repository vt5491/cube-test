(ns cube-test.msg-cube.msg-cube-scene
  (:require
   [re-frame.core :as re-frame]
   [cube-test.subs :as subs]
   [babylonjs :as bjs]
   [babylonjs-gui :as bjs-gui]
   [cube-test.main-scene :as main-scene]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.utils.fps-panel :as fps-panel]
   [cube-test.msg-cube.data.msg :as msg]))

(def canvas)
(def camera)
(def engine)
(def scene)
(def sphere)
(def env)
(def red-mat)
(def green-mat)
(def blue-mat)
(def grnd)
(def gui-plane)

(def msgs @(re-frame/subscribe [:msgs]))

; (defn ^:dev/after-load create-grnd [])
(defn create-grnd []
  (println "msg-cube-scene: create-grnd entered")
  (let [grnd (bjs/MeshBuilder.CreateGround "ground" (js-obj "width" 10 "height" 10 "subdivisions" 10))]
    (set! (.-material grnd) green-mat)
    (set! (.-ground env) grnd)))

(defn ^:dev/after-load init-gui []
; (defn init-gui []
  (println "now in init-gui")
  ; (when gui-plane
  ;   ())
  (let [top-plane (.getMeshByID main-scene/scene "top-plane")]
    (when top-plane
      (.dispose top-plane)))
  (let [top-plane (bjs/Mesh.CreatePlane. "top-plane" 5)
        top-adv-texture (bjs-gui/AdvancedDynamicTexture.CreateForMesh. top-plane 3048 2048)
        top-pnl (bjs-gui/Grid.)
        top-hdr (bjs-gui/TextBlock.)
        add-msg-btn (bjs-gui/Button.CreateSimpleButton. "add-msg-btn" "add")
        max-id-btn (bjs-gui/Button.CreateSimpleButton. "max-id-btn" "inc max-id")]
    (set! (.-position top-plane)(bjs/Vector3. 0 6 2))
    (set! (.-renderOutline top-plane) true)
    (set! gui-plane top-plane)
    (.addControl top-adv-texture top-pnl)
    (set! (.-text top-hdr) "commands:")
    ; (set! (.-height top-hdr) "120px")
    (set! (.-fontSize top-hdr) "200")
    (set! (.-color top-hdr) "white")
    ; (set! (.-autoScale top-hdr) true)
    ;; create 5 rows and 3 cols
    (.addRowDefinition top-pnl 0.33 false)
    (.addRowDefinition top-pnl 0.33)
    (.addRowDefinition top-pnl 0.33)
    ; (.addRowDefinition top-pnl 0.20)
    ; (.addRowDefinition top-pnl 0.20)
    (.addColumnDefinition top-pnl 0.5)
    (.addColumnDefinition top-pnl 0.5)
    ; (.addColumnDefinition top-pnl 0.33)
    (.addControl top-pnl top-hdr 0 0)
    ;; add-btn
    ; (set! (.-autoScale add-msg-btn) true)
    (set! (.-fontSize add-msg-btn) "150")
    (set! (.-color add-msg-btn) "white")
    (-> add-msg-btn .-onPointerUpObservable (.add (fn [value]
                                                    (println "add-msg-btn clicked")
                                                    (re-frame/dispatch [:msg-cube.add-msg { :text "hi"}]))))
    (.addControl top-pnl add-msg-btn 1 2)
    ;; max-id btn
    (set! (.-fontSize max-id-btn) "150")
    (set! (.-color max-id-btn) "white")
    (-> max-id-btn .-onPointerUpObservable (.add (fn [value]
                                                   (println "max-id-btn clicked")
                                                   (re-frame/dispatch [:msg-cube.inc-max-id]))))
    (.addControl top-pnl max-id-btn 0 1)))

(defn init-max-id-sub []
  (println "init-max-id-sub: pre")
  @(re-frame/subscribe [:msgs-cnt])
  (println "init-max-id-sub: post"))

(defn add-mesh-pick-action [mesh]
  (set! (.-actionManager mesh) (bjs/ActionManager. main-scene/scene))

  (let [eca (bjs/ExecuteCodeAction. (js-obj "trigger" bjs/ActionManager.OnPickTrigger))
        mesh-change-mat-f
          (fn [e]
            (do (let [mesh (.-meshUnderPointer e)
                      mesh-id (.-id mesh)
                      ; box-num (msg/extract-msg-box-num mesh-id)
                      msg-id (msg/extract-id mesh-id)]
                  (println "trigger-handler: msg-id=" msg-id)
                  ; (js-debugger)
                  (set! (-> (.-meshUnderPointer e) (.-material)) main-scene/blue-mat)
                  (re-frame/dispatch [:msg-cube.inc-level msg-id]))))]
    (set! (.-func eca) mesh-change-mat-f)
    (.registerAction (.-actionManager mesh) eca)))

(defn add-msg-cube [msg]
  (println "add-msg-cube: entered, msg=" msg)
  ; (when (and id (> id 0)))
  (let [id (msg :id)]
    (when (and id (> id 0))
      (let [scene main-scene/scene
            msg-cube (bjs/MeshBuilder.CreateBox (str "mc-" id) (js-obj "height" 1 "width" 1) scene)
            pos (bjs/Vector3. (* id 1.1) 0 0)]
        (set! (.-position msg-cube) pos)
        (add-mesh-pick-action msg-cube))))

  ;; return nil because this is a pure side effect
  nil)
      ; (set! (.-position msg-cube) (bjs/Vector3. (* id 1.1) 0 0)))))

; (defn update-msg-cube [id])
(defn update-msg-cube
  ([id]
   (update-msg-cube id nil nil))
  ([id level]
   (update-msg-cube id level nil))
  ; ([id _ text]
  ;  (update-msg-cube id nil text))
  ([id level text]
   (println "msg-cube-scene.update-msg-cube: id=" id ", level=" level ", text=" text)
   (when main-scene/scene
     (let [mesh (-> main-scene/scene (.getMeshByName (str "mc-" id)))]
           ; red-mat (bjs/StandardMaterial. "red-mat" scene)]
       (when mesh
         (set! (-> mesh (.-material)) main-scene/red-mat))))))


; (defn ^:dev/after-load init [])
(defn init []
  (println "msg-cube-scene.init: entered")
  (let [scene main-scene/scene]
      (let [light1 (bjs/HemisphericLight. "light1" (bjs/Vector3. 1 1 0) scene)
            light2 (bjs/PointLight. "light2" (bjs/Vector3. 0 1 -1) scene)
            sph (bjs/MeshBuilder.CreateSphere "sphere" (js-obj "diameter" 1) scene)]
        (set! sphere sph))
    (set! env (bjs/EnvironmentHelper.
               (js-obj
                "createGround" false
                "skyboxSize" 30)
               scene))
    (set! red-mat (bjs/StandardMaterial. "red-mat" scene))
    (set! (.-diffuseColor red-mat) (bjs/Color3. 1 0 0))
    (set! green-mat (bjs/StandardMaterial. "green-mat" scene))
    (set! (.-diffuseColor green-mat) (bjs/Color3. 0 1 0))
    (set! blue-mat (bjs/StandardMaterial. "blue-mat" scene))
    (set! (.-diffuseColor blue-mat) (bjs/Color3. 0 0 1))

    (set! (.-material sphere) green-mat)

    ; (create-grnd)

    (init-gui)
    (init-max-id-sub)
    (bjs/Debug.AxesViewer. scene)))
    ; @(re-frame/subscribe [:msgs-change])))

; (defn run-render-loop []
;   (println "msg-cube-scene: run-render-loop: engine=" engine)
;   (.runRenderLoop engine (fn [] (.render scene))))

(defn render-loop []
  (if (= main-scene/xr-mode "vr")
    (controller/tick)
    (controller-xr/tick))
  (if fps-panel/fps-pnl
    (fps-panel/tick main-scene/engine))
  (.render main-scene/scene))

(defn run-scene []
  ; (run-render-loop)
  (.runRenderLoop main-scene/engine (fn [] (render-loop))))
