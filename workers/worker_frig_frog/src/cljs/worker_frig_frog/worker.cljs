(ns worker-frig-frog.worker
  (:require
   [re-frame.core :as rf]))
   ; [worker-frig-frog.events :as events]))

(enable-console-print!)
(def i 0)

(defn say-hi []
  (prn "ff-worker: thank you, im fine")
  (js/postMessage "worker-hi"))

(defn handle-main-msg [e]
  ; (prn "handle-main-msg: data=" (.-data e))
  (let [data (js->clj (.-data e))
        ; msg (:msg data)
        msg (get data "msg")]
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
        (let [train (get data "train")]
              ; trains @(rf/subscribe [:trains-changed])]
          (prn "ff-worker: dispatching add-train, train=" train)
          (set! i (+ i 1))
          (rf/dispatch-sync [:worker-frig-frog.events/add-train train])
          (prn "ff-worker: done dispatching add-train")
          (let [dmy-2 @(rf/subscribe [:trains-changed])])))
      "ping"
      (do
        (prn "ff-work: ping received")
        (js/postMessage (js-obj "msg" "pong"))))))

; (set! js/onmessage (fn [e]))
; (set! js/self.onmessage (fn [e]
;                           (println "hi from ff-worker")
;                           (print "e=" e)
;                           (print "e.data=" (.-data e))
;                           (let [data (.-data e)]
;                             (when (= data "hi")
;                               (say-hi)))))

(prn "ff-worker: now setting onmessage")
(set! js/self.onmessage handle-main-msg)

; (reg-event-fx
;   ::add-train
;   (fn [cofx [_ opts]]
;     (let [db (:db cofx)
;           interval-id (js/setInterval #(re-frame/dispatch [::init-train opts] ) 2000)
;           ; interval-id (js/setInterval #(ff.train/add-train-mesh opts) 2000)
;           tmp (prn "hi-b")]
;       (prn "interval-id=" interval-id)
;       (utils/sleep #(js/clearInterval interval-id) 10000))
;     {
;      :db (:db cofx)}))
