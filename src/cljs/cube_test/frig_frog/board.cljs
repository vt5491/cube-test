;; TODO: make a .cljc ?
(ns cube-test.frig-frog.board
  (:require
   [re-frame.core :as re-frame]))
   ; [babylonjs :as bjs]
   ; [cube-test.frig-frog.tile :as ff-tile]))
   ;; Note: no longer nec.
   ; [clojure.walk :as clj-walk]))
   ;; note can't refernce events directly due to "circular dependencies"
   ; [cube-test.frig-frog.events :as events]))

;; constants for quick access from bjs e.g. that don't require rf/db
(def n-rows nil)
(def n-cols nil)
(def board-width nil)
(def board-length nil)
(def board-height 1.5)
(def board-heights {:btm 0.0 :top 5.0})
(def tile-width 1.2)
(def tile-height 1.2)

(def ^:dynamic *last-btm-board* (atom nil))
(def ^:dynamic *last-top-board* (atom nil))

(defn create-row [row-num n-cols]
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
  (if (some? val)
    (let [
          tmp (into (vector) (map (fn [kv] kv) val))
          kv-pairs (into [] tmp)
          tmp-2 (conj [:col idx] (map (fn [x] x) tmp))
          tmp-21 (vector (conj [] (nth tmp-2 0) (nth tmp-2 1)))
          tmp-22 (into [] (nth tmp-2 2))
          tmp-23 (into [] (flatten (conj tmp-21 tmp-22)))
          tmp-3 (first (into [] tmp-2))]
      tmp-23)
    nil))

(defn parse-diff-cols [diff-col]
  (let [result {}]
    (if (some? diff-col)
      (let [pdc-r (into [] (filter some? (map-indexed parse-diff-col diff-col)))
            r pdc-r]
        r)
      nil)))

(defn parse-diff-rows [idx diff-row]
  (let [result {}]
    (if (some? diff-row)
      (do
        (let [row-info (assoc result :row idx)
              col-info (first (into [] (map parse-diff-cols (vals diff-row))))
              col-info-2 (-> (map parse-diff-cols (vals diff-row)) (into []) first)]
          (conj row-info {:info col-info})
          (conj row-info {:info col-info-2})))
      nil)))

(declare expand-col-info)
;; go through a diff and extract which tiles have changed e.g. the row and col.
(defn parse-delta [diff]
  (let [parse-info (map-indexed parse-diff-rows diff)
        parse-info-2 (into [] (filter some? parse-info))
        flat-info (into [] (map
                            (fn [val]
                              (conj {:row (:row val)}
                                    {:col (get-in val [:info :col])}
                                    (apply hash-map (:info val))))
                            parse-info-2))]
    flat-info))

;; This is basically a 300-line regex.  Impossible to read and in dire need of
;; of re-factoring.  But for now at least it works.
(defn parse-delta-2 [diff]
  (let [parse-info (map-indexed parse-diff-rows diff)
        parse-info-2 (into [] (filter some? parse-info))
        flat-info (->
                    (map
                        (fn [val]
                         (into [] (expand-col-info (:row val) (:info val))))
                       parse-info-2)
                    (into []))
        ;; turn mult. array of arrays into one array of arrays
        flat-info-11 (if (> (count flat-info) 1)
                       (->> (reduce (fn [a v]
                                      (loop [a2 a x (- (count v) 1)]
                                        (if (>= x 0)
                                          (do
                                            (recur (conj a2 (nth v x)) (dec x)))
                                          a2)))
                                    []
                                    flat-info)
                            (into []))
                      (first flat-info))
        flat-info-2 (->>
                     (map
                       (fn [x]
                          (apply hash-map (flatten x)))
                      flat-info-11)
                     (into []))]
    flat-info-2))

(def tmp-s)
(defn expand-col-info [row col-info]
  (reduce
   (fn [accum val]
      (let [tmp
             (reduce-kv (fn [a i v]
                          (conj a v))
                        [:row row]
                        val)]
        (conj accum tmp)))
   []
   col-info))

(defn get-last-board [prfx]
  (case prfx
    :btm *last-btm-board*
    :top *last-top-board*))

(defn init-boards []
  ;; we have to do this each initialization because
  ;; users may now be swapping between scenes.
  (swap! *last-btm-board* (fn [x] nil))
  (swap! *last-top-board* (fn [x] nil)))
