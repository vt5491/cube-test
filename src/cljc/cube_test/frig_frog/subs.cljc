(ns cube-test.frig-frog.subs
  (:require
   [re-frame.core :as rf :refer [reg-sub subscribe]]
   [cube-test.frig-frog.events :as ff-events]
   [cube-test.frig-frog.board :as ff-board]
   [cube-test.frig-frog.scene-l1 :as scene-l1]
   [cube-test.utils :as utils]
   [clojure.data :as clj-data]
   [cube-test.macros :as macros]))

(def ^:dynamic *last-board* (atom nil))
(def ^:dynamic *last-frog-row-col* (atom nil))
(def ^:dynamic *last-trains* (atom nil))
; (def ^:dynamic *last-board* (atom ()))
; (def ^:dynamic *last-board* (atom {}))

;;
;; extractors
;;
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

(reg-sub
 :get-dev-mode
 (fn [db _]
   ; (get-in db [:dev-mode])
   (:dev-mode db)))

(reg-sub
 :get-trains
 (fn [db _]
   (:trains db)))

;;
;; computations
;;
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
   ; (prn "subs: *last-board*=" @*last-board*)
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
         ; (rf/dispatch [:cube-test.frig-frog.ff-events/draw-tile row col])
         (rf/dispatch [::ff-events/draw-tile row col]))))
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
       ; (rf/dispatch [:cube-test.frig-frog.ff-events/draw-frog row col])
       (rf/dispatch [::ff-events/draw-frog row col])
       (swap! *last-frog-row-col* #(identity row-col))))))
       ; (swap! *last-frog-row-col* (fn [x] row-col))))))

(reg-sub
 :frog-mode-changed
 :<- [:get-frog-mode]
 (fn [mode query-v]
   (prn "sub: frog-mode-changed: mode=" mode ", qv=" query-v)))
   ; (re-frame/dispatch [:events/jump-frog])))

(reg-sub
 :dev-mode-changed
 :<- [:get-dev-mode]
 (fn [mode query-v]
   (prn "sub: dev-mode-changed: mode=" mode ", qv=" query-v)
   ; (re-frame/dispatch [:cube-test.frig-frog.events/echo-db mode])
   ; (re-frame/dispatch [:cube-test.frig-frog.events/echo-fx mode])
   ;; handle the logic in the dispatch, since we need access to other parms in the db
   ;; and sub handler only has access to the db delta.
   ; (rf/dispatch [:cube-test.frig-frog.ff-events/dev-mode-changed mode])
   (rf/dispatch [::ff-events/dev-mode-changed mode])))

(reg-sub
 :trains-changed
 :<- [:get-trains]
 (fn [trains query-v]
   (prn "sub: trains-changed: trains=" trains ", qv=" query-v)
   (let [diff-full (clj-data/diff trains @*last-trains*)
         diff-a (first diff-full)
         diff-b (second diff-full)
         diff-a-2 (first (clj-data/diff @*last-trains* trains))
         diff-diff-a (first (clj-data/diff diff-a diff-b))
         ;; this gives the diff upon a drop
         ;; e.g drop n=2 (third element) gives:
         ; [nil nil {:init-row 5, :vx 1, :init-col 0, :id :tr-2, :length 2} nil {:init-row 4, :vx -1, :vy 0, :init-col 7, :id :tr-1, :length 1}]
         ; when count of last_trains > count trains
         diff-diff-b (second (clj-data/diff diff-a diff-b))]
        ; (prn "trains-changed: diff-full=" diff-full)
        (prn "trains-changed: diff-a=" diff-a)
        (prn "trains-changed: diff-b=" diff-b)
        (prn "cnt new-trains=" (count trains) ", cnt last-trains=" (count @*last-trains*))
        (prn "trains-changed: diff-diff-a=" diff-diff-a)
        (prn "trains-changed: diff-diff-b=" diff-diff-b)
        ; (prn (filter (fn [x] (even? x)) [4 5 6 7 8]))
        ; (prn (map (fn [x] (prn "x=" x)) [4 5 6]))
        (when (> (count trains) (count @*last-trains*))
          (when diff-a
            (do
              ; (prn "subs: about to call add-train-mesh: diff-a=" diff-a ",count diff-a=" (count diff-a))
              ; (prn (map (fn [x]
              ;             (prn "x=" x)
              ;             x)
              ;           [1 2 3]))
              (doall (map #(when %1
                             (prn "%1=" %1)
                             (rf/dispatch [::ff-events/add-train-mesh %1]))
                           diff-a)))))
        (when (< (count trains) (count @*last-trains*))
          (when diff-b
            (prn "in drop zone")
            (doall (map #(when %1
                           (prn "about to dispatch drop")
                           (rf/dispatch [::ff-events/drop-train-mesh %1]))
                        diff-b))))
        (when (= (count trains) (count @*last-trains*))
          (when diff-a
            (prn "in alter zone")
            (let [idxs-raw (map-indexed (fn [i x] (if (not (nil? x)) i x)) diff-a)
                  idxs (doall (filter #(some? %1) idxs-raw))]
              (prn "subs.idxs-raw=" idxs-raw)
              (prn "subs.idxs=" idxs)
              (doall (map #(when %1
                             (prn "%1=" %1)
                             (rf/dispatch [::ff-events/update-train-mesh-by-idx %1]))
                          idxs)))))
        (prn "bye"))
        ; (prn "trains-changed: diff-a-2=" diff-a-2))
   (swap! *last-trains* (fn [x] trains))))
