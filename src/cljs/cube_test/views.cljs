(ns cube-test.views
  (:require
   [re-frame.core :as re-frame]
   [cube-test.subs :as subs]
   [babylonjs :as bjs]
   [cube-test.ut-simp.msg :as msg]))

; <div style="z-index: 11; position: absolute; right: 20px; bottom: 50px;">
;<button class="babylonVRicon" title="immersive-vr - local-floor"></button></div>
     ; [:div :style {:z-index 12 :position "absolute" :right "20px" :bottom "50px"}]

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])
        msgs (re-frame/subscribe [:msgs])]
    [:div
     [:button.debug-view {:on-click #(re-frame/dispatch [:debug-view])} "debug-view"]
     [:br]
     [:button.print-grid {:on-click #(re-frame/dispatch [:pretty-print-grid])} "pprint-grid"]
     [:button.print-grid {:on-click #(re-frame/dispatch [:print-vrubik-grid])} "print-grid"]
     [:br]
     ; [:button.user-action {:on-click #(re-frame/dispatch [:vrubik-user-action])} "user action 1"]
     ; [:button.user-action {:on-click #(re-frame/dispatch [:print-db])} "print-db"]
     [:button.user-action {:on-click #(re-frame/dispatch [:msg-cube.inc-max-id])} "inc-max-id"]
     ; [:button.user-action {:on-click #(re-frame/dispatch [:simp-ut-action-2])} "user action 2"]
     [:button.user-action {:on-click #(re-frame/dispatch [:msg-cube.add-msg {:text "hi2" :level :INFO}])} "user action 2"]
    ; [:div
     [:text-field "abc"]
     [:br]
     [:text-field "msg-count=" @(re-frame/subscribe [:msgs-cnt])]
     [:br]
     (let [msgs @(re-frame/subscribe [:msgs])]
     ; (let [msgs @(re-frame/subscribe [:msgs-change])]
       [:text-field "abc"]
       [:ul#todo-list
          (for [m msgs]
            (do
              (println "doit")
              ^{:key (:id m)} m))])
     ;; Note: you actually have to extract the value with "@" to get a result
     (let [dmy @(re-frame/subscribe [:gen-msg-cube])])]))
     ; (let [dmy (re-frame/subscribe [:gen-msg-cube])]
       ; [:text-field "gen-msg-cube-dummy-field"])]))
     ; [:text-field "gen-msg-cube-dummy-field" @(re-frame/subscribe [:gen-msg-cube])]]))
     ; [:br "gen-msg-cube-dummy-field" @(re-frame/subscribe [:gen-msg-cube])]]))
     ; [:button.user-action {:on-click #(re-frame/dispatch [:add-msg-box
     ;                                                      {::msg/box-id 3
     ;                                                       ::msg/msg {::msg/text "ghi" ::msg/msg-level :INFO}}
     ;                                                      "user action 2"])}]

     ; note: now define in index.html since defining it here will put it under the "app" div
     ;; and this can interfere with bjs refreshibility.
     ; [:canvas
     ;  {:touchaction "none" :id "renderCanvas"
     ;   :style {:width 1024 :height 768 :border 5 :outline "black 3px solid"}}]
    ; [:div {:id "vt-div"}]))
         ;  [@(re-frame/subscribe [:msgs])]]]))

          ; [:button.babylonVRicon {:on-click #(re-frame/dispatch [:enter-vr])}]
          ; (.-element (bjs/WebXREnterExitUIButton.))]])
  ; (-> js/document (.getElementById "app") (.appendChild (.-element (bjs/WebXREnterExitUIButton.))))
  ; (let [el (-> js/document (.getElementById "app"))]
            ; node (get el 0)]
    ; (js-debugger)
    ; (.appendChild el (.-element (bjs/WebXREnterExitUIButton.)))))
    ; (re-frame/dispatch [:setup-btn])))
  ; (re-frame/dispatch [:add-msg-box-2
  ;                     {::msg-box/id 2 ::msg-box/msg {::msg-box/text "def" ::msg-box/msg-level :INFO}}]))
