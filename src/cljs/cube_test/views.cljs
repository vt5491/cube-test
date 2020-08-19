(ns cube-test.views
  (:require
   [re-frame.core :as re-frame]
   [cube-test.subs :as subs]
   [babylonjs :as bjs]))

; <div style="z-index: 11; position: absolute; right: 20px; bottom: 50px;">
;<button class="babylonVRicon" title="immersive-vr - local-floor"></button></div>
     ; [:div :style {:z-index 12 :position "absolute" :right "20px" :bottom "50px"}]

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:button.debug-view {:on-click #(re-frame/dispatch [:debug-view])} "debug-view"]
     [:button.print-grid {:on-click #(re-frame/dispatch [:print-grid])} "print-grid"]
     [:canvas
      {:touchaction "none" :id "renderCanvas"
       :style {:width 1024 :height 768 :border 5 :outline "black 3px solid"}}]
     [:div {:id "vt-div"}]]))
      ; [:button.babylonVRicon {:on-click #(re-frame/dispatch [:enter-vr])}]
      ; (.-element (bjs/WebXREnterExitUIButton.))]])
  ; (-> js/document (.getElementById "app") (.appendChild (.-element (bjs/WebXREnterExitUIButton.))))
  ; (let [el (-> js/document (.getElementById "app"))]
        ; node (get el 0)]
    ; (js-debugger)
    ; (.appendChild el (.-element (bjs/WebXREnterExitUIButton.)))))
    ; (re-frame/dispatch [:setup-btn])))
