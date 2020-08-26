;;
;; this is the object in the grid.  The main game object so to speak.
;;
(ns cube-test.tic-tac-attack.cell
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [cube-test.base :as base]))

;;
;; inits
;;
(defn init []
  (assoc {} :rot-axis nil, :rot-vel 0))
