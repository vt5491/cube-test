(ns cube-test.frig-frog.board
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.frig-frog.tile :as ff-tile]))
   ;; Note: no longer nec.
   ; [clojure.walk :as clj-walk]))
   ;; note can't refernce events directly due to "circular dependencies"
   ; [cube-test.frig-frog.events :as events]))

(defn draw-board [db]
  ; (re-frame/dispatch [:events/draw-tile 0 0]))
  ; (let [board (:board db)]
  ;   (if (not board)))
  ; (re-frame/dispatch [:cube-test.frig-frog.events/draw-tile])
  ; (assoc db :board (ff-tile/draw 0 0)))
  ; (let [board (ff-tile/draw 0 0)]
  ;   (prn "board.draw-board: board=" board)
  ;   (assoc db :board board))
  ; (keyword (str "tile-" 7))
  (let [n-rows (:n-rows db)
        board (:board db)
        range (range n-rows)]
    (reduce (fn [a v] (assoc a (keyword (str "tile-" v)) {}))
            board
            range)))
  ; (map-indexed (fn [i v] (prn "i=" i ",v=" v)) [1 2 3])

(defn init-row [row-num n-col board-accum]
  (as-> (reduce (fn [a col](assoc a (keyword (str "tile-" row-num "-" col)) {}))
                board-accum
                (range n-col)) row
        (conj [] row)))

        ; board-final (reduce-kv (fn [a i v] (conj a (hash-map (keyword (str "row-" i)) v)))
        ;                        {}
        ;                        board-accum)))
(defn init-board [db]
  (let [board (:board db)
        ; board-accum (as-> (clj-walk/postwalk identity (map-indexed (fn [i v] (init-row i (:n-cols db) board)))))
        ;; Note: doall is not sufficent to de-lazify the result, so we have to use 'into
        board-accum (as-> (into [] (map-indexed (fn [i v] (init-row i (:n-cols db) board))
                                                (range (:n-rows db)))) b-accum
                          (reduce-kv (fn [a i v] (conj a (hash-map (keyword (str "row-" i)) v)))
                                     {}
                                     b-accum))]

    (prn "init-board board-accum=" board-accum)
    ; (prn "init-board board-final=" board-final)
    ; board-final
    board-accum))
    ; (reduce (fn [a v] ()))))

; (def bf)
;
; (defn init-board-0 [db]
;   (let [board (:board db)
;         ; board-accum (as-> (clj-walk/postwalk identity (map-indexed (fn [i v] (init-row i (:n-cols db) board)))))
;         ; board-accum (map-indexed (fn [i v] (init-row i (:n-cols db) (range (:n-rows db)) board)))
;         board-accum (into [] (map-indexed (fn [i v] (init-row i (:n-cols db) board))
;                                           (range (:n-rows db))))]
;         ; board-accum (clj-walk/postwalk identity (map-indexed (fn [i v] (init-row i (:n-cols db) board))
;
;     (prn "init-board board-accum=" board-accum)
;     (prn "init-board type board-accum=" (type board-accum))
;     ; (prn "init-board realized board-accum=" (realized? board-accum))
;     (set! bf (reduce-kv (fn [a i v] (conj a (hash-map (keyword (str "row-" i)) v)))
;                         {}
;                         board-accum))
;
;     (prn "init-board bf=" bf)
;     bf))
    ; board-accum))
