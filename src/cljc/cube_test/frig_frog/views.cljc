(ns cube-test.frig-frog.views
  (:require
   [re-frame.core :refer [dispatch subscribe] :as re-frame]
   [cube-test.frig-frog.subs :as frig-frog.subs]
   [cube-test.frig-frog.events :as frig-frog.events]))

(defn init-panel []
  (prn "frig-frog-views.init-panel")
  (let [ dmy 7])
  [:div
   [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/init-board])} "init-board"]])
   ; [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/load-rock-candy])} "load rock-candy"]])
