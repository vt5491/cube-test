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
   [goog.object :as g]
   [ajax.core :as ajax]))
   ; [ajax.core :refer [GET]]))

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

   ; {:keys [db]}))
(reg-event-fx
 ::play-song-anim
 ; (fn [cofx [_ ])
 (fn [{:keys [db]} _]
   (println "events.play-song-anim: db=" db)
   ; {:fx [(beat-club.scene/play-song-anim (:db cofx))]}
   {:fx [(beat-club.scene/play-song-anim db)]}
   db))
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
          ; "sounds/music_tracks/montrose-rock-candy.mp3"
          "https://localhost:8281/sounds/music_tracks/montrose-rock-candy.mp3"
          beat-club.scene/mp3-loaded)
         (beat-club.scene/create-drum-twitches)
         (re-frame/dispatch [::load-intervals])]}))

   ; (re-frame/dispatch [:cube-test.beat-club.events/load-rock-candy])
   ; (re-frame/dispatch [:cube-test.beat-club.events/create-drum-twitches])))
   ; {:fx [(beat-club.scene/start-twitch-seq)]}
   ; {:fx [(beat-club.scene/load-rock-candy)
   ;       (beat-club.scene/create-drum-twitches)]}))
   ; {:fx [(beat-club.scene/load-mp3 "rock-candy" "sounds/music_tracks/montrose-rock-candy.mp3")
   ;       (beat-club.scene/create-drum-twitches)
   ;       ; (js/setTimeout #(beat-club.scene/play-track) 500)]}))
   ;       (beat-club.scene/play-track)]}))

; (reg-event-fx
;  ::dummy
;  (fn [cofx [_]]
;    (println "events.dummy: mutha=" (g/get js/window "console.log"))
;    ; (g/set js/window "my-js-property" false)
;    ; (g/set js/window "my-js-property" (fn [] (+ 1 1)))
;    (g/set js/window "my-js-property" (fn [x] (+ 1 x)))
;    (prn "my-js-propery=" ((g/get js/window "my-js-property") 7))
;    ; ((g/get js/window "console.log") "hi")
;    {:fx [(beat-club.scene/load-mp3
;           "rock-candy"
;           "sounds/music_tracks/montrose-rock-candy.mp3"
;           (js/cube_test.beat_club.scene.mutha2))]}))
;           ; (fn [] (prn "song loaded mutha"))
;           ; #(prn "song loaded mutha"))]}))
;           ; beat-club.scene/mutha)]}))
;           ; cube-test.beat-club.scene/mutha
;           ; (g/get js/window "cube_test.beat_club.scene.mutha2"))]}))
;           ; (fn [](. js/window cube_test.beat_club.scene.mutha)))]}))
;           ; (js/cube_test.beat_club.scene.mutha2))]}))

(reg-event-fx
 ::dummy
 (fn [cofx [_]]
   (prn "dummy: cofx=" cofx)
   (prn "db@intevals=" (:intervals (:db cofx)))))

(re-frame/reg-event-db
 :song-loaded
 (fn [db [_]]
   (prn "events: song-loaded")
   (assoc db :song-loaded true)))

(reg-event-fx
 ::load-intervals-old
 ; (fn [cofx [_]])
 (fn [_ [_ json-file]]
   (println "events.load-intervals: json-file=" json-file)
   {:fx [(beat-club.scene/load-intervals json-file)]}))

(reg-event-db
  ::success-http-result
  (fn [db [_ result]]
    (prn "success-http-result: result=" result)
    ; (assoc db :success-http-result result)))
    db))

(reg-event-db
  ::failure-http-result
  (fn [db [_ result]]
    ;; result is a map containing details of the failure
    ; (assoc db :failure-http-result result)
    (prn "failure-http-result: result=" result)
    db))

(reg-event-fx                             ;; note the trailing -fx
  ::handler-with-http                      ;; usage:  (dispatch [:handler-with-http])
  (fn [{:keys [db]} _]                    ;; the first param will be "world"
    (prn "events.handler-with-http entered: db=" db)
    {
     :db   (assoc db :show-twirly true)   ;; causes the twirly-waiting-dialog to show??
     :http-xhrio {:method          :get
                  :uri             "https://api.github.com/orgs/day8"
                  ; :uri             "https://localhost:8281/sounds/rock_candy_intervals.txt"
                  :timeout         8000                                           ;; optional see API docs
                  :response-format (ajax/json-response-format {:keywords? true})  ;; IMPORTANT!: You must provide this.
                  ; :on-success      [::success-http-result]
                  :on-success      [:cube-test.beat-club.events/success-http-result]
                  ; :on-failure      [::failure-http-result]
                  :on-failure      [:cube-test.beat-club.events/failure-http-result]}}))

; (reg-event-fx
;  ::process-intervals-json
;  ; (fn [cofx [_]])
;  (fn [_ [_ json db]]
;    ; (js-debugger)
;    (println "events.process-response: json=" json)
;    {:fx [(beat-club.scene/load-intervals json)]}))
(reg-event-db
  ::process-intervals-json
  (fn
    [db [_ json]]
    (let [intervals (beat-club.scene/load-intervals json)]
      (re-frame/dispatch [:cube-test.beat-club.events/inc-twitch-load-status])
      (assoc db :intervals intervals))))
    ; (-> db
    ;     (assoc :loading? false) ;; take away that "Loading ..." UI
    ;     (assoc :data (js->clj response)))))  ;; fairly lame processing

(reg-event-db
  ::bad-response
  (fn [db [_ result]]
    (prn "bad-response: result=" result)
    ; (assoc db :success-http-result result)))
    db))

(reg-event-db
  ::load-intervals
  (fn
    [db _]
    (ajax/GET
      ; "https://api.github.com/orgs/day8"
      "https://localhost:8281/sounds/rock_candy_intervals.txt"
      {:handler       #(re-frame/dispatch [::process-intervals-json %1 db])
       :error-handler #(re-frame/dispatch [::bad-response %1])})

    ; (assoc db :loading? true)
    db))

(reg-event-db
  ::inc-twitch-load-status
  (fn [db _]
   (let [current-twitch-load-status (:twitch-load-status db)]
      (prn "event: inc-twitch-load-status: current-twitch-load-status=" current-twitch-load-status)
      (assoc db :twitch-load-status (+ current-twitch-load-status 1)))))
      ; (assoc db :name "dummy"))))
      ; db)))
