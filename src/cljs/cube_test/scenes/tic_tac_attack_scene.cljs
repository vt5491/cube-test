(ns cube-test.scenes.tic-tac-attack-scene
  (:require-macros [cube-test.macros :as macros])
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [babylonjs-gui :as bjs-gui]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.utils.fps-panel :as fps-panel]
   [cube-test.base :as base]))

(def cross2)
(def ring-plex2)
(def cube-anim)
; (def rubiks-cube-grid)
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

(defn rubiks-cube-loaded [meshes particle-systems skeletons anim-groups user-cb]
  (println "tta.rubiks-cube-loaded")
  ; (js-debugger)
  (doall (map #(do
                 ; (prn "mesh-id=" (.-id %1))
                 (when (re-matches #"__root__" (.-id %1))
                     (set! (.-name %1) "rubiks-cube")
                     ;; the neg. on the x is needed otherwise the materials are "flipped"
                     (set! (.-scaling %1)(bjs/Vector3. -0.3 0.3 0.3))
                     (set! (.-position %1)(bjs/Vector3. 0 2 0))))
              meshes))
  (when user-cb (user-cb)))

(defn load-rubiks-cube [path file user-cb]
  ; (println "tta.load-rubiks-cube: path=" path ", file=" file)
  (.ImportMesh bjs/SceneLoader ""
               path
               file
               main-scene/scene
               #(rubiks-cube-loaded %1 %2 %3 %4 user-cb)))

(defn init-top-gui []
  (let [top-plane (bjs/Mesh.CreatePlane. "top-plane" 2)
        top-adv-texture (bjs-gui/AdvancedDynamicTexture.CreateForMesh. top-plane 1024 1024)
        ; top-pnl (bjs-gui/StackPanel.)
        top-pnl (bjs-gui/Grid.)
        top-hdr (bjs-gui/TextBlock.)
        rot-btn (bjs-gui/Button.CreateSimpleButton. "rot-btn" "rotate")
        rot-btn-2 (bjs-gui/Button.CreateSimpleButton. "rot-btn-2" "rotate2")
        rot-btn-3 (bjs-gui/Button.CreateSimpleButton. "rot-btn-3" "rotate3")
        left-side-rot-btn (bjs-gui/Button.CreateSimpleButton. "left-side-rot-btn" "left side rot")]
    (set! (.-position top-plane)(bjs/Vector3. 0 6 -2))
    (.addControl top-adv-texture top-pnl)
    (set! (.-text top-hdr) "commands")
    (set! (.-height top-hdr) "100px")
    (set! (.-fontSize top-hdr) "80")
    (set! (.-color top-hdr) "white")
    ;; create 4 rows and 2 cols
    (.addRowDefinition top-pnl 0.20 false)
    (.addRowDefinition top-pnl 0.20)
    (.addRowDefinition top-pnl 0.20)
    (.addRowDefinition top-pnl 0.20)
    (.addRowDefinition top-pnl 0.20)
    (.addColumnDefinition top-pnl 0.5)
    (.addColumnDefinition top-pnl 0.5)
    ; (.addControl top-pnl top-hdr)
    ; (set! (.-horizontalAlignment top-pnl) bjs-gui/StackPanel.HORIZONTAL_ALIGNMENT_CENTER)
    ; (set! (.-verticalAlignment top-pnl) bjs-gui/StackPanel.VERTICAL_ALIGNMENT_CENTER)
    (.addControl top-pnl top-hdr 0 0)
    ;; rot-btn
    (set! (.-autoScale rot-btn) true)
    (set! (.-fontSize rot-btn) "100")
    (set! (.-color rot-btn) "red")
    (-> rot-btn .-onPointerUpObservable (.add (fn [value]
                                                  (println "rot-btn pressed")
                                                  (re-frame/dispatch [:tta-rot-cube]))))
    ; (set! (.-horizontalAlignment rot-btn) bjs-gui/Control.HORIZONTAL_ALIGNMENT_LEFT)
    ; (set! (.-verticalAlignment rot-btn) bjs-gui/Control.VERTICAL_ALIGNMENT_TOP)
    (.addControl top-pnl rot-btn 2 0)
    ;; rot-btn-2
    (set! (.-autoScale rot-btn-2) true)
    (set! (.-fontSize rot-btn-2) "100")
    (set! (.-color rot-btn-2) "white")
    (-> rot-btn-2 .-onPointerUpObservable (.add (fn [value]
                                                  (println "rot-btn-2 pressed")
                                                  (re-frame/dispatch [:tta-rot-cube-2]))))
    (.addControl top-pnl rot-btn-2 2 1)
    ;; rot-btn-3
    (set! (.-autoScale rot-btn-3) true)
    (set! (.-fontSize rot-btn-3) "100")
    (set! (.-color rot-btn-3) "white")
    (-> rot-btn-3 .-onPointerUpObservable (.add (fn [value]
                                                  (println "rot-btn-3 pressed")
                                                  (re-frame/dispatch [:tta-rot-cube-3]))))
    (.addControl top-pnl rot-btn-3 3 0)

    ;; left-side-rot-btn
    (set! (.-autoScale left-side-rot-btn) true)
    (set! (.-fontSize left-side-rot-btn) "100")
    (set! (.-color left-side-rot-btn) "white")
    (-> left-side-rot-btn .-onPointerUpObservable (.add (fn [value]
                                                          (println "left-side-rot-btn pressed")
                                                          (re-frame/dispatch [:tta-left-side-anim])
                                                          (re-frame/dispatch [:tta-left-side-rot]))))
    (.addControl top-pnl left-side-rot-btn 4 0)))

(defn rot-cube []
  (println "now in rot-cube")
  ; (.beginAnimation main-scene/scene)
  (let [scene main-scene/scene
        red-cube-1 (.getMeshByID scene "red_cube_1")]
    ; (js-debugger)
    (println "pos cube=" (.-position red-cube-1) ", rot cube=" (.-rotation red-cube-1))
    (js/setTimeout
     #(do (let [rc1 (.getMeshByID main-scene/scene "red_cube_1")]
            (println "timer-pop: pos cube=" (.-position rc1) ", rot cube=" (.-rotation red-cube-1))))
     1000)
    (.beginAnimation scene red-cube-1 0 150 true)))

(defn rot-cube-2 []
  (let [scene main-scene/scene
        rc1 (.getMeshByID scene "red_cube_1")
        rot (.-rotation rc1)
        quat45 (.toQuaternion (.add rot (bjs/Vector3. 0 (/ js/Math.PI 4.0) 0)))
        new-rot (.rotateByQuaternionToRef rot quat45 (bjs/Vector3.))]
    ;; restore back to zero
    ; (.rotate rc1 (bjs/Vector3. 0 0 0) (bjs/Vector3. 0 0 0))
    (set! (.-rotationQuaternion rc1) (bjs/Quaternion.Zero))
    (set! (.-rotationQuaternion rc1) quat45)))
    ; (set! (.-rotation rc1) new-rot)
    ; (.rotate rc1 bjs/Vector3.Up new-rot)
    ; (.rotate rc1 (bjs/Vector3. 0 1 0) new-rot)))
    ; (.rotate rc1 bjs/Vector3.Up (* base/ONE-DEG 45))))

(defn rot-cube-3 []
  (let [scene main-scene/scene
        rc1 (.getMeshByID scene "red_cube_2")
        pivot (.-position rc1)
        quat45 (.toQuaternion (bjs/Vector3. 0 (* base/ONE-DEG 45.0) 0))
        cur-quat (.-rotationQuaternion rc1)]
    ; (set! (.-parent rc1) pivot)
    ; (.setPivotPoint rc1 pivot)
    ; (set! (.-rotationQuaternion pivot) quat45)
    ; (.rotate rc1 bjs/Axis.Y (* base/ONE-DEG 45) bjs/Space.WORLD)
    ; (set! (.-rotationQuaternion rc1) (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 45)))
    (set! (.-rotationQuaternion rc1) (.multiply cur-quat (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 15))))))
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

;; These are application level callback handlers when a mesh is loaded.  We just logically separate
;; them from the physical level handlers, which is handled by babylonjs' 'importMesh'
(defn init-cross []
  (let [cross-arch (-> main-scene/scene (.getNodeByName "cross-arch"))]
    (set! (.-position cross-arch)(bjs/Vector3. -1 1 0))
    (set! cross2 (.clone cross-arch))
    (set! (.-position cross2)(bjs/Vector3. -5 1 0))
    (set! (.-name cross2) "cross2")
    (.setEnabled cross-arch false)))

(defn init-ring-plex []
  (println "now in init-ring-plex")
  (let [ring-plex-arch (-> main-scene/scene (.getNodeByName "ring-plex-arch"))]
    ; (set! (.-position ring-plex-arch)(bjs/Vector3. -1 1 0))
    (set! ring-plex2 (.clone ring-plex-arch))
    (set! (.-position ring-plex2)(bjs/Vector3. 5 1 0))
    (set! (.-name ring-plex2) "ring-plex2")
    (.setEnabled ring-plex-arch false)))

; var animationBox = new BABYLON.Animation("myAnimation", "scaling.x", 30, BABYLON.Animation.ANIMATIONTYPE_FLOAT, BABYLON.Animation.ANIMATIONLOOPMODE_CYCLE);^
(defn init-rubiks-cube []
  (println "now in init-rubiks-cube")
  ; (set! cube-anim (bjs/Animation. "cube-anim" "rotation.z" 30 bjs/Animation.ANIMATIONTYPE_FLOAT bjs/Animation.ANIMATIONLOOPMODE_CONSTANT))
  ; (set! cube-anim (bjs/Animation. "cube-anim" "rotationQuaternion.z" 30 bjs/Animation.ANIMATIONTYPE_FLOAT bjs/Animation.ANIMATIONLOOPMODE_CONSTANT))
  (set! cube-anim (bjs/Animation. "cube-anim" "rotationQuaternion" 30 bjs/Animation.ANIMATIONTYPE_QUATERNION bjs/Animation.ANIMATIONLOOPMODE_CONSTANT))
  ; (set! cube-anim (bjs/Animation. "cube-anim" "position.x" 30 bjs/Animation.ANIMATIONTYPE_FLOAT bjs/Animation.ANIMATIONLOOPMODE_CONSTANT))
  ; (set! cube-anim (bjs/Animation. "cube-anim" "rotation" 30 bjs/Animation.ANIMATIONTYPE_VECTOR3 bjs/Animation.ANIMATIONLOOPMODE_CONSTANT))
  ; (set! cube-anim (bjs/Animation. "cube-anim" "scaling.x" 30 bjs/Animation.ANIMATIONTYPE_FLOAT bjs/Animation.ANIMATIONLOOPMODE_CONSTANT))
  (let [keys (array)
        ; quat45 (.toQuaternion (bjs/Vector3. 0 (/ js/Math.PI 4.0) 0))
        ; quat45 (.toQuaternion (bjs/Vector3. 0 (* base/ONE-DEG 45) 0))
        ; quat90 (.toQuaternion (bjs/Vector3. 0 (* base/ONE-DEG 90) 0))
        quat15 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 15)))
        quat30 (.normalize (.multiply quat15 (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 15))))
        quat45 (.normalize (.multiply quat30 (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 15))))
        quat90 (.normalize (.multiply quat45 (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 45))))
        quat180 (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 180))
        quat270 (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 270))
        quat360 (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 360))]
    ; (.push keys (js-obj "frame" 0 "value" 0))
    ; (.push keys (js-obj "frame" 30 "value" (* 90 base/ONE-DEG)))
    ; (.push keys (js-obj "frame" 30 "value" (/ (* 90 base/ONE-DEG) 30)))
    ; (.push keys (js-obj "frame" 30 "value" (* 20 base/ONE-DEG)))
    ; (.push keys (js-obj "frame" 0 "value" bjs/Vector3. 0 0 0))
    ; (.push keys (js-obj "frame" 30 "value" bjs/Vector3. 0 0 (/ (* 90 base/ONE-DEG) 30)))
    ; (.push keys (js-obj "frame" 0 "value" 1))
    ; (.push keys (js-obj "frame" 30 "value" 2))
    ; (.push keys (js-obj "frame" 0 "value" (bjs/Quaternion.Zero)))
    (.push keys (js-obj "frame" 0 "value" (bjs/Quaternion.Identity)))
    ; (.push keys (js-obj "frame" 150 "value" (bjs/Quaternion.FromEulerAngles 0 0 (* 180 base/ONE-DEG))))
    ; (.push keys (js-obj "frame" 150 "value" (.rotationaxis bjs/Quaternion bjs/Vector3.Up (* 180 base/ONE-DEG))))
    ; (.push keys (js-obj "frame" 150 "value" (bjs/Quaternion.RotationAxis (bjs/Vector3.Up) (* 180 base/ONE-DEG))))
    ; (.push keys (js-obj "frame" 150 "value" (bjs/Quaternion.RotationAxis bjs/Axis.Z (* 90 base/ONE-DEG))))
    ; (.push keys (js-obj "frame" 30 "value" quat45))
    ; (.push keys (js-obj "frame" 60 "value" quat90))
    ; (.push keys (js-obj "frame" 20 "value" quat15))
    ; (.push keys (js-obj "frame" 40 "value" quat30))
    (.push keys (js-obj "frame" 30 "value" quat180))
    (.push keys (js-obj "frame" 60 "value" quat360))
    (.setKeys cube-anim keys))
  (let [red-cube-1 (.getMeshByID main-scene/scene "red_cube_1")]
    (set! (.-animations red-cube-1) (array cube-anim))))
;; end mesh load application level handlers

(defn rubiks-cube-left-side-anim [rubiks-grid]
  (let [anim (bjs/Animation. "anim" "rotationQuaternion" 30 bjs/Animation.ANIMATIONTYPE_QUATERNION bjs/Animation.ANIMATIONLOOPMODE_CONSTANT)
        keys (array)
        quat90 (bjs/Quaternion.RotationAxis bjs/Axis.X (* base/ONE-DEG 90))]
        ; quat180 (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 180))
        ; quat360 (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 360))]
    (.push keys (js-obj "frame" 0 "value" (bjs/Quaternion.Identity)))
    (.push keys (js-obj "frame" 30 "value" quat90))
    (.setKeys anim keys)
    (let [;;top row
          ; left-top-rear-cube (get-in rubiks-grid [:rear :1])
          left-top-mid-cube (get-in rubiks-grid [:mid :1])
          left-top-front-cube (get-in rubiks-grid [:front :1])
          ;; front row
          ; left-front-top-cube (get-in rubiks-grid [:front :1])
          left-front-mid-cube (get-in rubiks-grid [:front :4])
          left-front-bottom-cube (get-in rubiks-grid [:front :7])
          ;; bottom row
          left-bottom-mid-cube (get-in rubiks-grid [:mid :7])
          left-bottom-rear-cube (get-in rubiks-grid [:rear :7])
          ;; rear row
          left-rear-mid-cube (get-in rubiks-grid [:rear :4])
          left-rear-top-cube (get-in rubiks-grid [:rear :1])
          ;; center
          left-center-cube (get-in rubiks-grid [:mid :4])]
      ;; top row
      ; (set! (.-animations left-top-rear-cube) (array anim))
      (set! (.-animations left-top-mid-cube) (array anim))
      (set! (.-animations left-top-front-cube) (array anim))
      ;; front row
      ; (set! (.-animations left-front-top-cube) (array anim))
      (set! (.-animations left-front-mid-cube) (array anim))
      (set! (.-animations left-front-bottom-cube) (array anim))
      ;; bottom row
      (set! (.-animations left-bottom-mid-cube) (array anim))
      (set! (.-animations left-bottom-rear-cube) (array anim))
      ;; rear row
      (set! (.-animations left-rear-mid-cube) (array anim))
      (set! (.-animations left-rear-top-cube) (array anim))
      ;; center
      (set! (.-animations left-center-cube) (array anim)))))

(defn rubiks-cube-left-side-rot [rubiks-grid]
  (let [scene main-scene/scene
        ;; top row
        ; left-top-rear-cube (get-in rubiks-grid [:rear :1])
        left-top-mid-cube (get-in rubiks-grid [:mid :1])
        left-top-front-cube (get-in rubiks-grid [:front :1])
        ;; front row
        ; left-front-top-cube (get-in rubiks-grid [:front :1])
        left-front-mid-cube (get-in rubiks-grid [:front :4])
        left-front-bottom-cube (get-in rubiks-grid [:front :7])
        ;; bottom row
        left-bottom-mid-cube (get-in rubiks-grid [:mid :7])
        left-bottom-rear-cube (get-in rubiks-grid [:rear :7])
        ;; rear row
        left-rear-mid-cube (get-in rubiks-grid [:rear :4])
        left-rear-top-cube (get-in rubiks-grid [:rear :1])
        ;; center
        left-center-cube (get-in rubiks-grid [:mid :4])]
    ;; top row
    ; (.beginAnimation scene left-top-rear-cube 0 30 true)
    (.beginAnimation scene left-top-mid-cube 0 30 true)
    (.beginAnimation scene left-top-front-cube 0 30 true)
    ;; front row
    ; (.beginAnimation scene left-front-top-cube 0 30 true)
    (.beginAnimation scene left-front-mid-cube 0 30 true)
    (.beginAnimation scene left-front-bottom-cube 0 30 true)
    ;; bottom row
    (.beginAnimation scene left-bottom-mid-cube 0 30 true)
    (.beginAnimation scene left-bottom-rear-cube 0 30 true)
    ;; rear row
    (.beginAnimation scene left-rear-mid-cube 0 30 true)
    (.beginAnimation scene left-rear-top-cube 0 30 true)
    ;; center
    (.beginAnimation scene left-center-cube 0 30 true)
    (re-frame/dispatch [:tta-print-rubiks-grid])
    ;; timer-pop
    (js/setTimeout
     #(do
        ; (println "timer-pop: rubiks-grid=" rubiks-grid)
        (re-frame/dispatch [:tta-left-side-rot-grid]))
     1000)))

;; update rubiks grid to be in the state after a forward rot of the left side.
(defn rubiks-cube-left-side-rot-grid [rubiks-grid]
  (let [result (doall (reduce (fn [accum tier-vec]
                                (let [def 17
                                       ;; note: don't have addressability to accum in inner map for
                                       ;; some reason, thus we need a user-accum
                                       accum-usr {:ghi 8}]
                                  (println "first tier-vec=" (first tier-vec))
                                  (println "second tier-vec=" (second tier-vec))
                                  (println "count second tier-vec=" (count (second tier-vec)))
                                  (println "outer-level accum=" accum)
                                  (condp = (first tier-vec)
                                    :front accum
                                    :mid (do
                                           (println "mid processing")
                                           (doall (map (fn [box-data]
                                                         (println "box-data=" box-data)
                                                         (println "second box-data=" (second box-data))
                                                         (println "inner level accum=" accum)
                                                         (println "inner level accum-usr=" accum-usr)
                                                         (println "inner level def=" def)
                                                         (let [box-num (first box-data)]
                                                           (println "box-num=" box-num)
                                                           (condp = box-num
                                                             :1 (assoc-in accum [:front :4] (second box-data))
                                                             ; :1 (assoc-in accum [:mid :1] (second box-data))
                                                             ; :1 (assoc-in accum-usr [:front :4] 7)
                                                             ; (assoc-in accum [:mid box-num] (second box-data))
                                                             (do
                                                               (assoc-in accum [:mid box-num] (second box-data))))))
                                                               ; (println "box-num " box-num "not matched")
                                                               ; (assoc-in accum-usr [:mid box-num] 1)))))
                                                       (second tier-vec))))
                                    :rear accum
                                    (println "unknown level " tier-vec))))
                              ; {:abc 7}
                              {}
                              rubiks-grid))]
    (println "rot-grid: rubiks-grid=" rubiks-grid)
    (println "rot-grid: result=" result)
    (re-frame/dispatch [:tta-print-rubiks-grid])
    result))
  ; (let [left-top-mid-cube (get-in rubiks-grid [:mid :1])
  ;       result rubiks-grid]
  ;   (println "rot-grid: rubiks-grid=" rubiks-grid)
  ;   (re-frame/dispatch [:tta-print-rubiks-grid])
  ;   result))

(defn print-rubiks-grid [rubiks-grid]
  ; (let [formatted (doall (reduce #(do)))])
  (let [formatted (reduce #(do
                             (let [level (first %2)]
                               (assoc %1 level (reduce (fn [a b] (assoc a (first b) (.-name (second b)))) {} (second %2)))))
                          {}
                          rubiks-grid)]
    (print "formatted=" formatted)
    rubiks-grid))

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
   (fn [] (re-frame/dispatch [:init-ring-plex])))
  (load-rubiks-cube
   "models/rubiks_cube/"
   "rubiks_cube.glb"
   (fn [] (do
            (re-frame/dispatch [:init-rubiks-cube])
            (println "rubiks-grid=" (re-frame/dispatch [:init-rubiks-grid]))
            ; (re-frame/dispatch [:print-db])
            (re-frame/dispatch [:unlazy-db])
            (let [s (re-frame/dispatch [:get-main-scene])]
              (println "s=" s))
            (let [db (re-frame/dispatch [:unlazy-db])]
              (println "db=" db)
              (println "db.main-scene=" (db :main-scene))))))
            ; (re-frame/dispatch [:trampoline-db]))))
            ; (js-debugger))))
  (init-top-gui)
  (println "now dispatching do-it")
  (re-frame/dispatch [:call-doit-with-db]))

(defn do-it [db]
  (println "do-it: db=" db)
  (println "do-it: db.main-scene=" (db :main-scene)))
