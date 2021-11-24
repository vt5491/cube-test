;; events is refer to many
(ns cube-test.beat-club.events
  (:require
   [re-frame.core :refer [dispatch reg-event-db reg-event-fx reg-fx after ] :as re-frame]
   ; [cube-test.events :as events]
   [cube-test.beat-club.game :as beat-club.game]
   [cube-test.beat-club.scene :as beat-club.scene]
   [cube-test.beat-club.db :as beat-club.db]
   [cube-test.beat-club.twitch-stream :as twitch-stream]
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
   ; cofx
   {
    ; :fx (:fx cofx)
    :db (:db cofx)}))

(re-frame/reg-event-fx
 ::init-game
 (fn [cofx _]
   beat-club.game/init
   {
    :fx [[:dispatch [::init-scene]]]}))

(re-frame/reg-event-fx
 ::run-game
 ; [twizzlers-check-spec-interceptor]
 (fn [cofx _]
   (beat-club.game/run-game)
   ; cofx
   {
    ; :fx (:fx cofx)
    :db (:db cofx)}))

(reg-event-db
 ::init-db
 (fn [db [_ id]]
   (println ":beat-club.init-db: now running")
   (beat-club.db/init-db db)))

(reg-event-fx
 ::play-song-anim
 ; (fn [cofx [_ ])
 (fn [{:keys [db] :as cofx} _]
   (beat-club.scene/play-song-anim db)
   (re-frame/dispatch [::twitch-streaming-active true])
   ; cofx
   {:fx (:fx cofx)
    :db (:db cofx)}))

   ; {:fx [[(do
   ;          (beat-club.scene/play-song-anim db)
   ;          (re-frame/dispatch [::twitch-streaming-active true]))]]})))

(reg-event-fx
 ::load-rock-candy
 (fn [cofx [_]]
   (println "events.load-rock-candy:")
   (beat-club.scene/load-mp3
                       "rock-candy"
                       "sounds/music_tracks/montrose-rock-candy.mp3"
                       beat-club.scene/mp3-loaded)
   ; cofx
   {
    ; :fx (:fx cofx)
    :db (:db cofx)}))
                       ; {:fx [[]]})))

(reg-event-fx
 ::pause-song
 (fn [cofx [_]]
   (println "events.pause-song:")
   (beat-club.scene/pause-song)
   ; cofx
   {
    ; :fx (:fx cofx)
    :db (:db cofx)}))
   ; {:fx [[(beat-club.scene/pause-song)]]}))

(reg-event-fx
 ::stop-song
 (fn [cofx [_]]
   (println "events.stop-song:")
   (beat-club.scene/stop-song)
   ; cofx
   {
    ; :fx (:fx cofx)
    :db (:db cofx)}))
   ; {:fx [[(beat-club.scene/stop-song)]]}))

(reg-event-fx
 ::play-track
 (fn [cofx [_]]
   (println "events.play-track:")
   (beat-club.scene/play-track)
   ; cofx
   {
    ; :fx (:fx cofx)
    :db (:db cofx)}))
   ; {:fx [[(beat-club.scene/play-track)]]}))

(reg-event-fx
 ::create-drum-twitches
 (fn [cofx [_]]
   (println "events.create-drum-twitches:")
   (beat-club.scene/create-drum-twitches)
   ; cofx
   {
    ; :fx (:fx cofx)
    :db (:db cofx)}))
   ; {:fx [[(beat-club.scene/create-drum-twitches)]]}))


(reg-event-fx
 ::dummy
 (fn [cofx [_]]
   (prn "dummy: cofx=" cofx)
   (prn "db@intevals=" (:intervals (:db cofx)))
   ; cofx
   {
    ; :fx (:fx cofx)
    :db (:db cofx)}))

(re-frame/reg-event-db
 ::song-loaded
 (fn [db [_]]
   (prn "events: song-loaded")
   (-> (assoc db :song-loaded true)
       (assoc-in [:song-status :loaded] true))))

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

(reg-event-db
  ::init-song
  (fn [db [_ active-song]]
    (prn "events.set-active-song: active-song= " active-song)
    (-> (assoc-in db [:song-status :name] active-song)
        (assoc-in [:song-status :loaded] false))))

(reg-event-fx
 ::full-twitch-seq
 (fn [cofx [_]]
   (if (-> cofx :db :twitch-load-status (= 2))
     (do
       {:fx [ [:dispatch [::play-track]]
              [:dispatch [::play-song-anim]]
              ; [:dispatch [::start-animation :ybot-rumba 1.6]]
              ; [:dispatch [::start-animation :ybot-head-bang 1.2222]]
              [:dispatch [::start-animation :ybot-combo 1.6 0 2.4]]]})
              ; [:dispatch [::start-animation :dynamic 1.6]]]})
     ;; else
     (do
       (beat-club.scene/load-mp3 "rock-candy" "https://localhost:8281/sounds/music_tracks/montrose-rock-candy.mp3" beat-club.scene/mp3-loaded)
       (beat-club.scene/create-drum-twitches)
       {:db (:db cofx)
        :fx [
             ; [(beat-club.scene/load-mp3 "rock-candy" "https://localhost:8281/sounds/music_tracks/montrose-rock-candy.mp3" beat-club.scene/mp3-loaded)]
             ; [(beat-club.scene/create-drum-twitches)]
             [:dispatch [::init-song "rock-candy"]]
             ;; unknown as to why we have to do a dispatch-later to get the second model loaded
             ; [:dispatch-later {:ms 200
             ;                   :dispatch [::load-model
             ;                              "models/beat_club/"
             ;                              "ybot_head_bang.glb"
             ;                              "ybot-head-bang"
             ;                              false
             ;                              false
             ;                              {:anim-fps 24 :anim-cycle 44}]}]
             [:dispatch [::load-intervals]]
             [:dispatch [::twitch-post-process "rock-candy"]]
             [:dispatch
              [::load-model
               "models/beat_club/"
               "ybot_combo.glb"
               "ybot-combo"
               true
               false
               {:anim-fps 30 :anim-cycle 72 :anim-factor 1.6}]]]}))))

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
   ; {:fx [[(do
   ;          (re-frame/dispatch [::twitch-streaming-active false])
   ;          (re-frame/dispatch [::stop-song])
   ;          ; (re-frame/dispatch [::stop-animation "ybot-rumba"])
   ;          ; (re-frame/dispatch [::stop-animation "ybot-head-bang"])
   ;          (re-frame/dispatch [::stop-animation "ybot-combo"]))]]
   {:fx [[:dispatch [::twitch-streaming-active false]]
         [:dispatch [::stop-song]]
         [:dispatch [::reset-anim]]
         [:dispatch [::stop-animation "ybot-combo"]]]}))

(reg-event-fx
 ::toggle-dancer
 (fn [cofx [_]]
   (beat-club.scene/toggle-dancer)
   {
    :db (:db cofx)}))

(reg-event-fx
 ::firework
 (fn [cofx [_]]
   (beat-club.scene/firework)
   ; cofx
   {
    ; :fx (:fx cofx)
    :db (:db cofx)}))
   ; {:fx [[(beat-club.scene/firework)]]}))

(reg-event-fx
 ::load-model
 (fn [cofx [_ path file name is-enabled is-playing props]]
   (beat-club.scene/load-model path file name is-enabled is-playing props)
   ; cofx
   {
    ; :fx (:fx cofx)
    :db (:db cofx)}))
   ; {:fx [[(beat-club.scene/load-model path file name is-enabled is-playing props)]]}))

(reg-event-fx
 ::load-model-2
 (fn [cofx [_ path file name]]
   (beat-club.scene/load-model-3 path file name)
   ; {:fx [[(beat-club.scene/load-model-3 path file name)]]}
   ; cofx
   {
    ; :fx (:fx cofx)
    :db (:db cofx)}))
   ; {:fx [[]]}))

(reg-event-fx
 ::load-model-4
 (fn [cofx [_ path file name]]
   (beat-club.scene/load-model-4 path file name)
   cofx))
   ; {
   ;  ; :fx (:fx cofx)
   ;  :db (:db cofx)}))

(reg-event-fx
 ::load-model-fbx
 (fn [cofx [_ path file name]]
   (beat-club.scene/load-model-fbx path file name)
   ; {:fx [[(beat-club.scene/load-model-3 path file name)]]}
   ; cofx
   {
    ; :fx (:fx cofx)
    :db (:db cofx)}))
   ; {:fx [[]]}))

;;ToDo kick off a sub when intervals *and* model is loaded
(reg-event-fx
 ::init-animation-speed
 (fn [{:keys [db]} [_ model-id]]
 ; (fn [cofx [_ model-id]]
   ; (prn "events.init-animation-speed db=" db ", model-id=" model-id)
   ; (cube-test.beat-club.twitch-stream/beat-sync-factor 72 30 80 :double-note)))
   (let [model-kw (keyword model-id)
         models (:models db)
         model (-> db :models model-kw)
         ; model (-> db :models :ybot-head-bang)
         anim-cycle (:anim-cycle model)
         anim-fps (:anim-fps model)
         bpm (-> db :intervals :bpm)
         tmp (prn "events: models=" models",model-kw=" model-kw ",model=" model ",anim-cycle=" anim-cycle ",anim-fps=" anim-fps ",bpm=" bpm)
         anim-factor (cube-test.beat-club.twitch-stream/beat-sync-factor anim-cycle anim-fps bpm :double-note)]
     (prn "events.init-animation-speed factor=" anim-factor)
     ; (beat-club.scene/init-animation-speed model-kw anim-factor)
     (beat-club.scene/init-animation-speed model-kw anim-factor)
     {
      ; :fx [[(beat-club.scene/init-animation-speed model-kw anim-factor)]]
      :db (assoc-in db [:models model-kw :anim-factor] anim-factor)})))

(reg-event-fx
 ::start-animation
 (fn [{:keys [db] :as cofx} [_ model-id speed-ratio from to]]
   (let [model-kw (keyword model-id)]
     ; (beat-club.scene/start-animation model-id (-> db :models model-kw :anim-factor))
     (beat-club.scene/start-animation model-id speed-ratio from to)
     {
        :db (:db cofx)})))

(reg-event-fx
 ::stop-animation
 (fn [cofx [_ name]]
   (prn "events: stop-animation: name=" name)
   (beat-club.scene/stop-animation name)
   ; cofx
   {
    ; :fx (:fx cofx)
    :db (:db cofx)}))
   ; {:fx [[(beat-club.scene/stop-animation name)]]}))

; (reg-event-db)
(reg-event-fx
  ::model-loaded
  ; (fn [db [_ model-id is-enabled is-playing props]])
  (fn [{:keys [db]} [_ model-id is-enabled is-playing props]]
    ; (prn "events.model-loaded: props=" props)
    (let [model-kw (keyword model-id)
          anim-factor (-> props :anim-factor)
          tmp-db
          (assoc-in db [:models (keyword model-id)]
                    {:is-loaded true
                     :is-enabled is-enabled
                     :is-playing is-playing
                     :model-id model-id})]
      ;; mix in all the additinal properties as well
      ; (prn "events.model-loaded: tmp-db=" tmp-db)
      {:fx [[:dispatch [::init-animation-speed model-id anim-factor]]]
       :db (reduce #(do
                      (let [k (first %2)
                            v (second %2)]
                        ; (prn "k=" k ",v=" v)
                        (assoc-in %1 [:models (keyword model-id) k] v)))
                   tmp-db
                   props)})))



; (reg-event-db
;   ::toggle-model-enabled
;   (fn [db [_ model-id]]
;     (prn "events.toggle-model-enabled: entered")
;     (let [model-kw (keyword model-id)
;           current-visibility (-> db :models model-kw :is-enabled)]
;       (assoc-in db [:models model-kw :is-enabled] (not current-visibility)))))

;; best to just call bjs directly and not incur re-frame overhead
;; in this case.
(reg-event-fx
  ::toggle-model-enabled
  (fn [{:keys [db]} [_ model-id]]
    (prn "events.toggle-model-enabled: entered")
    (let [model-kw (keyword model-id)
          current-visibility (-> db :models model-kw :is-enabled)]
      (utils/toggle-enabled model-id)
      {
       ; :fx [[(utils/toggle-enabled model-id)]]
       :db (assoc-in db [:models model-kw :is-enabled] (not current-visibility))})))

; (reg-event-db
;   ::init-song
;   (fn [db [_ active-song]]
;     (prn "events.set-active-song: active-song= " active-song)
;     (-> (assoc-in db [:song-status :name] active-song)
;         (assoc-in [:song-status :loaded] false))))

(reg-event-db
  ::twitch-post-process
  (fn [db [_ active-song]]
    (prn "events.twitch-post-process: active-song= " active-song)
    ; (-> (assoc-in db [:song-status :name] active-song)
    ;     (assoc-in [:song-status :loaded] false))))
    (case active-song
      "rock-candy"
        (do
          (assoc-in db [:control-intervals :toggle-model]
                    [])))))
                     ; {:obj  :ybot-rumba :intervals [2000 2000 2000 2000 2000 2000 2000 2000]}
                     ; {:obj  :ybot-head-bang :intervals [2000 2000 2000 2000 2000 2000 2000 2000]}])))))

; (reg-event-fx
;  ::reset-anim-loop-count
;  (fn [cofx [_]]))

(reg-event-fx
 ::reset-anim
 (fn [cofx [_]]
   (println "events.reset-anim:")
   (twitch-stream/reset-anim-loop-count)
   (twitch-stream/reset-anim-index)
   ; {:fx [[]]}))
   ; cofx
   {
    ; :fx (:fx cofx)
    :db (:db cofx)}))
