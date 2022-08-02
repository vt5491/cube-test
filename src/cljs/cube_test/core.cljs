;; This is re-frame's hook environment.
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
   [cube-test.utils.events]
   ; [cube-test.top-scene.events]
   ;; workers
   [cljs-workers.core :as main]
   [cljs-workers.worker :as worker]
   [re-frame-worker-fx.core]))

;; worker support start
(def worker-pool)

(defn worker-setup []
  (prn "core.worker-setup: entered")
  (re-frame/reg-event-fx
   :on-worker-fx-success
   (fn [_ [_ result]]
     (prn "worker success" result)))

  (re-frame/reg-event-fx
   :on-worker-fx-error
   (fn [_ [_ result]]
     (.debug js/console "worker error" result)))

  (re-frame/reg-event-fx
   :test-worker-fx
   (fn [coeffects [_ task]]
     (let [worker-pool (-> coeffects :db :worker-pool)
           task-with-pool (assoc task :pool worker-pool)]
       (set! cube-test.core/worker-pool worker-pool)
       {:worker task-with-pool})))

  (re-frame/reg-event-fx
   :db-hook
   (fn [cofx [_ val]]
     {
      :db (:db cofx)}))

  (re-frame/reg-event-fx
   :initialize
   (fn [_ _]
     {:db {:worker-pool (main/create-pool 2 "libs/cljs_workers/worker/worker.js")}
      :dispatch-n [[:test-worker-fx {:handler :mirror, :arguments {:a "Hallo" :b "Welt" :c 10} :on-success [:on-worker-fx-success] :on-error [:on-worker-fx-error]}]
                   [:test-worker-fx {:handler :mirror, :arguments {:a "Hallo" :b "Welt" :c 10 :d (js/ArrayBuffer. 10) :transfer [:d]} :transfer [:d] :on-success [:on-worker-fx-success] :on-error [:on-worker-fx-error]}]]}))

  (re-frame/reg-event-fx
   :initialize-2
   (fn [_ _]
     (let [db {:db {:worker-pool (main/create-pool 2 "libs/cljs_workers/worker/worker.js")}
               :dispatch-n [[:test-worker-fx {:handler :mirror, :arguments {:a "Hello" :b "World" :c 10} :on-success [:on-worker-fx-success] :on-error [:on-worker-fx-error]}]
                            [:test-worker-fx {:handler :mirror, :arguments {:a "Hallo" :b "Welt" :c 10 :d (js/ArrayBuffer. 10) :transfer [:d]} :transfer [:d] :on-success [:on-worker-fx-success] :on-error [:on-worker-fx-error]}]
                            [:test-worker-fx {:handler :count-worker, :arguments {:a "Bye" :b "Moon" :c 10} :on-success [:on-worker-fx-success] :on-error [:on-worker-fx-error]}]]}]
       (set! worker-pool (-> db :db :worker-pool))
       db)))

  (set! worker-pool (re-frame/dispatch-sync [:initialize-2])))

;; worker support end

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))
  ;; worker-support
  ; (worker-setup))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

;; worker support
(defn worker
  []
  (worker/register
   :mirror
   (fn [arguments]
     arguments))

  (worker/register
   :count-worker
   (fn [arguments]
     arguments))

  (worker/bootstrap))

(defn count-worker
  []
  (worker/register
   :count-worker
   (fn [arguments]
     (count arguments)))

  (worker/bootstrap))
;; end worker support

(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  ;; worker
  ; (worker-setup)
  (mount-root)
  (game/init))
