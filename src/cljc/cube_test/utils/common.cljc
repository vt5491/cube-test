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
    (prn "round-places: factor=" factor ", number=" number)
    ; (float (/ (int (* factor number)) number))
    ; (float (/ (int (* factor number)) factor))
    (float (/ (Math/round (* factor number)) factor))))

; (defn vr-render-loop [xr-mode fps-pnl controller]
;   (if (= xr-mode "vr")
;     (controller/tick)
;     (controller-xr/tick))
;   (if fps-panel/fps-pnl
;     (fps-panel/tick main-scene/engine))
;   ; (beat-club-scene/tick)
;   (.render main-scene/scene))
