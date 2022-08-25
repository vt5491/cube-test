;; developed approx between 8/7/2021 and 9/4/2021
(ns cube-test.beat-club.scene
  (:require-macros [cube-test.macros :as macros])
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [babylonjs-gui :as bjs-gui]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.cube-fx :as cube-fx]
   [cube-test.utils.fps-panel :as fps-panel]
   [cube-test.beat-club.note-twitch :as note-twitch]
   [cube-test.base :as base]
   [cube-test.utils :as utils]
   [cube-test.beat-club.twitch-stream :as twitch-stream]
   [babylonjs-loaders :as bjs-l]))

(declare twitch-stream)
(declare twitch-control-stream)
(declare start-animation)
(def rock-candy-track)
(def snare-twitch)
(def kick-twitch)
(def hi-hat-twitch)
(def tom-1-twitch)
(def tom-2-twitch)
(def tom-3-twitch)
(def crash-twitch)
(def ^:dynamic *twitch-streaming-active* (atom false))
(def particle-helper)
(def ^:dynamic *anim-loop-count* (atom 0))

(defn ^:export dummyadd [x]
  (prn "now in dummyadd, x=" x)
  (+ 1 7))

(defn set-mat-2 [mesh-id mat]
  (let [scene main-scene/scene
        mesh (.getMeshByID scene mesh-id)]
    (set! (.-material mesh) mat)))

(defn set-mat [mesh mat]
    (set! (.-material mesh) mat))

(defn twitch-note [voice]
  (let [scene main-scene/scene
        snare-twitch (.getMeshByID scene "snare-twitch")
        kick-twitch (.getMeshByID scene "kick-twitch")
        hi-hat-twitch (.getMeshByID scene "hi-hat-twitch")
        tom-1-twitch (.getMeshByID scene "tom-1-twitch")
        tom-2-twitch (.getMeshByID scene "tom-2-twitch")
        tom-3-twitch (.getMeshByID scene "tom-3-twitch")
        crash-twitch (.getMeshByID scene "crash-twitch")]

    (case voice
      :SNARE (do
               (set! (.-material snare-twitch) main-scene/orange-mat)
               (js/setTimeout #(set-mat-2 "snare-twitch" main-scene/blue-mat) 200))
      :KICK  (do
               (set! (.-material kick-twitch) main-scene/orange-mat)
               (js/setTimeout #(set-mat-2 "kick-twitch" main-scene/blue-mat) 200))
      :HI-HAT  (do
                  (set! (.-material hi-hat-twitch) main-scene/orange-mat)
                  (js/setTimeout #(set-mat-2 "hi-hat-twitch" main-scene/blue-mat) 200))
      :TOM-1 (do
                (set! (.-material tom-1-twitch) main-scene/orange-mat)
                (js/setTimeout #(set-mat-2 "tom-1-twitch" main-scene/blue-mat) 200))
      :TOM-2 (do
                (set! (.-material tom-2-twitch) main-scene/orange-mat)
                (js/setTimeout #(set-mat-2 "tom-2-twitch" main-scene/blue-mat) 200))
      :TOM-3 (do
                (set! (.-material tom-3-twitch) main-scene/orange-mat)
                (js/setTimeout #(set-mat-2 "tom-3-twitch" main-scene/blue-mat) 200))
      :CRASH (do
                (set! (.-material crash-twitch) main-scene/orange-mat)
                (js/setTimeout #(set-mat-2 "crash-twitch" main-scene/blue-mat) 200)))))


(defn twitch-interval [voice interval intervals]
  (cond
    (and (not (nil? interval)) (not (empty? intervals)))
    (js/setTimeout #(do
                      (twitch-note voice)
                      (twitch-stream voice intervals))
                   interval)
    (and (not (nil? interval)) (empty? intervals))
    (do
      (js/setTimeout #(do
                         (twitch-note voice))
                      interval))
    :else (prn "nulls all around")))

(defn twitch-stream [voice intervals]
  (when @*twitch-streaming-active*
    (twitch-interval voice (first intervals) (rest intervals))))

(defn twitch-control-interval [type obj interval intervals action]
  (cond
    (and (not (nil? interval)) (not (empty? intervals)))
    (js/setTimeout
      #(do
         (apply action [])
         (prn "hello from control-interval, action=" action)
         (twitch-control-stream type obj intervals action))
       interval)
    (and (not (nil? interval)) (empty? intervals))
    (js/setTimeout #(do action [])
                   interval)))

(defn twitch-control-stream [type obj intervals action]
  (prn "scene.twitch-control-stream: type=" type ",obj=" obj
       ",intervals=" intervals ",action=" action)
  (when @*twitch-streaming-active*
    (twitch-control-interval type obj (first intervals) (rest intervals) action)))

(defn play-song-anim [db]
  (swap! *twitch-streaming-active* (fn [x] true))
  (twitch-stream :SNARE (-> db :intervals :snare))
  (twitch-stream :KICK (-> db :intervals :kick))
  (twitch-stream :HI-HAT (-> db :intervals :hi-hat))
  (twitch-stream :TOM-1 (-> db :intervals :tom-1))
  (twitch-stream :TOM-2 (-> db :intervals :tom-2))
  (twitch-stream :TOM-3 (-> db :intervals :tom-3))
  (twitch-stream :CRASH (-> db :intervals :crash))
  ;; control intervals
  (when (-> db :control-intervals :toggle-model)
    (doall (map (fn [hash]
                  (prn "scene.play-song-anim: hash=" hash)
                  (prn "scene.play-song-anim: map? hash=" (map? hash) ",vector? hash=" (vector? hash))
                  (let [model (-> hash :obj name)
                        intervals (-> hash :intervals)]
                    ;;TODO I dont think type is needed at all
                    (twitch-control-stream :TOGGLE-MODEL
                                           model
                                           intervals
                                           #(utils/toggle-enabled model))))
                (-> db :control-intervals :toggle-model)))))

(defn stop-song-anim [db]
 (assoc db :twitch-streaming-active false))

(defn mp3-loaded []
  (prn "mp3-loaded2: rock-candy ready to roll, rock-candy-track=" rock-candy-track)
  (prn "mp3-loaded: about to dispatch inc-twitch-load-status")
  (re-frame/dispatch [:cube-test.beat-club.events/inc-twitch-load-status])
  (prn "mp3-loaded: about to dispatch song-loaded")
  (re-frame/dispatch [:cube-test.beat-club.events/song-loaded]))

(defn load-mp3
  ([id filename]
   (prn "load-mp3: 2 arg sig")
   (load-mp3 id filename mp3-loaded))
  ([id filename cb]
   (prn "load-mp3: 3 arg sig, cb=" cb)
   (prn "beat-club.scene.load-mp3: entered")
   (. js/window console.log "abcdef")
   (let []
        ;;  tmp js/mutha4]
     (prn "about to call bjs/Sound,id=" id ", filename=" filename)
     (set! rock-candy-track (bjs/Sound. id filename main-scene/scene
                              cb))
     (prn "just called bjs/Sound"))))

(defn pause-song []
  (.pause rock-candy-track))

(defn stop-song []
  (prn "scene: stop-song: stopping " rock-candy-track)
  (.stop rock-candy-track))

(defn play-track []
  (prn "play-track entered")
  (.play rock-candy-track))

(defn create-drum-twitches []
  (set! snare-twitch (note-twitch/init "snare-twitch" 1 1 (bjs/Vector3. 3 4 0)))
  (set! kick-twitch (note-twitch/init "kick-twitch" 2 2 (bjs/Vector3. 3 1 0)))
  (set! hi-hat-twitch (note-twitch/init "hi-hat-twitch" 1.5 1.5 (bjs/Vector3. 0 4 0) :SPHERE))
  (set! tom-1-twitch (note-twitch/init "tom-1-twitch" 0.75 0.75 (bjs/Vector3. 5 4 0) :CUBE))
  (set! tom-2-twitch (note-twitch/init "tom-2-twitch" 0.75 0.75 (bjs/Vector3. 6 4 0) :CUBE))
  (set! tom-3-twitch (note-twitch/init "tom-3-twitch" 0.75 0.75 (bjs/Vector3. 7 4 0) :CUBE))
  (set! crash-twitch (note-twitch/init "crash-twitch" 1 1 (bjs/Vector3. 7 6 0) :CUBE)))

(defn start-twitch-seq []
  (re-frame/dispatch [:cube-test.beat-club.events/load-rock-candy])
  (re-frame/dispatch [:cube-test.beat-club.events/create-drum-twitches])
  (re-frame/dispatch [:cube-test.beat-club.events/play-track]))

(defn parse-intervals [json]
  (prn "beat-club.scene.load-intervals: entered")
  (let [ intervals (js->clj (.parse js/JSON json) :keywordize-keys true)]
    intervals))

(defn init-gui []
  (let [
        top-plane (bjs/MeshBuilder.CreatePlane "top-plane" (js-obj "width" 5 "height" 3))
        top-adv-texture (bjs-gui/AdvancedDynamicTexture.CreateForMesh top-plane 2048 1024)
        top-pnl (bjs-gui/Grid.)
        top-hdr (bjs-gui/TextBlock.)
        play-twitch-btn (bjs-gui/Button.CreateSimpleButton "play-twitch-btn" "play twitch")
        stop-twitch-btn (bjs-gui/Button.CreateSimpleButton "stop-twitch-btn" "stop twitch")
        toggle-dancer-btn (bjs-gui/Button.CreateSimpleButton "toggle-dancer-btn" "toggle dancer")
        firework-btn (bjs-gui/Button.CreateSimpleButton "firework-btn" "firework")]
    (set! (.-position top-plane) (bjs/Vector3. 0 3 8))
    (.enableEdgesRendering top-plane)

    (.addControl top-adv-texture top-pnl)
    (set! (.-text top-hdr) "commands")
    (set! (.-height top-hdr) "500px")
    (set! (.-fontSize top-hdr) "160")
    (set! (.-color top-hdr) "white")

    ;; create 4 rows and 2 cols
    (.addRowDefinition top-pnl 0.25 false)
    (.addRowDefinition top-pnl 0.25)
    (.addRowDefinition top-pnl 0.25)
    (.addRowDefinition top-pnl 0.25)
    (.addColumnDefinition top-pnl 0.5)
    (.addColumnDefinition top-pnl 0.5)
    (.addControl top-pnl top-hdr 0 0)

    ;; play-twitch-btn
    (set! (.-autoScale play-twitch-btn) true)
    (set! (.-fontSize play-twitch-btn) "100")
    (set! (.-color play-twitch-btn) "white")
    (-> play-twitch-btn .-onPointerUpObservable
        (.add (fn [value]
                (println "play-twitch-btn pressed")
                (re-frame/dispatch [:cube-test.beat-club.events/full-twitch-seq]))))
    (.addControl top-pnl play-twitch-btn 1 0)

    ;; stop-twitch-btn
    (set! (.-autoScale stop-twitch-btn) true)
    (set! (.-fontSize stop-twitch-btn) "100")
    (set! (.-color stop-twitch-btn) "white")
    (-> stop-twitch-btn .-onPointerUpObservable
        (.add (fn [value]
                (println "stop-twitch-btn pressed")
                (re-frame/dispatch [:cube-test.beat-club.events/stop-song-anim]))))
    (.addControl top-pnl stop-twitch-btn 1 1)

    ;; toggle-dancer-btn
    (set! (.-autoScale toggle-dancer-btn) true)
    (set! (.-fontSize toggle-dancer-btn) "100")
    (set! (.-color toggle-dancer-btn) "white")
    (-> toggle-dancer-btn .-onPointerUpObservable
        (.add (fn [value]
                (println "toggle-dancer-btn pressed")
                (re-frame/dispatch [:cube-test.beat-club.events/toggle-dancer]))))
    (.addControl top-pnl toggle-dancer-btn 2 0)

    ;; firework-btn
    (set! (.-autoScale firework-btn) true)
    (set! (.-fontSize firework-btn) "100")
    (set! (.-color firework-btn) "white")
    (-> firework-btn .-onPointerUpObservable
        (.add (fn [value]
                (println "firework-btn pressed")
                (re-frame/dispatch [:cube-test.beat-club.events/firework]))))
    (.addControl top-pnl firework-btn 3 0)))

(defn firework []
  (prn "beat-club.scene: firework entered")
  (if particle-helper
    (do
      (if (.isStarted particle-helper)
        (.stop particle-helper)
        (.start particle-helper)))
    (do
      (set! particle-helper (bjs/ParticleHelper.CreateDefault. (bjs/Vector3. 0 3 4)))
      (.start particle-helper))))

;;TODO: put into some scene helper class as this isn't really direclty view-orientd
;; (a little but more state-oriented)
(defn toggle-dancer []
  (prn "beat-club.scene: toggle-dancer entered")
  (let [scene main-scene/scene
        mesh (.getMeshByID scene "ybot-combo")
        mesh-enabled (.isEnabled mesh)]
    (.setEnabled mesh (not mesh-enabled))))

;; TODO: alt way to not automatically start an anim upon loading
; BABYLON.SceneLoader.OnPluginActivatedObservable.addOnce(loader => {})
;   loader.animationStartMode = BABYLON.GLTFLoaderAnimationStartMode.NONE;
(defn model-loaded [new-meshes particle-systems skeletons name is-enabled is-playing props]
  (prn "model-loaded: count new-meshes=" (count new-meshes))
  (doall (map #(set! (.-scaling %1) (-> (js/BABYLON.Vector3.One) (.scale 2))) new-meshes))
  (doall (map #(do
                 (when (re-matches #"__root__" (.-id %1))
                     (set! (.-name %1) name)
                     (set! (.-id %1) name)))
              new-meshes))
  (re-frame/dispatch [:cube-test.beat-club.events/model-loaded name is-enabled is-playing props])
  ;;vt-x (re-frame/dispatch [:cube-test.beat-club.events/stop-animation name])
  (utils/toggle-enabled name))

(defn load-model [path file name is-enabled is-playing props]
  (prn "scene.load-model: name= " name)
  (.ImportMesh js/BABYLON.SceneLoader ""
             path
             file
             main-scene/scene
             #(model-loaded %1 %2 %3 name is-enabled is-playing props)))

(defn load-model-2 [path file name]
  (prn "scene.load-model-2: name=" name)
  (js/setTimeout #(.then (.ImportMeshAsync js/BABYLON.SceneLoader ""
                                              path
                                              file
                                              main-scene/scene
                                              (fn [](prn "on-progress")))
                         (fn [obj] (prn "model-2 loaded")))))

(defn load-model-3 [path file name]
  (prn "scene.load-model-3: name= " name)
  (.ImportMesh js/BABYLON.SceneLoader ""
             path
             file
             main-scene/scene
             #(do
                (prn "model" file " is loaded")
                (let [ag-1 (.getAnimationGroupByName main-scene/scene "mv_right_simp")
                      ag-2 (.getAnimationGroupByName main-scene/scene "mv_up_simp")
                      ag-3 (.getAnimationGroupByName main-scene/scene "mv_combo_baked")
                      ag-4 (.getAnimationGroupByName main-scene/scene "mv_rot_simp")
                      vt-baked-anim (.getAnimationGroupByName main-scene/scene "vt_baked")
                      ag-new (bjs/AnimationGroup. "ag-new")]
                  (.start vt-baked-anim true)))))

(defn load-model-4 [path file name]
  (prn "scene.load-model-4: name= " name)
  (.ImportMesh js/BABYLON.SceneLoader ""
             path
             file
             main-scene/scene
             #(do
                (prn "model" file " is loaded")
                (let [ag (.getAnimationGroupByName main-scene/scene "idle_grasp_baked")]
                  (.start ag true)))))

;; Note: bjs does not support fbx, so this doesn't work
(defn load-model-fbx [path file name]
  (let [am (bjs/AssetsManager. main-scene/scene)
        meshTask (.addMeshTask am "fbx-task" name path file)]
      (set! (.-onTaskSuccess meshTask)
        (fn []
          (prn "load-model-fbx: model loaded")
          (let [ag (.getAnimationGroupByName name)]
            (.start ag true))))
      (.load am)))

(defn init-animation-speed [model-kw speed-factor]
 (let [scene main-scene/scene
       anim-name-fq (str (name model-kw) "-anim")
       ag (.getAnimationGroupByName scene anim-name-fq)]
   (set! (.-speedRatio ag) speed-factor)))

(defn start-animation [anim-name speed-ratio from to]
 (prn "scene.start-animation: anim-name=" anim-name ", speed-ratio=" speed-ratio
      ",from=" from ", to=" to)
 (let [scene main-scene/scene
       anim-name-fq (str (name anim-name) "-anim")
       ag (.getAnimationGroupByName scene anim-name-fq)]
   (.reset ag)
   (.start ag true speed-ratio from to)
   (-> (.-onAnimationGroupLoopObservable ag) (.clear))
   (-> (.-onAnimationGroupLoopObservable ag) (.add twitch-stream/animGroupLoopHandler))))

(defn stop-animation [anim-name]
  (prn "scene.stop-animation: anim-name=" anim-name)
  (let [scene main-scene/scene
        anim-name-fq (str (name anim-name) "-anim")
        ag (.getAnimationGroupByName scene anim-name-fq)]
      (prn "scene.stop-animation: anim-name-fq=" anim-name-fq)
      (.stop ag)))

(defn init [db]
  (let [scene main-scene/scene
        light1 (bjs/PointLight. "pointLight" (bjs/Vector3. 0 5 -3) main-scene/scene)
        camera main-scene/camera
        camera-pos (.-position camera)]
    (init-gui)))
