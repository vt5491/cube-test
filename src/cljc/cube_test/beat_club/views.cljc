(ns cube-test.beat-club.views
  (:require
   [re-frame.core :refer [dispatch subscribe] :as re-frame]
   ; [cube-test.subs :as subs]
   [cube-test.beat-club.subs :as beat-club.subs]
   [cube-test.beat-club.events :as beat-club.events]))


(defn init-panel []
  (prn "beat-club-views.init-panel")
  (let [
        song-loaded         @(subscribe [:song-loaded])
        twitch-load-status  @(subscribe [:twitch-load-status])])
  [:div
     ; [:br] hi from beat-club.views])
    [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.beat-club.events/play-song-anim])} "play song anim"]
    [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.beat-club.events/load-rock-candy])} "load rock-candy"]
    [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.beat-club.events/pause-song])} "pause song"]
    [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.beat-club.events/stop-song])} "stop song"]
    [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.beat-club.events/play-track])} "play track"]
    [:br]
    [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.beat-club.events/create-drum-twitches])} "create drum twitches"]
    [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.beat-club.events/start-twitch-seq])} "full twitch seq"]
    [:br]
    ; [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.beat-club.events/handler-with-http])} "load intervals"]
    [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.beat-club.events/load-intervals])} "load intervals"]
    [:br]
    [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.beat-club.events/dummy])} "dummy"]
    ; (let [song-load-status @(subscribe [:beat-club.subs/sl])])
    ; (let [song-load-status @(subscribe [:cube-test.beat-club.subs/sl])])
    ; (let [song-load-status @(subscribe [::sl])])
    [:br]])
    ; [:button.user-action {:on-click #(re-frame/dispatch [::twiz.events/update-time])} "update time rule"]
    ; [:button.user-action {:on-click #(re-frame/dispatch [::twiz.events/update-dmy-atom])} "update dummy atom"]
    ; (let [tc-1 @(subscribe [:twiz-cnt])])
    ; (let [gen-twiz-cube @(subscribe [:gen-twiz-cube])])
    ; [:br]])
