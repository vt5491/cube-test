;; events is refer to many
(ns cube-test.frig-frog.events
  (:require
   [re-frame.core :refer [dispatch reg-event-db reg-event-fx reg-fx after ] :as re-frame]
   [cube-test.frig-frog.game :as ff.game]
   [cube-test.main-scene :as main-scene]
   [cube-test.frig-frog.scene-l1 :as ff.scene-l1]
   [cube-test.frig-frog.db :as ff.db]
   [cube-test.frig-frog.tile :as ff.tile]
   [cube-test.frig-frog.board :as ff.board]
   [cube-test.frig-frog.frog :as ff.frog]
   [cube-test.frig-frog.train :as ff.train]
   [cube-test.utils :as utils]
   [cube-test.utils.common :as common-utils]))

(re-frame/reg-event-fx
 ::ff-dummy
 (fn [cofx _]
   (prn "frig-frog: dummy event")
   {
    :db (:db cofx)}))

;; used for development
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
   (prn "seed-test-db: key=" key ", val=" val)
   (assoc db key val)))

;;
;; game level events
;;
(reg-event-db
 ::init-game-db
 (fn [db [_ game-db]]
   ; (println ":frig-frog.init-game-db: now running")
   (let [default-db ff.game/default-game-db
         board (ff.board/init-board default-db)]
     (assoc default-db :board board))))

(re-frame/reg-event-fx
 ::init-game
 (fn [cofx _]
   (ff.game/init)
   (condp (:active-scene (:db cofx))
     :ff-l1
     (do
       {
        :fx [[:dispatch [::init-scene-l1]]]}))))

(re-frame/reg-event-fx
 ::run-game
 (fn [cofx _]
   (ff.game/run-game)
   {
    :db (:db cofx)}))

(re-frame/reg-event-fx
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
    ; (ff.game/change-abc db val)
    (let [r (ff.game/change-abc db val)]
      (prn "events.change-abc: r=" r)
      r)))
    ; (prn "events: returned db=")
    ; db))

;; defunct
(re-frame/reg-event-fx
 ::ff-ctrl-mesh-loaded
 (fn [cofx [_ webVRController]]
   (prn "frig-frog.events: now calling ctrl-mesh-loaded-handler")
   (ff.game/ctrl-mesh-loaded-handler webVRController)
   {
    :db (:db cofx)}))

(reg-event-db
 ::toggle-dev-mode
 (fn [db [_]]
   ; (prn ":frig-frog.toggle-dev-mode: entered, db=" db)
   (if (not (:dev-mode db))
     (assoc db :dev-mode true)
     (assoc db :dev-mode (not (:dev-mode db))))))

(reg-event-fx
 ::dev-mode-changed
 (fn [cofx [_ dev-mode]]
   (let [db (:db cofx)
         active-scene (:active-scene db)]
     (prn "events.dev-mode-changed: dev-mode=" dev-mode ",active-scene=" active-scene)
     (when (and (not (nil? dev-mode)) active-scene)
       (condp = active-scene
         :ff-l1 (do
                  ; (if (= new-dev-mode false))
                  (if dev-mode
                    (do
                      (prn "events: calling create-walls")
                      (ff.scene-l1/create-walls))
                    (do
                      (prn "events: calling remove-walls")
                      (ff.scene-l1/remove-walls))))))
    {
      :db (:db cofx)})))

;;
;; scene-l1
;;
(re-frame/reg-event-fx
 ::init-scene-l1
 (fn [cofx _]
   (ff.scene-l1/init (:db cofx))
   {
    :db (:db cofx)}))

(re-frame/reg-event-fx
 ::init-non-vr-view
 (fn [cofx [_ rot-delta]]
   (prn "events.init-view: rot-delta=" rot-delta)
   ; (ff.scene/init-view rot-delta)
   (if rot-delta
     (ff.scene-l1/init-non-vr-view rot-delta)
     (ff.scene-l1/init-non-vr-view))
   {
     :db (:db cofx)}))

(re-frame/reg-event-fx
 ::init-vr-view
 (fn [cofx [_ rot-delta]]
   (prn "events.init-view: rot-delta=" rot-delta)
   ; (ff.scene/init-view rot-delta)
   (if rot-delta
     (ff.scene-l1/init-vr-view rot-delta)
     (ff.scene-l1/init-vr-view))
   {
     :db (:db cofx)}))

(re-frame/reg-event-fx
 ::init-view
 (fn [cofx [_ rot-delta]]
   (prn "events.init-view: rot-delta=" rot-delta)
   (if rot-delta
     (ff.scene-l1/init-view rot-delta)
     (ff.scene-l1/init-view))
   {
     :db (:db cofx)}))

(re-frame/reg-event-fx
 ::reset-view
 (fn [cofx [_]]
   (prn "events.reset-view: entered")
   (ff.scene-l1/reset-view)
   {
     :db (:db cofx)}))

(re-frame/reg-event-fx
 ::scene-l1-toggle-animation
 (fn [cofx [_]]
   ; (prn "events.reset-view: entered")
   (ff.scene-l1/toggle-animation)
   {
     :db (:db cofx)}))

;;
;; tile
;; defunct as board calls 'draw-tile' natively for performance and various other reasons.
;; Nope: active once again
(re-frame/reg-event-fx
 ::draw-tile
 (fn [cofx [_ pos-x pos-y]]
   (ff.tile/draw pos-x pos-y)
   {
    :db (:db cofx)}))

(reg-event-db
  ::update-tile
  (fn [db [_ row-num col-num update-fn]]
    (ff.tile/update-tile row-num col-num update-fn db)))
    ; (let [b (:board db)
    ;       row (nth b row-num)
    ;       row-kw (keyword (str "row-" row-num))
    ;       tmp (prn "b=" b ", row=" row ", row-kw=" row-kw)
    ;       tile (-> (row-kw row) (nth col-num))]
    ;   (prn "update-tile: tile=" tile))))

    ; (-> (:row-0 (nth b 0)) (nth 1) -> (:tile))))
    ; (assoc db :board (ff.board/init-board db))))

;;
;; board
;;
(reg-event-db
  ::init-board
  (fn [db [_ val]]
    (assoc db :board (ff.board/init-board db))))

;; this is mostly useful for testing and debugging
; (reg-event-db
;   ::init-row
;   (fn [db [_ row-num n-col]]
;     ; (ff.board/init-row row-num n-col (:board db))
;     (assoc db :board (ff.board/init-row row-num n-col (:board db)))))
;     ; db))

(reg-event-db
  ::init-board-2
  (fn [db [_]]
    ; (assoc db :board-2 [{:row-0 [{:tile-0-0 {}}, {:tile-0-1 {}}]},
    ;                     {:row-1 [{:tile-1-0 {}}, {:tile-1-1 {}}]}])
    ; (assoc db :board-2 {:row-0 [{:tile-0-0 {}}, {:tile-0-1 {}}],
    ;                     :row-1 [{:tile-1-0 {}}, {:tile-1-1 {}}]})
    ; (assoc db :board-2 [{:row-0 [{:tile-0-0 {}}, {:tile-0-1 {}}]}
    ;                     {:row-1 [{:tile-1-0 {}}, {:tile-1-1 {}}]}])
    (assoc db :board-2 [{:row-0 [{:tile :0-0 :state 7}, {:tile :0-1 :state 7}]}
                        {:row-1 [{:tile :1-0 :state 8}, {:tile :1-1 :state 8}]}])))

(reg-event-db
  ::add-dummy-tile
  (fn [db [_]]
    ; (assoc db :board (conj (:board db) {:tile-dmy {}}))
    (assoc db :board (conj (:board db) {:row-4 {:tile-4-0 {}, :tile-4-1 {}}}))))
    ; (assoc db :board (conj (:board db) {:row-4 [{:tile-4-0 {}}, {:tile-4-1 {}}]}))))

(re-frame/reg-event-fx
 ::update-n-rows
 (fn [cofx [_ n-rows]]
   (let [db (:db cofx)]
     (prn "events: updating n-rows to" n-rows)
     (set! ff.board/n-rows n-rows)
     (set! ff.board/board-length (* n-rows (:quanta-width db))))
   {
    :db (:db cofx)}))

(re-frame/reg-event-fx
 ::update-n-cols
 (fn [cofx [_ n-cols]]
   (let [db (:db cofx)]
     (prn "events: updating n-cols to" n-cols)
     (set! ff.board/n-cols n-cols)
     (set! ff.board/board-width (* n-cols (:quanta-width db))))
   {
    :db (:db cofx)}))

;;
;; frog
;;
; (reg-event-db
;   ::init-frog
;   (fn [db [_ row col]]
;     ; (assoc db :frog (ff.frog/init-frog db))
;     (ff.frog/init-frog row col db)))

(reg-event-db
  ::init-frog
  (fn [db [_]]
    ; (assoc db :frog (ff.frog/init-frog db))
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

(re-frame/reg-event-fx
 ::draw-frog
 (fn [cofx [_ row col]]
   (prn "events.draw-frog: row=" row ", col=" col)
   (ff.frog/draw-frog row col)
   {
    :db (:db cofx)}))

;;
;; train
;;
(def train-1-interval-id)

(reg-event-db
  ::init-train
  (fn [db [_ opts]]
    (prn "events.init-train opts=" opts)
    (ff.train/init opts db)))
; setInterval((x) => {console.log("hi")}, 1000)
; clearInterval(78)

(reg-event-fx
  ::init-trains
  (fn [cofx [_ opts]]
    ; (ff.train/init opts db)
    (let [db (:db cofx)
          ; interval-id (js/setInterval #(re-frame/dispatch [:cube-test.frig-frog.events/init-train] opts db) 1000)
          ; interval-id (js/setInterval #(re-frame/dispatch [:init-train opts] ) 1000)
          tmp (prn "hi-a")
          ; interval-id (js/setInterval #(prn "hi from interval" ) 1000)
          interval-id (js/setInterval #(re-frame/dispatch [::init-train opts] ) 2000)
          ; interval-id (js/setInterval #(ff.train/add-train-mesh opts) 2000)
          tmp (prn "hi-b")]
      (prn "interval-id=" interval-id)
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

; (re-frame/reg-event-fx
;  ::get-train-by-id
;  (fn [cofx [_ trains id]]
;    (prn "events.get-train-by-id: trains=" trains ", id=" id)
;    (ff.train/get-train-by-id trains id)
;    {
;     :db (:db cofx)}))

;; this wraps train/update-train-by-id, but updates the entire :trains field as a whole
(reg-event-db
  ::update-train-id-stem
  (fn [db [_ id-stem updates]]
    (let [trains (:trains db)
          new-train (ff.train/update-train-id-stem trains id-stem updates)
          tmp (prn "hi3")
          ; idx (common-utils/idx-of-id trains id-stem)
          idx (common-utils/idx-of-id-stem trains id-stem)
          tmp-2 (prn "hi4")]
      (prn "events.update-train-id-stem: new-train=" new-train)
      (prn "events.update-train-id-stem: idx=" idx)
      (assoc db :trains (assoc trains idx new-train)))))

; (re-frame/reg-event-fx
;  ::add-train-mesh
;  (fn [cofx [_ id]]
;    (prn "events.add-train-mesh: entered")
;    (let [db (:db cofx)
;          trains (:trains db)
;          train (ff.train/get-train-by-id trains id)]
;      (prn "events.add-train-mesh: train=" train)
;      (ff.train/add-train-mesh train))
;    {
;      :db (:db cofx)}))

(re-frame/reg-event-fx
 ::add-train-mesh
 (fn [cofx [_ train]]
   (prn "events.add-train-mesh: entered")
   (let [db (:db cofx)]
         ; trains (:trains db)
         ; train (ff.train/get-train-by-id trains id)]
     (prn "events.add-train-mesh: train=" train)
     (ff.train/add-train-mesh train))
   {
     :db (:db cofx)}))

(re-frame/reg-event-fx
 ::drop-train-mesh
 (fn [cofx [_ train]]
   (prn "events.drop-train-mesh: entered")
   (let [db (:db cofx)]
     (ff.train/drop-train-mesh train))
   {
     :db (:db cofx)}))

(re-frame/reg-event-fx
 ::update-train-mesh-by-idx
 ; (fn [cofx [_ train]])
 (fn [cofx [_ idx]]
   (prn "events.update-train-mesh: idx=" idx)
   (let [db (:db cofx)
         train (nth (:trains db) idx)]
     (ff.train/drop-train-mesh train)
     (ff.train/add-train-mesh train))
   {
     :db (:db cofx)}))

(re-frame/reg-event-fx
 ::reset-train-mesh
 (fn [{db :db} [_ train-mesh]]
   (prn "events.reset-train-mesh: train-mesh=" train-mesh)
   (ff.train/reset-train-mesh train-mesh db)
   {
    ; :db (:db cofx)
    :db db}))

(re-frame/reg-event-fx
 ::toggle-animate-trains
 (fn [{db :db} [_ train-mesh]]
   (set! ff.train/animate-trains (not ff.train/animate-trains))
   {
    :db db}))

(re-frame/reg-event-fx
 ::toggle-animate-train
 (fn [{db :db} [_ train-id]]
   (set! ff.train/animate-trains (not ff.train/animate-trains))
   (let [scene main-scene/scene
         train-mesh (.getMeshByID scene train-id)]
    (prn "events:toggle-animate-train: train-mesh=" train-mesh ",train-id=" train-id)
    (prn "events:toggle-animate-train: metadata=" (-> train-mesh (.-metadata)))
    (prn "events:toggle-animate-train: animate=" (-> train-mesh (.-metadata) (.-animate)))
    (when (not (nil? (and  train-mesh (.-metadata train-mesh) (-> train-mesh (.-metadata) (.-animate)))))
      (prn "events:toggle-animate-train: metadata=" (-> train-mesh (.-metadata)))
      ; (prn "hi")
      ; (when-let [animate (-> train-mesh (.-metadata) (.-animate))]
      ;   (prn "events:toggle-animate-train: animate=" animate)
      ;   (set! (-> train-mesh (.-metadata) (.-animate)) (not animate))
      ;   (prn "events:toggle-animate-train: new animate=" (-> train-mesh (.-metadata) (.-animate))))
      (when (not (nil? (-> train-mesh (.-metadata) (.-animate))))
        (let [old-animate (-> train-mesh (.-metadata) (.-animate))]
          (prn "events:toggle-animate-train: old-animate=" old-animate)
          (set! (-> train-mesh (.-metadata) (.-animate)) (not old-animate))
          (prn "events:toggle-animate-train: new animate=" (-> train-mesh (.-metadata) (.-animate)))))))
   {
    :db db}))

(re-frame/reg-event-fx
 ::train-toggle-animation
 (fn [cofx [_]]
   ; (prn "events.reset-view: entered")
   (ff.train/toggle-animation)
   {
     :db (:db cofx)}))
