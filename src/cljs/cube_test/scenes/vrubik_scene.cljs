;; reference few, accessible by many.
(ns cube-test.scenes.vrubik-scene
  (:require-macros [cube-test.macros :as macros])
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [babylonjs-gui :as bjs-gui]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.utils.fps-panel :as fps-panel]
   [cube-test.base :as base]
   [cube-test.utils :as utils]
   [cube-test.tic-tac-attack.cell :as cell]
   [cljstache.core :refer [render]]))

(def cube-anim)
(def result)
; (def cell-action-pending false)
(def vrubik-right-axis)
(def vrubik-up-axis)
(def ^:dynamic *cell-action-pending* (atom false))
;; cells (copied from vrubik-grid) that need action processing.
;; We make a local copy to avoid having to invoke re-frame on the tick.
(def ^:dynamic *action-cells* (atom []))
(def ^:dynamic *action* (atom {:name nil}))
(def side-anims
  {:left [:0 :3 :6 :9 :12 :15 :18 :21 :24],
   :top [:0 :1 :2 :9 :10 :11 :18 :19 :20]})
(def side-rot-axes
  {:left bjs/Axis.X
   :top bjs/Axis.Y})
; (def side-rot-axes
;   {:left vrubik-right-axis
;    :top vrubik-up-axis})
(def side-rot-xfrm-maps
  ; {:left {:0 :6, :3 :15, :6 :24, :9 :3, :15 :21, :18 :0, :21 :9, :24 :18}}
  {:left {:0 :6, :3 :15, :6 :24, :9 :3, :12 :12, :15 :21, :18 :0, :21 :9, :24 :18}
   ; :top {:0 :2, :1 :11, :2 :20, :11 :19, :20 :18, :19 :9, :18 :0, :9 :1}
   ; :top {:0 :2, :1 :11, :2 :20, :9 :1, :11 :19, :18 :0, :19 :9, :20 :18}
   :top {:0 :2, :1 :11, :2 :20, :9 :1, :10 :10, :11 :19, :18 :0, :19 :9, :20 :18}})
(declare pretty-print-vrubik-grid)
(declare init-cells)

(defn rubiks-cube-loaded [meshes particle-systems skeletons anim-groups user-cb]
  (println "vrubik.rubiks-cube-loaded")
  (doall (map #(do
                 (when (re-matches #"__root__" (.-id %1))
                     ; (set! (.-name %1) "rubiks-cube")
                     (set! (.-name %1) "vrubik-cube")
                     (set! (.-id %1) "vrubik-cube")
                     ;; the neg. on the x is needed otherwise the materials are "flipped"
                     (set! (.-scaling %1)(bjs/Vector3. -0.3 0.3 0.3))
                     (set! (.-position %1)(bjs/Vector3. 0 2 0))))
              meshes))
  (when user-cb (user-cb)))

(defn load-rubiks-cube [path file user-cb]
  (.ImportMesh bjs/SceneLoader ""
               path
               file
               main-scene/scene
               #(rubiks-cube-loaded %1 %2 %3 %4 user-cb)))

(defn init-top-gui []
  (let [top-plane (bjs/Mesh.CreatePlane. "top-plane" 2)
        top-adv-texture (bjs-gui/AdvancedDynamicTexture.CreateForMesh top-plane 1024 1024)
        top-pnl (bjs-gui/Grid.)
        top-hdr (bjs-gui/TextBlock.)
        rot-btn (bjs-gui/Button.CreateSimpleButton "rot-btn" "rotate")
        rot-btn-2 (bjs-gui/Button.CreateSimpleButton "rot-btn-2" "rotate2")
        rot-btn-3 (bjs-gui/Button.CreateSimpleButton "rot-btn-3" "rotate3")
        left-side-rot-btn (bjs-gui/Button.CreateSimpleButton "left-side-rot-btn" "left side rot")
        top-side-rot-btn (bjs-gui/Button.CreateSimpleButton "top-side-rot-btn" "top side rot")]
    (set! (.-position top-plane)(bjs/Vector3. 0 6 -2))
    (.addControl top-adv-texture top-pnl)
    (set! (.-text top-hdr) "commands")
    (set! (.-height top-hdr) "100px")
    (set! (.-fontSize top-hdr) "80")
    (set! (.-color top-hdr) "white")
    ;; create 5 rows and 3 cols
    (.addRowDefinition top-pnl 0.20 false)
    (.addRowDefinition top-pnl 0.20)
    (.addRowDefinition top-pnl 0.20)
    (.addRowDefinition top-pnl 0.20)
    (.addRowDefinition top-pnl 0.20)
    (.addColumnDefinition top-pnl 0.33)
    (.addColumnDefinition top-pnl 0.33)
    (.addColumnDefinition top-pnl 0.33)
    (.addControl top-pnl top-hdr 0 0)
    ;; rot-btn
    (set! (.-autoScale rot-btn) true)
    (set! (.-fontSize rot-btn) "100")
    (set! (.-color rot-btn) "red")

    ;; left-side-rot-btn
    (set! (.-autoScale left-side-rot-btn) true)
    (set! (.-fontSize left-side-rot-btn) "100")
    (set! (.-color left-side-rot-btn) "white")
    (-> left-side-rot-btn .-onPointerUpObservable (.add (fn [value]
                                                          (println "left-side-rot-btn pressed")
                                                          (re-frame/dispatch [:vrubik-side-fwd :left]))))
    (.addControl top-pnl left-side-rot-btn 4 0)
    ;; top-side-rot-btn
    (set! (.-autoScale top-side-rot-btn) true)
    (set! (.-fontSize top-side-rot-btn) "100")
    (set! (.-color top-side-rot-btn) "white")
    (-> top-side-rot-btn .-onPointerUpObservable (.add (fn [value]
                                                          (println "top-side-rot-btn pressed")
                                                          (re-frame/dispatch [:vrubik-side-fwd :top]))))
    (.addControl top-pnl top-side-rot-btn 4 2)))

;; rubiks-cube stuff
;; should be defunct once vrubik code is in place
(defn init-rubiks-cube []
  (println "now in init-rubiks-cube")
  (set! cube-anim (bjs/Animation. "cube-anim" "rotationQuaternion" 30 bjs/Animation.ANIMATIONTYPE_QUATERNION bjs/Animation.ANIMATIONLOOPMODE_CONSTANT))
  (let [keys (array)
        quat15 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 15)))
        quat30 (.normalize (.multiply quat15 (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 15))))
        quat45 (.normalize (.multiply quat30 (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 15))))
        quat90 (.normalize (.multiply quat45 (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 45))))
        quat180 (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 180))
        quat270 (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 270))
        quat360 (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG 360))]
    (.push keys (js-obj "frame" 0 "value" (bjs/Quaternion.Identity)))
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
    (re-frame/dispatch [:vrubik-print-rubiks-grid])
    ;; timer-pop
    (js/setTimeout
     #(do
        ; (println "timer-pop: rubiks-grid=" rubiks-grid)
        (re-frame/dispatch [:vrubik-left-side-rot-grid]))
     1000)))

(defn print-rubiks-grid [rubiks-grid]
  (let [formatted (reduce #(do
                             (let [level (first %2)]
                               (assoc %1 level (reduce (fn [a b] (assoc a (first b) (.-name (second b)))) {} (second %2)))))
                          {}
                          rubiks-grid)]
    (print "formatted=" formatted)
    rubiks-grid))
;;
;; vrubik-grid stuff
;;
(defn create-left-side-anim-fwd [vrubik-grid vrubik-game-state]
  (println "create-left-side-anim-fwd: entered")
  (let [anim (bjs/Animation. "anim" "rotationQuaternion" 30 bjs/Animation.ANIMATIONTYPE_QUATERNION bjs/Animation.ANIMATIONLOOPMODE_CONSTANT)
        keys (array)
        ; keys-0-90 (array)
        quat90 (bjs/Quaternion.RotationAxis bjs/Axis.X (* base/ONE-DEG 90))
        quat180 (bjs/Quaternion.RotationAxis bjs/Axis.X (* base/ONE-DEG 180))
        left-side-idxs [:0 :3 :6 :9 :12 :15 :18 :21 :24]
        left-side-rot (get-in vrubik-game-state [:rots :left-side])]
    (condp = left-side-rot
      0  (do
           (.push keys (js-obj "frame" 0 "value" (bjs/Quaternion.Identity)))
           (.push keys (js-obj "frame" 30 "value" quat90)))
      90 (do
           (.push keys (js-obj "frame" 0 "value" quat90)))
      (.push keys (js-obj "frame" 30 "value" quat180)))
    ; (.push keys-0-90 (js-obj "frame" 0 "value" (bjs/Quaternion.Identity)))
    ; (.push keys-0-90 (js-obj "frame" 30 "value" quat90))
    (.setKeys anim keys)
    ;; reset all prior anims
    ; (doseq [idx vrubik-grid] (set! (.-animations (vrubik-grid idx)) (array)))
    ; (doseq [idx vrubik-grid] (set! (.-animations (vrubik-grid idx)) nil))
    ; (doseq [idx (keys vrubik-grid)]
    ;   (let [box (vrubik-grid idx)]
    ;     (when (and box (.-animations box))
    ;       (js-delete box "animations"))))
    ; (println "create-left-side-anim-fwd: pre-delete" (print-vrubik-grid vrubik-grid))
    (doseq [x vrubik-grid]
      (when (.-animations (second x))
        (js-delete (second x) "animations")))
    ; (println "create-left-side-anim-fwd: pre-anim:" (print-vrubik-grid vrubik-grid))
    ;; and then set new ones
    ; (doseq [idx left-side-idxs] (set! (.-animations (vrubik-grid idx)) (array anim)))))
    (doseq [idx left-side-idxs] (do
                                  (let [box (vrubik-grid idx)]
                                    (println "setting animation on box " (.-name box))
                                    (set! (.-animations box) (array anim)))))
    (println "create-left-side-anim-fwd: post-anim:" (pretty-print-vrubik-grid vrubik-grid))))

(defn run-left-side-anim-fwd [vrubik-grid]
  (println "run-left-side-anim-fwd: entered")
  ; (println "run-left-side-anim-fwd: pre-anim 2:" (print-vrubik-grid vrubik-grid))
  (let [scene main-scene/scene
        left-side-idxs [:0 :3 :6 :9 :12 :15 :18 :21 :24]]
    (doseq [idx left-side-idxs] (.beginAnimation scene (vrubik-grid idx) 0 30 true))
    (println "run-left-side-anim-fwd: pre-anim 2:" (pretty-print-vrubik-grid vrubik-grid))
    (js/setTimeout
     #(do
        ; (println "timer-pop: vrubik-grid=" vrubik-grid)
        ; (println "run-left-side-anim-fwd: post-anim:" (print-vrubik-grid vrubik-grid))
        (re-frame/dispatch [:vrubik-update-grid])
        (re-frame/dispatch [:vrubik-set-side-rot :left-side 90]))
     1000)))

(defn update-grid-left-anim [vrubik-grid idx-src idx-map]
  (reduce
   (fn [accum slot]
     (let [slot-idx (first slot)
           cube (second slot)
           src-idx-pair (find idx-src slot-idx)]
       (if src-idx-pair
         (do
           (let [
                 src-idx (first src-idx-pair)
                 tgt-idx (idx-map slot-idx)]
             (println "now moving " src-idx " to " tgt-idx)
             ; (assoc accum tgt-idx (-> (vrubik-grid src-idx) (second)))
             (assoc accum tgt-idx (vrubik-grid src-idx))))
         (do
           ; (println "leaving " slot-idx " alone.")
           (assoc accum slot-idx (vrubik-grid slot-idx))))))
   {}
   vrubik-grid))

(defn set-side-rot [side val rots]
  (condp = side
    :left-side (assoc-in rots [:left-side] val)))

(defn update-grid [vrubik-grid]
  (let [
        ;idx-map [[:0 :6] [:3 :15] [:6 :24] [:9 :3] [:15 :21] [:18 :0] [:21 :9] [:24 :18]]
        ; idx-src [:0 :3 :6 :9 :15 :18 :21 :24]
        idx-src {:0 nil, :3 nil, :6 nil, :9 nil, :15 nil, :18 nil, :21 nil, :24 nil}
        ; idx-tgt [:6 :15 :24 :3 :21 :0 :9 :18]
        idx-map {:0 :6, :3 :15, :6 :24, :9 :3, :15 :21, :18 :0, :21 :9, :24 :18}
        result (update-grid-left-anim vrubik-grid idx-src idx-map)]
    ; (println "update-grid: result=" result)
    (doall result)))

(defn update-grid-2 [vrubik-grid side]
  (println "update-grid-2: side=" side)
  (let [side-idxs (get side-anims side)
        ;; convert side-idx to a map like {:0 nil, :3 nil}
        ;; The reason for this is a map is easier to search than a vector
        idx-src (reduce
                 (fn [accum keyword]
                   (assoc accum keyword nil))
                 {} side-idxs)
        idx-map (get side-rot-xfrm-maps side)
        result (update-grid-left-anim vrubik-grid idx-src idx-map)]
    (println "update-grid-2: result=" result)
    (doall result)))



(defn left-side-anim-fwd []
  (re-frame/dispatch [:vrubik-create-left-side-anim-fwd])
  (re-frame/dispatch [:vrubik-run-left-side-anim-fwd]))

;;
;; "manual" vrubik animation
;;
(defn side-fwd [vrubik-grid side]
  (println "side-fwd: entered, side=" side)
  (let [side-idxs (get side-anims side)
        side-cells (filter
                    (fn [kv-pair]
                      (let [key (first kv-pair)]
                        ;; Note: how you have to promote the key to a set for the 'some' to work.
                        (some #{key} side-idxs)))
                    vrubik-grid)
        anim-cells (map (fn [kv-pair]
                          (let [val-map (second kv-pair)
                                cell-map (get val :cell)]
                            (-> (assoc cell-map :rot-axis (get side-rot-axes side))
                                (assoc :rot-vel (* base/ONE-DEG 90)))))
                        side-cells)
        a (println "count side-idxs=" (count side-idxs))
        b (println "count side-cells=" (count side-cells) ", side-cells=" side-cells)
        c (println "count anim-cells=" (count anim-cells) ", anim-cells=" anim-cells)
        ;; clear out cell info from prev. animations
        vrubik-grid-2 (init-cells vrubik-grid)
        result (->
                (assoc-in vrubik-grid-2 [(get side-idxs 0) :cell] (nth anim-cells 0))
                (assoc-in [(get side-idxs 1) :cell] (nth anim-cells 1))
                (assoc-in [(get side-idxs 2) :cell] (nth anim-cells 2))
                (assoc-in [(get side-idxs 3) :cell] (nth anim-cells 3))
                (assoc-in [(get side-idxs 4) :cell] (nth anim-cells 4))
                (assoc-in [(get side-idxs 5) :cell] (nth anim-cells 5))
                (assoc-in [(get side-idxs 6) :cell] (nth anim-cells 6))
                (assoc-in [(get side-idxs 7) :cell] (nth anim-cells 7))
                (assoc-in [(get side-idxs 8) :cell] (nth anim-cells 8)))]
    (println "side-idxs=" side-idxs)
    ; (println "side-cells=" side-cells)
    ; (println "anim-cells=" anim-cells)
    (println "anim-cells 0 =" (nth anim-cells 0))
    (println "result=" result)
    (swap! *cell-action-pending* (fn [x] true))
    (swap! *action* (fn [x] (assoc @*action* :type "rot" :side side)))
    result))



(defn left-side-fwd [vrubik-grid]
  ; (println "left-side-fwd: vrubik-grid=" vrubik-grid)
  (let [left-side-idxs [:0 :3 :6 :9 :12 :15 :18 :21 :24]
        ; side-cells (some)
        ; side-cells (filter
        ;             (fn [kv-pair]
        ;               ; (println "kv-pair=" kv-pair)
        ;               (println "first kv-pair=" (first kv-pair))
        ;               ; (contains? left-side-idxs (first kv-pair))
        ;               (some left-side-idxs (first kv-pair)))
        ;             vrubik-grid)
        ; side-cells (some left-side-idxs vrubik-grid)]
        ; side-cells (map (fn [kv-pair]))]
        side-cells (filter
                    (fn [kv-pair]
                      (let [key (first kv-pair)]
                        ; (prn "key=" key ", some=" (some #{key} left-side-idxs) ", left-side-idxs=" left-side-idxs)
                        ;; Note: how you have to promote the key to a set for the 'some' to work.
                        (some #{key} left-side-idxs)))
                    vrubik-grid)
        anim-cells (map (fn [kv-pair]
                          (let [val-map (second kv-pair)
                                cell-map (get val :cell)]
                            (-> (assoc cell-map :rot-axis bjs/Axis.X) (assoc :rot-vel (* base/ONE-DEG 90)))))
                        side-cells)
        ; result (reduce
        ;         (fn [accum kv-pair]
        ;           (let [key (first kv-pair)
        ;                 key-num (utils/kw-to-int key)]))
        ;         {}
        ;         vrubik-grid)]
        result (->
                (assoc-in vrubik-grid [(get left-side-idxs 0) :cell] (nth anim-cells 0))
                (assoc-in [(get left-side-idxs 1) :cell] (nth anim-cells 1))
                (assoc-in [(get left-side-idxs 2) :cell] (nth anim-cells 2))
                (assoc-in [(get left-side-idxs 3) :cell] (nth anim-cells 3))
                (assoc-in [(get left-side-idxs 4) :cell] (nth anim-cells 4))
                (assoc-in [(get left-side-idxs 5) :cell] (nth anim-cells 5))
                (assoc-in [(get left-side-idxs 6) :cell] (nth anim-cells 6))
                (assoc-in [(get left-side-idxs 7) :cell] (nth anim-cells 7))
                (assoc-in [(get left-side-idxs 8) :cell] (nth anim-cells 8)))]
        ; result (reduce (fn [accum kv-pair]
        ;                  (let [k (first kv-pair)
        ;                        v (second kv-pair)]
        ;                    (assoc accum k))))]

    (println "side-cells=" side-cells)
    (println "anim-cells=" anim-cells)
    (println "anim-cells 0 =" (nth anim-cells 0))
    (println "result=" result)
    (swap! *cell-action-pending* (fn [x] true))
    result))
    ; (doseq [pair vrubik-grid
    ;         key (first pair)
    ;         cell (-> (second pair) (get :cell))]
    ;   (println "left-side-fwd: cell" key " =" cell))
    ; (reduce)))

(defn pretty-print-vrubik-grid [vrubik-grid]
  ; (println (render "Hello, {{name}} {{msg}}!" {:name "Felix" :msg "abc"}))
  ; (doseq [pair (into (sorted-map) vrubik-grid)])
  (doseq [pair (into (sorted-map-by (fn [k1 k2]
                                      (let [num-1 (utils/kw-to-int k1)
                                            num-2 (utils/kw-to-int k2)]
                                        ; (prn "num-1=" num-1 ", num-2" num-2)
                                        (compare num-1 num-2))))
                     vrubik-grid)]
    (let [k (first pair)
          cube (get (second pair) :mesh)]
      (println "grid: " k ", " (.-name cube)
               (render
                "pos={{px}},{{py}},{{pz}}"
                {:px (-> (.-position cube) (.-x))
                 :py (-> (.-position cube) (.-y))
                 :pz (-> (.-position cube) (.-z))})
               (render
                ",rot={{rx}},{{ry}},{{rz}}"
                {:rx (-> (.-rotation cube)(.-x))
                 :ry (-> (.-rotation cube)(.-y))
                 :rz (-> (.-rotation cube)(.-z))})
               (render
                ",rotquat={{rx}},{{ry}},{{rz}},{{rw}}"
                {:rx (-> (.-rotationQuaternion cube)(.-x))
                 :ry (-> (.-rotationQuaternion cube)(.-y))
                 :rz (-> (.-rotationQuaternion cube)(.-z))
                 :rw (-> (.-rotation cube)(.-w))})))))
               ; ", pos=" (-> (.-position cube) (.-x))(-> (.-position cube) (.-x))(-> (.-position cube) (.-x))))))
  ; (let [formatted (reduce (fn [a b]
  ;                           (let [key (first b)
  ;                                 cube (second b)])))]))
;; init
;;
; (defn init-game-state [vrubik-state]
(defn init-game-state [db]
  ; (js-debugger)
  ; (assoc-in vrubik-state [:vrubik-state :rots :left-side] 0))
  (assoc-in db [:vrubik-state :rots :left-side] 0))

(defn init-cells [vrubik-grid]
  (reduce
   (fn [accum box-vec]
     (let [key (first box-vec)
           val (second box-vec)]
       ; (println "init-cells: box=" box)
       ; (assoc box :cell (re-frame/dispatch [:cell-init-cell]))
       ; add a :cell subkey to the grid val and accumlate
       ; (assoc accum key (assoc val :cell (re-frame/dispatch [:cell-init])))
       (assoc accum key (assoc val :cell (cell/init)))))
   {} vrubik-grid))

(defn release []
  (prn "vrubik.release: entered")
  (utils/release-common-scene-assets))

(defn init []
  (println "vrubik.init: entered")
  (let [light (bjs/PointLight. "pointLight" (bjs/Vector3. 0 5 -3) main-scene/scene)]
    (.setEnabled light true))
  (bjs/HemisphericLight. "hemiLight" (bjs/Vector3. 0 1 0) main-scene/scene)
  (load-rubiks-cube
   "models/rubiks_cube/"
   "rubiks_cube.glb"
   (fn [] (do
            ; (re-frame/dispatch [:init-rubiks-cube])
            ; (println "rubiks-grid=" (re-frame/dispatch [:init-rubiks-grid]))
            (re-frame/dispatch [:init-vrubik-grid])
            (re-frame/dispatch [:unlazy-db])
            (re-frame/dispatch [:vrubik-init-cells])
            (let [vrubik-cube (.getMeshByID main-scene/scene "vrubik-cube")]
              (set! vrubik-right-axis (.-right vrubik-cube))
              (set! vrubik-up-axis (.-up vrubik-cube))))))
            ; (let [s (re-frame/dispatch [:get-main-scene])]
            ;   (println "s=" s))
            ; (let [db (re-frame/dispatch [:unlazy-db])]
            ;   (println "db=" db)))))
              ; (println "db.main-scene=" (db :main-scene))

  (init-top-gui)
  (println "now dispatching do-it")
  (re-frame/dispatch [:call-doit-with-db])
  (re-frame/dispatch [:vrubik-init-game-state])
  (re-frame/dispatch [:vrubik-init-cells])
  (main-scene/load-main-gui release)
  (let [grnd (.getMeshByID main-scene/scene "ground")]
   (when grnd
     (.setEnabled grnd true)
     (set! (.-isVisible grnd) true))))

;; make a local copy of the relevent cells from the re-frame db
;; so we can efficiently access them on the game-level "tick".
(defn localize-action-pending-cells [vrubik-grid]
  (println "localize-action-pending-cells: entered")
  (let [action-cells (filter
                      (fn [kv-pair]
                        (let [rot-vel (-> (second kv-pair) (get-in [:cell :rot-vel]))]
                          ; (println "localize: key=" (first kv-pair) ", rot-vel=" rot-vel)
                          ; (not (nil? rot-vel))
                          (not (= rot-vel 0))))
                      vrubik-grid)
        ;; add a frame count so animation is limited
        action-cells-2 (map
                        (fn [kv-pair]
                          (let [k (first kv-pair)
                                v (second kv-pair)]
                            [k (-> (assoc v :frame-cnt 60)
                                   (assoc :rot-accum 0)
                                   (assoc :rot-max (get-in v [:cell :rot-vel])))]))
                        action-cells)]
    (swap! *action-cells* (fn [x] action-cells-2))))
  ; (println "localize-action-pending-cells: action-cells=" @*action-cells*))

(defn toggle-cell-action-pending []
  (swap! *cell-action-pending* (fn [x] (not @*cell-action-pending*))))

(defn rot-cells []
  (let [result
        (doall (map (fn [kv-pair]
                      ; (println "render-loop: kv-pair=" kv-pair)
                      (let [key (first kv-pair)
                            val (second kv-pair)
                            rot-vel (get-in val [:cell :rot-vel])
                            rot-axis (get-in val [:cell :rot-axis])
                            rot-axis-abs (cond
                                           (.equals rot-axis bjs/Axis.X) vrubik-right-axis
                                           (.equals rot-axis bjs/Axis.Y) vrubik-up-axis)
                            frame-cnt (get-in val [:frame-cnt])
                            rot-accum (get-in val [:rot-accum])
                            rot-max (get-in val [:rot-max])
                            mesh (get-in val [:mesh])
                            mesh-quat (.-rotationQuaternion mesh)
                            rot-delta (* rot-vel (/ (.getDeltaTime main-scene/engine) 1000))
                            quat-delta (.normalize
                                        (.multiply
                                         (bjs/Quaternion.Identity)
                                         ; (rot-axis (* base/ONE-DEG 90 (/ (.getDeltaTime main-scene/engine) 1000)))
                                         ; (bjs/Quaternion.RotationAxis rot-axis (* rot-vel (/ (.getDeltaTime main-scene/engine) 1000)))
                                         ; (bjs/Quaternion.RotationAxis rot-axis rot-delta)
                                         (bjs/Quaternion.RotationAxis rot-axis-abs rot-delta)))]
                        ; (if (> frame-cnt 0))
                        (if (< rot-accum rot-max)
                          (do
                            ; (set! (.-rotationQuaternion mesh) (.normalize (.multiply mesh-quat quat-delta)))
                            ;; Note: we use rotate (instead of updating rotationQuaternion directly)
                            ;; because 'rotote' has a world axis parameter, and we need to rotate around
                            ;; the global axis not the local axis
                            (.rotate mesh rot-axis-abs rot-delta bjs/Space.WORLD)
                            ; [key (-> (assoc val :frame-cnt (dec frame-cnt)))]
                            [key (assoc val :rot-accum (+ rot-accum rot-delta))])
                          (do
                            ; (let [quat-90 (bjs/Quaternion.RotationAxis rot-axis (* base/ONE-DEG 90))])
                            (let [quat-90 (bjs/Quaternion.RotationAxis rot-axis-abs (* base/ONE-DEG 90))]
                              ; (set! (.-rotationQuaternion mesh) quat-90)
                              (println "rot-cells: done with anim. setting actions-cells to nil")
                              (println "last rot=" (.-rotationQuaternion mesh)))
                            ; (swap! *action-cells* (fn [x] nil))
                            nil))))
                    ;; return new action-cells with decremented frame-cnt
                    ; [key (assoc val :frame-cnt (dec frame-cnt))]))
                    @*action-cells*))]
    (when (every? nil? result)
      (println "rot-cells: anim done")
      (re-frame/dispatch [:vrubik-update-grid-2 (get @*action* :side)]))
    result))

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
  (when @*cell-action-pending*
    ; (rot-cells)
    (re-frame/dispatch [:vrubik-rot-cells-combo]))
  (let [action-cells @*action-cells*]
    (when (and action-cells (> (count action-cells) 0) (nth action-cells 0))
      (swap! *action-cells* rot-cells)))
  (main-scene/tick)
  (.render main-scene/scene))

(defn run-scene []
  (.runRenderLoop main-scene/engine (fn [] (render-loop))))
