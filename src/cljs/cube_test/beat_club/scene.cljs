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
   [goog.object :as g]))

(declare twitch-stream)
(def rock-candy-track)
(def snare-twitch)
(def kick-twitch)
; (def :SNARE "snare")
; (def :KICK "kick")

(defn ^:export dummyadd [x]
  (prn "now in dummyadd, x=" x)
  (+ 1 7))

(defn init [db]
  (let [scene main-scene/scene
        snare-drum (bjs/MeshBuilder.CreateBox "snare-drum" (js-obj "width" 2 "height" 2) scene)
        light1 (bjs/PointLight. "pointLight" (bjs/Vector3. 0 5 -3) main-scene/scene)]
    (prn "scene.blue-mat=" main-scene/blue-mat)
    (set! (.-material snare-drum) main-scene/blue-mat)
    (set! (.-position snare-drum) (bjs/Vector3. 0 1 0))
    (g/set js/window "cube_test.beat_club.scene.mutha3" (fn [x] (prn "mutha3")))
    (g/set js/window "cube_test.beat_club.scene.dummyadd" dummyadd)
    (g/set js/window "abc" dummyadd)))
;
; (defn render-loop []
;   (if (= main-scene/xr-mode "vr")
;     (controller/tick)
;     (controller-xr/tick))
;   (cube-fx/tick)
;   (fps-panel/tick main-scene/engine)
;   (.render main-scene/scene))
;
; (defn run-scene []
;   (.runRenderLoop main-scene/engine (fn [] (render-loop))))
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
        snare (.getMeshByID scene "snare-drum")
        snare-twitch (.getMeshByID scene "snare-twitch")
        kick-twitch (.getMeshByID scene "kick-twitch")]
    (set! (.-material snare) main-scene/green-mat)
    (js/setTimeout #(set-mat-2 "snare-drum" main-scene/blue-mat) 200)

    (case voice
      :SNARE (do
               (set! (.-material snare-twitch) main-scene/orange-mat)
               (js/setTimeout #(set-mat-2 "snare-twitch" main-scene/blue-mat) 200))
      :KICK  (do
               (set! (.-material kick-twitch) main-scene/orange-mat)
               (js/setTimeout #(set-mat-2 "kick-twitch" main-scene/blue-mat) 200)))))


(defn twitch-interval [voice interval intervals]
  (prn "twitch-snare-interval: interval=" interval ", intervals=" intervals ",
     (nil? intervals=)" (nil? intervals) ", (empty? intervals)" (empty? intervals))
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
      (prn "path b, interval=" interval)
      (js/setTimeout #(do
                         (prn "path-b2")
                         (twitch-note voice)
                         ; (set! (.-material (.getMeshByID main-scene/scene "snare-drum")) main-scene/red-mat)
                         (println "streaming done"))
                      interval))
    :else (prn "nulls all around")))

(defn twitch-stream [voice intervals]
  (prn "twitch-stream: intervals=" intervals)
  (twitch-interval voice (first intervals) (rest intervals)))

(defn play-song-anim []
  (prn "hello from play-song-anim")
  ; (let [snare-intervals [1000 1000 2000 1000]])
  ; (let [snare-intervals [780,1531,1533,1535,1534,1532,1538,384,193,384]])
  (let [snare-intervals [772,1541,1522,1536,1530,1531,1524,382,191,379,2115,1539,1525,1541,1531,1538,1532,1535,1527,1540,1520]]
  ; (let [snare-intervals [5,199,2863,391,1643,142,318,567,191,2873,382,2681,385,2691,388,1645,129,331,577,373,2690,383,1656,128,331,562,385,2687,377]]  ; (let [snare-intervals [500 500 1000 500]]
    (twitch-stream :SNARE snare-intervals))
  (let [kick-intervals [5,199,2863,391,1643,142,318,567,191,2873,382,2681,385,2691,388,1645,129,331,577,373,2690,383,1656,128,331,562,385,2687,377]]  ; (let [snare-intervals [500 500 1000 500]]
    (twitch-stream :KICK kick-intervals)))
    ; (prn "hi there"))
  ; (prn "bye there"))
    ; (js/setTimeout twitch-snare))
  ;   (doall (map #(js/setTimeout twitch-snare))))
  ; (twitch-snare))

; (defn play-song-2 [db]
;   (prn "abc")
;   (prn "def")
;   db)

(defn mp3-loaded []
  (prn "mp3-loaded2: rock-candy ready to roll, rock-candy-track=" rock-candy-track)
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
   ; (js-debugger)
   (. js/window console.log "abcdef")
   ; (set! rock-candy-track (bjs/Sound. id filename (-> js/window.console.log "mutha2")))
   ; (set! rock-candy-track (bjs/Sound. id filename (. js/window cube_test.beat_club.scene.mutha2)))))
   ; (set! rock-candy-track (bjs/Sound. id filename (. js/window cube_test.beat_club.scene.mp3-loaded)))

   ; (let [f (g/get js/window "cube_test.beat_club.scene.mutha3")]
   ;   (prn "f=" (f)))
     ; (prn "f()=" (f)))
   ; (js-debugger)
   (let [
         ; f (g/get js/window "cube_test.beat_club.scene.mutha3")
         f (g/get js/window "cube_test.beat_club.scene.dummyadd")
         ; g (g/get js/window "cube_test.beat_club.scene.mp3_loaded")]
         g (g/get js/window "cube-test.beat-club.scene.mp3-loaded")
         ; tmp (js/cube_test.beat_club.scene.mp3_loaded)
         ; tmp js/cube_test.beat_club.scene.mp3_loaded]
         ; tmp js/cube_test.beat_club.scene.dummyadd
         ;; native js func in index.html
         tmp js/mutha4]
     (prn "f=" f ",g=" g)
     (prn "tmp=" tmp)
     ; (prn "dummy_add fq=" js/window.cube_test.beat_club.scene.dummyadd)
     ; (prn "console.log=" (g/get js/window "console.log"))
     ; (prn "mutha3=" (g/get js/window "cube_test.beat_club.scene.mutha3"))
     ;; the following is how you pass an arg
     ; (prn "dummyadd non-force=" (js/cube_test.beat_club.scene.dummyadd 3))
     ; (prn "dummyadd non-force no arg=" (js/cube_test.beat_club.scene.dummyadd))
     ; (prn "dummyadd force=" ((js/cube_test.beat_club.scene.dummyadd) 2))
     ; (prn "f[]= " (:cube_test$beat_club$scene$dummyadd f))
     ; (js-debugger)
     (prn "about to call bjs/Sound,id=" id ", filename=" filename)
     ; (bjs/Sound. id filename main-scene/scene)
     (set! rock-candy-track (bjs/Sound. id filename main-scene/scene
                              cb))
                              ; tmp)))))
                             ; (js/cube_test.beat_club.scene.mp3_loaded))))))
                             ; (js/cube_test.beat_club.scene.dummyadd))))))
                             ; (js/mutha4))))))
                             ; (js/window "mutha4"))))))
                             ; (g/get js/window "cube_test.beat_club.scene.mutha3"))))))
                            ; "cube-test.beat-club.scene.dummyadd")))))
                              ; (g/get js/window "cube_test.beat_club.scene.mutha3"))))))
                            ; (if (nil? cb)
                            ;   (do main-scene/scene mp3-loaded)
                            ;   cb))))
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
  (prn "create-drum-twitchs: snare-twitch=" snare-twitch)
  (set! kick-twitch (note-twitch/init "kick-twitch" 2 2 (bjs/Vector3. 3 1 0)))
  (prn "create-drum-twitchs: kick-twitch=" kick-twitch))

(defn start-twitch-seq []
  (re-frame/dispatch [:cube-test.beat-club.events/load-rock-candy])
  (prn "point b")
  (re-frame/dispatch [:cube-test.beat-club.events/create-drum-twitches])
  (prn "point c")
  (re-frame/dispatch [:cube-test.beat-club.events/play-track]))
