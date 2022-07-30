(ns cube-test.frig-frog.player
   (:require
     [re-frame.core :as rf]
     [babylonjs :as bjs]
     [cube-test.main-scene :as main-scene]
     [cube-test.base :as base]
     [cube-test.utils :as utils]
     [cube-test.utils.common :as common]))

(def ^:dynamic jumped false)
(def ^:dynamic top-player-jumped false)
(def ^:dynamic btm-player-jumped false)
(def open-for-service true)
(def player-left-thumbstick)
(def player-move-snd)
(def player-hit-snd)

(defn get-mesh [player-id]
  (let [mesh-id (common/gen-mesh-id-from-rule-id player-id)
        scene main-scene/scene]
    (.getMeshByID scene mesh-id)))

;; absolute move.
(defn move-player-to
  ; ([mesh-id x y]  (move-player-to mesh-id x cube-test.frig-frog.board/board-height y))
  ([mesh-id x y]  (let [prfx (-> (re-matches #"^(.*)-.*" mesh-id) second)]
                    ; (prn "player.move-player-to: prfx=" prfx)
                    (case prfx
                      "top" (move-player-to mesh-id x (:top cube-test.frig-frog.board/board-heights) y)
                      "btm" (move-player-to mesh-id x (:btm cube-test.frig-frog.board/board-heights) y)
                      ; "btm" (move-player-to mesh-id x 0 y)
                      ; nil   (move-player-to mesh-id x cube-test.frig-frog.board/board-height y)
                      nil   (move-player-to mesh-id x 0 y))))
  ;; note: with 3-d we use the bjs actual x,y,z not pretend "y" coordinates when only using 2
  ([mesh-id x y z]
   (let [scene main-scene/scene
         ; mesh (or (.getMeshByID scene "player") (bjs/MeshBuilder.CreateCylinder. "player" (js-obj "tessellation" 6 "height" 0.7) scene))
         mesh (or (.getMeshByID scene mesh-id) (bjs/MeshBuilder.CreateCylinder. mesh-id (js-obj "tessellation" 6 "height" 0.7) scene))]
     (set! (.-position mesh) (bjs/Vector3. x y z)))))

;; relative move.
(defn move-player-delta [mesh-id dx dy]
  (let [scene main-scene/scene
        mesh (.getMeshByID scene mesh-id)
        pos (.-position mesh)]
      (move-player-to mesh-id (+ (.-x pos) dx) (.-y pos) (+ (.-z pos) dy))))

(defn get-jumped [id]
  (case id
    :cube-test.frig-frog.rules/top-player top-player-jumped
    :cube-test.frig-frog.rules/btm-player btm-player-jumped))

(defn set-jumped [id bool]
  (case id
    :cube-test.frig-frog.rules/top-player (set! top-player-jumped bool)
    :cube-test.frig-frog.rules/btm-player (set! btm-player-jumped bool)))

(defn jump-player-ctrl [id x-val y-val]
    ; (prn "player.jump-player-ctrl: id=" id)
  ; (let [player-jumped jumped])
  (cond
    (and (> y-val 0.5) (not (get-jumped id)))
    (do
      ; (set! player-jumped true)
      (set-jumped id true)
      (cube-test.frig-frog.rules.player-move-tile-delta id 0 -1))
    (and (< y-val -0.5) (not (get-jumped id)))
    (do
      ; (set! player-jumped true)
      (set-jumped id true)
      ; (re-frame/dispatch [:cube-test.frig-frog.events/jump-frog 1 0])
      (cube-test.frig-frog.rules.player-move-tile-delta id 0 1))
    (and (> x-val 0.5) (not (get-jumped id)))
    (do
      ; (set! player-jumped true)
      (set-jumped id true)
      (cube-test.frig-frog.rules.player-move-tile-delta id 1 0))
    (and (< x-val -0.5) (not (get-jumped id)))
    (do
      ; (set! player-jumped true)
      (set-jumped id true)
      (cube-test.frig-frog.rules.player-move-tile-delta id -1 0))))

(defn player-ctrl-handler [axes]
  (let [x (.-x axes)
        y (.-y axes)]
    (cond
      (or (> (Math/abs x) 0.5) (> (Math/abs y) 0.5))
      (do
        (jump-player-ctrl :cube-test.frig-frog.rules/top-player x y)
        (jump-player-ctrl :cube-test.frig-frog.rules/btm-player x y))
      :else
      ; (set! jumped false)
      (do
        (set-jumped :cube-test.frig-frog.rules/btm-player false)
        (set-jumped :cube-test.frig-frog.rules/top-player false)))))


(defn player-motion-ctrl-added [motion-ctrl]
  (when (= (.-handedness motion-ctrl) "left")
    (set! player-left-thumbstick (.getComponent motion-ctrl "xr-standard-thumbstick"))))

(defn ctrl-added [xr-ctrl]
  (-> xr-ctrl .-onMotionControllerInitObservable (.add player-motion-ctrl-added)))

(defn play-move-snd []
  (.play player-move-snd))

(defn play-hit-snd []
  (.play player-hit-snd))

; (defn init-snd [player-move-snd-file])
(defn init-snd [{:keys [move-snd-file hit-snd-file] :as parms}]
  (set! player-move-snd (bjs/Sound.
                           "plyr-move-snd"
                           move-snd-file
                           main-scene/scene))
  (set! player-hit-snd (bjs/Sound.
                           "plyr-hit-snd"
                           hit-snd-file
                           main-scene/scene)))
(defn init-player []
  (utils/disable-default-joystick-ctrl)
  (let [xr-helper main-scene/xr-helper]
    (-> xr-helper (.-input ) (.-onControllerAddedObservable) (.add ctrl-added)))
  ; (init-snd "sounds/frig_frog/plastic_swipe.ogg")
  ; (init-snd "sounds/frig_frog/cloth_slide.ogg")
  ; (init-snd "sounds/frig_frog/paper_slide.ogg")
  (init-snd {:move-snd-file "sounds/frig_frog/paper_slide.ogg"
             :hit-snd-file "sounds/frig_frog/wipeout.ogg"}))

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
