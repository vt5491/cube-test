(ns cube-test.lvs.scenes.reflect-scene
  (:require
   [re-frame.core :as rf]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [babylonjs-gui :as bjs-gui]
   [cube-test.controller-xr :as controller-xr]
   [babylonjs-loaders :as bjs-l]
   [cube-test.utils :as utils]
   [cube-test.beat-club.scene :as scene]
   [promesa.core :as p]
   [cube-test.utils.fps-panel :as fps-panel]))

;;(defn mirror-loaded [meshes particle-systems skeletons anim-groups name user-cb])
(def ctrl-fps-text)
(def left-ctrl-light)

(defn mirror-loaded [meshes particle-systems skeletons anim-groups name user-cb]
 (println "mirror-loaded, length messhes= " (alength meshes))
 ;; (js-debugger)
 (let [scene main-scene/scene
       probe (bjs/ReflectionProbe. "probe" 512 scene)]
   (prn "scene=" main-scene/ground)
   (doall (map #(do
                  (when (re-matches #"__root__" (.-id %1))
                    (do (prn "found _root")
                      (set! (.-name %1) "ball-mirror-root")
                      (set! (.-id %1) "ball-mirror-root")))
                  (when (re-matches #"mirror" (.-id %1))
                    (do 
                      (prn "found mirror")
                     ;;  (js-debugger)
                     ;;  (-> probe .-renderList (.push (.getMeshByID scene "Cube.005")))
                     ;;  (set! (.-reflectionTexture %1) (bjs/MirrorTexture. "plane-texture" 2048 scene true))
                      (let [mirror-mat (bjs/StandardMaterial. "mirror-mat" scene)
                            cube-5 (.getMeshByID scene "Cube.005")
                            cyl (.getMeshByID scene "Cylinder")]
                       ;;  (set! (.-reflectionTexture mirror-mat) (bjs/MirrorTexture. "mirror-mat" 2048 scene true))
                        (set! (.-reflectionTexture mirror-mat) (.-cubeTexture probe))
                       ;;  (set! (-> mirror-mat .-reflectionTexture .-renderList) [cube-5])
                       ;;  (set! (-> mirror-mat .-reflectionTexture .-mirrorPlane) (bjs/Plane. 0 0 -1 0))
                        (set! (-> mirror-mat .-reflectionTexture .-mirrorPlane)  
                          (bjs/Plane.FromPositionAndNormal 
                            (.-position %1) 
                            (-> (.getFacetNormal %1 0) (.scale -1))))
                        (set! (-> mirror-mat .-reflectionTexture .-level) 0.5)
                       ;;  (set! (.-renderList (.-reflectionTexture mirror-mat)) [cube-5])
                       ;;  (js-debugger)
                        (-> probe .-renderList (.push cube-5 cyl))
                        (set! (.-material %1) mirror-mat)
                        (.attachToMesh probe %1)
                        ;; put a probe on cube-5
                        (let [cube-probe (bjs/ReflectionProbe. "cube-probe" 512 scene)
                              cube-mat (.-material cube-5)]
                         ;;  (js-debugger)
                          (-> cube-probe .-renderList (.push %1))
                          (set! (.-reflectionTexture cube-mat) (.-cubeTexture cube-probe))
                          (.attachToMesh cube-probe cube-5))
                        ;; put a probe on Cylinder 
                        (let [cyl-probe (bjs/ReflectionProbe. "cyl-probe" 512 scene)
                              cyl-mat (.-material cyl)]
                          (-> cyl-probe .-renderList (.push %1))
                          (set! (.-reflectionTexture cyl-mat) (.-cubeTexture cyl-probe))
                          (.attachToMesh cyl-probe cyl)))))
                  (when (re-matches #"Plane.009" (.-id %1))
                    (do 
                      (prn "found plane.009")
                      (set! (.-material %1) main-scene/green-mat)
                      (set! (.-sideOrientation %1) bjs/Mesh.DOUBLESIDE)))
                  (when (re-matches #"Cube.005" (.-id %1))
                    (do 
                      (prn "found cube")
                      (set! (.-material %1) main-scene/red-mat)))
                  (when (re-matches #"Sphere.002" (.-id %1))
                    (let [grnd-mirror-mat (bjs/StandardMaterial. "grnd-mirror-mat" scene) 
                          box (.getMeshByID scene "box")
                          sph (.getMeshByID scene "Sphere")
                          grnd (.getMeshByID scene "ground")
                          probe-size 512
                          grnd-probe (bjs/ReflectionProbe. "grnd-probe" probe-size scene)
                          sph-probe (bjs/ReflectionProbe. "sph-probe" probe-size scene)
                          sph2-probe (bjs/ReflectionProbe. "sph2-probe" probe-size scene)]
                      (prn "found sphere.002, sph=" sph)
                      (set! (.-material %1) main-scene/black-mat)
                      (set! (.-position %1) (.add (.-position %1) (bjs/Vector3. -3 1 0)))
                      (set! (.-position sph) (.add (.-position sph) (bjs/Vector3. -3 3 0)))
                      (set! (.-reflectionTexture grnd-mirror-mat)(.-cubeTexture grnd-probe))
                      (set! (-> grnd-mirror-mat .-reflectionTexture .-level) 0.5)
                      (set! (-> grnd-mirror-mat .-reflectionTexture .-mirrorPlane)
                        (bjs/Plane.FromPositionAndNormal 
                         (.-position grnd)(-> (.getFacetNormal grnd 0)(.scale -1))))
                      (-> grnd-probe .-renderList (.push %1 box))

                      (-> sph2-probe .-renderList (.push grnd box sph))
                      (set! (.-reflectionTexture (.-material %1)) (.-cubeTexture sph2-probe))
                      (.attachToMesh sph2-probe %1)

                      (-> sph-probe .-renderList (.push grnd box %1))
                      (set! (.-reflectionTexture (.-material sph)) (.-cubeTexture sph-probe))
                      (.attachToMesh sph-probe sph))))
                     ;;  (set! (.-diffuseColor %1) (.-blue-mat scene)))))
               ;;  (when (re-matches #"__root__" (.-id %1))
               ;;      (prn "mesh name=" (.-id %1))
               ;;      (set! (.-name %1) "ball-mirror-root")
               ;;      (set! (.-id %1) "ball-mirror-root")
               ;;      ;; the neg. on the x is needed otherwise the materials are "flipped"
               ;;      (set! (.-position %1)(bjs/Vector3. 0 0 0))))
               ;;     ;;  (set! (.-reflectionTexture walkway-l-mat) (bjs/MirrorTexture. "walkway-l-texture" 2048 scene true))
             meshes))))

(defn edit-mirror []
  (prn "now in edit-mirror"))

(def source-plane)
(def samp-plane)
(def fps-pnl)

(defn ctrl-fps-gui-loaded [])

(defn move-fps-pnl []
  ;; (let  [fps-pnl cube-test.lvs.scenes.reflect-scene/fps-pnl]
  (set! (.-position fps-pnl) (.add (.-position fps-pnl) (bjs/Vector3. 0.5 0 0))))

(defn init []
  (prn "lv-strip.scene.init: entered")
  ;; top-plane (bjs/MeshBuilder.CreatePlane "top-plane" (js-obj "width" 5 "height" 3))
  (set! source-plane (bjs/Plane. 0 0 1 0))
  (let [scene main-scene/scene]
    (prn main-scene/blue-mat) 
    (utils/load-model "models/lvs/" "ball_mirror.glb" "mirror" mirror-loaded) 
    ;; (utils/load-model "models/lvs/" "lvs_main.glb" "lvs-main" mirror-loaded) 
    (set! samp-plane (bjs/MeshBuilder.CreatePlane "samp-plane" 
                                          (js-obj "width" 6, "height" 4 "sideOrientation" bjs/Mesh.DOUBLESIDE "sourcePlane" source-plane) scene))
    (set! (.-renderOutline samp-plane) true)
    (let [scene main-scene/scene
          ;; source-plane (bjs/Plane. 0 -1.0 0 -2.0)
          samp-plane (.getMeshByID scene "samp-plane")
          mat (.getMaterialByName scene "blue-mat")
          b-mat (bjs/StandardMaterial. "b-mat" scene)
          ;; box (.createBox bjs/MeshBuilder "box" 2 scene)
          box (bjs/MeshBuilder.CreateBox "box" (js-obj "width" 2, "height" 2 "depth" 2) scene)
          light-1 (bjs/PointLight. "pointLight" (bjs/Vector3. 0 5 10) main-scene/scene)
          light-2 (bjs/PointLight. "pointLight" (bjs/Vector3. 0 5 -10) main-scene/scene)]
      (set! (.-diffuseColor b-mat) (bjs/Color3. 0 0 1))
      ;; (set! (.-material samp-plane) scene.green-mat)
      (set! (.-material samp-plane) b-mat)
      (set! (.-material box) b-mat)
      ;; (prn "normals of source-plane=" (.-normal source-plane))
      (prn "normals samp-plane=" (.-normal samp-plane))
      (set! (.-position box) (bjs/Vector3. 3 0 -1))
      (set! (.-position samp-plane) (bjs/Vector3. 0 0 4)))))
    ;; (prn "rs.init: fps-pnl=" (.getMeshByID scene "fps-panel"))
    ;; (set! fps-pnl (.getMeshByID scene "fps-panel"))  
    ;; (set! (.-scaling fps-pnl)(bjs/Vector3. 0.03 0.03 0.03))
    ;; (set! left-ctrl-light (bjs/PointLight. "left-ctrl-light" (bjs/Vector3. 0 0 0) main-scene/scene))
    ;; (set! (.-intensity left-ctrl-light) 0.35)))

(defn tick []
  ;; (let [engine main-scene/engine
  ;;       left-xr-ctrl controller-xr/left-ctrl-xr]
  ;;   (when left-xr-ctrl
  ;;     (set! (.-position fps-pnl) (-> left-xr-ctrl .-pointer .-absolutePosition))
  ;;     (set! (.-rotationQuaternion fps-pnl) (-> left-xr-ctrl .-pointer .-rotationQuaternion))
  ;;     (set! (.-position left-ctrl-light) (-> left-xr-ctrl .-grip .-position)))
    (main-scene/tick))