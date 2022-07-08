(ns worker-frig-frog.worker
  (:require
   [re-frame.core :as rf]))
   ; [worker-frig-frog.events :as events]))

(enable-console-print!)
(def i 0)
(declare handle-main-msg)
(declare post-db-content)

(set! js/self.onmessage handle-main-msg)

(defn say-hi []
  (prn "ff-worker: thank you, im fine dude 3")
  ; (js/postMessage "worker-hi")
  (js/postMessage (clj->js {:msg "worker-hi"})))

;; (defn post [jsObj]
;;   (when (not ff-worker)
;;     (start-worker))
;;   (.postMessage ff-worker jsObj))

;;
;; handle messages coming from the the main thread.
;;
(defn handle-main-msg [e]
  (prn "worker:handle-main-msg: e=" e)
  ; (prn "handle-main-msg: data=" (.-data e))
  (let [
        ;; data (js->clj (.-data e))
        ;; data (.parse js/JSON (js->clj (.-data e) :keywordize-keys true))
         data (js->clj (.parse js/JSON (.-data e)) :keywordize-keys true)
        ;;  e-parse (js->clj (.parse js/JSON e))
        ;;  e-parse (js->clj e)
        ;;  tmp (prn "worker: e-parse=" e-parse)
        ;;  data (get e-parse "data")
        ; msg (:msg data)
        ;; msg (get data "msg")
        msg (get data :msg)]
    (prn "handle-main-msg: data=" data)
    (prn "handle-main-msg: msg=" msg)
    ; (js-debugger)
    (case msg
      "hi"
        (say-hi)
      "print-db"
      (do
        (prn "ff-worker: dispatching print-db")
        (rf/dispatch [:worker-frig-frog.events/print-db]))
      "add-train"
      (do
        (let [
              ;; train (js->clj (get data "train") :keywordize-keys true)
              ;; train (.parse js/JSON (get data "train"))
              train (get data :train)]
              ; trains @(rf/subscribe [:trains-changed])]
          (prn "ff-worker: dispatching add-train, train2=" train)
          (set! i (+ i 1))
          (rf/dispatch-sync [:worker-frig-frog.events/add-train train])
          (prn "ff-worker: done dispatching add-train")
          (let [dmy-2 @(rf/subscribe [:trains-changed])])))
      "ping"
      (do
        (prn "ff-work: ping received")
        (js/postMessage (js-obj "msg" "pong")))
      "drop-train"
      (do
       (let [id (get data :id)]
         (prn "worker: drop-train, id=" id)
         (rf/dispatch [:worker-frig-frog.events/drop-train id])))
      "sync-db"
      (do
        (let [
              ;; db1 (rf/dispatch-sync [:worker-frig-frog.events/sync-db])
              db2 "hi"]
          ;; (prn "worker-frig-frog: sync-db: db1=" db1)
          ;; (post-db-content db1)
          ;; (post-db-content (rf/dispatch-sync [:worker-frig-frog.events/sync-db]))
          (post-db-content @re-frame.db/app-db)))
      "init-frog-2"
      (do
        (let [row (get data :row)
              col (get data :col)]
         (prn "ff-work: init-frog-2. row=" row ",col=" col)
         ;; yes, it is necessary to do a 'dispatch-sync'.
         (rf/dispatch-sync [:worker-frig-frog.events/update-frog-2 row col])
        ;;  (rf/dispatch [:worker-frig-frog.events/update-frog-2 row col])
         (let [dmy-sub @(rf/subscribe [:frog-2-changed])])))
      "move-frog-2"
      (do
        (let [x (get data :x)
              y (get data :y)]
         (prn "ff-worker: move-frog-2. x=" x, ",y=" y)
         (rf/dispatch-sync [:worker-frig-frog.events/move-frog-2 x y])
         (let [dmy-sub @(rf/subscribe [:frog-2-changed])]))))))


      ;;  (let [id (get data :id)]
      ;;    (prn "worker: drop-train, id=" id)
      ;;    (rf/dispatch [:worker-frig-frog.events/drop-train id]))))))

;;
;; Send messages back to main
;;
;; re-frame.db/app-db
(defn post-db-content [db]
  (prn "worker.ff: post-db-content: db=" db)
  ;; (prn "worker.ff: post-db-content: app-db=" @re-frame.db/app-db)
  ;; (js/postMessage (clj->js {:msg "post-db-content" :db-content (clj->js db)}))
  ;; (js/postMessage (.stringify js/JSON (clj->js {:msg "post-db-content" :db-content (clj->js db)})))
  (js/postMessage (.stringify js/JSON (clj->js {:msg "post-db-content" :db-content db}))))
  ;; (js/postMessage (js-obj)))

;; (.postMessage ff-worker (.stringify js/JSON (clj->js {:msg "add-train" :train train})))
;; Note: called by events.cljs
;; (defn draw-frog-2 [frog-2]
;;   (prn "**worker.ff: draw-frog-2: frog-2" frog-2)
;;   ;; (js/postMessage (clj->js {:msg "draw-frog-2" :frog-2 (clj->js frog-2)})))
;;   ;; (js/postMessage (.stringify js/JSON (clj->js {:msg "draw-frog-2" :frog-2 frog-2}))))
;;   ;; (js/postMessage (.stringify js/JSON (clj->js {:msg "draw-frog-2" :frog-2 (clj->js frog-2)}))))
;;   ;; (js/postMessage (js-obj "msg" "draw-frog-2" "frog-2" (.stringify js/JSON (clj->js frog-2)))))
;;   (js/postMessage (.stringify js/JSON (clj->js {:msg "draw-frog-2" :frog-2 frog-2}))))
