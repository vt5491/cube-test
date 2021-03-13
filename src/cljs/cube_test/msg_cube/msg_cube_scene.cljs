(ns cube-test.msg-cube.msg-cube-scene
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
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
(def green-mat)
(def blue-mat)
(def grnd)

; (defn ^:dev/after-load create-grnd [])
(defn create-grnd []
  (println "msg-cube-scene: create-grnd entered")
  (let [grnd (bjs/MeshBuilder.CreateGround "ground" (js-obj "width" 10 "height" 10 "subdivisions" 10))]
    (set! (.-material grnd) green-mat)
    (set! (.-ground env) grnd)))

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
    (set! green-mat (bjs/StandardMaterial. "green-mat" scene))
    (set! (.-diffuseColor green-mat) (bjs/Color3. 0 1 0))
    (set! blue-mat (bjs/StandardMaterial. "blue-mat" scene))
    (set! (.-diffuseColor blue-mat) (bjs/Color3. 0 0 1))

    (set! (.-material sphere) blue-mat)

    ; (create-grnd)

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
