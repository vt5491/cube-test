;; This is for common utils that can be written in .cljc
;; For .cljs specifics utils, refer to 'cube-test.utils
(ns cube-test.utils.common)
   ; (:require [clojure.math.numeric-tower :as math]))
  ; (:require))

;; This gives percentage difference with respect to the biggest of the two.
;; e.g (relavtive-difference 5 4) => 0.2
(defn relative-difference ^double [^double x ^double y]
  (/ (Math/abs (- x y))
     (max (Math/abs x) (Math/abs y))))

;; e.g is the second number to with with 10% of the first (0.5 in this case)
;; (common-utils/close? 0.1 5.0 5.5) => true
;; (common-utils/close? 0.1 5.0 5.6) => false
(defn close? [tolerance x y]
  (< (relative-difference x y) tolerance))

;; works with math.numeric-tower which won't work in a js environment.
; (defn round-places [number decimals]
;   (let [factor (math/expt 10 decimals)]
;     (bigdec (/ (math/round (* factor number)) factor))))
(defn round-places [number decimals]
  ; (float (/ (int (* 100 123.1299)) 100)))
  (let [factor (Math/pow 10 decimals)]
    ; (prn "round-places: factor=" factor ", number=" number)
    ; (float (/ (int (* factor number)) number))
    ; (float (/ (int (* factor number)) factor))
    (float (/ (Math/round (* factor number)) factor))))

(defn merge-dbs [db1 db2]
  "Merge two maps into one"
  (prn "hi2 from cube-test.utils.common.merge-dbs")
  (reduce #(do
             (assoc %1 (first %2) (second %2)))
          db1 db2))
; (defn vr-render-loop [xr-mode fps-pnl controller]
;   (if (= xr-mode "vr")
;     (controller/tick)
;     (controller-xr/tick))
;   (if fps-panel/fps-pnl
;     (fps-panel/tick main-scene/engine))
;   ; (beat-club-scene/tick)
;   (.render main-scene/scene))
;; return
; (doseq [[i x] (map-indexed vector items)]
;   (println i ":" x))
; (map-indexed #(when (= %1 2) true) b)

(defn idx-of-id
  "Given a vector of hashes (each with an :id key), return the (first occurence)
   index that matches the supplied id.
   Example:
   (idx-of-id [{:a 1 :id :1},{:b 2 :id :3},{:c 3 :id :2},{:d 4 :id :3}] :3) -> 1"
  [vec id]
  ; (let [r (into [] (filter #(= (:id %1) id) vec))])
  (let [tmp (map-indexed #(if (= (:id %2) id)
                            %1
                            nil)
                          vec)
        tmp-2 (filter #(not (nil? %1)) tmp)]
    ; (prn "idx-of-id: tmp=" tmp)
    ; (prn "idx-of-id: tmp-2=" tmp-2)
    ; (first (into []) tmp-2)
    (first  tmp-2)))

(defn idx-of-id-stem
  "Given a vector of hashes (each with an :id-stem key), return the (first occurence)
   index that matches the supplied id.
   Example:
   (idx-of-id-stem [{:a 1 :id-stem :1},{:b 2 :id-stem :3},{:c 3 :id-stem :2},{:d 4 :id-stem :3}] :3) -> 1"
  [vec id-stem]
  ; (let [r (into [] (filter #(= (:id %1) id) vec))])
  (let [tmp (map-indexed #(if (= (:id-stem %2) id-stem)
                            %1
                            nil)
                          vec)
        tmp-2 (filter #(not (nil? %1)) tmp)]
    (first  tmp-2)))

;; get all the meshes that start with the given stem-id
;; e.g. return [(mesh tr-1-0) (mesh tr-1-1)] for stem-id "tr-1"
(defn get-meshes-by-stem [stem-id])
