(ns cube-test.frig-frog.subs
  (:require
   [re-frame.core :as re-frame :refer [reg-sub subscribe]]
   [cube-test.frig-frog.events :as events]
   [cube-test.frig-frog.board :as ff-board]
   [cube-test.frig-frog.scene :as scene]
   [cube-test.utils :as utils]
   [clojure.data :as clj-data]
   [cube-test.macros :as macros]))

(def ^:dynamic *last-board* (atom nil))
(def ^:dynamic *last-frog-row-col* (atom nil))
; (def ^:dynamic *last-board* (atom ()))
; (def ^:dynamic *last-board* (atom {}))

;; extractors
(reg-sub
 :get-board
 (fn [db _]
   (:board db)))

(reg-sub
 :get-frog
 (fn [db _]
   (:frog db)))

(reg-sub
 :get-frog-row
 (fn [db _]
   (get-in db [:frog :row])))

(reg-sub
 :get-frog-row-col
 (fn [db _]
   (conj (hash-map :row (get-in db [:frog :row])) (hash-map :col (get-in db [:frog :col])))))

(reg-sub
 :get-frog-mode
 (fn [db _]
   (get-in db [:frog :mode])))

;; computations
(reg-sub
 :board-changed-0
 :<- [:get-board]
 (fn [db query-v]
   (prn "board has changed")))

(reg-sub
 :board-changed
 :<- [:get-board]
 (fn [board query-v]
   ; (re-frame/dispatch [:cube-test.frig-frog.events/draw-tile 0 0])
   (prn "subs: *last-board*=" @*last-board*)
   ; (prn "subs: diff=" (first (clj-data/diff board @*last-board*)))
   ; (prn "subs: board-changed: board=" board ",query-v=" query-v)
   (let [diff-full (clj-data/diff board @*last-board*)
         diff-a (first diff-full)
         diff-b (second diff-full)
         diff-c (nth diff-full 2)]
     ; (let [diff (clj-data/diff  board @*last-board*)]
     ; (prn "diff-a=" diff-a)
     ; (prn "count diff-a=" (count diff-a))
     ; (prn "diff-b=" diff-b)
     ; (prn "count diff-b=" (count diff-b))
     ; (prn "diff-c=" diff-c)
     ; (let [[item1 _ item3 _ item5 _] names])
     (let [[row0 row1 row2 ] diff-a]
          ; (prn "row0=" row0 ", row1=" row1 ", row2=" row2)
          (let [changed-tiles (filter some? diff-a)]))
     ; (prn "changed-tiles=" changed-tiles)
     ; (prn "count changed-tiles=" (count changed-tiles))))
        ; (-> (re-matches #"row-(\d+)" (name :row-12)) (nth 1) (js/parseInt))))
     ; (let [[{_ [col0 col1 col2]} ] diff]
     ;   (prn "col0=" col0 ", col1=" col1 ", col2=" col2)))
     ; (ff-board/parse-delta diff-1))
     ; (prn "parse-delta=" (into (sorted-map) (ff-board/parse-delta diff-a)))
     ; (prn "parse-delta=" (ff-board/parse-delta diff-a))
     ; "parse-delta=" [{:row 0, :col 0, :abc -15, :state 15} {:row 2, :col 2, :abc -1, :state 1}]
     (let [tile-deltas (ff-board/parse-delta-2 diff-a)]
       ; (prn "subs: tile-deltas=" tile-deltas)
       ; (prn "subs: count tile-deltas=" (count tile-deltas))
       ; (map (fn [{:keys [row col abc state]}]) tile-deltas)
       (doseq [{:keys [row col abc state]} tile-deltas]
         ; (prn "doseq: row=" row ",col=" col)
         (re-frame/dispatch [:cube-test.frig-frog.events/draw-tile row col]))))
    ; (prn "row=" row

   ; (let [diff-row-1 (first
   ;                   (clj-data/diff
   ;                    (get-in board [1 :row-1 1 :state]) (get-in @*last-board* [1 :row-1 1 :state])))]
   ;   (prn "detailed diff=" diff-row-1))
   (swap! *last-board* (fn [x] board))))

(reg-sub
 :frog-changed
 :<- [:get-frog :col]
 (fn [frog query-v]
   (prn "sub: frog-changed: frog=" frog ", qv=" query-v)))

(reg-sub
 :frog-row-changed
 :<- [:get-frog-row]
 (fn [row query-v]
   (prn "sub: frog-row-changed: row=" row ", qv=" query-v)))

;; main frog pos handler
(reg-sub
 :frog-row-col-changed
 :<- [:get-frog-row-col]
 (fn [row-col query-v]
   (prn "sub: frog-row-col-changed: row=" row-col ", qv=" query-v)
   (prn "sub: frog-row-col-changed: last-frog-row-col=" @*last-frog-row-col*)
   ; (when (and (:row row-col) (:col row-col)))
   (macros/when-let* [row (:row row-col)
                      col (:col row-col)]
     (let [
           ; row (:row row-col)
           ; col (:col row-col)
           diff-full (clj-data/diff row-col @*last-frog-row-col*)
           diff-a (first diff-full)
           diff-b (second diff-full)
           last-row (or (:row diff-b) row)
           last-col (or (:col diff-b) col)]
       (prn "frog-row-col-changed: diff-a=" diff-a)
       (prn "frog-row-col-changed: diff-b=" diff-b)
       (prn "frog-row-col-changed: last-row=" last-row ",last-col=" last-col)
       (re-frame/dispatch [:cube-test.frig-frog.events/draw-frog row col])
       (swap! *last-frog-row-col* #(identity row-col))))))
       ; (swap! *last-frog-row-col* (fn [x] row-col))))))

(reg-sub
 :frog-mode-changed
 :<- [:get-frog-mode]
 (fn [mode query-v]
   (prn "sub: frog-mode-changed: mode=" mode ", qv=" query-v)))
   ; (re-frame/dispatch [:events/jump-frog])))
