;; defunct
(ns cube-test.frig-frog.demo-workers-setup-cljs
  (:require
    [re-frame.core :as rf]))

(def w)

(defn startWorker []
  (when (not w)
    (set! w (js/Worker. "libs/worker_simple_2_release/worker_simple_2.js"))
    (prn "worker-setup: w=" w))
  (set! (.-onmessage w) (fn [event]
                          ; (prn "i-cljs=" (.-data event))
                          (let [data (.-data event)]
                            (when (= data "worker-hi")
                              (prn "hello worker. Im fine too, thanks for asking"))))))

(defn stopWorker []
  (.terminate w)
  (set! w nil))

(defn post-hi []
  (prn "main: about to postMessage hi")
  (.postMessage w "hi"))

(def w2)

(defn post-hi-2 []
  (prn "main: about to postMessage hi")
  (.postMessage w2 (clj->js {:msg "hi"})))

(defn handle-worker-msg [e]
  (prn "main.handle-worker-msg: e=" e)
  (let [data (js->clj (.-data e))
        msg (get data "msg")]
    (prn "handler-worker-msg: msg=" msg)
    (case msg
      "worker-add-train-mesh"
      (do
        (prn "main.handle-worker-msg: now adding mesh")
        (let [train {:id-stem "tr-1", :length 1,
                     :init-col 7, :init-row 2,
                     :vx -1, :vy 0}]
          (rf/dispatch [:cube-test.frig-frog.events/add-train-mesh train])))
      "worker-add-train-mesh-min"
      (do
        (prn "main.handle-worker-msg: now adding mesh-min")
        (let [train {:id-stem "tr-1", :length 1,
                     :init-col 7, :init-row 2,
                     :vx -0.5, :vy 0}]
          (cube-test.frig-frog.train/add-train-mesh train)))
      "pong"
      (do
        (prn "main: pong received"))
      "worker-hi"
      (prn "oh, thats good to know"))))

;;;
;;; Worker 2
;;;
(defn startWorker2 []
  (when (not w2)
    (set! w2 (js/Worker. "libs/worker_frig_frog/app.js")))
  (set! (.-onmessage w2) handle-worker-msg))

(defn stopWorker2 []
  (prn "stopWorker2: w2=" w2)
  (.terminate w2)
  (set! w2 nil))

(defn print-db []
  (prn "main: about to postMessage print-db")
  (.postMessage w2 (clj->js {:msg "print-db"})))

(defn post-add-train []
  (prn "demo-worker.main: about to postMessage add-train")
  (.postMessage w2 (js-obj "msg" "add-train", "train" (js-obj "id-stem" "tr-1"))))

(defn post-ping []
  (prn "main: about to postMessage post-ping")
  (.postMessage w2 (js-obj "msg" "ping")))
