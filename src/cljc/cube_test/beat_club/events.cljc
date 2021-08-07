;; events is refer to many
(ns cube-test.beat-club.events
  (:require
   [re-frame.core :refer [reg-event-db reg-event-fx reg-fx after ] :as re-frame]
   ; [cube-test.events :as events]
   [cube-test.beat-club.game :as beat-club.game]
   [cube-test.beat-club.scene :as beat-club.scene]
   [cube-test.beat-club.db :as beat-club.db]
   ; [cube-test.twizzlers.twizzler :as twizzlers.twizzler]
   ; [cube-test.twizzlers.rules :as twizzlers.rules]
   [cube-test.utils :as utils]
   [goog.object :as g]))

(re-frame/reg-event-fx
 ::init-scene
 (fn [cofx _]
   ; (prn "::init-scene: cofx=" cofx)
   (beat-club.scene/init (:db cofx))
   cofx))

(re-frame/reg-event-fx
 ::init-game
 (fn [cofx _]
   beat-club.game/init
   {
    :fx [[:dispatch [::init-scene]]]}))

(re-frame/reg-event-fx
 ::run-game
 ; [twizzlers-check-spec-interceptor]
 (fn [_]
   (beat-club.game/run-game)))

(reg-event-db
 ::init-db
 (fn [db [_ id]]
   (println ":beat-club.init-db: now running")
   (beat-club.db/init-db db)))

(reg-event-fx
 ::play-song-anim
 (fn [cofx [_]]
   (println "events.play-song:")
   {:fx [(beat-club.scene/play-song-anim)]}))
   ; (beat-club.scene/play-song)))
   ; (beat-club.scene/ps2)))
   ; {:fx [(beat-club.scene/ps2)]}))

; (reg-event-db
;  ::play-song-2
;  (fn [db [_ id]]
;    (println ":beat-club.play-song-2: now running")
;    (beat-club.scene/play-song-2 db)))

(reg-event-fx
 ::load-rock-candy
 (fn [cofx [_]]
   (println "events.load-rock-candy:")
   {:fx [(beat-club.scene/load-mp3
          "rock-candy"
          "sounds/music_tracks/montrose-rock-candy.mp3"
          ; js/cube_test.beat_club.scene.mutha2
          ; js/cube_test.beat_club.scene.mp3-loaded
          beat-club.scene/mp3-loaded)]}))

(reg-event-fx
 ::pause-song
 (fn [cofx [_]]
   (println "events.pause-song:")
   {:fx [(beat-club.scene/pause-song)]}))

(reg-event-fx
 ::stop-song
 (fn [cofx [_]]
   (println "events.stop-song:")
   {:fx [(beat-club.scene/stop-song)]}))

(reg-event-fx
 ::play-track
 (fn [cofx [_]]
   (println "events.play-track:")
   {:fx [(beat-club.scene/play-track)]}))

(reg-event-fx
 ::create-drum-twitches
 (fn [cofx [_]]
   (println "events.create-drum-twitches:")
   {:fx [(beat-club.scene/create-drum-twitches)]}))

(reg-event-fx
 ::start-twitch-seq
 (fn [cofx [_]]
   (println "events.start-twitch-seq:")
   {:fx [(beat-club.scene/load-mp3
          "rock-candy"
          "sounds/music_tracks/montrose-rock-candy.mp3"
          beat-club.scene/mp3-loaded)
         (beat-club.scene/create-drum-twitches)]}))

   ; (re-frame/dispatch [:cube-test.beat-club.events/load-rock-candy])
   ; (re-frame/dispatch [:cube-test.beat-club.events/create-drum-twitches])))
   ; {:fx [(beat-club.scene/start-twitch-seq)]}
   ; {:fx [(beat-club.scene/load-rock-candy)
   ;       (beat-club.scene/create-drum-twitches)]}))
   ; {:fx [(beat-club.scene/load-mp3 "rock-candy" "sounds/music_tracks/montrose-rock-candy.mp3")
   ;       (beat-club.scene/create-drum-twitches)
   ;       ; (js/setTimeout #(beat-club.scene/play-track) 500)]}))
   ;       (beat-club.scene/play-track)]}))

(reg-event-fx
 ::dummy
 (fn [cofx [_]]
   (println "events.dummy: mutha=" (g/get js/window "console.log"))
   ; (g/set js/window "my-js-property" false)
   ; (g/set js/window "my-js-property" (fn [] (+ 1 1)))
   (g/set js/window "my-js-property" (fn [x] (+ 1 x)))
   (prn "my-js-propery=" ((g/get js/window "my-js-property") 7))
   ; ((g/get js/window "console.log") "hi")
   {:fx [(beat-club.scene/load-mp3
          "rock-candy"
          "sounds/music_tracks/montrose-rock-candy.mp3"
          (js/cube_test.beat_club.scene.mutha2))]}))
          ; (fn [] (prn "song loaded mutha"))
          ; #(prn "song loaded mutha"))]}))
          ; beat-club.scene/mutha)]}))
          ; cube-test.beat-club.scene/mutha
          ; (g/get js/window "cube_test.beat_club.scene.mutha2"))]}))
          ; (fn [](. js/window cube_test.beat_club.scene.mutha)))]}))
          ; (js/cube_test.beat_club.scene.mutha2))]}))

(re-frame/reg-event-db
 :song-loaded
 (fn [db [_]]
   (prn "events: song-loaded")
   (assoc db :song-loaded true)))
