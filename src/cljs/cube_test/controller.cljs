(ns cube-test.controller
  (:require [babylonjs]
            [re-frame.core :as re-frame]
            [cube-test.db :as db]))
            ; [re-con.main-scene :as main-scene]))
;; 'scene' and 'vr-helper' need to be passed to us. It's our hook into main_scene.  We can't require it though
;; due to circular dependencies.
(def scene)
(def vr-helper)
;; helper variable
(def camera)

; (def count 0)
(def isGripping false)
(def gripStartPos)
; (def startCtrlPos)
(def playerStartPos)
(def lastPlayerPos)
(def lastGripVel)
; Note: this is incredibly sensitive
; (def gripFactor 1.8)
(def gripFactor 1.9)
(def lastGripTime)
; (def vrHelper)
; (def camera)
(def leftController)
(def GRIP_DECELERATION_INT 1000)

;; Separated out because I thought at one time these had to be intialized up VR-Enter event.
;; But now it seems OK to call at scene initialization.
(defn init-vars []
  (set! lastGripTime 0)
  (set! lastGripVel (js/BABYLON.Vector3. 0 0 0))
  (set! lastPlayerPos (.-position camera)))

(defn init [tgt-scene tgt-vr-helper cam]
  (set! scene tgt-scene)
  (set! vr-helper tgt-vr-helper)
  ; (set! camera (.-webVRCamera vr-helper))
  (set! camera cam)
  (init-vars))


(defn ^:export tick []
  ; (println "tick2: entry: camera.pos=" (.-position re-con.core/camera))
  (when camera
    (set! leftController (.-leftController camera)))
  ; (println "tick: camera=" camera ",leftController=" leftController)
  (when (and camera leftController)
    (cond
      isGripping
      (let [ctrlDeltaPos (-> (.-devicePosition leftController) (.subtract gripStartPos) (.multiplyByFloats gripFactor gripFactor gripFactor))
            newPos (.subtract (.-position camera) ctrlDeltaPos)]
        (set! (.-position camera) newPos)
        (set! lastGripVel (.subtract newPos lastPlayerPos))
        (set! lastPlayerPos (.-position camera)))
      (and (not isGripping) lastGripVel (< (- (.now js/Date)  lastGripTime) 1000))
      (let [deltaTime (- (.now js/Date) lastGripTime)
            velStrength (* 5.6 (- 1.0 (/ deltaTime GRIP_DECELERATION_INT)))
            deltaPos (.multiplyByFloats lastGripVel velStrength velStrength velStrength)]
        (set! (.-position camera) (.add (.-position camera) deltaPos))))))

(defn enter-vr-cb []
  (println "entered VR")
  (init-vars))

(defn ^:export setup-vr-callbacks [vrHelper]
  (-> vrHelper (.-onEnteringVR) (.add enter-vr-cb)))

(defn click-handler [] (fn []
                         (println "controler.cljs: click detected")))

(defn trigger-handler [stateObject])
  ;; simply promote to a re-frame method so we can have access to the db.
  ; (println "babylon triggerStateChangeObservable fired")
  ; (re-frame/dispatch [:trigger-handler-2 stateObject])
  ; (re-frame/dispatch [:trigger-handler stateObject])
  ; (re-frame/dispatch [:rebus-panel-trigger-handler stateObject]))

(defn grip-handler [stateObject]
  ; (println "now in cljs side-trigger-handler")
  (if (.-pressed stateObject)
    ; (when (and scene.camera.leftController (not (isGripping))))
    ; (when (and (-> scene .-camera .-leftController) (not (isGripping))))
    (when (and (-> camera .-leftController) (not isGripping))
      ; (set! gripStartPos (-> vrHelper (.-webVRCamera) (.-leftController) (.-devicePosition) (.clone)))
      (set! gripStartPos (-> camera (.-leftController) (.-devicePosition) (.clone)))
      (set! isGripping true)
      (set! playerStartPos (.-position camera))
      (set! lastGripTime (.now js/Date)))
    (set! isGripping false)))

(defn controller-mesh-loaded-handler [webVRController]
  (println "now in controller-mesh-loaded-handler")
  ; (.add (.-onSecondaryTriggerStateChangedObservable webVRController) trigger-handler))
  (-> webVRController (.-onTriggerStateChangedObservable) (.add trigger-handler))
  (-> webVRController (.-onSecondaryTriggerStateChangedObservable) (.add grip-handler))
  (js/window (.addEventListener "onclick" click-handler)))

(defn setup-controller-handlers [vrHelper]
  (println "setup-controler-handlers: entered")
  (-> vrHelper (.-onControllerMeshLoaded) (.add controller-mesh-loaded-handler)))

;; xr support
