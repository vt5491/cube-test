(ns cube-test.frig-frog.board
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.frig-frog.tile :as ff-tile]))
   ;; Note: no longer nec.
   ; [clojure.walk :as clj-walk]))
   ;; note can't refernce events directly due to "circular dependencies"
   ; [cube-test.frig-frog.events :as events]))

(defn create-row [row-num n-cols]
  ; (prn "create-row: row-num=" row-num ", n-cols=" n-cols)
  (let [
        row-key (keyword (str "row-" row-num))
        tmp (map-indexed (fn [i x]
                           (hash-map :tile (keyword (str row-num "-" i))))
                        (range n-cols))
        tmp2 (reduce (fn [accum val]
                       (assoc accum row-key (conj (accum row-key) val)))
                     (hash-map row-key [])
                     tmp)]
    tmp2))

;; defunct
(defn init-row [row-num n-col board-accum])

(defn init-board [db]
  (let [n-rows (:n-rows db)
        n-cols (:n-cols db)]
    (reduce (fn [accum row-num]
              (let [row (create-row row-num n-cols)]
                (conj accum row)))
            []
            (range n-rows))))
