(ns worker-frig-frog.views
  (:require
   [re-frame.core :refer [dispatch subscribe] :as rf]
   [worker-frig-frog.subs :as subs]
   [worker-frig-frog.events :as events]))


(defn main-panel []
  (prn "ffw-main-panel: entered")
  (let [;name (rf/subscribe [::subs/name])
        trains       @(subscribe [:trains-changed])]
    [:div
     ; [:h1
     ;  "Hello from " @name]
     [:br]
     [:button.user-action {:on-click #(rf/dispatch [::events/dummy])} "dummy"]
     [:button.user-action {:on-click #(rf/dispatch [::events/add-def 8])} "add :def"]
     [:br]
     [:button.user-action {:on-click #(rf/dispatch [::events/add-train 2 3])} "add train"]]))
