;; good docs:
;;https://doc.babylonjs.com/how_to/introduction_to_webxr
(ns cube-test.controller-xr
  (:require [re-frame.core :as re-frame]
            ; [re-con.main-scene :as main-scene]; note: generates circular dependency))
            [cube-test.main-scene :as main-scene]
            [cube-test.base :as base]))
            ; [cube-test.utils :as utils]))

(def scene)
(def xr)
(def ctrl-xr)
(def left-ctrl-xr)
(def right-ctrl-xr)
(def main-trigger)
(def grip)
(def x-btn)
(def left-ray)
(def right-ray)
(def is-gripping false)
(def grip-start-pos)
(def player-start-pos)
(def last-player-pos)
(def last-grip-vel)
(def last-grip-time)
; (def grip-factor 1.9) ;; probably the best
; (def grip-factor 2.1) ;; good for scale-factor=100
(def grip-factor (if (>= base/scale-factor 100) 2.1 1.9))
; (def GRIP_DECELERATION_INT 1000)
(def GRIP_DECELERATION_INT 1500)
(def game-pad-mgr)

(declare trigger-handler-xr)
(declare grip-handler-xr)
; (declare grip-handler-xr-2)
(declare get-ctrl-handedness)
(declare ctrl-added)
(declare left-trigger-handler)
(declare right-trigger-handler)
(declare pointer-collider-handler)
(declare x-btn-handler)
(declare gamepad-evt-handler)

; const ray = getWorldPointerRayToRef(controller);
; (defn init [tgt-scene xr-helper])
(defn init [xr-helper]
  (println "controller-xr.init entered")
  ; (set! scene tgt-scene)
  (set! xr xr-helper)
  (set! last-grip-time (.now js/Date))
  (set! last-grip-vel (js/BABYLON.Vector3. 0 0 0))
  (set! last-player-pos (.-position main-scene/camera)))

(defn ^:export setup-xr-ctrl-cbs [xr-helper]
  (-> xr-helper (.-input ) (.-onControllerAddedObservable) (.add ctrl-added)))

(defn motion-controller-added [motion-ctrl]
  (prn "gamepad-evt-hander entered, motion-ctrl=" motion-ctrl)
  (set! grip (-> motion-ctrl (.getComponent "xr-standard-squeeze")))
  (when grip
    (prn "setting up grip btn handler")
    (-> grip (.-onButtonStateChangedObservable) (.add grip-handler-xr))))

;; Note: should also be able to directly access the controllers via:
;; xr.input.controllers[0 or 1]
(defn ctrl-added [xr-controller]
  (println "controller-xr.ctrl-added: xr-controller.uniqueId=" (.-uniqueId xr-controller) ",handedness=" (get-ctrl-handedness xr-controller))
  (let [handedness (get-ctrl-handedness xr-controller)]
     (when (= handedness :left)
       (set! left-ctrl-xr xr-controller))
     (when (= handedness :right)
       (set! right-ctrl-xr xr-controller)))
  ; (when (-> xr-controller .-inputSource .-gamepad)
  (-> xr-controller .-onMotionControllerInitObservable (.add motion-controller-added)))

(defn trigger-handler-xr [trigger-state]
  (re-frame/dispatch [:trigger-handler (js-obj "pressed" (.-pressed trigger-state))]))

;; Note: this will be called multiple times, each tick in fact (automatically by BJS).
;; It is complemented by the 'tick' method in this namespace, which you
;; have to manually set up to be called on each tick.  It's in this tick where the actual
;; deltas of motion are calculated i.e if you call 'grip-handler-xr' without calling
;; controller-xr.tick, you'll get no movement.
(defn grip-handler-xr [cmpt]
  ; (prn "controller-xr: now in grip-handler-xr")
  ; (js-debugger)
  (if (.-pressed cmpt)
    (when (and left-ctrl-xr (not is-gripping))
      (set! grip-start-pos (-> left-ctrl-xr (.-grip) (.-position) (.clone)))
      (set! is-gripping true)
      (set! player-start-pos (.-position main-scene/camera))
      (set! last-grip-time (.now js/Date)))
    (if is-gripping
      (do
        ;; transition from gripping to non-gripping
        (set! is-gripping false)
        (set! last-grip-time (.now js/Date))
        ;; secret for good coasting velocity is to go off camera deltas not grip deltas.
        (let [normal-vel (.normalize (.subtract (.-position main-scene/camera) player-start-pos))
              mag (.length last-grip-vel)]
          (set! last-grip-vel (.multiplyByFloats normal-vel mag mag mag))))
      ;; non-transitioning non-gripping
      (set! is-gripping false))))

(defn left-trigger-handler []
  (println "left trigger fired"))

(defn right-trigger-handler []
  (println "right trigger fired"))

(defn pointer-collider-handler []
  (println "pointer collider detected"))

(defn x-btn-handler [state]
  (when (.-pressed state)
    (println "x-btn pressed")
    (set! (.-position main-scene/camera) main-scene/camera-init-pos)))

;; determine if id of the ctrl is "left" or "right"
(defn get-ctrl-handedness [ctrl]
  (let [id (.-uniqueId ctrl)]
    (if (re-matches #".*-(left).*" id)
      :left
      (if (re-matches #".*-(right).*" id)
        :right))))

(defn mesh-select-predicate [mesh]
  (if (= (.-name mesh) "tmp-obj")
    false
    true))

(defn ^:export tick []
  (when left-ctrl-xr
    (cond
      is-gripping
      (let [ctrl-delta-pos (-> left-ctrl-xr (.-grip) (.-position) (.subtract grip-start-pos) (.multiplyByFloats grip-factor grip-factor grip-factor))
            new-pos (.subtract (.-position main-scene/camera) ctrl-delta-pos)]
        (set! (.-position main-scene/camera) new-pos)
        (set! last-grip-vel (.subtract new-pos last-player-pos))
        (set! last-player-pos (.-position main-scene/camera)))
      (and (not is-gripping) last-grip-vel (< (- (.now js/Date)  last-grip-time) GRIP_DECELERATION_INT))
      (let [delta-time (- (.now js/Date) last-grip-time)
            vel-strength (* 5.6 (- 1.0 (/ delta-time GRIP_DECELERATION_INT)))
            delta-pos (.multiplyByFloats last-grip-vel vel-strength vel-strength vel-strength)]
        (set! (.-position main-scene/camera) (.add (.-position main-scene/camera) delta-pos))))))
