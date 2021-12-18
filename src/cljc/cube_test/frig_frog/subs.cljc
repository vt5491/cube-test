(ns cube-test.frig-frog.subs
  (:require
   [re-frame.core :as re-frame :refer [reg-sub subscribe]]
   [cube-test.frig-frog.events :as events]
   [cube-test.frig-frog.scene :as scene]
   [cube-test.utils :as utils]
   [clojure.data :as clj-data]))

(def ^:dynamic *last-board* (atom nil))
; (def ^:dynamic *last-board* (atom ()))
; (def ^:dynamic *last-board* (atom {}))

;; extractors
(reg-sub
 :get-board
 (fn [db _]
   (:board db)))

;; computations
(reg-sub
 :board
 :<- [:get-board]
 (fn [db query-v]
   (prn "board has changed")))

   ; 0,0
   ; [{:row-0 [{:state 1}]}]
   ; 1,1
   ; [nil {:row-1 [nil {:state 1}]}]
   ; 2,3
   ; [nil nil {:row-2 [nil nil nil {:state 1}]}]))
(reg-sub
 :board-changed
 :<- [:get-board]
 (fn [board query-v]
   (re-frame/dispatch [:cube-test.frig-frog.events/draw-tile 0 0])
   (prn "subs: *last-board*=" @*last-board*)
   (prn "subs: diff=" (first (clj-data/diff board @*last-board*)))
   (prn "subs: board-changed: board=" board ",query-v=" query-v)
   (let [diff (first (clj-data/diff board @*last-board*))]
   ; (let [diff (clj-data/diff  board @*last-board*)]
    (prn "diff=" diff)
    (prn "count diff=" (count diff))
    ; (let [[item1 _ item3 _ item5 _] names])
    (let [[row0 row1 row2 ] diff]
      (prn "row0=" row0 ", row1=" row1 ", row2=" row2)))
    ; (let [[{_ [col0 col1 col2]} ] diff]
    ;   (prn "col0=" col0 ", col1=" col1 ", col2=" col2)))
   (let [diff-row-1 (first
                     (clj-data/diff
                      (get-in board [1 :row-1 1 :state]) (get-in @*last-board* [1 :row-1 1 :state])))]
     (prn "detailed diff=" diff-row-1))
   (swap! *last-board* (fn [x] board))))
