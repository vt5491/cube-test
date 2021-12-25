;; TODO: make a .cljc ?
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
; (defn init-row [row-num n-col board-accum])

(defn init-board [db]
  (let [n-rows (:n-rows db)
        n-cols (:n-cols db)]
    (reduce (fn [accum row-num]
              (let [row (create-row row-num n-cols)]
                (conj accum row)))
            []
            (range n-rows))))

; (defn parse-diff-col [idx diff-col]
;   (prn "parse-diff-col: idx=" idx ", diff-col=" diff-col)
;   (let [result {}]
;     (if (some? diff-col)
;       (assoc result :col idx)
;       nil)))

; (defn parse-delta [diff]
;   (let [parse-info (filter parse-diff-row diff)]
;     parse-info))

  ; (let [info-row (map-indexed [i val]
  ;                             (if (some?)))]
  ;   info-row))
(defn parse-diff-col [idx val]
  ; (let [result {}])
  (prn "parse-diff-col: idx=" idx ",val=" val)
  (if (some? val)
    ; (assoc result :col idx)
    ; (into {} [[:col idx]])
    ; (into (hash-map) [[:col idx] [(first (keys val)) (first (vals val))]])
    ; (let [kv-pairs])
    ; (into (hash-map) [[:col idx] [(first (keys val)) (first (vals val))]])
    (let [
          ; tmp (doall (map (fn [kv] kv) val))
          tmp (into (vector) (map (fn [kv] kv) val))
          tmp-p (prn "tmp-p=" tmp)
          ; kv-pairs (first (into [] (map (fn [kv] kv) val)))
          ; kv-pairs (first (into [] tmp))
          kv-pairs (into [] tmp)
          tmp-2 (conj [:col idx] (map (fn [x] x) tmp))
          ; tmp-22 (doall (map (fn [x] x) (into [] (conj [[:col idx]] (into [] (nth (map (fn [x] x) tmp-2) 2))))))
          tmp-21 (vector (conj [] (nth tmp-2 0) (nth tmp-2 1)))
          tmp-22 (into [] (nth tmp-2 2))
          tmp-23 (into [] (flatten (conj tmp-21 tmp-22)))
          ; tmp-23 (flatten (conj tmp-21 tmp-22))
          tmp-3 (first (into [] tmp-2))]
      (prn "tmp=" tmp)
      (prn "kv-pairs=" kv-pairs)
      (prn "tmp-2=" tmp-2)
      (prn "tmp-21=" tmp-21)
      (prn "tmp-22=" tmp-22)
      (prn "type tmp-22=" (type tmp-22))
      (prn "tmp-23=" tmp-23)
      (prn "type tmp-23=" (type tmp-23))
      (prn "tmp-3=" tmp-3)
      ; (into (hash-map) [[:col idx]])
      ; (into (hash-map) tmp-23))
      tmp-23)
      ; (into (hash-map) (conj [:col idx] (map (fn [x] x) kv-pairs))))
    nil))

(defn parse-diff-cols [diff-col]
  (prn "parse-diff-cols: diff-col=" diff-col)
  (let [result {}]
    (if (some? diff-col)
      ; (assoc result (map-indexed parse-diff-col diff-col))
      (let [r (first (into [] (filter some? (map-indexed parse-diff-col diff-col))))]
      ; (let [tmp (map-indexed parse-diff-col diff-col)
      ;       r (filter some? (map-indexed parse-diff-col diff-col))]
      ; (let [r (map-indexed parse-diff-col diff-col)]
        ; (prn "parse-diff-cols: tmp=" tmp)
        (prn "parse-diff-cols: r=" r)
        ; (prn "parse-diff-cols: type r=" (type r))
        ; (prn "parse-diff-cols: realized r=" (realized? r))
        r)
        ; (first r))
      nil)))

(defn parse-diff-rows [idx diff-row]
  (let [result {}]
    (if (some? diff-row)
      (do
        ; (assoc result :row idx :col (map-indexed parse-diff-col (vals diff-row)))
        ; (assoc result :row idx :col (map parse-diff-cols (vals diff-row)))
        (let [row-info (assoc result :row idx)
              col-info (first (into [] (map parse-diff-cols (vals diff-row))))]
              ; col-info (map parse-diff-cols (vals diff-row))]
              ; col-info (clj-walk/postwalk identity (map parse-diff-cols (vals diff-row)))]
          (prn "parse-diff-rows: row-info=" row-info ", col-info=" col-info)
          ; (prn "parse-diff-rows: type col-info=" (type col-info))
          ; (prn "parse-diff-rows: realized? col-info=" (realized? col-info))
          ; (conj row-info col-info)
          (conj row-info {:info col-info})))
        ; (conj (assoc result :row idx) (map parse-diff-cols (vals diff-row))))
      ; else
      nil)))

;; go through a diff and extract which tiles have changed e.g. the row and col.
(defn parse-delta [diff]
  ; (let [parse-info (map-indexed parse-diff-rows diff)])
  (let [parse-info (map-indexed parse-diff-rows diff)
        parse-info-2 (into [] (filter some? parse-info))
        flat-info (into [] (map (fn [val] (conj {:row (:row val) } (apply hash-map (:info val)))) parse-info-2))
        tmp (prn "parse-delta: flat-info=" flat-info)]
        ; flat-info-2 (conj {:row (:row val)} flat-info)]
        ; flat-info-2 (conj {:a 7} flat-info)]
    (prn "parse-delta: parse-info-2=" parse-info-2)
    ; (prn "parse-delta: flat-info-2=" flat-info-2)
    ; (first)
    ; parse-info-2
    flat-info))
    ; flat-info-2))
    ; (into [] (filter some? parse-info))))
    ; (filter some? (into [] parse-info))))
