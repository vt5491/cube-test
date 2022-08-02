;; events is refer to many
(ns cube-test.frig-frog.events
  (:require
   [re-frame.core :refer [dispatch dispatch-sync reg-event-db reg-event-fx reg-fx after ] :as rf]
   [cube-test.frig-frog.game :as ff.game]
   [cube-test.main-scene :as main-scene]
   [cube-test.frig-frog.scene-l1 :as ff.scene-l1]
   [cube-test.frig-frog.db :as ff.db]
   [cube-test.frig-frog.tile :as ff.tile]
   [cube-test.frig-frog.board :as ff.board]
   [cube-test.frig-frog.frog :as ff.frog]
   [cube-test.frig-frog.frog-2 :as ff.frog-2]
   [cube-test.frig-frog.player :as ff.player]
   [cube-test.frig-frog.train :as ff.train]
   [cube-test.utils :as utils]
   [cube-test.utils.common :as common-utils]
   [cube-test.frig-frog.demo-workers-setup-cljs :as tmp]
   [cube-test.frig-frog.demo-workers-cljs :as tmp-2]
   [cube-test.frig-frog.demo-workers-cljs :as tmp-2]
   [cube-test.frig-frog.ff-worker :as ff-worker]
   [cube-test.frig-frog.rules :as ff.rules]))

;;
;; dev stuff
;;
(rf/reg-event-fx
 ::ff-dummy
 (fn [cofx _]
   (prn "frig-frog: dummy event")
   {
    :db (:db cofx)}))

(rf/reg-event-fx
 ::web-worker-demo
 (fn [cofx _]
   {
    :db (:db cofx)}))

(rf/reg-event-fx
 ::start-worker
 (fn [cofx _]
   (prn "frig-frog: startWorker, i=" (ff-worker/start-worker))
   {
    :db (:db cofx)}))

(rf/reg-event-fx
 ::stop-worker
 (fn [cofx _]
   (prn "frig-frog: stopWorker, i=" (tmp/stopWorker))
   {
    :db (:db cofx)}))

(rf/reg-event-fx
 ::post-hi
 (fn [cofx _]
   (tmp/post-hi)
   {
    :db (:db cofx)}))

(rf/reg-event-fx
 ::start-worker-2
 (fn [cofx _]
   (prn "frig-frog: startWorker-2, i=" (tmp/startWorker2))
   {
    :db (:db cofx)}))

(rf/reg-event-fx
 ::stop-worker-2
 (fn [cofx _]
   (prn "frig-frog: stopWorker-2, i=" (ff-worker/stop-worker))
   {
    :db (:db cofx)}))

(rf/reg-event-fx
 ::post-hi-2
 (fn [cofx _]
   (tmp/post-hi-2)
   {
    :db (:db cofx)}))

(rf/reg-event-fx
 ::worker-print-db
 (fn [cofx _]
   (ff-worker/print-db)
   {
    :db (:db cofx)}))

(rf/reg-event-fx
 ::post-add-train
 (fn [cofx [_ train]]
   (ff.rules/inc-train-id-cnt)
   (let [train-n (-> (ff.rules/query-train-id-cnt) (first) (:n))
         train-id (str "tr-" train-n)
         updated-train (assoc-in train [:id-stem] train-id)]
     (ff-worker/post-add-train updated-train))
   {
    :db (:db cofx)}))

(rf/reg-event-fx
 ::drop-train
 (fn [cofx [_ id]]
   (ff-worker/drop-train id)
   {
    :db (:db cofx)}))

(rf/reg-event-fx
 ::worker-abc
 (fn [cofx _]
   {
    :dispatch-n [[:test-worker-fx {:handler :mirror, :arguments {:a "Hello" :b "World2" :c 10} :on-success [:on-worker-fx-success] :on-error [:on-worker-fx-error]}]
                 [:test-worker-fx {:handler :mirror, :arguments {:a "Hallo" :b "Welt" :c 10 :d (js/ArrayBuffer. 10) :transfer [:d]} :transfer [:d] :on-success [:on-worker-fx-success] :on-error [:on-worker-fx-error]}]]}))

(rf/reg-event-fx
 ::post-ping
 (fn [cofx _]
   (tmp/post-ping)
   {
    :db (:db cofx)}))

;; used for development
(defn heavy-cpu []
   (prn "heavy-cpu: starting")
   (let [r 0
         big-num (js/Math.pow 10 9.5)]
      (prn "big-num=" big-num)
      (doall (dotimes [i big-num
                              (+ r (* (js/Math.atan i) (js/Math.tan i)))])))
   (prn "heavy-cpu: done"))

(rf/reg-event-fx
 ::heavy-cpu
 (fn [cofx _]
   (heavy-cpu)
   {
    :db (:db cofx)}))

(rf/reg-event-fx
 ::heavy-cpu-worker
 (fn [cofx [_ task]]
   (prn "heavy-cpu-worker: task=" task)
   (prn "heavy-cpu-worker: cofx.db=" (-> cofx :db))
   (let [worker-pool (-> cofx :db :worker-pool)
         task-with-pool (assoc task :pool worker-pool)]
     {:worker task-with-pool})
   (heavy-cpu)
   {
    :db (:db cofx)}))

(defn heavy-cpu-2 []
  (let [big-num (js/Math.pow 10 7)]
    (prn "big-num=" big-num)
    (reduce (fn [a v] (+ a (* (js/Math.atan v) (js/Math.tan v))))
            0
            (range big-num))))

(rf/reg-event-fx
 ::heavy-cpu-2
 (fn [cofx _]
   (prn "heavy-cpu-2 r=" (heavy-cpu-2))
   {
    :db (:db cofx)}))

(rf/reg-event-fx
 ::heavy-cpu-2-worker
 (fn [cofx [_ task]]
   (let [worker-pool (-> cofx :db :worker-pool)
         task-with-pool (assoc task :pool worker-pool)]
     {:worker task-with-pool})))

(reg-event-db
 ::echo-db
 (fn [db [_ val]]
   (prn "events.echo-db: db=" db ",val=" val)
   db))

(reg-event-fx
 ::echo-fx
 (fn [cofx [_ val]]
   (prn "events.echo-fx: cofx=" cofx ",val=" val)
   {
    :db (:db cofx)}))

;;
;; db events (for testing )
;;
(reg-event-db
 ::seed-test-db
 (fn [db [_ key val]]
   (assoc db key val)))

;;
;; game level events
;;
(reg-event-db
 ::init-game-db
 (fn [db [_ game-db]]
   (let [default-db ff.game/default-game-db]
         ; board (ff.board/init-board default-db)]
     ; (assoc default-db :board board)
     default-db)))

(rf/reg-event-fx
 ::init-game
 (fn [cofx _]
   (ff.game/init)
   (condp (:active-scene (:db cofx))
     :ff-l1
     (do
       {
        :fx [[:dispatch [::init-scene-l1]]]}))))

(rf/reg-event-fx
 ::run-game
 (fn [cofx _]
   (ff.game/run-game)
   {
    :db (:db cofx)}))

(rf/reg-event-fx
 ::update-quanta-width
 (fn [cofx [_ new-quanta-width]]
   ; (prn "events: updating quanta-width to" new-quanta-width)
   (set! ff.game/quanta-width new-quanta-width)
   {
    :db (:db cofx)}))

;; change-abc is for testing
(reg-event-db
  ::change-abc
  (fn [db [_ val]]
    (let [r (ff.game/change-abc db val)]
      (prn "events.change-abc: r=" r)
      r)))

;; defunct
(rf/reg-event-fx
 ::ff-ctrl-mesh-loaded
 (fn [cofx [_ webVRController]]
   (ff.game/ctrl-mesh-loaded-handler webVRController)
   {
    :db (:db cofx)}))

(reg-event-db
 ::toggle-dev-mode
 (fn [db [_]]
   (if (not (:dev-mode db))
     (assoc db :dev-mode true)
     (assoc db :dev-mode (not (:dev-mode db))))))

(reg-event-fx
 ::dev-mode-changed
 (fn [cofx [_ dev-mode]]
   (let [db (:db cofx)
         active-scene (:active-scene db)]
     (when (and (not (nil? dev-mode)) active-scene)
       (condp = active-scene
         :ff-l1 (do
                  (if dev-mode
                    (do
                      (ff.scene-l1/create-walls))
                    (do
                      (ff.scene-l1/remove-walls))))))
    {
      :db (:db cofx)})))

;;
;; scene-l1
;;
(rf/reg-event-fx
 ::init-scene-l1
 (fn [cofx _]
   (ff.scene-l1/init (:db cofx))
   {
    :db (:db cofx)}))

(rf/reg-event-fx
 ::init-non-vr-view
 (fn [cofx [_ rot-delta]]
   (if rot-delta
     (ff.scene-l1/init-non-vr-view rot-delta)
     (ff.scene-l1/init-non-vr-view))
   {
     :db (:db cofx)}))

(rf/reg-event-fx
 ::init-vr-view
 (fn [cofx [_ rot-delta]]
   (if rot-delta
     (ff.scene-l1/init-vr-view rot-delta)
     (ff.scene-l1/init-vr-view))
   {
     :db (:db cofx)}))

; (rf/reg-event-fx
;  ::init-view
;  (fn [cofx [_ rot-delta]]
;    (if rot-delta
;      (ff.scene-l1/init-view rot-delta)
;      (ff.scene-l1/init-view))
;    {
;      :db (:db cofx)}))

(rf/reg-event-fx
 ::reset-view
 (fn [cofx [_]]
   (ff.scene-l1/reset-view)
   {
     :db (:db cofx)}))

(rf/reg-event-fx
 ::scene-l1-toggle-animation
 (fn [cofx [_]]
   (ff.scene-l1/toggle-animation)
   {
     :db (:db cofx)}))

(rf/reg-event-fx
 ::init-reflector
 (fn [cofx _]
   (ff.scene-l1/init-reflector)
   {
    :db (:db cofx)}))

;;
;; tile
;; defunct as board calls 'draw-tile' natively for performance and various other reasons.
;; Nope: active once again
(rf/reg-event-fx
 ::draw-tile
 (fn [cofx [_ pos-x pos-y prfx]]
   ; (prn "events.draw-tile. prfx=" prfx)
   (ff.tile/draw prfx pos-x pos-y)
   {
    :db (:db cofx)}))

(reg-event-db
  ::update-tile
  (fn [db [_ row-num col-num update-fn]]
    (ff.tile/update-tile row-num col-num update-fn db)))

;;
;; board(s)
;;
(reg-event-db
  ::init-btm-board
  (fn [db [_ val]]
    (assoc db :btm-board (ff.board/init-board db))))

(reg-event-db
  ::init-top-board
  (fn [db [_ val]]
    (assoc db :top-board (ff.board/init-board db))))

(reg-event-db
  ::init-board-2
  (fn [db [_]]
    (assoc db :board-2 [{:row-0 [{:tile :0-0 :state 7}, {:tile :0-1 :state 7}]}
                        {:row-1 [{:tile :1-0 :state 8}, {:tile :1-1 :state 8}]}])))

(reg-event-db
  ::add-dummy-tile
  (fn [db [_]]
    (assoc db :board (conj (:board db) {:row-4 {:tile-4-0 {}, :tile-4-1 {}}}))))

(rf/reg-event-fx
 ::update-n-rows
 (fn [cofx [_ n-rows]]
   (let [db (:db cofx)]
     (set! ff.board/n-rows n-rows)
     (set! ff.board/board-length (* n-rows (:quanta-width db))))
   {
    :db (:db cofx)}))

(rf/reg-event-fx
 ::update-n-cols
 (fn [cofx [_ n-cols]]
   (let [db (:db cofx)]
     (set! ff.board/n-cols n-cols)
     (set! ff.board/board-width (* n-cols (:quanta-width db))))
   {
    :db (:db cofx)}))

;;
;; frog
;;

(reg-event-db
  ::init-frog
  (fn [db [_]]
    (ff.frog/init-frog db)))

(reg-event-db
  ::jump-frog
  (fn [db [_ row-delta col-delta]]
    (let [frog-row (get-in db [:frog :row])
          frog-col (get-in db [:frog :col])]
      (-> (assoc-in db [:frog :row] (+ frog-row row-delta))
          (assoc-in [:frog :col] (+ frog-col col-delta))))))

;; kind of a dummy event for now
(reg-event-db
  ::set-frog-mode
  (fn [db [_ new-mode]]
    (assoc-in db [:frog :mode] new-mode)))

(reg-event-db
  ::inc-frog-mode
  (fn [db [_]]
    (let [last-frog-mode (get-in db [:frog :mode])]
      (assoc-in db [:frog :mode] (+ last-frog-mode 1)))))

(rf/reg-event-fx
 ::draw-frog
 (fn [cofx [_ row col]]
   (ff.frog/draw-frog row col)
   {
    :db (:db cofx)}))
;;
;; frog-2
;;
(reg-event-db
  ::init-frog-2
  (fn [db [_ row col]]
    (ff-worker/init-frog-2 row col)))

(rf/reg-event-fx
 ::move-frog-2
 (fn [cofx [_ x y]]
   (ff-worker/move-frog-2 x y)))

(rf/reg-event-fx
 ::draw-frog-2
 (fn [cofx [_ frog-2]]
   (let [row (:row frog-2)
         col (:col frog-2)]
     {
      :db (:db cofx)
      :fx (ff.frog-2/draw-frog-2 frog-2)})))

;;
;; player
;;

(reg-event-db
  ::init-player
  (fn [db [_]]
    (ff.player/init-player)
    db))

;;
;; train
;;
(def train-1-interval-id)

(reg-event-db
  ::init-train
  (fn [db [_ opts]]
    (ff.train/init opts db)))

(reg-event-fx
  ::init-trains
  (fn [cofx [_ opts]]
    (let [db (:db cofx)
          interval-id (js/setInterval #(rf/dispatch [::init-train opts] ) 2000)
          tmp (prn "hi-b")]
      (utils/sleep #(js/clearInterval interval-id) 10000))
    {
     :db (:db cofx)}))

(reg-event-db
  ::drop-train-idx
  (fn [db [_ idx]]
    (let [new-trains (ff.train/drop-train-idx (:trains db) idx)]
      (assoc db :trains new-trains))))

(reg-event-db
  ::drop-train-id-stem
  (fn [db [_ id-stem]]
    (let [new-trains (ff.train/drop-train-id-stem (:trains db) id-stem)]
      (assoc db :trains new-trains))))

(reg-event-db
  ::update-train-idx
  (fn [db [_ idx]]
    (let [new-trains (ff.train/drop-train-idx (:trains db) idx)]
      (assoc db :trains new-trains))))

;; this wraps train/update-train-by-id, but updates the entire :trains field as a whole
(reg-event-db
  ::update-train-id-stem
  (fn [db [_ id-stem updates]]
    (let [trains (:trains db)
          new-train (ff.train/update-train-id-stem trains id-stem updates)
          idx (common-utils/idx-of-id-stem trains id-stem)]
      (assoc db :trains (assoc trains idx new-train)))))

(rf/reg-event-fx
 ::add-train-mesh
 (fn [cofx [_ train]]
   (let [db (:db cofx)]
     (ff.train/add-train-mesh train))
   {
     :db (:db cofx)}))

(rf/reg-event-fx
 ::drop-train-mesh
 (fn [cofx [_ train]]
   (let [db (:db cofx)]
     (ff.train/drop-train-mesh train))
   {
     :db (:db cofx)}))

(rf/reg-event-fx
 ::update-train-mesh-by-idx
 (fn [cofx [_ idx]]
   (let [db (:db cofx)
         train (nth (:trains db) idx)]
     (ff.train/drop-train-mesh train)
     (ff.train/add-train-mesh train))
   {
     :db (:db cofx)}))

(rf/reg-event-fx
 ::reset-train-mesh
 (fn [{db :db} [_ train-mesh]]
   (ff.train/reset-train-mesh train-mesh db)
   {
    :db db}))

(rf/reg-event-fx
 ::toggle-animate-trains
 (fn [{db :db} [_ train-mesh]]
   (set! ff.train/animate-trains (not ff.train/animate-trains))
   {
    :db db}))

(rf/reg-event-fx
 ::toggle-animate-train
 (fn [{db :db} [_ train-id]]
   (set! ff.train/animate-trains (not ff.train/animate-trains))
   (let [scene main-scene/scene
         train-mesh (.getMeshByID scene train-id)]
    (when (not (nil? (and  train-mesh (.-metadata train-mesh) (-> train-mesh (.-metadata) (.-animate)))))
      (when (not (nil? (-> train-mesh (.-metadata) (.-animate))))
        (let [old-animate (-> train-mesh (.-metadata) (.-animate))]
          (set! (-> train-mesh (.-metadata) (.-animate)) (not old-animate))))))
   {
    :db db}))

(rf/reg-event-fx
 ::train-toggle-animation
 (fn [cofx [_]]
   (ff.train/toggle-animation)
   {
     :db (:db cofx)}))

;; worker
(reg-event-db
  ::init-worker
  (fn [db [_]]
    (assoc db :worker-pool cube-test.core.worker-pool)))

;; ff-worker
(rf/reg-event-fx
 ; ::start-ff-worker
 ::ff-worker-start
 (fn [cofx _]
   (ff-worker/start-worker)
   {
    :db (:db cofx)}))

(rf/reg-event-fx
 ; ::stop-ff-worker
 ::ff-worker-stop
 (fn [cofx _]
   (ff-worker/stop-worker)
   {
    :db (:db cofx)}))

(rf/reg-event-fx
 ::ff-worker-restart
 (fn [cofx _]
   {
    :db (:db cofx)
    :fx  [[:dispatch [::ff-worker-stop]]
          [:dispatch [::ff-worker-start]]]}))

(rf/reg-event-fx
 ::ff-worker-ping
 (fn [cofx _]
   (ff-worker/ping)
   {
    :db (:db cofx)}))

(rf/reg-event-fx
 ::ff-worker-print-db
 (fn [cofx _]
   (ff-worker/print-db)
   {
    :db (:db cofx)}))

(rf/reg-event-fx
 ::main-train-stream
 (fn [{db :db} [_ train-opts cnt]]
   (when (> cnt 0)
     (utils/sleep
       #(rf/dispatch [::main-train-stream train-opts (- cnt 1)])
       1000))
   {
    :db (ff.train/init train-opts db)}))

(rf/reg-event-fx
 ::ff-worker-train-stream
 (fn [{db :db} [_ train-opts cnt]]
   {
    :db (ff.train/init train-opts db)}))

;;
;; rules
;;
(rf/reg-event-fx
 ::init-rules
 (fn [cofx _]
   (ff.rules/init-session)
   {
    :db (:db cofx)}))

(rf/reg-event-fx
 ::reset-rules
 (fn [cofx _]
   (ff.rules/init-session)
   {
    :db (:db cofx)}))
