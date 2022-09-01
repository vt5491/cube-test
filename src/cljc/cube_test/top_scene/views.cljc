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
     [:button.user-action {:on-click #(events/soft-switch-app :frig-frog )} "soft switch to frig-frog"]
     [:button.user-action {:on-click #(events/switch-app :frig-frog )} "hard switch to frig-frog"]
     [:button.user-action {:on-click #(bjs-serializers/GLTF2Export.GLTFAsync cube-test.main-scene/scene "gl_export.gltf")} "export to gltf"]
     ; [:button.user-action {:on-click #(cube-test.top-scene.top-scene/init-choice-carousel)} "init  carousel"]
     [:button.user-action {:on-click #(rf/dispatch [:cube-test.utils.choice-carousel.events/init-choice-carousel {:id :abc :choices [{:id :def}]}])} "init 2nd carousel"]
     [:br]
     [:button.user-action {:on-click #(cube-test.top-scene.top-scene/tmp-rot :x)} "rot-x2"]
     [:button.user-action {:on-click #(cube-test.top-scene.top-scene/tmp-rot :y)} "rot-y"]
     [:button.user-action {:on-click #(cube-test.top-scene.top-scene/tmp-rot :z)} "rot-z"]
     [:br]
     [:button.user-action {:on-click #(.removeAllFromScene cube-test.top-scene.top-scene.face-slot-assets)} "remove face-slot assets"]
     [:button.user-action {:on-click #(.addAllToScene cube-test.top-scene.top-scene.face-slot-assets)} "add face-slot assets"]
     [:button.user-action {:on-click #(.removeAllFromScene cube-test.top-scene.top-scene.geb-cube-assets)} "remove geb-cube assets"]
     [:button.user-action {:on-click #(.addAllToScene cube-test.top-scene.top-scene.geb-cube-assets)} "add geb-cube assets"]
     [:br]
     [:button.user-action {:on-click #(rf/dispatch [:cube-test.top-scene.events/remove-asset-containers])} "remove asset-containers"]
     [:button.user-action {:on-click #(rf/dispatch [:cube-test.top-scene.events/add-asset-containers])} "add asset-containers"]
     [:br]
     [:button.user-action {:on-click #(rf/dispatch [::ts-events/tmp 7])} "tmp"]]))
     ; [:button.user-action {:on-click #(rf/dispatch [:cube-test.top-scene.events/tmp 7])} "tmp"]]))
     ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/init-frog-2 0 5])} "init frog-2"]]))
  ; (let [choices [{:id :ff} {:id :cube-spin} {:id :face-slot}]
  ;       parms {:id :app-cc :choices choices}]
  ;   (rf/dispatch [:cube-test.utils.choice-carousel.events/init-choice-carousel parms])))
