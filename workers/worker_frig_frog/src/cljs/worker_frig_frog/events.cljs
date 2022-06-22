(ns worker-frig-frog.events
  (:require
   ; [re-frame.core :as rf]
   [re-frame.core :refer [dispatch reg-event-db reg-event-fx reg-fx after ] :as rf]
   [worker-frig-frog.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [worker-frig-frog.worker :as worker]
   [worker-frig-frog.train :as train]))


;; general
(rf/reg-event-db
 ::initialize-db
 ; (fn-traced [_ _])
 (fn-traced [db _]
   (prn "worker-ff:events: initialize-db: db/default-db=" db/default-db)
   ; db/default-db
   (db/init-db db)))

(rf/reg-event-fx
 ::dummy
 (fn [cofx _]
   (js/console.debug "worker-frig-frog.dummy entered")
   {
    :db (:db cofx)}))

(reg-event-db
 ::add-def
 (fn [db [_ val]]
   (prn "events.add-def: db=" db ",val=" val)
   (prn "hi from cube-test build")
   (assoc db :def val)))

(reg-event-db
 ::add-ghi
 (fn [db [_ val]]
   ; (prn "events.add-def: db=" db ",val=" val)
   (assoc db :ghi val)))

(reg-event-db
 ::print-db
 (fn [db [_]]
   (prn "worker-events.print-db: db=" db)
   ; (js-debugger)
   ; (prn "worker-events.print-db: re_frame.db=" js/self.re_frame.db.app_db.state)
   db))

(reg-event-db
 ::sync-db
 (fn [db [_]]
   (prn "worker-events.sync: db=" db)
   db))

;; train
; (defn init-events []
;   (prn "wff.event: init-events entered"))
; (let [dmy @(rf/subscribe [:trains-changed])]
(reg-event-db
 ::drop-train
 (fn [db [_ id]]
   (let [trains (:trains db)
         new-trains (train/drop-train id trains)]
     (assoc-in db [:trains] new-trains))))

(reg-event-db
 ::add-train
 ; (fn [db [_ row col]])
 (fn [db [_ train]]
   ; (prn "wff.events.add-train: row=" row ",col=" col)
   (prn "wff.events.add-train: train=" train)
   ; (heavy-cpu)
   (let [
         ; dmy @(rf/subscribe [:trains-changed])
         trains (:trains db)]
     ; (assoc db :trains (conj trains {:init-row row :init-col col}))
     (assoc db :trains (conj trains train)))))

;; (.stringify js/JSON (clj->js {:key "value"}))
(rf/reg-event-fx
 ::add-train-mesh
 (fn [cofx [_ train]]
   ; (js/console.debug "wff-events.add-train-mesh entered ")
   (prn "wff-events.add-train-mesh entered, train=" train)
   (prn "wff-events.add-train-mesh entered, js-obj train=" (js-obj "train" train))
   ; (js/postMessage "worker-add-train-mesh")
   (let [id-stem (:id-stem train)]
    ;;  (js/postMessage (js-obj "msg" "worker-add-train-mesh" 
    ;;                          "abc" 7
    ;;                          "id-stem" id-stem
    ;;                          "train" (.stringify js/JSON (clj->js train))))
     (js/postMessage (.stringify js/JSON (clj->js {:msg "worker-add-train-mesh" :train train}))))
   {
    :db (:db cofx)}))

(rf/reg-event-fx
 ::add-train-mesh-min
 (fn [cofx [_ train]]
   (prn "wff-events.add-train-mesh-min entered, train=" train)
   ; (js/postMessage "worker-add-train-mesh")
   (let [id-stem (:id-stem train)]
     (js/postMessage (js-obj "msg" "worker-add-train-mesh-min" "id-stem" id-stem)))
   {
    :db (:db cofx)}))

;;
;; frog-2
;;
(reg-event-db
 ::update-frog-2
 (fn [db [_ row col]]
   (prn "events.update-frog-2: db=" db ",row" row ",col=" col)
   (assoc db :frog-2 {:row row :col col})))

(reg-event-db
 ::move-frog-2
 (fn [db [_ x y]]
   (prn "events.move-frog-2: db=" db ",x=" x ",y=" y)
   (let [frog-2 (:frog-2 db)
         row (:row frog-2)
         col (:col frog-2)]
     ;;  (assoc db :frog-2 {:row row :col col})
     (prn "worker.events: row=" row ",col=" col)
     (-> (assoc-in db [:frog-2 :row] (+ row y))
         (assoc-in [:frog-2 :col] (+ col x))))))

(rf/reg-event-fx
 ::draw-frog-2
;;  (fn [cofx [_ row col]]
 (fn [cofx [_ frog-2]]
  ;;  (prn "wff-events.draw-frog-2 entered, row=" row ",col=" col)
   (prn "wff-events.draw-frog-2 entered, frog-2" frog-2)
  ;;  (let [id-stem (:id-stem train)])
  ;;  (js/postMessage (js-obj "msg" "draw-frog-2" 
  ;;                        "row" row))
  ;;  (js/postMessage (.stringify js/JSON (clj->js {:msg "draw-frog-2" :frog-2 {:row row :col col}})))
   (js/postMessage (.stringify js/JSON (clj->js {:msg "draw-frog-2" :frog-2 frog-2})))))

;; misc
(defn heavy-cpu []
   (prn "worker: heavy-cpu: starting")
   (let [r 0
         big-num (js/Math.pow 10 9.5)]
      (prn "big-num=" big-num)
      (doall (dotimes [i big-num
                              (+ r (* (js/Math.atan i) (js/Math.tan i)))])))
   (prn "worker: heavy-cpu: done"))
