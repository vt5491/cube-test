(ns cube-test.utils)
  ; (:require))
   ;; Note: get circulard dep. warning if you include 'cube-test.core'
   ; [cube-test.core :as re-frame]))
   ; [cube-test.base :as base]))

; (defn create-fps-panel [])
;; Convert ":17" to 17, for example
(defn kw-to-int [kw]
  (-> (re-find #"^:(\d{1,3})" (str kw)) (nth 1) (js/parseInt)))

; (reduce #(do
;            (println "%1=" %1 ", %2=" %2)
;            (assoc %1 (first %2) (second %2)))
;          ddb g-db)
(defn merge-dbs [db1 db2]
  "Merge two maps into one"
  (reduce #(do
             (assoc %1 (first %2) (second %2)))
          db1 db2))
