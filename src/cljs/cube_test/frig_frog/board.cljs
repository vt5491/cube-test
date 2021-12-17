(ns cube-test.frig-frog.board
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.frig-frog.tile :as ff-tile]))
   ;; Note: no longer nec.
   ; [clojure.walk :as clj-walk]))
   ;; note can't refernce events directly due to "circular dependencies"
   ; [cube-test.frig-frog.events :as events]))

;; defunct -- use init-board instead
; (defn draw-board [db]
;   ; (re-frame/dispatch [:events/draw-tile 0 0]))
;   ; (let [board (:board db)]
;   ;   (if (not board)))
;   ; (re-frame/dispatch [:cube-test.frig-frog.events/draw-tile])
;   ; (assoc db :board (ff-tile/draw 0 0)))
;   ; (let [board (ff-tile/draw 0 0)]
;   ;   (prn "board.draw-board: board=" board)
;   ;   (assoc db :board board))
;   ; (keyword (str "tile-" 7))
;   (let [n-rows (:n-rows db)
;         board (:board db)
;         range (range n-rows)]
;     (reduce (fn [a v] (assoc a (keyword (str "tile-" v)) {}))
;             board
;             range)))
;   ; (map-indexed (fn [i v] (prn "i=" i ",v=" v)) [1 2 3])

; (defn init-row [row-num n-col board-accum]
;   (prn "init-row: row-num=" row-num ", n-col=" n-col ",board-accum=" board-accum)
;   (as-> (reduce (fn [a col](assoc a (keyword (str "tile-" row-num "-" col)) {}))
;                 board-accum
;                 (range n-col)) row
;         (conj [] row)))
;
; (defn init-board [db]
;   (let [board (:board db)
;         ;; Note: doall is not sufficent to de-lazify the result, so we have to use 'into
;         board-accum (as-> (into [] (map-indexed (fn [i v] (init-row i (:n-cols db) board))
;                                                 (range (:n-rows db)))) b-accum
;                           (reduce-kv (fn [a i v] (conj a (hash-map (keyword (str "row-" i)) v)))
;                                      {}
;                                      b-accum))]
;
;     (prn "init-board board-accum=" board-accum)
;     board-accum))


(defn create-row [row-num n-cols]
  ; (prn "create-row: row-num=" row-num ", n-cols=" n-cols)
  (let [
        row-key (keyword (str "row-" row-num))
        ; col-arr []
        ; row-hash (hash-map row-key col-arr)
        ; tmp (into [] (map-indexed (fn [i x] (hash-map :tile (keyword (str row-num)) (keyword (str row-num "-" i))))))
        ; tmp (into (vector) (map-indexed (fn [i x])))
        tmp (map-indexed (fn [i x]
                           ; (prn "mi: i=" i ",x=" x)
                           (hash-map :tile (keyword (str row-num "-" i))))
                        (range n-cols))
        ; tmp2 (assoc row-hash row-key tmp)
        ; tmp2 (into (vector) (map (fn [x] (conj (row-key row-hash) x))
        ;                          tmp))]
        ; tmp2 (reduce (fn [accum val] (conj (accum row-key) val)))
        tmp2 (reduce (fn [accum val]
                       ; (prn "accum=" accum ",val=" val)
                       (assoc accum row-key (conj (accum row-key) val)))
                     ; row-hash
                     (hash-map row-key [])
                     tmp)]
        ; tmp2 (map (fn [x] (conj))
        ;           tmp)]
        ; tmp (reduce (fn [accum col]
        ;               (assoc))
        ;             row-hash
        ;             (range n-col))]
        ; tmp (as->
        ;       {(keyword (str "row-" row-num))} tile
        ;           (reduce (fn [accum col]
        ;                     ; (assoc accum (keyword (str "row-" row-num)) tile)
        ;                     (assoc accum (keyword (str "row-" row-num))
        ;                            (hash-map :tile (keyword (str row-num "-" col)) :state col)))
        ;                   {}
        ;                   (range n-col)))]
    ; (prn "tmp=" tmp)
    ; (prn "row-hash=" row-hash)
    ; (prn "tmp2=" tmp2)
    tmp2))

  ; (let [row []]
  ;   (as-> (reduce (fn [a col](assoc a (keyword (str "tile-" row-num "-" col)) {}))
  ;                 board-accum
  ;                 (range n-col)) row)))
  ;         ; (conj [] row)))

;; defunct
(defn init-row [row-num n-col board-accum])

(defn init-board [db]
  (let [n-rows (:n-rows db)
        n-cols (:n-cols db)]
    ; (map-indexed (fn [i x]))
    (reduce (fn [accum row-num]
              ; (let [row-num])
              ; (prn "reduce: row-num=" row-num)
              (let [row (create-row row-num n-cols)]
                ; (prn "init-board: accum=" accum)
                ; (prn "init-board: row=" row)
                (conj accum row)))
            []
            (range n-rows))))

; (defn init-board [db]
;   (let [board (:board db)
;         ;; Note: doall is not sufficent to de-lazify the result, so we have to use 'into
;         board-accum (as-> (into [] (map-indexed (fn [i v] (init-row i (:n-cols db) board))
;                                                 (range (:n-rows db)))) b-accum
;                           (reduce-kv (fn [a i v] (conj a (hash-map (keyword (str "row-" i)) v)))
;                                      {}
;                                      b-accum))]
;
;     (prn "init-board board-accum=" board-accum)
;     board-accum))
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
