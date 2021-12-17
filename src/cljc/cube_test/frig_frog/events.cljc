;; events is refer to many
(ns cube-test.frig-frog.events
  (:require
   [re-frame.core :refer [dispatch reg-event-db reg-event-fx reg-fx after ] :as re-frame]
   [cube-test.frig-frog.game :as ff.game]
   [cube-test.frig-frog.scene :as frig-frog.scene]
   [cube-test.frig-frog.db :as frig-frog.db]
   [cube-test.frig-frog.tile :as ff.tile]
   [cube-test.frig-frog.board :as ff.board]
   [cube-test.utils :as utils]))

(reg-event-db
 ::init-game-db
 (fn [db [_ game-db]]
   (println ":frig-frog.init-game-db: now running")
   ; (frig-frog.db/init-game-db db)
   game-db))

(re-frame/reg-event-fx
 ::init-scene
 (fn [cofx _]
   (frig-frog.scene/init (:db cofx))
   {
    :db (:db cofx)}))

(re-frame/reg-event-fx
 ::init-game
 (fn [cofx _]
   (ff.game/init)
   {
    ; :fx [[:dispatch [::init-scene]]]
    :fx [[:dispatch [::init-scene]]]}))
    ; :db (:db cofx)}))

(re-frame/reg-event-fx
 ::run-game
 (fn [cofx _]
   (ff.game/run-game)
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

;; tile
;; defunct as board calls 'draw-tile' natively for performance and various other reasons.
;; Nope: active once again
(re-frame/reg-event-fx
 ::draw-tile
 (fn [cofx [_ pos-x pos-y]]
   (ff.tile/draw pos-x pos-y)
   {
    :db (:db cofx)}))

;; board
; (re-frame/reg-event-fx
;  ::draw-board
;  ; (fn [cofx _])
;  (fn [{:keys [db] :as cofx}]
;    ; (ff.board/draw-board (:db cofx))
;    ; (let [db (:db cofx)]
;      {
;       ; :db (ff.board/draw-board (:db cofx))
;       :db (assoc db :board (ff.board/draw-board db))}))
;         ; :db (:db cofx)}))

; (reg-event-db
;   ::draw-board
;   (fn [db [_ val]]
;     (assoc db :board (ff.board/draw-board db))))

(reg-event-db
  ::init-board
  (fn [db [_ val]]
    (assoc db :board (ff.board/init-board db))))

;; this is mostly useful for testing and debugging
(reg-event-db
  ::init-row
  (fn [db [_ row-num n-col]]
    ; (ff.board/init-row row-num n-col (:board db))
    (assoc db :board (ff.board/init-row row-num n-col (:board db)))))
    ; db))

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
