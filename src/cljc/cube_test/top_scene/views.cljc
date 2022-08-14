(ns cube-test.top-scene.views
  (:require
   [re-frame.core :refer [dispatch subscribe] :as rf]
   [cube-test.events :as events]
   [cube-test.top-scene.events :as ts-events]
   [cube-test.frig-frog.events :as ff-events]
   [cube-test.utils.choice-carousel.subs :as cc-subs]
   [babylonjs :as bjs]
   ["babylonjs-serializers" :as bjs-serializers]))

; BABYLON.GLTF2Export.GLTFAsync(scene, "fileName").then((gltf) => {})
;   gltf.downloadFiles();
; ;
(defn init-panel []
  (prn "view.top-scene.init-panel entered")
  (let [
        ; ccs        @(subscribe [::cc-subs/choice-carousels-changed])]
        ; ccs    @(subscribe [:cube-test.utils.choice-carousel.subs/choice-carousels-changed])
        ;; Note: how sub getters are globals? (no need to fully qualify the path)
        ; ccs    @(subscribe [:get-choice-carousels])
        ccs    @(subscribe [:choice-carousels-changed])
        _ (prn "now in top-scene.init-panel")]
    [:div
     [:br]
     ; [:button.user-action {:on-click #(rf/dispatch [::events/switch-app :frig-frog ])} "switch to frig-frog"]
     [:button.user-action {:on-click #(events/soft-switch-app :frig-frog )} "switch to frig-frog"]
     [:button.user-action {:on-click #(bjs-serializers/GLTF2Export.GLTFAsync cube-test.main-scene/scene "gl_export.gltf")} "export to gltf"]
     ; [:button.user-action {:on-click #(cube-test.top-scene.top-scene/init-choice-carousel)} "init  carousel"]
     [:button.user-action {:on-click #(rf/dispatch [:cube-test.utils.choice-carousel.events/init-choice-carousel {:id :abc :choices [{:id :def}]}])} "init 2nd carousel"]]))
     ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/init-frog-2 0 5])} "init frog-2"]]))
  ; (let [choices [{:id :ff} {:id :cube-spin} {:id :face-slot}]
  ;       parms {:id :app-cc :choices choices}]
  ;   (rf/dispatch [:cube-test.utils.choice-carousel.events/init-choice-carousel parms])))
