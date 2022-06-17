(ns worker-frig-frog.train
 (:require
  [re-frame.core :as rf]))

(defn drop-train [id trains]
  (vec (remove #(= (:id-stem %1) id) trains)))
  ;; (let [trains (:trains db)]
  ;;   (remove #(= (:id %1) "tr-2") trains)))