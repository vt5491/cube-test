(ns cube-test.lvs.views
  (:require
   [re-frame.core :refer [dispatch subscribe] :as rf]
   [cube-test.events :as events]
   [cube-test.lvs.events :as lvs.events]))


(defn init-panel []
  (prn "lvs.view: init-panel entered")
  [:div
    [:button.user-action {:on-click #(rf/dispatch [::lvs.events/tmp])} "tmp"]
    ;; [:button.user-action {:on-click #(rf/dispatch [::lvs.events/rot-cam])} "rot cam"]
    [:button.user-action {:on-click #(rf/dispatch [::lvs.events/enter-js-debugger])} "enter js-debugger"]])
    ;; [:button.user-action {:on-click #(re-frame/dispatch [::twiz.events/update-time])} "update time rule"]])