(ns worker-frig-frog.events
  (:require
   ; [re-frame.core :as rf]
   [re-frame.core :refer [dispatch reg-event-db reg-event-fx reg-fx after ] :as rf]
   [worker-frig-frog.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [worker-frig-frog.worker :as worker]))


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

;; train
; (defn init-events []
;   (prn "wff.event: init-events entered"))
; (let [dmy @(rf/subscribe [:trains-changed])]
(defn heavy-cpu []
   (prn "worker: heavy-cpu: starting")
   (let [r 0
         big-num (js/Math.pow 10 9.5)]
      (prn "big-num=" big-num)
      (doall (dotimes [i big-num
                              (+ r (* (js/Math.atan i) (js/Math.tan i)))])))
   (prn "worker: heavy-cpu: done"))

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

(rf/reg-event-fx
 ::add-train-mesh
 (fn [cofx [_ train]]
   ; (js/console.debug "wff-events.add-train-mesh entered ")
   (prn "wff-events.add-train-mesh entered, train=" train)
   ; (js/postMessage "worker-add-train-mesh")
   (let [id-stem (:id-stem train)]
     (js/postMessage (js-obj "msg" "worker-add-train-mesh" "id-stem" id-stem)))
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
