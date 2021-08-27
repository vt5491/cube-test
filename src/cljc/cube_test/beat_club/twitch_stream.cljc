(ns cube-test.beat-club.twitch-stream
   (:require
    ; [clojure.math.numeric-tower :as math]
    [cube-test.utils.common :as common]))
  ; (:require))
   ; [cube-test.base :as base]))

(defn dummy []
  8)

;; Given a bpm (beats-per-minute) for a song, and the number of
;; steps and the frame rate of an animation, calculate the factor
;; to make the animation match the specified beat.
(defn beat-sync-factor [anim-steps anim-frame-rate bpm beat-type]
  (let [
        anim-interval (/ anim-steps anim-frame-rate)
        beat-interval (/ 60.0 bpm)
        ; anim-interval (double (/ anim-steps anim-frame-rate))
        ; beat-interval (double (/ 60 bpm))
        quarter-note-factor (/ anim-interval beat-interval)]
    (prn "anim-interval=" anim-interval ", beat-interval=" beat-interval)
    (case beat-type
      ; :quarter-note (format "%.4f" quarter-note-factor)
      :quarter-note (common/round-places quarter-note-factor 4)
      :double-note (common/round-places (/ quarter-note-factor 2.0) 4))))
