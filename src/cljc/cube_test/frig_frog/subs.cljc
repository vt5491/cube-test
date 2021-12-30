(ns cube-test.frig-frog.subs
  (:require
   [re-frame.core :as re-frame :refer [reg-sub subscribe]]
   [cube-test.frig-frog.events :as events]
   [cube-test.frig-frog.board :as ff-board]
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
   (let [diff-full (clj-data/diff board @*last-board*)
         diff-a (first diff-full)
         diff-b (second diff-full)
         diff-c (nth diff-full 2)]
   ; (let [diff (clj-data/diff  board @*last-board*)]
     (prn "diff-a=" diff-a)
     (prn "count diff-a=" (count diff-a))
   ; (prn "diff-b=" diff-b)
   ; (prn "count diff-b=" (count diff-b))
   ; (prn "diff-c=" diff-c)
   ; (let [[item1 _ item3 _ item5 _] names])
     (let [[row0 row1 row2 ] diff-a]
         (prn "row0=" row0 ", row1=" row1 ", row2=" row2)
         (let [changed-tiles (filter some? diff-a)]
           (prn "changed-tiles=" changed-tiles)
           (prn "count changed-tiles=" (count changed-tiles))))
      ; (-> (re-matches #"row-(\d+)" (name :row-12)) (nth 1) (js/parseInt))))
    ; (let [[{_ [col0 col1 col2]} ] diff]
    ;   (prn "col0=" col0 ", col1=" col1 ", col2=" col2)))
    ; (ff-board/parse-delta diff-1))
     ; (prn "parse-delta=" (into (sorted-map) (ff-board/parse-delta diff-a)))
     ; (prn "parse-delta=" (ff-board/parse-delta diff-a))
     ; "parse-delta=" [{:row 0, :col 0, :abc -15, :state 15} {:row 2, :col 2, :abc -1, :state 1}]
     (let [tile-deltas (ff-board/parse-delta-2 diff-a)]
       (prn "subs: tile-deltas=" tile-deltas)
       ; (map (fn [{:keys [row col abc state]}]) tile-deltas)
       (doseq [{:keys [row col abc state]} tile-deltas]
         (prn "doseq: row=" row ",col=" col)
         (re-frame/dispatch [:cube-test.frig-frog.events/draw-tile row col]))))
  ; (prn "row=" row

   ; (let [diff-row-1 (first
   ;                   (clj-data/diff
   ;                    (get-in board [1 :row-1 1 :state]) (get-in @*last-board* [1 :row-1 1 :state])))]
   ;   (prn "detailed diff=" diff-row-1))
   (swap! *last-board* (fn [x] board))))
