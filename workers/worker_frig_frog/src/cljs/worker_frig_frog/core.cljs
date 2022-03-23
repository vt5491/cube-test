(ns worker-frig-frog.core
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as rf]
   [worker-frig-frog.events :as events]
   [worker-frig-frog.views :as views]
   [worker-frig-frog.config :as config]
   [worker-frig-frog.main :as main]))



(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

; (defn worker? [] (-> js/self .-document cljs.core/undefined?))
(defn worker? [] (-> js/self .-document undefined?))

; (def trains)

(defn ^:dev/after-load mount-root []
  (rf/clear-subscription-cache!)
  ;; vt-comment out
  (when (not (worker?))
    (let [root-el (.getElementById js/document "app")]
      (rdom/unmount-component-at-node root-el)
      (rdom/render [views/main-panel] root-el)))
  (when (worker?)))
    ; (prn "wff: now subbing to :trains-changed")
    ; (rdom/render [views/main-panel] nil)
    ; (views/main-panel)
    ; (let []))
          ; trains @(rf/subscribe [:trains-changed])]
      ; (prn "wff: trains=" trains)))
    ; (rf/subscribe [:trains-changed])))
  ;;vt add
  ; (events/init-events))
  ;;vt end

(defn init []
  (prn "about to call initialize-db 2")
  (rf/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root)
  (main/init))
