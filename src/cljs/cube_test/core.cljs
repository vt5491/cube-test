(ns cube-test.core
  (:require
   [reagent.core :as reagent]
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [cube-test.events :as events]
   [cube-test.views :as views]
   [cube-test.subs :as subs]
   [cube-test.config :as config]
   [cube-test.game :as game]
   ;; game-level
   [cube-test.twizzlers.events]))

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
