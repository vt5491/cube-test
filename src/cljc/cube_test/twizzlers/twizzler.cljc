(ns cube-test.twizzlers.twizzler
  (:require
   [re-frame.core :as re-frame]))

; (assoc m :a (conj (m :a) 2))
;; fully qualified
; (defn add-twizzler [db]
;   (let [id (count (db :cube-test.twizzlers.db/twizzlers))
;         ; new-twiz {:id id}
;         new-twiz {:cube-test.twizzlers.db/id id}]
;     (prn "add-twizzler: count=" (count (db :cube-test.twizzlers.db/twizzlers)))
;     (assoc db :cube-test.twizzlers.db/twizzlers
;            (conj (db :cube-test.twizzlers.db/twizzlers) new-twiz))))
;            ; (conj (db :twizzlers) new-twiz))))

(defn add-twizzler [db]
  (let [id (count (db :twizzlers))
        new-twiz {:cube-test.twizzlers.db/id id}]
    (prn "add-twizzler: count=" (count (db :twizzlers)))
    (assoc db :twizzlers
           (conj (db :twizzlers) new-twiz))))
