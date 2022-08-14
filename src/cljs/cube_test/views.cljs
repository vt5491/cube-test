(ns cube-test.views
  (:require
   [re-frame.core :refer [dispatch subscribe] :as re-frame]
   [reagent.core :as r]
   ["react-router-dom" :refer (Route Link Routes) :rename {BrowserRouter Router}]
   [cube-test.base :as base]
   [cube-test.utils :as utils]
   [cube-test.main-scene :as main-scene]
   [cube-test.subs :as subs]
   [babylonjs :as bjs]
   ; ["babylonjs-serializers" :as bjs-serializers]
   [cube-test.ut-simp.msg :as msg]
   [cube-test.msg-cube.views :as msg-cube.views]
   [cube-test.twizzlers.views :as twizzlers.views]
   [cube-test.beat-club.views :as beat-club.views]
   [cube-test.frig-frog.views :as frig-frog.views]
   [cube-test.top-scene.views :as top-scene.views]
   [cube-test.game :as game]))

(defn index []
  [:h2 "Home"])

(defn users []
  [:h2 "Users"])

(defn do-it []
  (rand-int 9))

(defn about []
  (let [
        ; _ (prn "now in about")
        r (do-it)]
        ; r2 (game/init :frig-frog)]
    ; [:h2 "About"]
    [:h2 (do
           ; (println "hi from view")
           (+ r 1))]))

;; react-router wants react component classes
(def Index (r/reactify-component index))
(def Users (r/reactify-component users))
; (def About (r/reactify-component about))
(def About (r/as-element (about)))

(defn root []
  [:> Router
    [:div
     [:nav
      [:ul
       [:li
        [:> Link {:to "/"} "Home"]]
       [:li
        [:> Link {:to "/about/"} "About"]]
       [:li
        [:> Link {:to "/users/"} "Users"]]]]
     [:> Routes
      [:> Route {:path "/about/" :element (r/as-element (about))}]]]])
      ; [:> Route {:path "/users/" :element Users}]]]])

(defn main-panel
  ([] (main-panel base/top-level-scene))
  ([top-level-scene]
   (prn "views.main-panel: top-level-scene=" top-level-scene)
   (let [name (re-frame/subscribe [::subs/name])
         ; msgs (re-frame/subscribe [:msgs])
         input-id @(re-frame/subscribe [:input-id])]
     [:div
      [:button.debug-view {:on-click #(re-frame/dispatch [:debug-view])} "debug-view"]
      [:button.print-grid {:on-click #(re-frame/dispatch [:sync-db base/db-worker-thread])} "sync worker db"]
      ; [:button.print-grid {:on-click #(prn "meshes=" (.-meshes main-scene/scene))} "print meshes"]
      [:button.print-grid {:on-click #(utils/pretty-print-meshes main-scene/scene)} "print meshes"]
      ; [:button.print-grid {:on-click #(re-frame/dispatch [:pretty-print-grid])} "pprint-grid"]
      ; [:button.print-grid {:on-click #(re-frame/dispatch [:print-vrubik-grid])} "print-grid"]
      [:br]
      (case top-level-scene
            :msg-cube (do (msg-cube.views/init-panel))
            :twizzlers (do (twizzlers.views/init-panel))
            :beat-club (do (beat-club.views/init-panel))
            :frig-frog (do (frig-frog.views/init-panel))
            :top-scene (do
                         (top-scene.views/init-panel))
                         ; (cube-test.top-scene.top-scene/init-choice-carousel))
            [:br])
      [:br]])))
         ; (root)]))
