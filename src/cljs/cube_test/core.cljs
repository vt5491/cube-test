(ns cube-test.core
  (:require
   ;; try calling rules early so re-frame doesnt hook it
   ; [cube-test.twizzlers.rules]
   ;;
   [reagent.core :as reagent]
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [cube-test.events :as events]
   [cube-test.views :as views]
   [cube-test.subs :as subs]
   [cube-test.config :as config]
   [cube-test.game :as game]
   ;; game-level
   ;; Note: if you are going to use global ns (e.g for events)
   ;; like :cube-test.utils.events, you need to make sure at
   ;; least one place in the code requires the ns, and that place
   ;; is here.
   [cube-test.twizzlers.events]
   [cube-test.utils.events]))

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

(defn init []
  (println "core.init: entered")
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root)
  (game/init))
