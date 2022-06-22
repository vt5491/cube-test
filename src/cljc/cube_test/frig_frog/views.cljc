(ns cube-test.frig-frog.views
  (:require
   [re-frame.core :refer [dispatch subscribe] :as rf]
   [cube-test.frig-frog.subs :as frig-frog.subs]
   [cube-test.frig-frog.events :as ff-events]
   [cube-test.events :as events]
   [cube-test.frig-frog.board :as ff-board]
   [cube-test.frig-frog.tile :as ff-tile]
   [cube-test.frig-frog.game :as ff-game]
   [cube-test.frig-frog.ff-worker :as ff-worker]
   [cube-test.frig-frog.rules :as ff-rules]))

(defn init-panel []
  (prn "frig-frog-views.init-panel")
  ; (let [ dmy 7])
  (let [
        board                 @(subscribe [:board-changed])
        ; board-0               @(subscribe [:board-changed-0])
        ; frog                  @(subscribe [:frog-changed])
        ; frog-row              @(subscribe [:frog-row-changed])
        frog-row-col          @(subscribe [:frog-row-col-changed])
        ; frog-mode             @(subscribe [:frog-mode-changed])]
        dev-mode              @(subscribe [:dev-mode-changed])
        trains                @(subscribe [:trains-changed])
        quanta-width          @(subscribe [:quanta-width-changed])
        n-rows                @(subscribe [:n-rows-changed])
        n-cols                @(subscribe [:n-cols-changed])]
    [:div
     [:br]
     ; [:button.user-action {:on-click #(rf/dispatch [:cube-test.events/print-db ])} "print main db"]
     ; [:button.user-action {:on-click #(rf/dispatch [::events/print-hi ])} "print main db"]
     ; [:button.user-action {:on-click #(rf/dispatch [:print-db ])} "print main db"]
     ; [:br]
     [:button.user-action {:on-click #(rf/dispatch [::ff-events/ff-worker-restart ])} "restart worker"]
     [:button.user-action {:on-click #(rf/dispatch [::ff-events/ff-worker-start ])} "start worker"]
     ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/stop-worker-2 ])} "stop worker 2"]
     [:button.user-action {:on-click #(rf/dispatch [::ff-events/stop-worker-2 ])} "stop worker"]
     ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/post-hi-2 ])} "post hi 2"]
     [:button.user-action {:on-click #(rf/dispatch [::ff-events/worker-print-db ])} "worker print-db"]
     [:button.user-action {:on-click #(rf/dispatch [::ff-events/post-add-train
                                                    {:id-stem :tr-1 :vx -1 :vy 0 :length 5 :init-row 2 :init-col 4}])} "worker post-add-train"]
     ; [:button.user-action {:on-click cube-test.frig-frog.demo-workers-setup-cljs/post-add-train} "worker post-add-train min"]
     ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/post-add-train ])} "worker post-add-train min"]
     [:button.user-action {:on-click #(rf/dispatch [::ff-events/post-ping ])} "worker post-ping"]
     [:br]
     [:button.user-action {:on-click #(ff-rules/query-all-rules)} "query all rules"]
     [:button.user-action {:on-click #(ff-rules/query-train-id-cnt)} "query train-id-cnt"]
     [:button.user-action {:on-click #(ff-rules/query-frog)} "query frog"]
     [:br]
     [:br]
     [:button.user-action {:on-click #(rf/dispatch [::ff-events/init-frog-2 0 5])} "init frog-2"]
     ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/move-frog-2 0 2])} "move frog-2 forward"
     [:button.user-action {:on-click #(ff-worker/move-frog-2 0 1)} "move frog-2 forward"]
     [:br]
     [:br]
     [:button.user-action {:on-click #(ff-rules/init-frog)} "init frog rule"]]))
        ; [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/init-game-db ff-game/default-game-db])} "reset-db"]
        ; [:br]
        ; [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/init-non-vr-view -10])} "reset non-vr -10"]
        ; [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/init-non-vr-view 10])} "reset non-vr 10"]
        ; [:br]
        ; [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/init-vr-view -10])} "reset vr -10"]
        ; [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/init-vr-view 10])} "reset vr 10"]
        ; [:br]
        ; [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/init-view -10])} "reset float vr -10"]
        ; [:button.user-action {:on-click #(re-frame/dispatch [:cube-test.frig-frog.events/init-view 10])} "reset float vr 10"]
        ; [:br]
        ; [:button.user-action
        ;  {:on-click #(re-frame/dispatch
        ;               [:cube-test.frig-frog.events/update-tile 0 0
        ;                ff-tile/state-update-fn])}
        ;  "update-tile 0 0"]
        ; [:button.user-action {:on-click #(rf/dispatch [:cube-test.frig-frog.events/inc-frog-mode])} "inc frog mode"]
        ; [:button.user-action {:on-click #(rf/dispatch [:cube-test.frig-frog.events/jump-frog 1 0])} "jump frog forward"]
        ; [:button.user-action {:on-click #(rf/dispatch [:cube-test.frig-frog.events/jump-frog -2 0])} "jump frog back -2"]
        ; [:button.user-action {:on-click #(rf/dispatch [:cube-test.frig-frog.events/jump-frog 0 1])} "jump frog right"]
        ; [:br]
        ; [:button.user-action {:on-click #(rf/dispatch [:cube-test.frig-frog.events/toggle-dev-mode])} "toggle dev-mode"]
        ; [:br]
        ; [:button.user-action {:on-click #(rf/dispatch
        ;                                   [::ff-events/init-train
        ;                                    {:id-stem :tr-1 :vx -1 :vy 0 :length 1 :init-row 4 :init-col 7}])}
        ;  "init-train 0"]
        ; ; [:button.user-action {:on-click #(cube-test.frig-frog.train/init)} "init-train 0 min"]
        ; ; [:button.user-action {:on-click #(prn "db=" re-frame.db/app-db)} "init-train 0 min"]
        ; [:button.user-action {:on-click #(cube-test.frig-frog.train/init-2
        ;                                     {:id-stem :tr-1 :vx -1 :vy 0 :length 1 :init-row 4 :init-col 7}
        ;                                     ; js/window.re_frame.db.app_db.state)} "init-train 0 min"]
        ;                                     re-frame.db/app-db)} "init-train 0 min"]
        ; [:button.user-action {:on-click #(rf/dispatch
        ;                                   [::ff-events/init-train
        ;                                    {:id-stem :tr-2 :vx 1 :vy 0 :length 2 :init-row 5 :init-col 0}])}
        ;  "init-train 2"]
        ; [:br]
        ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/drop-train-idx 0])} "drop train 0"]
        ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/drop-train-idx 2])} "drop train 2"]
        ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/drop-train-id-stem :tr-1])} "drop train id :tr-1"]
        ; [:br]
        ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/update-train-idx 1])} "update train 1"]
        ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/update-train-by-id :tr-1 {:length 3}])} "update train id :tr-1"]
        ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/update-train-by-id :tr-2 {:length 4}])} "update train id :tr-2"]
        ; [:br]
        ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/add-train-mesh :tr-1])} "add train mesh :tr-1"]
        ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/add-train-mesh :tr-2])} "add train mesh :tr-2"]
        ; [:br]
        ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/toggle-animate-trains])} "toggle train anim"]
        ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/toggle-animate-train "tr-1-0"])} "toggle tr-1-0 anim"]
        ; [:br]
        ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/web-worker-demo ])} "web worker demo"]
        ; [:br]
        ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/start-worker ])} "start worker"]
        ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/stop-worker ])} "stop worker"]
        ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/post-hi ])} "post hi"]
        ; [:br]]]))
        ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/start-worker-2 ])} "start worker 2"]
        ; [:button.user-action {:on-click
        ;                       ; (fn [](.postMessage cube-test.frig-frog.demo-workers-setup-cljs/w2 (js-obj "msg" "ping")))
        ;                       cube-test.frig-frog.demo-workers-setup-cljs/post-ping
        ;                       "worker post-ping"}]
        ; [:button.user-action {:on-click cube-test.frig-frog.demo-workers-setup-cljs/post-ping} "worker min ping"]
        ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/drop-train "tr-1"])} "drop train"]
        ; ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/post-ping ])} "worker min ping"]
        ; [:br]
        ;   ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/worker-abc ])} "worker abc"]
        ;   ; [:button.user-action {:on-click #(rf/dispatch-sync [:cube-test.core/initialize-2])} "worker abc"]
        ; [:button.user-action {:on-click #(rf/dispatch-sync [:initialize-2])} "worker abc"]
        ; [:button.user-action {:on-click #(rf/dispatch [:db-hook])} "db hook"]
        ; ; [:button.user-action {:on-click #(rf/dispatch [:test-worker-fx]
        ; ;                                               {:handler :mirror,
        ; ;                                                :arguments {:a "Hallo" :b "Welt" :c 10}
        ; ;                                                :on-success [:on-worker-fx-success]
        ; ;                                                :on-error [:on-worker-fx-error]})} "test-worker-fx"]
        ; [:br]
        ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/init-reflector])} "init reflector"]
        ; [:br]
        ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/ff-worker-start])} "start ff-worker"]
        ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/ff-worker-stop])} "stop ff-worker"]
        ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/ff-worker-ping])} "ping ff-worker"]
        ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/ff-worker-print-db ])} "ff-worker print-db"]
        ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/main-train-stream
        ;                                                {:id-stem :tr-1 :vx -1 :vy 0 :length 1 :init-row 4 :init-col 7}
        ;                                                3])}
        ;    "train stream (non) ff-worker"]
        ; ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/ff-worker-train-stream])} "train stream ff-worker"]
        ; ; [:button.user-action {:on-click #(rf/dispatch [::ff-events/ff-worker-train-stream
        ; ;                                                {:id-stem :tr-1 :vx -1 :vy 0 :length 1 :init-row 2 :init-col 7}
        ; ;                                                3])}
        ; ;    "train stream ff-worker"]
        ; [:button.user-action {:on-click #(ff-worker/train-stream
        ;                                    {:id-stem :tr-1 :vx -1 :vy 0 :length 1 :init-row 2 :init-col 7}
        ;                                    3)}
        ;    "train stream ff-worker"]]))
        ; ; [:button.user-action {:on-click #(cube-test.frig-frog.train/train-stream
        ; ;                                   {:id-stem :tr-1 :vx -1 :vy 0 :length 1 :init-row 4 :init-col 7})} "train stream ff-worker"]]))
