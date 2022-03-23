(ns worker-frig-frog.db
  (:require
    ; [worker-frig-frog.utils.common :as common]
    [cube-test.utils.common :as common]))

(def default-db
  {:name "re-frame"
   :abc 7
   :trains []})

(defn init-db [db]
 (common/merge-dbs db default-db))
