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
(def tom-1-twitch)
(def tom-2-twitch)
(def tom-3-twitch)
(def crash-twitch)
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
        hi-hat-twitch (.getMeshByID scene "hi-hat-twitch")
        tom-1-twitch (.getMeshByID scene "tom-1-twitch")
        tom-2-twitch (.getMeshByID scene "tom-2-twitch")
        tom-3-twitch (.getMeshByID scene "tom-3-twitch")
        crash-twitch (.getMeshByID scene "crash-twitch")]
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

(defn play-song-anim [db]
  (swap! *twitch-streaming-active* (fn [x] true))
  (twitch-stream :SNARE (-> db :intervals :snare))
  (twitch-stream :KICK (-> db :intervals :kick))
  (twitch-stream :HI-HAT (-> db :intervals :hi-hat))
  (twitch-stream :TOM-1 (-> db :intervals :tom-1))
  (twitch-stream :TOM-2 (-> db :intervals :tom-2))
  (twitch-stream :TOM-3 (-> db :intervals :tom-3))
  (twitch-stream :CRASH (-> db :intervals :crash)))

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
        top-adv-texture (bjs-gui/AdvancedDynamicTexture.CreateForMesh. top-plane 2048 1024)
        top-pnl (bjs-gui/Grid.)
        top-hdr (bjs-gui/TextBlock.)
        play-twitch-btn (bjs-gui/Button.CreateSimpleButton. "play-twitch-btn" "play twitch")
        stop-twitch-btn (bjs-gui/Button.CreateSimpleButton. "stop-twitch-btn" "stop twitch")
        firework-btn (bjs-gui/Button.CreateSimpleButton. "firework-btn" "firework")]
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
