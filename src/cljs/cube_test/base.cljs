;;
(ns cube-test.base
  (:require
   [re-frame.core :as re-frame]))

(def ONE-DEG (/ Math/PI 180.0))
(def scale-factor 1)

;; use-xr is actually defunct as main_scene.cljs simply sets based on browser.
(def use-xr false)
;(def use-xr true)
(def dummy 9)

; (def top-level-scene :cube-spin-scene)
; (def top-level-scene :face-slot-scene)
; (def top-level-scene :tic-tac-attack-scene)
; (def top-level-scene :vrubik-scene)
; (def top-level-scene :geb-cube-scene)
; (def top-level-scene :skyscrapers-scene)
; (def top-level-scene :ut-simp-scene)
; (def top-level-scene :simp-scene)
(def top-level-scene :msg-cube)
