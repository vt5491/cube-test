(ns cube-test.beat-club.views
  (:require
   [re-frame.core :refer [dispatch subscribe] :as re-frame]
   ; [cube-test.subs :as subs]
   [cube-test.beat-club.subs :as beat-club.subs]
   [cube-test.beat-club.events :as beat-club.events]))


(defn init-panel []
  (prn "beat-club-views.init-panel")
  (let [
        song-loaded           @(subscribe [:song-loaded])
        twitch-load-status    @(subscribe [:twitch-load-status])
        models                @(subscribe [:model-changed])
        model-rumba           @(subscribe [:model-changed-rumba])
        model-head-bang       @(subscribe [:model-changed-head-bang])
        model-head-bang-is-enabled  @(subscribe [:model-changed.head-bang.is-enabled])])
        ; [items  @(subscribe [:items])]])
  [:div
    [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.beat-club.events/play-song-anim])} "play song anim"]
    [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.beat-club.events/load-rock-candy])} "load rock-candy"]
    [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.beat-club.events/pause-song])} "pause song"]
    [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.beat-club.events/stop-song])} "stop song"]
    [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.beat-club.events/play-track])} "play track"]
    [:br]
    [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.beat-club.events/create-drum-twitches])} "create drum twitches"]
    [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.beat-club.events/full-twitch-seq])} "full twitch seq"]
    [:br]
    ; [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.beat-club.events/handler-with-http])} "load intervals"]
    [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.beat-club.events/load-intervals])} "load intervals"]
    [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.beat-club.events/stop-song-anim])} "stop song anim"]
    ; [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.beat-club.events/twitch-streaming-active false])} "stop song anim"]
    [:br]
    [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.beat-club.events/firework])} "firework"]
    [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.beat-club.events/load-model-2 "models/beat_club/" "ybot_head_bang.glb" "ybot-head-bang"])} "load-model-2"]
    [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.beat-club.events/toggle-model-enabled :ybot-rumba])} "toggle enabled"]
    [:br]
    [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.beat-club.events/dummy])} "dummy"]
    [:br]])
