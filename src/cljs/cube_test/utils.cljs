;; this is for utils that need to be .cljs.  If it's a generic
;; util not dependent on cljs, consider using 'cljc/cube_test/utils/common.cljc
(ns cube-test.utils
  (:require
   ;; Note: get circulard dep. warning if you include 'cube-test.core'
   ; [cube-test.core :as re-frame]))
   ; [cube-test.base :as base]))
   [re-frame.loggers :as loggers]
   [clojure.spec.alpha :as s]
   [cube-test.main-scene :as main-scene]
   [cube-test.base :as base]
   [babylonjs :as bjs]))


;; Convert ":17" to 17, for example
(defn kw-to-int [kw]
  (-> (re-find #"^:(\d{1,3})" (str kw)) (nth 1) (js/parseInt)))

(defn merge-dbs [db1 db2]
  "Merge two maps into one"
  (reduce #(do
             (assoc %1 (first %2) (second %2)))
          db1 db2))


(defn check-and-throw
  "Throws an exception if `db` doesn't match the Spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))

(defn rf-override-logger [& args]
  (let [text (apply str args)]
    (when (not (re-matches #".*twizzlers.*" text))
      (js/console.warn text))))

(defn rf-odoyle-warn-override-logger [& args]
  (let [text (apply str args)]
    (if (re-matches #".*no handler registered for effect:.*Ignoring.*" text)
      (js/console.log "probable rf-odoyle interceptor no handler msg warning detected")
      (js/console.warn text))))

(defn get-xr-camera []
  (case main-scene/xr-mode
    "vr" (.-deviceOrientationCamera main-scene/vrHelper)
    ; "xr" (.-camera main-scene/xr-helper)
    ; "xr" (-> main-scene/xr-helper .-baseExperience .-camera)
    ;; note: our xr-mode basically makes sure that the main-scene camera
    ;; has the appropriate xr or non-xr camera, so this is kind of a
    ;; degenerate case i.e we simply return the main-scene camera.
    "xr" (-> main-scene/camera)))

(defn toggle-visibility [mesh-id]
  (let [scene main-scene/scene
        mesh (.getMeshByID scene mesh-id)
        current-visibility (.-isVisble mesh)]
    (.setEnabled mesh (not current-visibility))))

(defn toggle-enabled [mesh-id]
  (let [scene main-scene/scene
        mesh (.getMeshByID scene mesh-id)
        current-enabled (.isEnabled mesh)]
    (.setEnabled mesh (not current-enabled))))

;; Note: "visibility" is better controlled by setting the 'enabled'
;; property, as some meshes, such as models, are a tree structure
;; and merely setting the .visibility of the root mesh is not suffictient
;; to make the entire tree visble/invisible.
(defn set-enabled [mesh-id value]
  (let [scene main-scene/scene
        mesh (.getMeshByID scene mesh-id)]
    (.setEnabled mesh value)))

(defn start-animation [anim-name speed-ratio from to]
 (let [scene main-scene/scene
       ag (.getAnimationGroupByName scene anim-name)]
   (.start ag true speed-ratio from to)))

(defn stop-animation [anim-name]
  (prn "utils.stop-animation: anim-name=" anim-name)
  ; (js-debugger)
  (let [scene main-scene/scene
        ; anim-name-fq (str (name anim-name) "-anim")
        ag (.getAnimationGroupByName scene anim-name)]
      (prn "utils.stop-animation: ag=" ag)
      (.stop ag)))


;; example call:
;; (sleep #(prn "hi") 5000)
(defn sleep [f ms]
  (js/setTimeout f ms))

(defn disable-default-joystick-ctrl []
  "disable the default teleoporation features of the bjs xr experience"
  ; (prn "utils.ddjc a")
  (when (= main-scene/xr-mode "xr")
    (let [xr-helper main-scene/xr-helper
          fm (-> xr-helper (.-baseExperience) (.-featuresManager))
          tf (.getEnabledFeature fm bjs/WebXRFeatureName.TELEPORTATION)]
        ;; basically turn off the default rotation, so we can override at the scene/game level
        ; (prn "utils.ddjc b")
        ;; by not having a detach and only zeroing out the rotationAngle, we can at least
        ;; use the joystick of the right controller to do a teleport and thus get a ray pointer.
        ;; Kind of a hack, but good enough for now.
        ; (.detach tf)
        (set! (.-rotationAngle tf) 0))))

;; Every scene may need to potentially alter the xr camera, so we supply
;; this in utils instead of duplicating for each scene.
(defn tweak-xr-view [xr yr zr xr-state]
  ; ([state delta-rot]
   (prn "utils: init view entered,xr=" xr, ",state=" xr-state)
   (when (= xr-state bjs/WebXRState.IN_XR)
     (let [
           ;; this is a floating value that covers both vr and non-vr cameras
           current-cam (.-activeCamera main-scene/scene)
           ; quat-delta (bjs/Quaternion.RotationYawPitchRoll (* base/ONE-DEG delta-rot) 0 0)
           quat-delta (bjs/Quaternion.RotationYawPitchRoll (* base/ONE-DEG xr) (* base/ONE-DEG yr) (* base/ONE-DEG zr))
           pos-delta (bjs/Vector3. 0 0 (if (neg? xr)
                                         -1
                                         1))
           pos-delta-left (bjs/Vector3. -5 0 6)
           pos-delta-top (bjs/Vector3. 0 0 6)
           x-rot (-> current-cam (.-rotation) (.-x))
           y-rot (-> current-cam (.-rotation) (.-y))
           delta-rot-rads (-> (bjs/Angle.FromDegrees xr) (.radians))
           unit-circ (bjs/Vector3. (js/Math.sin (+ y-rot delta-rot-rads)) 0 (js/Math.cos (+ y-rot delta-rot-rads)))
           new-tgt (.add (.-position current-cam) unit-circ)]
        (.setTarget current-cam new-tgt)
        ; (set! (.-rotation current-cam) (bjs/Vector3. (* 20 base/ONE-DEG) 0 0))
        (set! (.-rotation current-cam) (bjs/Vector3. (* xr base/ONE-DEG) (* yr base/ONE-DEG) (* yr base/ONE-DEG)))
        (set! (.-position current-cam) (.add (.-position current-cam) pos-delta-top)))))

;; pretty-print all the meshes in a scene
(defn pretty-print-meshes [scene]
  (let [mesh-objs (.-meshes scene)]
    (doall (map #(prn (.-name %)) mesh-objs))))


(defn load-model [path file name user-cb]
  (.ImportMesh bjs/SceneLoader ""
               path
               file
               main-scene/scene
               user-cb))

;;TODO: this really belongs in top-scene (?)
(defn release-common-scene-assets []
  (prn "utils/release-main-scene-assets: entered")
  ; (set! top-scene-assets (bjs/AssetContainer. main-scene/scene))
  ; (set! keep-assets (bjs/KeepAssets.))
  (let [scene main-scene/scene
        top-scene-assets (bjs/AssetContainer. main-scene/scene)
        keep-assets (bjs/KeepAssets.)
        uni-cam (.getCameraByID scene "uni-cam")
        webxr (.getCameraByID scene "webxr")
        rig-camera (nth (.-rigCameras main-scene/xr-helper.baseExperience.camera) 0)
        sky-box (.getMeshByID scene "sky-box")
        teleportation-target (.getMeshByID scene "teleportationTarget")
        bg-helper (.getMeshByID scene "BackgroundHelper")
        grnd (.getMeshByID scene "ground")]
    ; (js-debugger)
    (.push (.-cameras keep-assets) uni-cam webxr)
    (when rig-camera
      (.push (.-cameras keep-assets) rig-camera))
    ; (.push (.-cameras keep-assets) uni-cam)
    (.push (.-meshes keep-assets) sky-box teleportation-target bg-helper grnd)
    ; (.push (.-meshes keep-assets) teleportation-target)
    (.moveAllFromScene top-scene-assets keep-assets)))
    ; (.removeAllFromScene top-scene-assets)))
 
;; A generalized btn handler to throw up a gui in front of the camera
(defn gui-btn-handler [btn gui]
  ;; (prn "utils-btn-handler: btn pressed hasChanges=" (.-hasChanges btn))
  (when (.-hasChanges btn)
    (let [scene main-scene/scene
          ;; main-gui-plane (.getMeshByID scene "main-gui-plane")
          cam (cube-test.utils.get-xr-camera)
          cam-quat (.-rotationQuaternion cam)
          cam-rot (.toEulerAngles cam-quat)
          cam-rot-y (.-y cam-rot)
          gui-radius 4.0
          gui-delta-x (* gui-radius (js/Math.cos (.-x cam-rot)))
          gui-delta-z (* gui-radius (js/Math.sin (.-z cam-rot)))
          ; gui-delta (bjs/Vector3. gui-delta-x 0 gui-delta-z)
          ; gui-delta (bjs/Vector3. 0 0 8)
          gui-delta (bjs/Vector3. (* gui-radius (js/Math.sin cam-rot-y)) 0 (* gui-radius (js/Math.cos cam-rot-y)))]
      (when gui
        ;; (prn "gui-btn-handler: cam-rot=" cam-rot)
        (set! (.-position gui) (.add (.-position cam) gui-delta))
        (set! (.-rotation gui) (bjs/Vector3. 0 cam-rot-y 0))
        ; (.addInPlace  (.-position main-gui-plane) gui-delta)
        ;; and toggle the visibility
        ; (.setEnabled main-gui-plane true)
        (if (not (.isEnabled gui))
          (.setEnabled gui true)
          (.setEnabled gui false))))))
