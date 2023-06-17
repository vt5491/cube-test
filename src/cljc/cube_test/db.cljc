;; this supplants the global db handler under .cljs dir.
(ns cube-test.db
   (:require
     [cube-test.utils.common :as common]))

(def default-db
  {:name "re-frame:cljc"})

(defn init-game-db [db game-db]
  (prn "cube-test.db: init-game-db-2: entered")
  (common/merge-dbs db game-db))

;; Basically, delete everything from the current db except for ':globals'
(defn reset-db [db]
  (prn "db.reset-db: db=" db)
  (prn "db.reset-db: (:globals db)=" (:globals db))
  (assoc {} :globals (:globals db)))
