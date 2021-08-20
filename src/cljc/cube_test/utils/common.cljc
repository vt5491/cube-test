;; This is for common utils that can be written in .cljc
;; For .cljs specifics utils, refer to 'cube-test.utils
(ns cube-test.utils.common)
  ; (:require))

(defn relative-difference ^double [^double x ^double y]
  (/ (Math/abs (- x y))
     (max (Math/abs x) (Math/abs y))))

(defn close? [tolerance x y]
  (< (relative-difference x y) tolerance))
