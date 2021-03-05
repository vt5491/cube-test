(ns cube-test.ut-simp.ut-simp-scene
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.utils.fps-panel :as fps-panel]
   [clojure.spec.alpha :as s]
   ; [cube-test.ut-simp.msg-box :as msg-box]
   [cube-test.ut-simp.msg :as msg]
   [cube-test.ut-simp.msg-box-phys :as msg-box-phys]))
   ; [cube-test.specs.ut-simp-spec :as ut-simp-spec]))

;; structs
;; main data struct.  This is a series of "message boxes" that represent a message.  Each message
;; box is either green=info, yellow=warn, or red=severe.
; (def msg-boxes)
(def ^:dynamic *msg-boxes-atom* (atom []))
; (s-def)
   ; mesh.actionManager = new BABYLON.ActionManager(scene);
   ;      mesh.actionManager.registerAction()
   ;          new BABYLON.ExecuteCodeAction()
   ;              {
   ;                  trigger: BABYLON.ActionManager.OnPickTrigger,}
   ;              ,
   ;              function (e) {}
   ;
   ;                  console.log(e));
;; inits

(defn init []
  (println "ut-simp-scene/scene.init: entered")
  (let [light (bjs/PointLight. "pointLight" (bjs/Vector3. 0 5 -3) main-scene/scene)]
    (.setEnabled light true))
  ; (msg-box/add-msg-box *msg-boxes-atom*)
  ; (assoc-in db [:vrubik-state :rots :left-side] 0)
  ;; 2nd method
  (re-frame/dispatch [:init-msgs])
  ; (re-frame/dispatch [:add-msg-box
  ;                     {::msg/box-id 2 ::msg/msg {::msg/text "def" ::msg/msg-level :INFO}}])
  (re-frame/dispatch [:add-msg "new" :INFO]))
  ; (re-frame/dispatch [:add-msg-box (db :msg-boxes-atom)])
  ; (println "init: *msg-boxes-atom*=" *msg-boxes-atom*)
  ; (println "init: @*msg-boxes-atom*=" @*msg-boxes-atom*))
  ; (println "init: (db :msg-boxes-atom=)" (db :msg-boxes-atom))
  ; (println "init: (db :abc=)" (db :abc))
  ; (println "init: db=" db))

; ;; this is to create a scene without being dependent on main-scene in an attempt to get
; ;; a refreshable scene
; (def canvas)
; (def camera)
; (def engine)
; (def scene)
; (def sphere)
; (def env)
; (def green-mat)
; (def blue-mat)
; (def grnd)
;
; (defn ^:dev/after-load create-grnd []
;   (let [grnd (bjs/MeshBuilder.CreateGround "ground" (js-obj "width" 10 "height" 10 "subdivisions" 10))]
;     (set! (.-material grnd) blue-mat)
;     (set! (.-ground env) grnd)))
;
;
; (defn init-2 []
;   (println "main-scene.create-scene: entered")
;   (set! canvas (.getElementById js/document "renderCanvas"))
;   (set! engine (bjs/Engine. canvas true))
;   (set! scene (bjs/Scene.))
;   (let [ninety-deg (/ js/Math.PI 2)]
;     (set! camera (bjs/ArcRotateCamera.
;                   "Camera"
;                   ninety-deg
;                   ninety-deg
;                   2
;                   (bjs/Vector3. 0 1 -8)
;                   scene)))
;   (.attachControl camera canvas true)
;   (.setTarget camera (bjs/Vector3. 0 0 0))
;   (let [light1 (bjs/HemisphericLight. "light1" (bjs/Vector3. 1 1 0) scene)
;         light2 (bjs/PointLight. "light2" (bjs/Vector3. 0 1 -1) scene)
;         sph (bjs/MeshBuilder.CreateSphere "sphere" (js-obj "diameter" 1) scene)]
;     (set! sphere sph))
;   (set! env (bjs/EnvironmentHelper.
;              (js-obj
;               "createGround" false
;               "skyboxSize" 30)
;              scene))
; ; (set! (.-material ground) green-mat
;   (set! green-mat (bjs/StandardMaterial. "green-mat" scene))
;   (set! (.-diffuseColor green-mat) (bjs/Color3. 0 1 0))
;   (set! blue-mat (bjs/StandardMaterial. "blue-mat" scene))
;   (set! (.-diffuseColor blue-mat) (bjs/Color3. 0 0 1))
;
;   (set! (.-material sphere) green-mat)
;
;   (create-grnd)
;
;   (bjs/Debug.AxesViewer. scene))


  ;; render
(defn render-loop []
  ; (println "ut-simp-scene.render-loop")
  (if (= main-scene/xr-mode "vr")
    (controller/tick)
    (controller-xr/tick))
  (fps-panel/tick main-scene/engine)
  (.render main-scene/scene))

; (defn run-render-loop-2 []
;   (println "run-render-loop-2: engine=" engine)
;   (.runRenderLoop engine (fn [] (.render scene))))

(defn run-scene []
  (println "ut-simp-scene.run-scene: entered")
  (.runRenderLoop main-scene/engine (fn [] (render-loop))))

; (defn run-scene-2 []
;   (run-render-loop-2))
