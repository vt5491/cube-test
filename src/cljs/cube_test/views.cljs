(ns cube-test.views
  (:require
   [re-frame.core :refer [dispatch subscribe] :as re-frame]
   [cube-test.base :as base]
   [cube-test.subs :as subs]
   [babylonjs :as bjs]
   [cube-test.ut-simp.msg :as msg]
   [cube-test.msg-cube.views :as msg-cube.views]))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])
        ; msgs (re-frame/subscribe [:msgs])
        input-id @(re-frame/subscribe [:input-id])]
    [:div
     [:button.debug-view {:on-click #(re-frame/dispatch [:debug-view])} "debug-view"]
     [:br]
     [:button.print-grid {:on-click #(re-frame/dispatch [:pretty-print-grid])} "pprint-grid"]
     [:button.print-grid {:on-click #(re-frame/dispatch [:print-vrubik-grid])} "print-grid"]
     [:br]
     ; [:button.user-action {:on-click #(re-frame/dispatch [:vrubik-user-action])} "user action 1"]
     ; [:button.user-action {:on-click #(re-frame/dispatch [:print-db])} "print-db"]
     ; (condp = base/top-level-scene)
     (case base/top-level-scene
        :msg-cube (do
                       (msg-cube.views/init-panel))
        :twizzlers [:br]
        [:br])]))
