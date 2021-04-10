(ns cube-test.twizzlers.views
  (:require
   [re-frame.core :refer [dispatch subscribe] :as re-frame]
   [cube-test.subs :as subs]
   [cube-test.twizzlers.subs :as twiz.subs]
   [cube-test.twizzlers.events :as twiz.events]))


(defn init-panel []
  [:div
    [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.twizzlers.events/add-twizzler])} "add twizzler"]
    [:button.user-action {:on-click #(re-frame/dispatch [::twiz.events/update-time])} "update time rule"]
    [:button.user-action {:on-click #(re-frame/dispatch [::twiz.events/update-dmy-atom])} "update dummy atom"]
    (let [tc-1 @(subscribe [:twiz-cnt])])
    (let [gen-twiz-cube @(subscribe [:gen-twiz-cube])])
    [:br]])
