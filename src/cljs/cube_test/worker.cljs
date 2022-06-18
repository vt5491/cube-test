;; app-independent interface to workers
(ns cube-test.worker
  (:require
    [re-frame.core :as rf]))
    ;; [cube-test.events :as events]))
    ;; [cube-test.events :refer [update-db]]))

;;
;; handle messages sent by the worker to us (the main thread).
;;
(defn handle-worker-msg [e]
  (prn "main.handle-worker-msg: e=" e)
  (let [data (js->clj (.-data e) :keywordize-keys true)
        msg (get data :msg)]
    (prn "main.handle-worker-msg: data" data)
    (prn "main.handle-worker-msg: msg=" msg)
    (case msg
      "post-db-content"
      (do
        (let [db-content (get data :db-content)]
          (prn "db-content=" db-content)
          ;; (cube-test.events.update-db :worker-ff db-content)
          ;; (js-debugger)
          ;; (rf/dispatch [:cube-test.events/update-db :worker-ff db-content])
          (rf/dispatch [:update-db :worker-ff db-content]))))))
          ;; (rf/dispatch [:events/update-db :worker-ff db-content]))))))
          ;; (rf/dispatch [:cube-test.frig-frog.events/toggle-dev-mode])
          ;; (rf/dispatch [:cube-test.events/print-hi]))))))

;;
;; Send message to the worker
;;
(defn sync-db [worker-thread]
  (prn "main: about to postMessage sync-db, worker-thread=" worker-thread)
  ; (.postMessage ff-worker (.stringify js/JSON (clj->js {:msg "drop-train" :id id})))
  (.postMessage worker-thread (.stringify js/JSON (clj->js {:msg "sync-db"}))))
