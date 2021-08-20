(ns cube-test.beat-club.twitch-stream)
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
      :quarter-note quarter-note-factor
      :double-note (/ quarter-note-factor 2.0))))
