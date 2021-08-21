(ns cube-test.beat-club.subs
  (:require
   [re-frame.core :as re-frame :refer [reg-sub subscribe]]
   [cube-test.beat-club.events :as events]
   [clojure.data :as clj-data]))

(def ^:dynamic *last-models* (atom nil))
; (reg-sub
;  ; :sl
;  :song-loaded-sub
;  :<- [:song-loaded]
;  ; :<- [:interval-map]
;  (fn [song-loaded query-v]
;  ; (fn [db query-v]
;    ; (prn "subs.song-loaded: db=" db)
;    (prn "subs.song-loaded: song-loaded=" song-loaded)
;    7))

; (re-frame.core/reg-sub)  ;; a part of the re-frame API
;; extractors
(reg-sub
 :get-song-loaded
 (fn [db _]
   (:song-loaded db)))

(reg-sub
 :get-twitch-load-status
 (fn [db _]
   (prn "subs:get-twitch-load-status: extractor driven")
   (:twitch-load-status db)))

(reg-sub
 :models
 (fn [db _]
   (prn "subs:models: extractor driven")
   (:models db)))

;; computations
(reg-sub
 :song-loaded
 :<- [:get-song-loaded]
 (fn [db query-v]
  (prn "subs: song-loaded entered")))
  ; (prn "subs.song-loaded: about to dispatch event")
  ; (if (< (:twitch-load-status db) 10)
  ;   (re-frame/dispatch [:cube-test.beat-club.events/inc-twitch-load-status]))
  ; (prn "subs.song-loaded: after dispatch event")))
  ; (let [current-twitch-load-status (:twitch-load-status db)]
  ;     (prn "sub: song-loaded: current-twitch-load-status=" current-twitch-load-status)
  ;     (assoc db :twitch-load-status (+ current-twitch-load-status 1)))))

(reg-sub
 :twitch-load-status
 :<- [:get-twitch-load-status]
 ; (fn [db query-v])
 (fn [load-status query-v]
   ; (prn "subs: twitch-load-status entered, db=" db)
   (prn "subs: twitch-load-status entered, load-status=" load-status)
   ; (when (= (:twitch-load-status db) 2))
   (when (= load-status 2)
     (re-frame/dispatch [:cube-test.beat-club.events/play-track])
     (re-frame/dispatch [:cube-test.beat-club.events/play-song-anim])
     (re-frame/dispatch [:cube-test.beat-club.events/start-animation :ybot-rumba]))))
     ; (re-frame/dispatch [:events/play-song-anim]))))
   ; (prn "subs: twitch-load-status=" (:twitch-load-status db))))
   ; (let [song-status (:song-loaded db)]
   ;   (prn "song-status=" song-status))))   ;; trivial extraction - no computationk
     ; (when song-status
     ;   (re-frame/dispatch [:cube-test.beat-club.events/play-track])
     ;   (re-frame/dispatch [:cube-test.beat-club.events/play-song-anim])))))

(reg-sub
 :model-changed
 ; :<- [:models :ybot-rumba]
 :<- [:models]
 ; (fn [db query-v])
 (fn [models query-v]
   ; (prn "subs:  diff=" (clj-data/diff {:a 7 :b 8} {:a 7}))
   ; (prn "subs: *last-models* (non-ref)=" *last-models*)
   (prn "subs: *last-models*=" @*last-models*)
   (prn "subs: diff=" (first (clj-data/diff models @*last-models*)))
   (prn "subs: model-changed: models=" models ",query-v=" query-v)
   (let [diff (first (clj-data/diff models @*last-models*))]
     (when (and diff (-> diff empty? not))
       (let [ model-id (first (keys diff))
              property-changed (-> diff model-id keys first)]
         (prn "subs: changed-model-id=" model-id
              ",property-changed=" property-changed
              ",new value=" (-> models model-id property-changed))))
     (swap! *last-models* (fn [x] models)))))
