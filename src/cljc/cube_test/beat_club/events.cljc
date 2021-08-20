;; events is refer to many
(ns cube-test.beat-club.events
  (:require
   [re-frame.core :refer [dispatch reg-event-db reg-event-fx reg-fx after ] :as re-frame]
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
   ; (println "events.play-song-anim: db=" db)
   ; {:fx [(beat-club.scene/play-song-anim (:db cofx))]}
   {:fx [(do
           (beat-club.scene/play-song-anim db)
           (re-frame/dispatch [::twitch-streaming-active true]))]}))
   ; (assoc db :twitch-streaming-active true)))

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
 ::dummy
 (fn [cofx [_]]
   (prn "dummy: cofx=" cofx)
   (prn "db@intevals=" (:intervals (:db cofx)))
   {}))

(re-frame/reg-event-db
 ::song-loaded
 (fn [db [_]]
   (prn "events: song-loaded")
   (assoc db :song-loaded true)))

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

; (reg-event-fx                             ;; note the trailing -fx
;   ::handler-with-http                      ;; usage:  (dispatch [:handler-with-http])
;   (fn [{:keys [db]} _]                    ;; the first param will be "world"
;     (prn "events.handler-with-http entered: db=" db)
;     {
;      :db   (assoc db :show-twirly true)   ;; causes the twirly-waiting-dialog to show??
;      :http-xhrio {:method          :get
;                   :uri             "https://api.github.com/orgs/day8"
;                   ; :uri             "https://localhost:8281/sounds/rock_candy_intervals.txt"
;                   :timeout         8000                                           ;; optional see API docs
;                   :response-format (ajax/json-response-format {:keywords? true})  ;; IMPORTANT!: You must provide this.
;                   ; :on-success      [::success-http-result]
;                   :on-success      [:cube-test.beat-club.events/success-http-result]
;                   ; :on-failure      [::failure-http-result]
;                   :on-failure      [:cube-test.beat-club.events/failure-http-result]}}))

(reg-event-db
  ::process-intervals-json
  (fn
    [db [_ json]]
    (let [intervals (beat-club.scene/parse-intervals json)]
      (prn "events.process-intervals-json: about to call inc-twitch-load-status")
      ; (re-frame/dispatch [:cube-test.beat-club.events/inc-twitch-load-status])
      (re-frame/dispatch [::inc-twitch-load-status])
      (assoc db :intervals intervals))))

(reg-event-db
  ::bad-response
  (fn [db [_ result]]
    (prn "bad-response: result=" result)
    db))

(reg-event-db
  ::load-intervals
  (fn [db _]
    (ajax/GET
      "https://localhost:8281/sounds/rock_candy_intervals.txt"
      {:handler       #(re-frame/dispatch [::process-intervals-json %1 db])
       :error-handler #(re-frame/dispatch [::bad-response %1])})

    db))

(reg-event-db
  ::inc-twitch-load-status
  (fn [db _]
   (let [current-twitch-load-status (:twitch-load-status db)]
      (prn "event: inc-twitch-load-status: current-twitch-load-status=" current-twitch-load-status)
      (assoc db :twitch-load-status (+ current-twitch-load-status 1)))))

(reg-event-fx
 ::full-twitch-seq
 (fn [cofx [_]]
   (if (-> cofx :db :twitch-load-status (= 2))
     (do
       {:fx [ [:dispatch [::play-track]]
              [:dispatch [::play-song-anim]]
              [:dispatch [::start-animation]]]})
     ; (do
     ;   (re-frame/dispatch [:cube-test.beat-club.events/play-track])
     ;   (re-frame/dispatch [:cube-test.beat-club.events/play-song-anim])
     ;   (re-frame/dispatch [:cube-test.beat-club.events/start-animation])
     ;   {})
               ; (re-frame/dispatch [::load-model "models/beat_club/" "ybot_rumba.glb"]))
     ;; else
     (do
       ; (re-frame/dispatch [::load-model "models/beat_club/" "ybot_rumba.glb" "ybot-rumba"])
       {:fx [
             ; [(do
             ;    (beat-club.scene/load-mp3
             ;     "rock-candy"
             ;     ; "sounds/music_tracks/montrose-rock-candy.mp3"
             ;     "https://localhost:8281/sounds/music_tracks/montrose-rock-candy.mp3")
             ;    beat-club.scene/mp3-loaded
             ;    (beat-club.scene/create-drum-twitches))]
             ;;vt-x2
             [(beat-club.scene/load-mp3 "rock-candy" "https://localhost:8281/sounds/music_tracks/montrose-rock-candy.mp3" beat-club.scene/mp3-loaded)]
             [(beat-club.scene/create-drum-twitches)]
             ; [(beat-club.scene/load-model "models/beat_club/" "ybot_head_bang.glb" "ybot-head-bang")]
             ; [:dispatch [::load-model "models/beat_club/" "ybot_head_bang.glb" "ybot-head-bang"]]
             ; [:dispatch [::load-model-2 "models/beat_club/" "ybot_head_bang.glb" "ybot-head-bang"]]
             ;; unknown as to why we have to do a dispatch-later to get the second model loaded
             [:dispatch-later {:ms 200 :dispatch [::load-model "models/beat_club/" "ybot_head_bang.glb" "ybot-head-bang"]}]
             [:dispatch [::load-intervals]]
             ; [:dispatch [::dummy]]
             [:dispatch [::load-model "models/beat_club/" "ybot_rumba.glb" "ybot-rumba"]]]}))))
             ; [(re-frame/dispatch [::load-model "models/beat_club/" "ybot_rumba.glb" "ybot-rumba"])]]}))))
             ; [(re-frame/dispatch [::load-model "models/beat_club/" "ybot_head_bang.glb" "ybot-head-bang"])]
              ; (beat-club.scene/load-model "models/beat_club/" "ybot_rumba.glb" "ybot-rumba"))]}))))
              ; (beat-club.scene/load-model "models/beat_club/" "ybot_head_bang.glb" "ybot-head-bang"))]}))))
              ; (re-frame/dispatch [::load-intervals]))]}))))
              ; (re-frame/dispatch [::load-model-2 "models/beat_club/" "ybot_head_bang.glb" "ybot-head-bang"]))]}))))
       ; {:fx [(re-frame/dispatch [::load-model-2 "models/beat_club/" "ybot_head_bang.glb" "ybot-head-bang"])]}))))


(reg-event-db
  ::twitch-streaming-active
  (fn [db [_ status]]
    (prn "events.twitch-streaming-ative: status=" status)
    (assoc db :twitch-streaming-active status)))

(reg-event-fx
 ::stop-song-anim
 (fn [{:keys [db]}_]
 ; (fn [db [_]]
   (println "events:beat-club.stop-song-anim: now running")
   ; (beat-club.scene/stop-song-anim db)
   ; {:fx [(re-frame/dispatch [::twitch-streaming-active [false]])]}
   ; (set! beat-club.scene/twitch-streaming-active false)
   (swap! beat-club.scene/*twitch-streaming-active* (fn [x] false))
   ; {:fx []}))
   {:fx [(do
           (re-frame/dispatch [::twitch-streaming-active false])
           (re-frame/dispatch [::stop-song])
           (re-frame/dispatch [::stop-animation]))]}))

(reg-event-fx
 ::firework
 (fn [cofx [_]]
   {:fx [(beat-club.scene/firework)]}))

(reg-event-fx
 ::load-model
 (fn [cofx [_ path file name]]
   {:fx [(beat-club.scene/load-model path file name)]}))

(reg-event-fx
 ::load-model-2
 (fn [cofx [_ path file name]]
   {:fx [(beat-club.scene/load-model-2 path file name)]}))

(reg-event-fx
 ::start-animation
 (fn [cofx [_ path file]]
   {:fx [(beat-club.scene/start-animation)]}))

(reg-event-fx
 ::stop-animation
 (fn [cofx [_ path file]]
   {:fx [[(beat-club.scene/stop-animation)]]}))

(reg-event-db
  ::model-loaded
  (fn [db [_ model-id is-loaded is-playing]]
    (prn "events.update-models: model-id=" model-id)
    (assoc-in db [:models (keyword model-id)]
              {:is-loaded is-loaded
               :is-playing is-playing})))
