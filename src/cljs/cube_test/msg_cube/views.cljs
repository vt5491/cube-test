(ns cube-test.msg-cube.views
  (:require
   [re-frame.core :refer [dispatch subscribe] :as re-frame]
   [cube-test.subs :as subs]))

(defn init-panel []
   (let [name (re-frame/subscribe [::subs/name])
         input-id @(re-frame/subscribe [:input-id])]
     (prn "msg-cube.init-panel: entered")
     [:div
      [:button.user-action {:on-click #(re-frame/dispatch [:msg-cube.inc-max-id])} "inc-max-id"]
      [:button.user-action {:on-click #(re-frame/dispatch [:msg-cube.add-msg {:text "hi2" :level :INFO}])} "user action 2"]
      [:button.user-action {:on-click #(re-frame/dispatch [:msg-cube.add-msg-2 {:text "hi2" :level :INFO}])} "user action 2a"]
      [:button.user-action {:on-click #(re-frame/dispatch [:msg-cube.inc-level input-id])} "inc-level msgs@cube<input-id>"]
      [:button.user-action {:on-click #(re-frame/dispatch [:msg-cube.inc-level-2 3])} "inc-level msgs2@cube-3"]
      [:text-field "abc"]
      [:br]
      [:text-field "msg-count=" @(re-frame/subscribe [:msgs-cnt])]
      [:br]
      [:button.user-action {:on-click #(re-frame/dispatch [:msg-cube.add-ints])} "add ints"]
      ;; Note: you actually have to extract the value with "@" to get a result
      ;; this is needed to actually see a cube
      (let [dmya @(re-frame/subscribe [:add-scene-msg-cube])])
      (let [dmyc @(re-frame/subscribe [:scene-msgs-cnt])])
      [:br]
      (let [mc-4 @(subscribe [:msg-changed-by-id 2])])
      [:br]
      (let [mc2-1 @(subscribe [:msg-changed-by-id-2 3])]
        (println "view: mc2-1=" mc2-1)
        [:text-field (str "mc2-1=" mc2-1)]
        [:div#msg-box-proxies])
      (let [msgs @(subscribe [:msgs])]
        [:ul#msgs-list
         (for [m msgs]
              (do
                ^{:key (:id m)} m @(subscribe [:msg-changed-by-id (:id m)])))])
      [:br]
      [:text-field "msg-box-proxies"] [:div {:class "col-lg-4"}]
      [:label "inc-id" "Inc id:"]
      [:br]
      [:form {:on-submit (fn [e]
                           (.preventDefault e))}
       [:input {:type :text :name "input-id"
                :default-value @(subscribe [:input-id])
                :on-change (fn [e]
                             (let [val (-> e (.-target) (.-value) (js/parseInt))])
                             (println "val=" val)
                             (when (int? val))
                             (println "now dispatching")
                             (re-frame/dispatch [:msg-cube.update-input-id val]))}]]
      [:br]
      (let [ints @(re-frame/subscribe [:ints])]
        [:ul#ints-list
         (for [i ints]
           (do
             ^{:key (:id (str "int-" i))} i))])]))
