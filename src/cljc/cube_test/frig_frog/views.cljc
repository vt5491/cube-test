(ns cube-test.frig-frog.views
  (:require
   [re-frame.core :refer [dispatch subscribe] :as rf]
   [cube-test.frig-frog.subs :as frig-frog.subs]
   [cube-test.frig-frog.events :as ff-events]
   [cube-test.frig-frog.board :as ff-board]
   [cube-test.frig-frog.tile :as ff-tile]
   [cube-test.frig-frog.game :as ff-game]))

(defn init-panel []
  (prn "frig-frog-views.init-panel")
  ; (let [ dmy 7])
  (let [
        board                 @(subscribe [:board-changed])
        ; board-0               @(subscribe [:board-changed-0])
        ; frog                  @(subscribe [:frog-changed])
        ; frog-row              @(subscribe [:frog-row-changed])
        frog-row-col          @(subscribe [:frog-row-col-changed])
        ; frog-mode             @(subscribe [:frog-mode-changed])]
        dev-mode              @(subscribe [:dev-mode-changed])
        trains                @(subscribe [:trains-changed])
        quanta-width          @(subscribe [:quanta-width-changed])
        n-rows                @(subscribe [:n-rows-changed])
        n-cols                @(subscribe [:n-cols-changed])]
    [:div
     ; [:br]
     ; [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/init-game-db ff-game/default-game-db])} "reset-db"]
     ; [:br]
     ; [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/init-non-vr-view -10])} "reset non-vr -10"]
     ; [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/init-non-vr-view 10])} "reset non-vr 10"]
     ; [:br]
     ; [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/init-vr-view -10])} "reset vr -10"]
     ; [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/init-vr-view 10])} "reset vr 10"]
     ; [:br]
     ; [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/init-view -10])} "reset float vr -10"]
     ; [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/init-view 10])} "reset float vr 10"]
     ; [:br]
     ; [:button.user-action
     ;  {:on-click #(re-frame/dispatch
     ;               [:cube-test.frig-frog.events/update-tile 0 0
     ;                ff-tile/state-update-fn])}
     ;  "update-tile 0 0"]
     [:br]
     [:button.user-action {:on-click #(rf/dispatch [:cube-test.frig-frog.events/inc-frog-mode])} "inc frog mode"]
     [:button.user-action {:on-click #(rf/dispatch [:cube-test.frig-frog.events/jump-frog 1 0])} "jump frog forward"]
     [:button.user-action {:on-click #(rf/dispatch [:cube-test.frig-frog.events/jump-frog -2 0])} "jump frog back -2"]
     [:button.user-action {:on-click #(rf/dispatch [:cube-test.frig-frog.events/jump-frog 0 1])} "jump frog right"]
     [:br]
     [:button.user-action {:on-click #(rf/dispatch [:cube-test.frig-frog.events/toggle-dev-mode])} "toggle dev-mode"]
     [:br]
     [:button.user-action {:on-click #(rf/dispatch
                                       [::ff-events/init-train
                                        {:id-stem :tr-1 :vx -1 :vy 0 :length 1 :init-row 4 :init-col 7}])}
      "init-train 0"]
     [:button.user-action {:on-click #(rf/dispatch
                                       [::ff-events/init-train
                                        {:id-stem :tr-2 :vx 1 :vy 0 :length 2 :init-row 5 :init-col 0}])}
      "init-train 2"]
     [:br]
     [:button.user-action {:on-click #(rf/dispatch [::ff-events/drop-train-idx 0])} "drop train 0"]
     [:button.user-action {:on-click #(rf/dispatch [::ff-events/drop-train-idx 2])} "drop train 2"]
     [:button.user-action {:on-click #(rf/dispatch [::ff-events/drop-train-id-stem :tr-1])} "drop train id :tr-1"]
     [:br]
     [:button.user-action {:on-click #(rf/dispatch [::ff-events/update-train-idx 1])} "update train 1"]
     [:button.user-action {:on-click #(rf/dispatch [::ff-events/update-train-by-id :tr-1 {:length 3}])} "update train id :tr-1"]
     [:button.user-action {:on-click #(rf/dispatch [::ff-events/update-train-by-id :tr-2 {:length 4}])} "update train id :tr-2"]
     [:br]
     [:button.user-action {:on-click #(rf/dispatch [::ff-events/add-train-mesh :tr-1])} "add train mesh :tr-1"]
     [:button.user-action {:on-click #(rf/dispatch [::ff-events/add-train-mesh :tr-2])} "add train mesh :tr-2"]
     [:br]
     [:button.user-action {:on-click #(rf/dispatch [::ff-events/toggle-animate-trains])} "toggle train anim"]
     [:button.user-action {:on-click #(rf/dispatch [::ff-events/toggle-animate-train "tr-1-0"])} "toggle tr-1-0 anim"]]))
