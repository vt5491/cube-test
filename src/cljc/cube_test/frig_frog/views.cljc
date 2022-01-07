(ns cube-test.frig-frog.views
  (:require
   [re-frame.core :refer [dispatch subscribe] :as re-frame]
   [cube-test.frig-frog.subs :as frig-frog.subs]
   [cube-test.frig-frog.events :as frig-frog.events]
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
        frog-row-col          @(subscribe [:frog-row-col-changed])]
        ; frog-mode             @(subscribe [:frog-mode-changed])]
    [:div
     [:br]
     [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/init-game-db ff-game/default-game-db])} "reset-db"]
     [:br]
     [:button.user-action
      {:on-click #(re-frame/dispatch
                   [:cube-test.frig-frog.events/update-tile 0 0
                    ff-tile/state-update-fn])}
      "update-tile 0 0"]
     [:br]
     [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/inc-frog-mode])} "inc frog mode"]
     [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/jump-frog 1 0])} "jump frog forward"]
     [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/jump-frog -2 0])} "jump frog back -2"]
     [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/jump-frog 0 1])} "jump frog right"]
     [:br]]))
     ; [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/draw-tile 1 0])} "draw-tile"]
     ; [:br]]))
     ; [:button.user-action {:on-click #}]]))
     ; [:button.user-action
     ;  {:on-click #(re-frame/dispatch
     ;               [:cube-test.frig-frog.events/update-tile 1 1
     ;                ff-tile/state-update-fn])}
     ;  "update-tile 1 1"]
     ; [:button.user-action
     ;  {:on-click #(re-frame/dispatch
     ;               [:cube-test.frig-frog.events/update-tile 2 3
     ;                ff-tile/state-update-fn])}
     ;  "update-tile 2 3"]
     ; [:br]
     ; [:button.user-action
     ;  {:on-click #(do (re-frame/dispatch
     ;                   [:cube-test.frig-frog.events/update-tile 0 0
     ;                    ff-tile/state-update-fn])
     ;                  (re-frame/dispatch
     ;                       [:cube-test.frig-frog.events/update-tile 0 1
     ;                        ff-tile/state-update-fn]))}
     ;  "update-2-tile 0-0 0-1"]
     ; [:button.user-action
     ;  {:on-click #(do (re-frame/dispatch
     ;                   [:cube-test.frig-frog.events/update-tile 0 0
     ;                    ff-tile/state-update-fn])
     ;                  (re-frame/dispatch
     ;                       [:cube-test.frig-frog.events/update-tile 0 2
     ;                        ff-tile/state-update-fn]))}
     ;  "update-2-tile 0-0 0-2"]
     ; [:button.user-action
     ;  {:on-click #(do (re-frame/dispatch
     ;                   [:cube-test.frig-frog.events/update-tile 0 0
     ;                    ff-tile/state-update-fn])
     ;                  (re-frame/dispatch
     ;                       [:cube-test.frig-frog.events/update-tile 1 2
     ;                        ff-tile/state-update-fn]))}
     ;  "update-2-tile 0-0 1-2"]
     ; [:button.user-action
     ;  {:on-click #(do (re-frame/dispatch
     ;                   [:cube-test.frig-frog.events/update-tile 0 0
     ;                    ff-tile/state-update-fn])
     ;                  (re-frame/dispatch
     ;                       [:cube-test.frig-frog.events/update-tile 2 2
     ;                        ff-tile/state-update-fn]))}
     ;  "update-2-tile 0-0 2-2"]]))
