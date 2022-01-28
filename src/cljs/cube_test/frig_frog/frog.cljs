(ns cube-test.frig-frog.frog
   (:require
     [re-frame.core :as re-frame]
     [babylonjs :as bjs]
     [cube-test.main-scene :as main-scene]
     [cube-test.base :as base]))
     ; [cube-test.frig-frog.events :as events]))

(def jumped)

(defn dummy [x y]
  7)

; (defn init-frog [row col db])
(defn init-frog [db]
  ;; hook the VRHelper joystick control here.
  (set! (.-_rotationAllowed main-scene/vrHelper) false)
  ; (js-debugger)
  (set! jumped false)
  (let [n-cols (:n-cols db)
        tmp-db (assoc db :frog {})
        tmp-db-2 (assoc-in tmp-db [:frog :row] 0)
        tmp-db-3 (assoc-in tmp-db-2 [:frog :col] (quot (- n-cols 1) 2))
        tmp-db-4 (assoc-in tmp-db-3 [:frog :mode] 0)]
      tmp-db-4))

(defn draw-frog [row col]
  (prn "draw.frog: row=" row ", col=" col)
  (let [scene main-scene/scene
        frog-mesh (.getMeshByID scene "frog")]
      (when frog-mesh
        (.dispose frog-mesh))
      (let [frog (bjs/Mesh.CreateBox "frog" 1 scene)]
        (set! (.-position frog) (bjs/Vector3. (* col 1.2) 1 (* row 1.2))))))

; (defn move-frog [row col db]
;   (let [scene main-scene/scene
;         frog (:frog db)]
;         ; frog-mesh (.getMeshByID scene "frog")]
;       ; (when frog-mesh
;       ;   (.dispose frog-mesh))
;       ; (draw-frog row col)
;       (prn "move-frog row=" row ", col=" col)
;       (-> (assoc-in db [:frog :row] row)
;           (assoc-in [:frog :col] col))))
;; control the frog's movement with the left stick of the vr controller
(defn jump-frog-ctrl [ctrl]
  (let [l-stick (.-leftStick ctrl)
        x-val (.-x l-stick)
        y-val (.-y l-stick)]
    ; (cond
    ;   (and (> y-val 0.5) (not jumped))
    ;   (do
    ;    (prn "frog: jump bwd")
    ;    (set! jumped true)
    ;    (re-frame/dispatch [:cube-test.frig-frog.events/jump-frog -1 0]))
    ;   (and (< y-val -0.5) (not jumped))
    ;   (do
    ;     (prn "frog: jump fwd")
    ;     (set! jumped true)
    ;     (re-frame/dispatch [:cube-test.frig-frog.events/jump-frog 1 0]))
    ;   (and (> y-val -0.5) (< y-val 0.5))
    ;   (do
    ;    ; (prn "frog: neutral zone")
    ;    (set! jumped false)))
    ; (cond
    ;  (and (> x-val 0.5) (not jumped))
    ;  (do
    ;   (prn "frog: jump right")
    ;   (set! jumped true)
    ;   (re-frame/dispatch [:cube-test.frig-frog.events/jump-frog 0 -1]))
    ;  (and (< x-val -0.5) (not jumped))
    ;  (do
    ;    (prn "frog: jump left")
    ;    (set! jumped true)
    ;    (re-frame/dispatch [:cube-test.frig-frog.events/jump-frog 0 1]))
    ;  (and (> x-val -0.5) (< x-val 0.5))
    ;  (do
    ;   (set! jumped false)))
    ;;
    (cond
      (and (> y-val 0.5) (not jumped))
      (do
       (prn "frog: jump bwd")
       (set! jumped true)
       (re-frame/dispatch [:cube-test.frig-frog.events/jump-frog -1 0]))
      (and (< y-val -0.5) (not jumped))
      (do
        (prn "frog: jump fwd")
        (set! jumped true)
        (re-frame/dispatch [:cube-test.frig-frog.events/jump-frog 1 0]))
      (and (> x-val 0.5) (not jumped))
      (do
         (prn "frog: jump right")
         (set! jumped true)
         (re-frame/dispatch [:cube-test.frig-frog.events/jump-frog 0 1]))
      (and (< x-val -0.5) (not jumped))
      (do
          (prn "frog: jump left")
          (set! jumped true)
          (re-frame/dispatch [:cube-test.frig-frog.events/jump-frog 0 -1]))
      ; :else
      (and (> y-val -0.5) (< y-val 0.5) (> x-val -0.5) (< x-val 0.5))
      (do
         (set! jumped false)))))

(defn ^:export tick []
  ;; Note: accessing the vr/xr controller has to be "on the tick".  It's simply not
  ;; available if you're not in full vr mode (hit the vr button *and* have the headset on)
  ; (prn "frig-frog.game.tick: left-ctrl=" (.-leftController main-scene/camera))
  (when-let [l-ctrl (.-leftController main-scene/camera)]
    ; (prn "frig-frog.game.tick: left-stick=" (.-leftStick l-ctrl))))
    ; (js-debugger)))
    (jump-frog-ctrl l-ctrl)))
  ; (println "tick2: entry: camera.pos=" (.-position re-con.core/camera))
  ; (when camera
  ;   (set! leftController (.-leftController camera)))
  ; ; (println "tick: camera=" camera ",leftController=" leftController)
  ; (when (and camera leftController)))
