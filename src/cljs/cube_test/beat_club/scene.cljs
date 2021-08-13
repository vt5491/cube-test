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
   [cube-test.utils :as utils]))
   ; [goog.object :as g]
   ; [clojure.data.json :as json]))

(declare twitch-stream)
(def rock-candy-track)
(def snare-twitch)
(def kick-twitch)
(def hi-hat-twitch)
;;TODO: turn into an atom?
; (def twitch-streaming-active)
(def ^:dynamic *twitch-streaming-active* (atom false))
(def particle-helper)

(defn ^:export dummyadd [x]
  (prn "now in dummyadd, x=" x)
  (+ 1 7))

(defn set-mat-2 [mesh-id mat]
  (let [scene main-scene/scene
        mesh (.getMeshByID scene mesh-id)]
    (set! (.-material mesh) mat)))

(defn set-mat [mesh mat]
  ; (let [scene main-scene/scene]
        ; mesh (.getMeshByID mesh-id scene)]
    (set! (.-material mesh) mat))

(defn twitch-note [voice]
  (let [scene main-scene/scene
        ; snare (.getMeshByID scene "snare-drum")
        snare-twitch (.getMeshByID scene "snare-twitch")
        kick-twitch (.getMeshByID scene "kick-twitch")
        hi-hat-twitch (.getMeshByID scene "hi-hat-twitch")]
    ; (set! (.-material snare) main-scene/green-mat)
    ; (js/setTimeout #(set-mat-2 "snare-drum" main-scene/blue-mat) 200)

    (case voice
      :SNARE (do
               (set! (.-material snare-twitch) main-scene/orange-mat)
               (js/setTimeout #(set-mat-2 "snare-twitch" main-scene/blue-mat) 200))
      :KICK  (do
               (set! (.-material kick-twitch) main-scene/orange-mat)
               (js/setTimeout #(set-mat-2 "kick-twitch" main-scene/blue-mat) 200))
      :HI-HAT  (do
                  (set! (.-material hi-hat-twitch) main-scene/orange-mat)
                  (js/setTimeout #(set-mat-2 "hi-hat-twitch" main-scene/blue-mat) 200)))))


(defn twitch-interval [voice interval intervals]
  ; (prn "twitch-snare-interval: interval=" interval ", intervals=" intervals ",
     ; (nil? intervals=)" (nil? intervals) ", (empty? intervals)" (empty? intervals))
  ; (when interval
    ; (if intervals)
  (cond
    (and (not (nil? interval)) (not (empty? intervals)))
    (js/setTimeout #(do
                      (twitch-note voice)
                      (twitch-stream voice intervals))
                      ; (prn "hi"))
                   interval)
    (and (not (nil? interval)) (empty? intervals))
    (do
      ; (prn "path b, interval=" interval)
      (js/setTimeout #(do
                         ; (prn "path-b2")
                         (twitch-note voice))
                         ; (set! (.-material (.getMeshByID main-scene/scene "snare-drum")) main-scene/red-mat)
                         ; (println "streaming done"))
                      interval))
    :else (prn "nulls all around")))

(defn twitch-stream [voice intervals]
  ; (prn "twitch-stream: intervals=" intervals)
  (when @*twitch-streaming-active*
    (twitch-interval voice (first intervals) (rest intervals))))

(defn play-song-anim [db]
  ; (prn "hello from play-song-anim, db=" db)
  ; (prn "play-song-anim: snare-stream from db=" (-> db :intervals :snare))

  ; (set! twitch-streaming-active true)
  (swap! *twitch-streaming-active* (fn [x] true))
  (twitch-stream :SNARE (-> db :intervals :snare))
  (twitch-stream :KICK (-> db :intervals :kick))
  (twitch-stream :HI-HAT (-> db :intervals :hi-hat)))
  ; (assoc db :twitch-streaming-active true))
  ; (let [snare-intervals [772,1541,1522,1536,1530,1531,1524,382,191,379,2115,1539,1525,1541,1531,1538,1532,1535,1527,1540,1520]]
  ;   (twitch-stream :SNARE snare-intervals))
  ; (let [kick-intervals [5,199,2863,391,1643,142,318,567,191,2873,382,2681,385,2691,388,1645,129,331,577,373,2690,383,1656,128,331,562,385,2687,377]]  ; (let [snare-intervals [500 500 1000 500]]
  ;   (twitch-stream :KICK kick-intervals)))

(defn stop-song-anim [db]
  (assoc db :twitch-streaming-active false))

(defn mp3-loaded []
  (prn "mp3-loaded2: rock-candy ready to roll, rock-candy-track=" rock-candy-track)
  (prn "mp3-loaded: about to dispatch inc-twitch-load-status")
  (re-frame/dispatch [:cube-test.beat-club.events/inc-twitch-load-status])
  (prn "mp3-loaded: about to dispatch song-loaded")
  (re-frame/dispatch [:song-loaded]))
  ; (.play rock-candy-track))
  ; (bjs/Sound. ))

(defn ^:export mutha []
  (prn "mutha"))

(defn ^:export mutha2 []
  (prn "mutha2"))


(defn load-mp3
  ([id filename]
   (prn "load-mp3: 2 arg sig")
   (load-mp3 id filename mp3-loaded))
  ([id filename cb]
   (prn "load-mp3: 3 arg sig, cb=" cb)
   (prn "beat-club.scene.load-mp3: entered")
   (. js/window console.log "abcdef")
   (let [
         tmp js/mutha4]
     (prn "about to call bjs/Sound,id=" id ", filename=" filename)
     (set! rock-candy-track (bjs/Sound. id filename main-scene/scene
                              cb))
     (prn "just called bjs/Sound"))))

(defn pause-song []
  (.pause rock-candy-track))

(defn stop-song []
  (.stop rock-candy-track))

(defn play-track []
  (prn "play-track entered")
  (.play rock-candy-track))
  ; (play-song-anim))

(defn create-drum-twitches []
  (set! snare-twitch (note-twitch/init "snare-twitch" 1 1 (bjs/Vector3. 3 4 0)))
  ; (prn "create-drum-twitchs: snare-twitch=" snare-twitch)
  (set! kick-twitch (note-twitch/init "kick-twitch" 2 2 (bjs/Vector3. 3 1 0)))
  ; (prn "create-drum-twitchs: kick-twitch=" kick-twitch)
  (set! hi-hat-twitch (note-twitch/init "hi-hat-twitch" 1.5 1.5 (bjs/Vector3. 0 4 0) :SPHERE)))

(defn start-twitch-seq []
  (re-frame/dispatch [:cube-test.beat-club.events/load-rock-candy])
  (prn "point b")
  (re-frame/dispatch [:cube-test.beat-club.events/create-drum-twitches])
  (prn "point c")
  (re-frame/dispatch [:cube-test.beat-club.events/play-track]))

(defn parse-intervals [json]
  ; (prn "beat-club.scene.load-intervals: json=" json)
  (prn "beat-club.scene.load-intervals: entered")
  ; (def a (.parse js/JSON json))
  ; (js->clj a :keywordize-keys true)
  ; (let [intervals (js->clj json)])
  (let [ intervals (js->clj (.parse js/JSON json) :keywordize-keys true)]
        ; intervals-2 (.parse js/JSON json)]
    ; (prn "scene.load-intervals: intervals=" intervals ", snare intervals=" (:snare intervals))
    intervals))

(defn init-gui []
  (let [
        top-plane (bjs/MeshBuilder.CreatePlane "top-plane" (js-obj "width" 5 "height" 3))
        top-adv-texture (bjs-gui/AdvancedDynamicTexture.CreateForMesh. top-plane 2048 1024)
        top-pnl (bjs-gui/Grid.)
        top-hdr (bjs-gui/TextBlock.)
        play-twitch-btn (bjs-gui/Button.CreateSimpleButton. "play-twitch-btn" "play twitch")
        stop-twitch-btn (bjs-gui/Button.CreateSimpleButton. "stop-twitch-btn" "stop twitch")
        firework-btn (bjs-gui/Button.CreateSimpleButton. "firework-btn" "firework")]
        ; left-side-rot-btn (bjs-gui/Button.CreateSimpleButton. "left-side-rot-btn" "left side rot")]
        ; y-quat-neg-90 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Y (* base/ONE-DEG -90)))]
    (set! (.-position top-plane) (bjs/Vector3. 0 3 8))
    ; (set! (.-rotationQuaternion top-plane) y-quat-neg-90)
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

    ;; firework-btn
    (set! (.-autoScale firework-btn) true)
    (set! (.-fontSize firework-btn) "100")
    (set! (.-color firework-btn) "white")
    (-> firework-btn .-onPointerUpObservable
        (.add (fn [value]
                (println "firework-btn pressed")
                (re-frame/dispatch [:cube-test.beat-club.events/firework]))))
    (.addControl top-pnl firework-btn 3 0)))

;BABYLON.ParticleHelper.CreateDefault(new BABYLON.Vector3(0, 0.5, 0)).start());
(defn firework []
  (prn "beat-club.scene: firework entered")
  ; (set! particle-helper (bjs/ParticleHelper.CreateDefault. (bjs/Vector3 0 3 0)))
  (let [ph (bjs/ParticleHelper.CreateDefault. (bjs/Vector3. 0 3 4))]
    (.start ph)))

(defn init [db]
  (let [scene main-scene/scene
        ; snare-drum (bjs/MeshBuilder.CreateBox "snare-drum" (js-obj "width" 2 "height" 2) scene)
        light1 (bjs/PointLight. "pointLight" (bjs/Vector3. 0 5 -3) main-scene/scene)]
    (init-gui)))
    ; (prn "scene.blue-mat=" main-scene/blue-mat)
    ; (set! (.-material snare-drum) main-scene/blue-mat)
    ; (set! (.-position snare-drum) (bjs/Vector3. 0 1 0))))
