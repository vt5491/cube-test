(ns cube-test.twizzlers.views
  (:require
   [re-frame.core :refer [dispatch subscribe] :as re-frame]
   [cube-test.subs :as subs]))

(defn init-panel []
  [:div
    [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.twizzlers.events/add-twizzler])} "add twizzler"]])
