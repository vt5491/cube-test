(ns cube-test.frig-frog.player
   (:require
     [re-frame.core :as rf]
     [babylonjs :as bjs]
     [cube-test.main-scene :as main-scene]
     [cube-test.base :as base]
     [cube-test.utils :as utils]))

(def jumped false)
(def open-for-service true)
(def player-left-thumbstick)
;; absolute move.
(defn move-player-to
  ([mesh-id x y]  (move-player-to mesh-id x cube-test.frig-frog.board/board-height y))
  ;; note: with 3-d we use the bjs actual x,y,z not pretend "y" coordinates when only using 2
  ([mesh-id x y z]
   (let [scene main-scene/scene
         mesh (or (.getMeshByID scene "player") (bjs/MeshBuilder.CreateCylinder. "player" (js-obj "tessellation" 6 "height" 0.7) scene))]
     (set! (.-position mesh) (bjs/Vector3. x y z)))))

;; relative move.
(defn move-player-delta [mesh-id dx dy]
  (let [scene main-scene/scene
        mesh (.getMeshByID scene mesh-id)
        pos (.-position mesh)]
      (move-player-to mesh-id (+ (.-x pos) dx) (.-y pos) (+ (.-z pos) dy))))

(defn jump-player-ctrl [id x-val y-val]
    (cond
      (and (> y-val 0.5) (not jumped))
      (do
       (set! jumped true)
       (cube-test.frig-frog.rules.player-move-tile-delta id 0 -1))
      (and (< y-val -0.5) (not jumped))
      (do
        (set! jumped true)
        ; (re-frame/dispatch [:cube-test.frig-frog.events/jump-frog 1 0])
        (cube-test.frig-frog.rules.player-move-tile-delta id 0 1))
      (and (> x-val 0.5) (not jumped))
      (do
       (set! jumped true)
       (cube-test.frig-frog.rules.player-move-tile-delta id 1 0))
      (and (< x-val -0.5) (not jumped))
      (do
       (set! jumped true)
       (cube-test.frig-frog.rules.player-move-tile-delta id -1 0))))

(defn player-ctrl-handler [axes]
  (let [x (.-x axes)
        y (.-y axes)]
    (cond
      (or (> (Math/abs x) 0.5) (> (Math/abs y) 0.5))
      (do
        (jump-player-ctrl "player" x y))
      :else
      (set! jumped false))))


(defn player-motion-ctrl-added [motion-ctrl]
  (when (= (.-handedness motion-ctrl) "left")
    (set! player-left-thumbstick (.getComponent motion-ctrl "xr-standard-thumbstick"))))

(defn ctrl-added [xr-ctrl]
  (-> xr-ctrl .-onMotionControllerInitObservable (.add player-motion-ctrl-added)))

(defn init-player []
  (utils/disable-default-joystick-ctrl)
  (let [xr-helper main-scene/xr-helper]
    (-> xr-helper (.-input ) (.-onControllerAddedObservable) (.add ctrl-added))))

(defn ^:export tick []
  ;; Note: accessing the vr/xr controller has to be "on the tick".  It's simply not
  ;; available if you're not in full vr mode (hit the vr button *and* have the headset on)
  ; (when (= main-scene/xr-mode "vr")
  ;   (when-let [l-ctrl (.-leftController main-scene/camera)]
  ;     (jump-frog-ctrl (.-x l-ctrl) (.-y l-ctrl))))
  (when (= main-scene/xr-mode "xr")
    (when (and player-left-thumbstick (.-hasChanges player-left-thumbstick))
      (let [axes (.-axes player-left-thumbstick)]
        (player-ctrl-handler axes)))))
