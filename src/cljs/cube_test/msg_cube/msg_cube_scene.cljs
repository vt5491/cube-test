(ns cube-test.msg-cube.msg-cube-scene
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [babylonjs-gui :as bjs-gui]
   [cube-test.main-scene :as main-scene]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.utils.fps-panel :as fps-panel]))

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
        add-msg-btn (bjs-gui/Button.CreateSimpleButton. "add-msg-btn" "add")]
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

    (.addControl top-pnl add-msg-btn 1 2)))


; (defn ^:dev/after-load init [])
(defn init []
  (println "msg-cube-scene.init: entered")
  (let [scene main-scene/scene]
    ; (set! canvas (.getElementById js/document "renderCanvas"))
    ; (set! engine (bjs/Engine. canvas true))
    ; (set! scene (bjs/Scene.))
    ; (let [ninety-deg (/ js/Math.PI 2)]
    ;   (set! camera (bjs/ArcRotateCamera.
    ;                 "Camera"
    ;                 ninety-deg
    ;                 ninety-deg
    ;                 2
    ;                 (bjs/Vector3. 0 1 -8)
    ;                 scene)))
    ; (.attachControl camera canvas true)
    ; (.setTarget camera (bjs/Vector3. 0 0 0))
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
    (bjs/Debug.AxesViewer. scene)))

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
