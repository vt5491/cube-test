;; subs is refer to many, referred by few
(ns cube-test.frig-frog.subs
  (:require
   [re-frame.core :as rf :refer [reg-sub subscribe]]
   [cube-test.frig-frog.events :as ff-events]
   [cube-test.frig-frog.board :as ff-board]
   ; [cube-test.frig-frog.game :as ff.game]
   [cube-test.frig-frog.scene-l1 :as scene-l1]
   [cube-test.utils :as utils]
   [cube-test.utils.common :as common]
   [clojure.data :as clj-data]
   [cube-test.macros :as macros]))

; (def ^:dynamic *last-board* (atom nil))
(def ^:dynamic *last-frog-row-col* (atom nil))
(def ^:dynamic *last-trains* (atom nil))

;;
;; extractors
;;
(reg-sub
 :get-btm-board
 (fn [db _]
   (:btm-board db)))

(reg-sub
 :get-top-board
 (fn [db _]
   (:top-board db)))

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
   (:dev-mode db)))

(reg-sub
 :get-trains
 (fn [db _]
   (:trains db)))

(reg-sub
 :get-quanta-width
 (fn [db _]
   (:quanta-width db)))

(reg-sub
 :get-n-rows
 (fn [db _]
   (:n-rows db)))

(reg-sub
 :get-n-cols
 (fn [db _]
   (:n-cols db)))

;;
;; computations
;;

(defn board-changed [board query-v prfx]
  (when (and board (not (empty? board)))
    (let [prfx (-> query-v second (:prfx))
          last-board (ff-board/get-last-board prfx)
          diff-full (clj-data/diff board @last-board)
          diff-a (first diff-full)
          diff-b (second diff-full)
          diff-c (nth diff-full 2)]
      (let [tile-deltas (ff-board/parse-delta-2 diff-a)]
        (doseq [{:keys [row col abc state]} tile-deltas]
          (rf/dispatch [::ff-events/draw-tile row col prfx])))
      ;;TODO: have a last-btm-board, last-top-board?
      (swap! last-board (fn [x] board)))))

(reg-sub
 :btm-board-changed
 :<- [:get-btm-board]
 board-changed)

(reg-sub
 :top-board-changed
 :<- [:get-top-board]
 board-changed)

(reg-sub
 :frog-changed
 :<- [:get-frog :col]
 (fn [frog query-v]))

(reg-sub
 :frog-row-changed
 :<- [:get-frog-row]
 (fn [row query-v]))

;; main frog pos handler
(reg-sub
 :frog-row-col-changed
 :<- [:get-frog-row-col]
 (fn [row-col query-v]
   (macros/when-let* [row (:row row-col)
                      col (:col row-col)]
     (let [
           diff-full (clj-data/diff row-col @*last-frog-row-col*)
           diff-a (first diff-full)
           diff-b (second diff-full)
           last-row (or (:row diff-b) row)
           last-col (or (:col diff-b) col)]
       (rf/dispatch [::ff-events/draw-frog row col])
       (swap! *last-frog-row-col* #(identity row-col))))))

(reg-sub
 :frog-mode-changed
 :<- [:get-frog-mode]
 (fn [mode query-v]))

(reg-sub
 :dev-mode-changed
 :<- [:get-dev-mode]
 (fn [mode query-v]
   ;; handle the logic in the dispatch, since we need access to other parms in the db
   ;; and sub handler only has access to the db delta.
   (rf/dispatch [::ff-events/dev-mode-changed mode])))

(reg-sub
 :trains-changed
 :<- [:get-trains]
 (fn [trains query-v]
   (let [diff-full (clj-data/diff trains @*last-trains*)
         diff-a (first diff-full)
         diff-b (second diff-full)
         diff-a-2 (first (clj-data/diff @*last-trains* trains))
         diff-diff-a (first (clj-data/diff diff-a diff-b))
         ;; this gives the diff upon a drop
         ;; e.g drop n=2 (third element) gives:
         diff-diff-b (second (clj-data/diff diff-a diff-b))]
        (when (> (count trains) (count @*last-trains*))
          (when diff-a
            (do
              ;; add-zone
              (doall (map #(when %1
                             (rf/dispatch [::ff-events/add-train-mesh %1]))
                           diff-a)))))
        (when (< (count trains) (count @*last-trains*))
          (when diff-b
            (doall (map #(when %1
                           (rf/dispatch [::ff-events/drop-train-mesh %1]))
                        diff-b))))
        (when (= (count trains) (count @*last-trains*))
          (when diff-a
            (let [idxs-raw (map-indexed (fn [i x] (if (not (nil? x)) i x)) diff-a)
                  idxs (doall (filter #(some? %1) idxs-raw))]
              (doall (map #(when %1
                             (rf/dispatch [::ff-events/update-train-mesh-by-idx %1]))
                          idxs))))))
   (swap! *last-trains* (fn [x] trains))))

(reg-sub
 :quanta-width-changed
 :<- [:get-quanta-width]
 (fn [quanta-width query-v]
   (rf/dispatch [::ff-events/update-quanta-width quanta-width])))

(reg-sub
 :n-rows-changed
 :<- [:get-n-rows]
 (fn [n-rows query-v]
   (when (not (nil? n-rows))
     (rf/dispatch [::ff-events/update-n-rows n-rows]))))

(reg-sub
 :n-cols-changed
 :<- [:get-n-cols]
 (fn [n-cols query-v]
   (when (not (nil? n-cols))
     (rf/dispatch [::ff-events/update-n-cols n-cols]))))
