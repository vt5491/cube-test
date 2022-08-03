;;
(ns cube-test.base
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]))

(def ONE-DEG (/ Math/PI 180.0))
(def scale-factor 1)
(def X-QUAT-90 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.X (* ONE-DEG 90))))
(def X-QUAT-NEG-90 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.X (* ONE-DEG -90))))
(def Y-QUAT-NEG-90 (.normalize (bjs/Quaternion.RotationAxis bjs/Axis.Y (* ONE-DEG -90))))

;; use-xr is actually defunct as main_scene.cljs simply sets based on browser.
(def use-xr false)
; (def use-xr true)
(def dummy 9)
;; to be dynamically set at runtime by any app that has a remote reframe worker db
(def db-worker-thread)

; (def top-level-scene :cube-spin-scene)
; (def top-level-scene :face-slot-scene)
; (def top-level-scene :tic-tac-attack-scene)
; (def top-level-scene :vrubik-scene)
; (def top-level-scene :geb-cube-scene)
; (def top-level-scene :skyscrapers-scene)
; (def top-level-scene :ut-simp-scene)
; (def top-level-scene :simp-scene)
; (def top-level-scene :msg-cube)
; (def top-level-scene :twizzlers)
; (def top-level-scene :beat-club)
(def top-level-scene :frig-frog)
; (def top-level-scene :top-scene)
