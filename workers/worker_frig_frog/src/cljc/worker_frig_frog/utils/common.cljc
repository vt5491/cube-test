;; This is for common utils that can be written in .cljc
;; For .cljs specifics utils, refer to 'worker-frig-frog.utils
(ns worker-frig-frog.utils.common)

(defn merge-dbs [db1 db2]
  "Merge two maps into one"
  (reduce #(do
             (assoc %1 (first %2) (second %2)))
          db1 db2))
