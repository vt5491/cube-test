;; TODO: make a .cljc ?
(ns cube-test.frig-frog.board
  (:require
   [re-frame.core :as re-frame]
   ; [babylonjs :as bjs]
   [cube-test.frig-frog.tile :as ff-tile]))
   ;; Note: no longer nec.
   ; [clojure.walk :as clj-walk]))
   ;; note can't refernce events directly due to "circular dependencies"
   ; [cube-test.frig-frog.events :as events]))

;; constants for quick access from bjs e.g. that don't require rf/db
(def n-rows nil)
(def n-cols nil)
(def board-width nil)
(def board-length nil)

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

(defn init-board [db]
  (let [n-rows (:n-rows db)
        n-cols (:n-cols db)]
    (reduce (fn [accum row-num]
              (let [row (create-row row-num n-cols)]
                (conj accum row)))
            []
            (range n-rows))))

(defn parse-diff-col [idx val]
  ; (prn "parse-diff-col: idx=" idx ",val=" val)
  (if (some? val)
    (let [
          tmp (into (vector) (map (fn [kv] kv) val))
          ; tmp-p (prn "tmp-p=" tmp)
          kv-pairs (into [] tmp)
          tmp-2 (conj [:col idx] (map (fn [x] x) tmp))
          tmp-21 (vector (conj [] (nth tmp-2 0) (nth tmp-2 1)))
          tmp-22 (into [] (nth tmp-2 2))
          tmp-23 (into [] (flatten (conj tmp-21 tmp-22)))
          ; tmp-24 (into (hash-map) (flatten (conj tmp-21 tmp-22)))
          ; tmp-23 (flatten (conj tmp-21 tmp-22))
          tmp-3 (first (into [] tmp-2))]
      ; (prn "tmp=" tmp)
      ; (prn "kv-pairs=" kv-pairs)
      ; (prn "tmp-2=" tmp-2)
      ; (prn "tmp-21=" tmp-21)
      ; (prn "tmp-22=" tmp-22)
      ; (prn "type tmp-22=" (type tmp-22))
      ; (prn "tmp-23=" tmp-23)
      ; (prn "tmp-24=" tmp-24)
      ; (prn "type tmp-23=" (type tmp-23))
      ; (prn "tmp-3=" tmp-3)
      ; (into (hash-map) [[:col idx]])
      ; (into (hash-map) tmp-23))
      tmp-23)
      ; (into (hash-map) (conj [:col idx] (map (fn [x] x) kv-pairs))))
    nil))

(defn parse-diff-cols [diff-col]
  ; (prn "parse-diff-cols: diff-col=" diff-col)
  (let [result {}]
    (if (some? diff-col)
      (let [pdc-r (into [] (filter some? (map-indexed parse-diff-col diff-col)))
            ; r (first pdc-r)
            r pdc-r]
        ; (prn "parse-diff-cols: pdc-r=" pdc-r)
        ; (prn "parse-diff-cols: r=" r)
        r)
      nil)))

(defn parse-diff-rows [idx diff-row]
  (let [result {}]
    (if (some? diff-row)
      (do
        (let [row-info (assoc result :row idx)
              col-info (first (into [] (map parse-diff-cols (vals diff-row))))
              col-info-2 (-> (map parse-diff-cols (vals diff-row)) (into []) first)]
          ; (prn "parse-diff-rows: row-info=" row-info ", col-info=" col-info)
          ; (prn "parse-diff-rows: col-info-2=" col-info-2)
          (conj row-info {:info col-info})
          (conj row-info {:info col-info-2})))
      nil)))

(declare expand-col-info)
;; go through a diff and extract which tiles have changed e.g. the row and col.
(defn parse-delta [diff]
  (let [parse-info (map-indexed parse-diff-rows diff)
        parse-info-2 (into [] (filter some? parse-info))
        ; tmp0 (prn "parse-delta: parse-info-2=" parse-info-2)
        ; flat-info (into [] (map (fn [val] (conj {:row (:row val) } (apply hash-map (:info val)))) parse-info-2))
        ; row (-> parse-info-2 -> first -> :row)
        ; tmp1 (prn "parse-delta: row=" row)
        flat-info (into [] (map
                            (fn [val]
                              (prn "parse-delta: val=" val)
                              (conj {:row (:row val)}
                                    {:col (get-in val [:info :col])}
                                    (apply hash-map (:info val))))
                            parse-info-2))]
        ; tmp (prn "parse-delta: flat-info=" flat-info)]
    ; (prn "parse-delta: parse-info-2=" parse-info-2)
    flat-info))

;; This is basically a 300-line regex.  Impossible to read and in dire need of
;; of re-factoring.  But for now at least it works.
(defn parse-delta-2 [diff]
  (let [parse-info (map-indexed parse-diff-rows diff)
        parse-info-2 (into [] (filter some? parse-info))
        tmp0 (prn "parse-delta-2: parse-info-2=" parse-info-2)
        ; expand-col-info (expand-col-info (:row val) (:info val))
        ; flat-info (into [] (map))
        ; flat-info (doall)
        flat-info (->
                    (map
                        (fn [val]
                         ; (prn "parse-delta-2: val=" val)
                         (into [] (expand-col-info (:row val) (:info val))))
                         ; (let [expand-val (expand-col-info (:row val) (:info val))]
                         ;   (prn "parse-delta-2: expand-val=" expand-val))
                         ; (conj {:row (:row val)}
                         ;       {:col (get-in val [:info :col])}
                         ;       (apply hash-map (:info val))))
                       parse-info-2)
                    ; (into []) first
                    (into []))
        ; tmp1 (prn "parse-delta-2: flat-info=" flat-info)
        ; tmp11 (prn "parse-delta-2: take 1 flat-info=" (take 1 flat-info))
        ; tmp12 (prn "parse-delta-2: count flat-info=" (count flat-info))
        ; tmp13 (prn "parse-delta-2: first flat-info=" (first (into [] flat-info)))
        ;; turn mult. array of arrays into one array of arrays
        flat-info-11 (if (> (count flat-info) 1)
                       (->> (reduce (fn [a v]
                                      ; (prn "v=" v)
                                      ; (prn "first v=" (first v))
                                      ; (prn "a=" a)
                                      ; (conj a (first v))
                                      ; (conj a v))
                                      ; (-> (conj a (first v))
                                      ;     (conj (second v))
                                      ;     (conj (nth v 2)))
                                      ; (map-indexed
                                      ;    (fn [i x](conj a (nth v i)))
                                      ;    (range 3)))
                                      ; (conj a (reduce (fn [a2 x2](conj a2 (nth v i))) [] v)))
                                      ; (loop [a2 a x 2])
                                      ; (loop [a2 a x (- (count flat-info) 1)])
                                      (loop [a2 a x (- (count v) 1)]
                                        (if (>= x 0)
                                          (do
                                            ; (prn "recur x=" x ",v=" v)
                                            (recur (conj a2 (nth v x)) (dec x)))
                                          a2)))
                                    []
                                    flat-info)
                            (into []))
                      (first flat-info))
                      ; flat-info)
        ; tmp14 (prn "parse-delta-2: flat-info-11=" flat-info-11)
        flat-info-2 (->>
                     (map
                       (fn [x]
                          ; (prn "flat-info-2: x=" x)
                          (apply hash-map (flatten x)))
                      flat-info-11)
                     (into []))]
        ; tmp2 (prn "parse-delta-2: flat-info-2=" flat-info-2)]
    flat-info-2))

        ; (doseq [x [1 2 3]
        ;         y (seq [4 5 6])]
        ;   (prn "x=" x)
        ;   (prn "y=" y))]))
; :info ([[:col 0 :tile :0-0] [:col 1 :tile :0-1] [:col 2 :tile :0-2] [:col 3 :tile :0-3]])
; into-> [{:row 0, :col 0, :abc -15, :state 15} {:row 2, :col 2, :abc -1, :state 1}]
(def tmp-s)
(defn expand-col-info [row col-info]
  (prn "expand-col-info:  col-info=" col-info)
  ; (reduce
  ;   (fn [a v]
  ; (map (fn [x]
  ;       (prn "expand-col-info: x=" x)
  ;       (reduce-kv (fn [a i v]
  ;                    (prn "expand-col-info: accum=" a ",v=" v ",i=" i)
  ;                    ; (conj a (apply array-map (conj v :row row)))
  ;                    (conj a v))
  ;                  [:row row]
  ;                  x))
  ;     col-info)
  (reduce
   (fn [accum val]
      (let [tmp
             (reduce-kv (fn [a i v]
                          ; (prn "expand-col-info: accum=" a ",v=" v ",i=" i)
                          ; (conj a (apply array-map (conj v :row row)))
                          (conj a v))
                        [:row row]
                        val)]
        (conj accum tmp)))
   []
   col-info))
