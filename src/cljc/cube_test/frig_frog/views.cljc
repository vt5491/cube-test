(ns cube-test.frig-frog.views
  (:require
   [re-frame.core :refer [dispatch subscribe] :as re-frame]
   [cube-test.frig-frog.subs :as frig-frog.subs]
   [cube-test.frig-frog.events :as frig-frog.events]
   [cube-test.frig-frog.board :as ff-bd]
   [cube-test.frig-frog.game :as ff-game]))

(defn init-panel []
  (prn "frig-frog-views.init-panel")
  ; (let [ dmy 7])
  (let [
        board                 @(subscribe [:board-changed])])
  [:div
   [:br]
   [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/init-game-db ff-game/default-game-db])} "reset-db"]
   [:br]
   [:br]
   [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/draw-tile 0 0])} "draw-tile"]])
   ; [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/init-board])} "init-board"]
   ; [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/init-board-2])} "init-board-2"]
   ; [:br]
   ; [:br]
   ; [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/init-row 0 1])} "init-row 0-1"]
   ; [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/add-dummy-tile])} "add-dummy-tile"]
   ; [:br]
   ; [:br]
   ; [:button.user-action {:on-click #(ff-bd/create-row 0 2)} "create-row 0"]
   ; [:button.user-action {:on-click #(ff-bd/create-row 1 4)} "create-row 1"]])
