;; events is refer to many 
(ns cube-test.lvs.db
  (:require
   [re-frame.core :refer [reg-event-db reg-event-fx reg-fx after ] :as rf]
   [cube-test.lvs.game :as lvs.game]
   [cube-test.utils :as utils]))

(def default-db
  {
   :scenes {:reflect {:path "models/space_portal/" :fn "space_portal.glb" :loaded false}
            :lvs-main {:path "models/hemisferic/" :fn "hemisferic.glb" :loaded false}}
  ;;  :default-scene :lvs-main
   :default-scene :lvs-reflect})

(defn init-db [db]
  ; (utils/merge-dbs db cube-test.msg-cube.spec.db/default-db)
  (utils/merge-dbs db default-db))