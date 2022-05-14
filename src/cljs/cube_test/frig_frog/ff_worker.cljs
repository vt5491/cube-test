(ns cube-test.frig-frog.ff-worker
  (:require
    [re-frame.core :as rf]))

(def ff-worker)
(declare start-worker)

; (defn post [worker jsObj]
;   (when (not ff-worker)
;     (start-worker))
;   (.postMessage worker jsObj))

(defn post [jsObj]
  (when (not ff-worker)
    (start-worker))
  (.postMessage ff-worker jsObj))
; (defn post [worker jsObj]
;   (prn "hi")
;   (.pos))
;;
;; handle messages sent by the worker to us (the main thread).
;;
(defn handle-ff-worker-msg [e]
  (prn "main.handle-ff-worker-msg: e=" e)
  (let [data (js->clj (.-data e))
        msg (get data "msg")]
    (prn "handler-ff-worker-msg: msg=" msg)
    (case msg
      "worker-add-train-mesh"
      (do
        (prn "main.handle-ff-worker-msg: now adding mesh")
        (let [train {:id-stem "tr-1", :length 1,
                     :init-col 7, :init-row 2,
                     :vx -1, :vy 0}]
          (rf/dispatch [:cube-test.frig-frog.events/add-train-mesh train])))
      "worker-add-train-mesh-min"
      (do
        (prn "main.handle-ff-worker-msg: now adding mesh-min")
        (let [train {:id-stem "tr-1", :length 1,
                     :init-col 7, :init-row 2,
                     :vx -0.5, :vy 0}]
          ; (rf/dispatch [:cube-test.frig-frog.events/add-train-mesh train])))
          (cube-test.frig-frog.train/add-train-mesh train)))
      "pong"
      (do
        (prn "main: pong received")))))

;;
;; Send messages to the worker
;;
(defn start-worker []
  (prn "start-worker: ff-worker=" ff-worker)
  (when (not ff-worker)
    (set! ff-worker (js/Worker. "libs/workers/ff-worker/ff-worker.js")))
  (set! (.-onmessage ff-worker) handle-ff-worker-msg))

(defn stop-worker []
  (prn "stop-worker: ff-worker=" ff-worker)
  (.terminate ff-worker)
  (set! ff-worker nil))

(defn ping []
  (prn "ff-worker: about to postMessage post-ping")
  ; (.postMessage ff-worker (js-obj "msg" "ping"))
  (post (js-obj "msg" "ping")))

(defn print-db []
  (prn "main: about to postMessage print-db")
  (.postMessage ff-worker (clj->js {:msg "print-db"})))

(defn train-stream [train-opts length]
  (prn "ff-worker: about to postMessage train-stream")
  ; (.postMessage ff-worker (js-obj "msg" "add-train" "train-opts" train-opts "length" length))
  ; (.postMessage ff-worker (clj->js {:msg "add-train" :train (clj->js train-opts)}))
  (post (clj->js {:msg "add-train" :train (clj->js train-opts)})))
