(ns cube-test.msg-cube.data.cube
  (:require
   [re-frame.core :as re-frame]
   [clojure.spec.alpha :as s]))

;; sample
;; Note: pos is some sort of grid pos, not pixel pos.  We defer actual pixel level
;; pos to the scene.
{:id 1, :x-pos 0, :y-pos 1}
