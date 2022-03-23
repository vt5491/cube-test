(ns worker-frig-frog.main
  (:require
   [re-frame.core :as rf]
   [worker-frig-frog.utils.common :as common]
   [worker-frig-frog.db :as db]
   [worker-frig-frog.events :as events]))

(defn init []
  ; (db/init-db {:abc 7}))
  (prn "init: about to call add-ghi")
  (rf/dispatch-sync [::events/add-ghi 9]))
