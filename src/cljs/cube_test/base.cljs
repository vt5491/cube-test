;;
(ns cube-test.base
  (:require
   [re-frame.core :as re-frame]))

(def ONE-DEG (/ Math/PI 180.0))
(def scale-factor 1)

; (def use-xr false)
(def use-xr true)

; (def top-level-scene :cube-spin-scene)
; (def top-level-scene :face-slot-scene)
(def top-level-scene :tic-tac-attack-scene)
