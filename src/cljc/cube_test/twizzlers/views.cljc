(ns cube-test.twizzlers.views
  (:require
   [re-frame.core :refer [dispatch subscribe] :as re-frame]
   [cube-test.subs :as subs]
   [cube-test.twizzlers.subs :as twiz.subs]
   [cube-test.events :as events]
   [cube-test.twizzlers.events :as twiz.events]
   [cube-test.twizzlers.rules :as twiz.rules]))


(defn init-panel []
  (prn "twizzlers.view: init-panel entered")
  [:div
    [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.twizzlers.events/add-twizzler])} "add twizzler"]
    [:button.user-action {:on-click #(re-frame/dispatch [::twiz.events/update-time])} "update time rule"]
    [:button.user-action {:on-click #(re-frame/dispatch [::twiz.events/update-twiz-cnt 3])} "update twiz cnt"]
    ;; [:button.user-action {:on-click #(re-frame/dispatch [::twiz.events/update-dmy-atom])} "update dummy atom"]
    [:button.user-action {:on-click #(twiz.rules/query-twiz-cnt)} "query twiz-cnt"]
    [:br]
    ; [:button.user-action {:on-click #(re-frame/dispatch [::events/switch-app :frig-frog])} "switch to frig-frog"]
    [:button.user-action {:on-click #(events/switch-app :frig-frog)} "hard-switch to frig-frog"]
    [:button.user-action {:on-click #(events/switch-app :top-scene)} "hard-switch to top-scene"]
    [:button.user-action {:on-click #(events/soft-switch-app :top-scene cube-test.twizzlers.scene/cleanup)} "soft-switch to top-scene"]
    (let [tc-1 @(subscribe [:twiz-cnt])]
      [:p "twiz-cnt-2: " tc-1])
    (let [gen-twiz-cube @(subscribe [:gen-twiz-cube])])
    [:br]
    ;; [:text-field]
    [:p "twiz-cnt: " @(subscribe [:twiz-cnt])]])
