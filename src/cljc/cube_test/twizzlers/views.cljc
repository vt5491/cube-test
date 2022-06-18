(ns cube-test.twizzlers.views
  (:require
   [re-frame.core :refer [dispatch subscribe] :as re-frame]
   [cube-test.subs :as subs]
   [cube-test.twizzlers.subs :as twiz.subs]
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
    (let [tc-1 @(subscribe [:twiz-cnt])]
      [:p "twiz-cnt-2: " tc-1])
    (let [gen-twiz-cube @(subscribe [:gen-twiz-cube])])
    [:br]
    ;; [:text-field]
    [:p "twiz-cnt: " @(subscribe [:twiz-cnt])]])
