(ns cube-test.views
  (:require
   [re-frame.core :refer [dispatch subscribe] :as re-frame]
   [reagent.core :as r]
   ["react-router-dom" :refer (Route Link Routes) :rename {BrowserRouter Router}]
   [cube-test.base :as base]
   [cube-test.subs :as subs]
   [babylonjs :as bjs]
   [cube-test.ut-simp.msg :as msg]
   [cube-test.msg-cube.views :as msg-cube.views]
   [cube-test.twizzlers.views :as twizzlers.views]
   [cube-test.beat-club.views :as beat-club.views]
   [cube-test.frig-frog.views :as frig-frog.views]))

(defn index []
  [:h2 "Home"])

(defn users []
  [:h2 "Users"])

(defn about []
  [:h2 "About"])

;; react-router wants react component classes
(def Index (r/reactify-component index))
(def Users (r/reactify-component users))
(def About (r/reactify-component about))

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
      [:> Route {:path "/" :exact true :component Index}]
      [:> Route {:path "/about/" :component About}]
      [:> Route {:path "/users/" :component Users}]]]])

(defn main-panel []
  ; [:div
  ;   (root)]
  (let [name (re-frame/subscribe [::subs/name])
        ; msgs (re-frame/subscribe [:msgs])
        input-id @(re-frame/subscribe [:input-id])]
    [:div
     [:button.debug-view {:on-click #(re-frame/dispatch [:debug-view])} "debug-view"]
     [:button.print-grid {:on-click #(re-frame/dispatch [:sync-db base/db-worker-thread])} "sync worker db"]
     ; [:button.print-grid {:on-click #(re-frame/dispatch [:pretty-print-grid])} "pprint-grid"]
     ; [:button.print-grid {:on-click #(re-frame/dispatch [:print-vrubik-grid])} "print-grid"]
     [:br]
     (case base/top-level-scene
        :msg-cube (do (msg-cube.views/init-panel))
        :twizzlers (do (twizzlers.views/init-panel))
        :beat-club (do (beat-club.views/init-panel))
        :frig-frog (do (frig-frog.views/init-panel))
        [:br])
     [:br]
     (root)]))
