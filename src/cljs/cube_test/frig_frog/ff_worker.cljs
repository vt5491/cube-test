;; Handle messages to and from the worker
(ns cube-test.frig-frog.ff-worker
  (:require
    [re-frame.core :as rf]
    [cube-test.frig-frog.train :as ff.train]
    [cube-test.frig-frog.frog-2 :as ff.frog2]
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
;; data (js->clj (.parse js/JSON (.-data e)) :keywordize-keys true)
(defn handle-ff-worker-msg [e]
  (prn "main.handle-ff-worker-msg: e=" e)
  (let [
        data (js->clj (.parse js/JSON (.-data e)) :keywordize-keys true)
        msg (get data :msg)]
    (prn "handler-ff-worker-msg: msg=" msg ",data=" data)
    (case msg
      "worker-add-train-mesh"
      (do
        (let [train (get data :train)]
          (ff.train/add-train-mesh train)))
      "worker-add-train-mesh-min"
      (do
        (let [train {:id-stem "tr-1", :length 3,
                     :init-col 7, :init-row 2,
                     :vx -0.5, :vy 0}]
          (cube-test.frig-frog.train/add-train-mesh train)))
      "pong"
      (do
        (prn "main: pong received"))
      "draw-frog-2"
      (do
        (let [frog-2 (get data :frog-2)]
          (prn "main: draw-frog-2 received, frog-2=" frog-2)
          (ff.frog2/draw-frog-2 frog-2)))
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

;;
;; train
;;
(defn post-add-train [train]
  (prn "main: about to postMessage add-train, train=" train)
  ;; following 2 work
  ; (.postMessage ff-worker (.stringify js/JSON (js-obj "msg" "add-train", "train" (clj->js train))))
  (.postMessage ff-worker (.stringify js/JSON (clj->js {:msg "add-train" :train train}))))
  ;; (.postMessage ff-worker (js-obj "msg" "add-train", "train" (js-obj "id-stem" "tr-1"))))

(defn train-stream [train-opts length]
  ; (prn "ff-worker: about to postMessage train-stream")
  (post (clj->js {:msg "add-train" :train (clj->js train-opts)})))

(defn drop-train [id]
  ; (prn "main: about to postMessage drop-train, id=" id)
  (.postMessage ff-worker (.stringify js/JSON (clj->js {:msg "drop-train" :id id}))))

;;
;; frog-2
;;
(defn init-frog-2 [row col]
  (prn "main: about to postMessage init-frog-2,")
  (.postMessage ff-worker (.stringify js/JSON (clj->js {:msg "init-frog-2" :row row :col col}))))

(defn move-frog-2 [x y]
  (prn "main: about to postMessage move-frog-2,")
  (.postMessage ff-worker (.stringify js/JSON (clj->js {:msg "move-frog-2" :x x :y y}))))
