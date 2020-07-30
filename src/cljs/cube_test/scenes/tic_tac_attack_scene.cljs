(ns cube-test.scenes.tic-tac-attack-scene
  (:require-macros [cube-test.macros :as macros])
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [babylonjs-gui :as bjs-gui]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.utils.fps-panel :as fps-panel]))

(def cross2)
(def ring-plex2)
;;
;; load models
;;
(defn cross-loaded [meshes particle-systems skeletons anim-groups user-cb]
  (println "tta.cross-loaded")
  (doall (map #(do
                 (prn "mesh-name=" (.-name %1))
                 (when (re-matches #"^cross.*" (.-name %1))
                   (set! (.-scaling %1) (bjs/Vector3. 0.1 0.1 0.1))
                   ; (set! (.-position %1)(bjs/Vector3. 0 1 0))
                   (set! (.-name (.-parent %1)) "cross-arch")))
              meshes))
  (when user-cb (user-cb)))

(defn load-cross [path file user-cb]
  (println "tta.load-cross: path=" path ", file=" file)
  (.ImportMesh bjs/SceneLoader ""
               path
               file
               main-scene/scene
               #(cross-loaded %1 %2 %3 %4 user-cb)))

(defn ring-plex-loaded [meshes particle-systems skeletons anim-groups user-cb]
  (println "tta.ring-plex-loaded")
  (doall (map #(do
                 (prn "mesh-name=" (.-name %1))
                 (when (re-matches #"^ringPlex_xy.*" (.-name %1))
                   (let [ring-plex-parent (.-parent %1)]
                     (set! (.-name ring-plex-parent) "ring-plex-arch")
                     (set! (.-scaling ring-plex-parent)(bjs/Vector3. 0.1 0.1 0.1)))))
                 ;   (set! (.-scaling %1) (bjs/Vector3. 0.1 0.1 0.1))
                 ;   (set! (.-name (.-parent %1)) "cross-arch")))
              meshes))
  (when user-cb (user-cb)))

(defn load-ring-plex [path file user-cb]
  (println "tta.load-ring-plex: path=" path ", file=" file)
  (.ImportMesh bjs/SceneLoader ""
               path
               file
               main-scene/scene
               #(ring-plex-loaded %1 %2 %3 %4 user-cb)))
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
(defn init-cross []
  (let [cross-arch (-> main-scene/scene (.getNodeByName "cross-arch"))]
    (set! (.-position cross-arch)(bjs/Vector3. -1 1 0))
    (set! cross2 (.clone cross-arch))
    (set! (.-position cross2)(bjs/Vector3. -2 1 0))
    (set! (.-name cross2) "cross2")
    (.setEnabled cross-arch false)))

(defn init-ring-plex []
  (println "now in init-ring-plex")
  (let [ring-plex-arch (-> main-scene/scene (.getNodeByName "ring-plex-arch"))]
    ; (set! (.-position ring-plex-arch)(bjs/Vector3. -1 1 0))
    (set! ring-plex2 (.clone ring-plex-arch))
    (set! (.-position ring-plex2)(bjs/Vector3. 2 1 0))
    (set! (.-name ring-plex2) "ring-plex2")
    (.setEnabled ring-plex-arch false)))

(defn init []
  (println "tic-tac-attack.init: entered")
  (let [light (bjs/PointLight. "pointLight" (bjs/Vector3. 0 5 -3) main-scene/scene)]
    (.setEnabled light true))
  (bjs/HemisphericLight. "hemiLight" (bjs/Vector3. 0 1 0) main-scene/scene)
  (load-cross
   "models/tic_tac_attack/"
   "cross.glb"
   (fn [] (re-frame/dispatch [:init-cross])))
  (load-ring-plex
   "models/tic_tac_attack/"
   "ring_plex.glb"
   (fn [] (re-frame/dispatch [:init-ring-plex]))))
