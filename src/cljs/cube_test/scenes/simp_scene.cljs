(ns cube-test.scenes.simp-scene
  (:require
   [babylonjs :as bjs]
   [cube-test.base :as base]))

;; this is to create a scene without being dependent on main-scene in an attempt to get
;; a refreshable scene
(def canvas)
(def camera)
(def engine)
(def scene)
(def sphere)
(def env)
(def green-mat)
(def blue-mat)
(def grnd)

(declare init)

(comment
 (+ 1 1)
 (+ 1 2)
 ,)

(defn ^:dev/after-load simp-scene-restart []
  (println "simp-scene-restart: top-level-scene=" base/top-level-scene)
  (when (= base/top-level-scene :simp-scene)
    (println "now in simp-scene-restart 3")))
  ; (init))

(defn ^:dev/after-load create-grnd []
  (println "simp-cene: create-grnd entered")
  (let [grnd (bjs/MeshBuilder.CreateGround "ground" (js-obj "width" 10 "height" 10 "subdivisions" 10))]
    (set! (.-material grnd) blue-mat)
    (set! (.-ground env) grnd)))


(defn init []
  (println "simp-scene.init: entered")
  (set! canvas (.getElementById js/document "renderCanvas"))
  (set! engine (bjs/Engine. canvas true))
  (set! scene (bjs/Scene.))
  (let [ninety-deg (/ js/Math.PI 2)]
    (set! camera (bjs/ArcRotateCamera.
                  "Camera"
                  ninety-deg
                  ninety-deg
                  2
                  (bjs/Vector3. 0 1 -8)
                  scene)))
  (.attachControl camera canvas true)
  (.setTarget camera (bjs/Vector3. 0 0 0))
  (let [light1 (bjs/HemisphericLight. "light1" (bjs/Vector3. 1 1 0) scene)
        light2 (bjs/PointLight. "light2" (bjs/Vector3. 0 1 -1) scene)
        sph (bjs/MeshBuilder.CreateSphere "sphere" (js-obj "diameter" 1) scene)]
    (set! sphere sph))
  (set! env (bjs/EnvironmentHelper.
             (js-obj
              "createGround" false
              "skyboxSize" 30)
             scene))
; (set! (.-material ground) green-mat
  (set! green-mat (bjs/StandardMaterial. "green-mat" scene))
  (set! (.-diffuseColor green-mat) (bjs/Color3. 0 1 0))
  (set! blue-mat (bjs/StandardMaterial. "blue-mat" scene))
  (set! (.-diffuseColor blue-mat) (bjs/Color3. 0 0 1))

  (set! (.-material sphere) green-mat)

  (create-grnd)

  (bjs/Debug.AxesViewer. scene))

(defn init-once []
  (println "simp-scene: now in init-once")
  (defonce scene-init (do
                        (println "init-once: now calling init")
                        (init)
                        true))
  (println "scene-init =" scene-init))

(defn run-render-loop []
  (println "simp-scene: run-render-loop: engine=" engine)
  (.runRenderLoop engine (fn [] (.render scene))))

(defn run-scene []
  (run-render-loop))
