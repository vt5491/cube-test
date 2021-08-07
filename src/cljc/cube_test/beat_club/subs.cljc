(ns cube-test.beat-club.subs
  (:require
   [re-frame.core :as re-frame :refer [reg-sub subscribe]]))

(reg-sub
 :sl
 :<- [:song-loaded]
 ; :<- [:interval-map]
 (fn [song-loaded query-v]
 ; (fn [db query-v]
   ; (prn "subs.song-loaded: db=" db)
   (prn "subs.song-loaded: song-loaded=" song-loaded)
   7))

(re-frame.core/reg-sub  ;; a part of the re-frame API
 :loaded                   ;; usage: (subscribe [:id])
 (fn [db query-v]      ;; `db` is the map out of `app-db`
   (prn "subs.loaded")
   (let [song-status (:song-loaded db)]
     (prn "song-status=" song-status)   ;; trivial extraction - no computationk
     (when song-status
       (re-frame/dispatch [:cube-test.beat-club.events/play-track])
       (re-frame/dispatch [:cube-test.beat-club.events/play-song-anim])))))
