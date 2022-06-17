;; Handle messages to and from the worker
(ns cube-test.frig-frog.ff-worker
  (:require
    [re-frame.core :as rf]
    [cube-test.frig-frog.train :as ff.train]
    [cube-test.base :as base]
    [cube-test.worker :as worker]))

(def ff-worker)
(declare start-worker)

(defn post [jsObj]
  (when (not ff-worker)
    (start-worker))
  (.postMessage ff-worker jsObj))

;;
;; handle messages sent by the worker to us (the main thread).
;;
(defn handle-ff-worker-msg [e]
  (prn "main.handle-ff-worker-msg: e=" e)
  (let [data (js->clj (.-data e) :keywordize-keys true)
        ; msg (get data "msg")
        msg (get data :msg)
        train (get data :train)]
    (prn "handler-ff-worker-msg: msg=" msg ",data=" data)
    (prn "handler-ff-worker-msg: train=" train)
    (prn "handler-ff-worker-msg: json.parse train=" (.parse js/JSON train))
    (prn "handler-ff-worker-msg: json.parse js->clj=" (js->clj (.parse js/JSON train) :keywordize-keys true))
    (case msg
      "worker-add-train-mesh"
      (do
        (prn "main.handle-ff-worker-msg: now adding mesh")
        (let [trn (js->clj (.parse js/JSON train) :keywordize-keys true)
              length (get-in trn [:length])
              tmp (prn "length=" length)
              ; train {:id-stem "tr-1", :length (- length 2),
              ;        :init-col 7, :init-row 2,
              ;        :vx -1, :vy 0}
              train trn
              tmp-2 (prn "train=" train)]
          ; (rf/dispatch [:cube-test.frig-frog.events/add-train-mesh train])
          (ff.train/add-train-mesh train)))
      "worker-add-train-mesh-min"
      (do
        (prn "main.handle-ff-worker-msg: now adding mesh-min")
        (let [train {:id-stem "tr-1", :length 3,
                     :init-col 7, :init-row 2,
                     :vx -0.5, :vy 0}]
          ; (rf/dispatch [:cube-test.frig-frog.events/add-train-mesh train])))
          (cube-test.frig-frog.train/add-train-mesh train)))
      "pong"
      (do
        (prn "main: pong received"))
      ;; xfer to the app-independent msg handler
      (do
        (prn "*** transfer")
        (worker/handle-worker-msg e)))))

;;
;; Send messages to the worker
;;
(defn start-worker []
  (prn "start-worker: ff-worker=" ff-worker)
  (when (not ff-worker)
    (set! ff-worker (js/Worker. "libs/workers/ff-worker/ff-worker.js"))
    (set! (.-onmessage ff-worker) handle-ff-worker-msg)
    (set! base/db-worker-thread ff-worker)))

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
  (.postMessage ff-worker (.stringify js/JSON (clj->js {:msg "print-db"}))))
  ; (.postMessage ff-worker (clj->js {:msg :print-db})))

(defn post-add-train [train]
  (prn "main: about to postMessage add-train, train=" train)
  ; (.postMessage ff-worker (js-obj "msg" "add-train", "train" (js-obj "id-stem" "tr-1" "length" 5)))
  ; (.postMessage ff-worker (js-obj "msg" "add-train", "train" (.stringify js/JSON (clj->js train))))
  ; (.postMessage ff-worker (js-obj "msg" "add-train", "train" (.stringify js/JSON (clj->js train))))
  ;; following 2 work
  ; (.postMessage ff-worker (.stringify js/JSON (js-obj "msg" "add-train", "train" (clj->js train))))
  (.postMessage ff-worker (.stringify js/JSON (clj->js {:msg "add-train" :train train}))))
  ;; no work
  ; (.postMessage ff-worker (clj->js {:msg "add-train" :train train})))

(defn train-stream [train-opts length]
  (prn "ff-worker: about to postMessage train-stream")
  ; (.postMessage ff-worker (js-obj "msg" "add-train" "train-opts" train-opts "length" length))
  ; (.postMessage ff-worker (clj->js {:msg "add-train" :train (clj->js train-opts)}))
  (post (clj->js {:msg "add-train" :train (clj->js train-opts)})))

(defn drop-train [id]
  (prn "main: about to postMessage drop-train, id=" id)
  (.postMessage ff-worker (.stringify js/JSON (clj->js {:msg "drop-train" :id id}))))
