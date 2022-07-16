(ns cube-test.frig-frog.views
  (:require
   [re-frame.core :refer [dispatch subscribe] :as rf]
   [cube-test.frig-frog.subs :as frig-frog.subs]
   [cube-test.frig-frog.events :as ff-events]
   [cube-test.events :as events]
   [cube-test.frig-frog.board :as ff-board]
   [cube-test.frig-frog.tile :as ff-tile]
   [cube-test.frig-frog.game :as ff-game]
   [cube-test.frig-frog.ff-worker :as ff-worker]
   [cube-test.frig-frog.rules :as ff-rules]))

(defn init-panel []
  (let [
        ; board                 @(subscribe [:board-changed])
        btm-board             @(subscribe [:btm-board-changed {:prfx :btm}])
        top-board             @(subscribe [:top-board-changed {:prfx :top}])
        frog-row-col          @(subscribe [:frog-row-col-changed])
        dev-mode              @(subscribe [:dev-mode-changed])
        trains                @(subscribe [:trains-changed])
        quanta-width          @(subscribe [:quanta-width-changed])
        n-rows                @(subscribe [:n-rows-changed])
        n-cols                @(subscribe [:n-cols-changed])]
    [:div
     [:br]
     [:button.user-action {:on-click #(rf/dispatch [::ff-events/reset-rules ])} "reset rules"]
     [:br]
     [:button.user-action {:on-click #(rf/dispatch [::ff-events/ff-worker-restart ])} "restart worker"]
     [:button.user-action {:on-click #(rf/dispatch [::ff-events/ff-worker-start ])} "start worker"]
     [:button.user-action {:on-click #(rf/dispatch [::ff-events/stop-worker-2 ])} "stop worker"]
     [:button.user-action {:on-click #(rf/dispatch [::ff-events/worker-print-db ])} "worker print-db"]
     [:button.user-action {:on-click #(rf/dispatch [::ff-events/post-add-train
                                                    {:id-stem :tr-1 :vx -1 :vy 0 :length 5 :init-row 2 :init-col 4}])} "worker post-add-train"]
     [:button.user-action {:on-click #(rf/dispatch [::ff-events/post-ping ])} "worker post-ping"]
     [:br]
     [:button.user-action {:on-click #(ff-rules/query-all-rules)} "query all rules"]
     [:button.user-action {:on-click #(ff-rules/query-train-id-cnt)} "query train-id-cnt"]
     [:button.user-action {:on-click #(ff-rules/query-player)} "query player"]
     [:br]
     [:br]
     [:button.user-action {:on-click #(rf/dispatch [::ff-events/init-frog-2 0 5])} "init frog-2"]
     [:button.user-action {:on-click #(ff-worker/move-frog-2 0 1)} "move frog-2 forward"]
     [:br]
     [:br]
     [:label "rules: "]
     [:button.user-action {:on-click #(ff-rules/init-frog)} "init frog rule"]
     ; [:button.user-action {:on-click #(ff-rules/init-ball-pos "ball-1" 8 5 -1 0 true)} "init ball"]
     ; [:button.user-action {:on-click #(ff-rules/player-move-to ::ff-rules/player 5 1)} "move player"]
     [:button.user-action {:on-click #(ff-rules/player-move-tile-delta ::ff-rules/player 0 1)} "move player"]
     [:button.user-action {:on-click #(ff-rules/player-move-tile-delta ::ff-rules/btm-player 0 1)} "move btm-player"]
     [:button.user-action {:on-click #(ff-rules/player-move-tile-delta ::ff-rules/top-player 0 1)} "move top-player"]
     [:button.user-action {:on-click #(set! cube-test.frig-frog.player.jumped false )} "set jumped to false"]]))
