;; frog uses local re-frame so it causes a noticable jerk.  I'm leaving as is though
;; for comparison purposes.
(ns cube-test.frig-frog.frog
   (:require
     [re-frame.core :as re-frame]
     [babylonjs :as bjs]
     [odoyle.rules :as o]
     [cube-test.main-scene :as main-scene]
     [cube-test.base :as base]
     [cube-test.frig-frog.player :as ff.player]))
     ; [cube-test.frig-frog.events :as events]))

(def jumped)
(def frog-left-thumbstick)

(defn dummy [x y]
  7)

(defn frog-motion-ctrl-added [motion-ctrl]
  (prn "frog: frog-motion-ctrl-added=" frog-motion-ctrl-added)
  ; (js-debugger)
  (when (= (.-handedness motion-ctrl) "left")
    (set! frog-left-thumbstick (.getComponent motion-ctrl "xr-standard-thumbstick"))))

(defn ctrl-added [xr-ctrl]
  (prn "frog: ctrl-added")
  (-> xr-ctrl .-onMotionControllerInitObservable (.add frog-motion-ctrl-added)))

; (defn init-frog [row col db])
(defn init-frog [db]
  ;; hook the VRHelper joystick control here.
  (when (= main-scene/xr-mode "vr")
    (set! (.-_rotationAllowed main-scene/vrHelper) false))
  (when (= main-scene/xr-mode "xr")
    (let [xr-helper main-scene/xr-helper
          fm (-> xr-helper (.-baseExperience) (.-featuresManager))
          tf (.getEnabledFeature fm bjs/WebXRFeatureName.TELEPORTATION)]
        ; (set! (.-rotationAngle tf) base/ONE-DEG)
        ;; basically turn off the default rotation, so we can override at the frog level
        (set! (.-rotationAngle tf) 0)
        ; (-> xr-helper (.-baseExperience) (.-featuresManager) (.getEnabledFeature "xr-controller-teleportation") (.detach))
        ; (-> xr-helper (.-baseExperience) (.-featuresManager)
        ;     (.getEnabledFeature "xr-controller-teleportation") (.detach))
        ; fm.disableFeature(WebXRFeatureName.POINTER_SELECTION)));
        ; (.disableFeature fm bjs/WebXRFeatureName.TELEPORTATION)
        ; (.-baseExperience xr-helper)
        ;; override the enter-xr event locally, so we can customize for just this game.
        (-> xr-helper (.-baseExperience) (.-onStateChangedObservable)
            ; (.add #(prn "frog: onStateChangedObservable, state=" %1))
            (.add #(do
                     ; (prn "frog.onStateChangedObservable: state=" %1)
                     ; (prn "frog.onStateChangedObservable: IN_XR=" bjs/WebXRState.IN_XR)
                     (when (= %1 bjs/WebXRState.IN_XR)
                       (let [camera main-scene/camera
                             cam-pos (.-position camera)
                             new-pos (bjs/Vector3. (.-x cam-pos) (+ (.-y cam-pos) 3) (.-z cam-pos))]
                          (prn "frog: reseting height of camera")
                          (set! (.-position camera) new-pos))))))

        ; (.-baseExperience xr-helper)
        (-> xr-helper (.-input ) (.-onControllerAddedObservable) (.add ctrl-added))))

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
;; control the fuog's movement with the left stick of the vr controller
; (defn jump-frog-ctrl [ctrl])
(defn jump-frog-ctrl [x-val y-val]
  ; (let [l-stick (.-leftStick ctrl)
  ;       x-val (.-x l-stick)
  ;       y-val (.-y l-stick)]
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
         (set! jumped false))))

(defn ^:export tick []
  ;; Note: accessing the vr/xr controller has to be "on the tick".  It's simply not
  ;; available if you're not in full vr mode (hit the vr button *and* have the headset on)
  (when (= main-scene/xr-mode "vr")
    (when-let [l-ctrl (.-leftController main-scene/camera)]
      (jump-frog-ctrl (.-x l-ctrl) (.-y l-ctrl))))
  (when (= main-scene/xr-mode "xr")
    (when (and frog-left-thumbstick (.-hasChanges frog-left-thumbstick))
      (let [axes (.-axes frog-left-thumbstick)]
        (ff.player/player-ctrl-handler axes)
        (jump-frog-ctrl (.-x axes) (.-y axes))))))
        ; (cube-test.frig-frog.rules/update-left-ctrl-thumbstick axes)
