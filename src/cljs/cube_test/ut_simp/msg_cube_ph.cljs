;; msg-cube is the physical (i.e. bjs level) representation of a msb-box.
;; Because it's physical state, it only has uts, no specs.
(ns cube-test.ut-simp.msg-cube-ph
  (:require
   [re-frame.core :as re-frame]
   [cube-test.ut-simp.msg-box :as msg-box]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]))

(def cube)

(defn add-msg-cube-ph [msg-box]
  (println "***msg-cube.add-msg-cube: msg-box=" msg-box)
  ; (println "msg-cube.add-msg-cube: msg-box.id=" (msg-box :cube-test.ut-simp.msg-box/id)))
  (println "***msg-cube.add-msg-cube: msg-box.id=" (msg-box ::msg-box/id))
  (println "***msg-cube.add-msg-cube: msg-box.msg=" (msg-box ::msg-box/msg))
  (println "***msg-cube.add-msg-cube: pre-debug")
  ; (js-debugger)
  (println "***msg-cube.add-msg-cube: post-debug")
  (println "***msg-cube.add-msg-cube: main-scene/scene=" main-scene/scene)
  (let [id (msg-box ::msg-box/id)
        mesh (bjs/MeshBuilder.CreateBox. "cube" (js-obj "width" 1 "height" 1 "depth" 1) main-scene/scene)
        pos (bjs/Vector3. (* id 1.5) 0 0)]
    (set! (.-position mesh) pos)))
  ; (set! cube (bjs/MeshBuilder.CreateBox. "cube"
  ;                                               (js-obj "height" 1 "width" 1 "depth" 0.5)
  ;                                               main-scene/scene))
