;; msg-cube is the physical (i.e. bjs level) representation of a msb-box.
;; Because it's physical state, it only has uts, no specs.
(ns cube-test.ut-simp.msg-box-phys
  (:require
   [re-frame.core :as re-frame]
   [cube-test.ut-simp.msg :as msg]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]))

(def cube)

(defn add-mesh-pick-action [mesh]
  (set! (.-actionManager mesh) (bjs/ActionManager. main-scene/scene))
  ; (let [action-mgr (.-actionManager mesh)]))
  ; (js/alert "abc")

  ; (let [am (.-actionManager mesh)
  ;       ; eca (bjs/ExecuteCodeAction. (js-obj "trigger" 1 "evt") (js-obj "func" (fn [e] (println "ac"))))
  ;       f (fn [e] (println "hi, e=" e))
  ;       eca (bjs/ExecuteCodeAction. (js-obj "trigger" 1) f)]
  ; ;   (println "ampa: am=" am ", eca=" eca ", f=" f)
  ; ;   (set! (.-func eca) (fn [e] (println "dp")))
  ; ;   (.registerAction am eca))
  ;
  ;   ; (.registerAction am eca #(println "hi2"))
  ;   (.registerAction am eca)
  ;   (js-debugger))

    ; (.registerAction am eca (fn [] ((fn [] (println "xyz")))))))
    ; (.registerAction am eca (fn [] ()))))
    ; (.registerAction am eca (clj->js (fn [] (println "rush"))))))
    ; (.registerAction am (fn [] (bjs/ExecuteCodeAction. (js-obj "trigger" bjs/ActionManager.OnPickTrigger)
    ;                                    (fn [e] (println "mesh-trigger: e=" e)))))))

  (let [eca (bjs/ExecuteCodeAction. (js-obj "trigger" bjs/ActionManager.OnPickTrigger))
        ; mesh-f (fn [e] (set! (-> (.-meshUnderPointer mesh) (.-diffuseColor)) main-scene))
        mesh-change-mat-f
          (fn [e]
            (do (let [mesh (.-meshUnderPointer e)
                      mesh-id (.-id mesh)
                      box-num (msg/extract-msg-box-num mesh-id)]
                  (println "trigger-handler: box-num=" box-num)
                  ; (js-debugger)
                  (set! (-> (.-meshUnderPointer e) (.-material)) main-scene/blue-mat))))]
    ; (set! (.-func eca) (fn [e] (do (js-debugger)(println "e=" e))))
    ; (set! (.-func eca) (fn [e] (println "e=" e)))
    (set! (.-func eca) mesh-change-mat-f)
    (.registerAction (.-actionManager mesh) eca)))

  ; (-> (.-actionManager mesh)
  ;     (.registerAction
  ;      #(bjs/ExecuteCodeAction. (js-obj "trigger" bjs/ActionManager.OnPickTrigger))
  ;      (fn [] (bjs/ExecuteCodeAction. (js-obj "trigger" bjs/ActionManager.OnPickTrigger)
  ;                                     (fn [e] (println "mesh-trigger: e=" e)))))))

(defn add-msg-box-phys [msg-box]
  (println "***msg-cube.add-msg-cube: msg-box=" msg-box)
  ; (println "msg-cube.add-msg-cube: msg-box.id=" (msg-box :cube-test.ut-simp.msg-box/id)))
  (println "***msg-cube.add-msg-cube: msg-box.id=" (msg-box ::msg/id))
  (println "***msg-cube.add-msg-cube: msg-box.msg=" (msg-box ::msg/msg))
  (println "***msg-cube.add-msg-cube: pre-debug")
  ; (js-debugger)
  (println "***msg-cube.add-msg-cube: post-debug")
  (println "***msg-cube.add-msg-cube: main-scene/scene=" main-scene/scene)
  (let [id (msg-box ::msg/box-id)
        mesh-id (str "msg-box-" id)
        mesh (bjs/MeshBuilder.CreateBox. "cube" (js-obj "width" 1 "height" 1 "depth" 1) main-scene/scene)
        pos (bjs/Vector3. (* id 1.5) 0 0)
        msg-level (-> (msg-box ::msg/msg) ::msg/msg-level)]
    (set! (.-position mesh) pos)
    (set! (.-id mesh) mesh-id)
    (case msg-level
      :INFO (set! (.-material mesh) main-scene/green-mat)
      :WARN (set! (.-material mesh) main-scene/orange-mat)
      :SEVERE (set! (.-material mesh) main-scene/red-mat))
    (add-mesh-pick-action mesh)))
  ; (set! cube (bjs/MeshBuilder.CreateBox.  "cube"
  ;                                               (js-obj "height" 1 "width" 1 "depth" 0.5)
  ;                                               main-scene/scene))
