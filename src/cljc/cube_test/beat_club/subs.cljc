(ns cube-test.beat-club.subs
  (:require
   [re-frame.core :as re-frame :refer [reg-sub subscribe]]))

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
(reg-sub
 :song-loaded
 ; :<- [:song-loaded]
 (fn [db query-v]
  (prn "subs: song-loaded entered, db=" db)))
  ; (prn "subs.song-loaded: about to dispatch event")
  ; (if (< (:twitch-load-status db) 10)
  ;   (re-frame/dispatch [:cube-test.beat-club.events/inc-twitch-load-status]))
  ; (prn "subs.song-loaded: after dispatch event")))
  ; (let [current-twitch-load-status (:twitch-load-status db)]
  ;     (prn "sub: song-loaded: current-twitch-load-status=" current-twitch-load-status)
  ;     (assoc db :twitch-load-status (+ current-twitch-load-status 1)))))

(reg-sub
 :twitch-load-status
 ; :<- [:twitch-load-status]
 (fn [db query-v]
   (prn "subs: twitch-load-status entered")
   (when (= (:twitch-load-status db) 2)
     (re-frame/dispatch [:cube-test.beat-club.events/play-track])
     (re-frame/dispatch [:cube-test.beat-club.events/play-song-anim]))))
   ; (prn "subs: twitch-load-status=" (:twitch-load-status db))))
   ; (let [song-status (:song-loaded db)]
   ;   (prn "song-status=" song-status))))   ;; trivial extraction - no computationk
     ; (when song-status
     ;   (re-frame/dispatch [:cube-test.beat-club.events/play-track])
     ;   (re-frame/dispatch [:cube-test.beat-club.events/play-song-anim])))))
