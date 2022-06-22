(ns worker-frig-frog.subs
  (:require
   [re-frame.core :as rf :refer [reg-sub subscribe]]
   [worker-frig-frog.events :as wff-events]
   [clojure.data :as clj-data]))

; (re-frame/reg-sub
;  ::name
;  (fn [db]
;    (:name db)))

(def ^:dynamic *last-trains* (atom nil))
;;
;; extractors
;;
(reg-sub
 :get-trains
 (fn [db _]
   (:trains db)))

(reg-sub
 :get-frog-2
 (fn [db _]
   (:frog-2 db)))
;;
;; computations
;;
(reg-sub
 :trains-changed
 :<- [:get-trains]
 (fn [trains query-v]
   (prn "worker.sub: trains-changed: trains=" trains ", qv=" query-v)
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
        (prn "worker:trains-changed: diff-a=" diff-a)
        (prn "worker:trains-changed: diff-b=" diff-b)
        (prn "worker:cnt new-trains=" (count trains) ", cnt last-trains=" (count @*last-trains*))
        (prn "worker:trains-changed: diff-diff-a=" diff-diff-a)
        (prn "worker:trains-changed: diff-diff-b=" diff-diff-b)
        (when (> (count trains) (count @*last-trains*))
          (when diff-a
            (do
              ;; add-zone
              (doall (map #(when %1
                             (prn "worker:%1=" %1 ",hi2")
                             (prn "worker: js->clj %1=" (js->clj %1 :keywordize-keys true))
                            ;;  (prn "worker: json %1=" (.parse js/JSON %1))
                            ;;  (rf/dispatch [::wff-events/add-train-mesh %1])
                            ;;  (rf/dispatch [::wff-events/add-train-mesh (js->clj %1 :keywordize-keys false)])
                             (let [h (zipmap (map (fn [x] (keyword x)) (keys %1)) (vals %1))]
                               (prn "worker: h=" h)
                               (rf/dispatch [::wff-events/add-train-mesh h])))
                            ;;  (rf/dispatch [::wff-events/add-train-mesh-min %1]))
                           diff-a))))))
        ; (when (< (count trains) (count @*last-trains*))
        ;   (when diff-b
        ;     (prn "in drop zone")
        ;     (doall (map #(when %1
        ;                    (prn "about to dispatch drop")
        ;                    (rf/dispatch [::ff-events/drop-train-mesh %1]))
        ;                 diff-b))))
        ; (when (= (count trains) (count @*last-trains*))
        ;   (when diff-a
        ;     (prn "in alter zone")
        ;     (let [idxs-raw (map-indexed (fn [i x] (if (not (nil? x)) i x)) diff-a)
        ;           idxs (doall (filter #(some? %1) idxs-raw))]
        ;       (prn "subs.idxs-raw=" idxs-raw)
        ;       (prn "subs.idxs=" idxs)
        ;       (doall (map #(when %1
        ;                      (prn "%1=" %1)
        ;                      (rf/dispatch [::ff-events/update-train-mesh-by-idx %1]))
        ;                   idxs)))))
        ; (prn "bye"))
   (swap! *last-trains* (fn [x] trains))))

(reg-sub
 :frog-2-changed
 :<- [:get-frog-2]
 (fn [frog-2 query-v]
   (prn "worker.sub: frog-2-changed: frog-2" frog-2 ", qv=" query-v)
   (let [row (:row frog-2)
         col (:col frog-2)]
     (rf/dispatch [::wff-events/draw-frog-2 {:row row :col col}]))))
    ;;  (rf/dispatch [::wff-events/draw-frog-2 row col]))))