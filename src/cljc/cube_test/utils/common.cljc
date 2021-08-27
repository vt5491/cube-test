;; This is for common utils that can be written in .cljc
;; For .cljs specifics utils, refer to 'cube-test.utils
(ns cube-test.utils.common)
   ; (:require [clojure.math.numeric-tower :as math]))
  ; (:require))

(defn relative-difference ^double [^double x ^double y]
  (/ (Math/abs (- x y))
     (max (Math/abs x) (Math/abs y))))

(defn close? [tolerance x y]
  (< (relative-difference x y) tolerance))

;; works with math.numeric-tower which won't work in a js environment.
; (defn round-places [number decimals]
;   (let [factor (math/expt 10 decimals)]
;     (bigdec (/ (math/round (* factor number)) factor))))
(defn round-places [number decimals]
  ; (float (/ (int (* 100 123.1299)) 100)))
  (let [factor (Math/pow 10 decimals)]
    (prn "round-places: factor=" factor ", number=" number)
    ; (float (/ (int (* factor number)) number))
    ; (float (/ (int (* factor number)) factor))
    (float (/ (Math/round (* factor number)) factor))))
