(ns cube-test.beat-club.twitch-stream
   (:require
    ; [clojure.math.numeric-tower :as math]
    [babylonjs :as bjs]
    [cube-test.utils.common :as common]
    [cube-test.utils :as utils]))

(def ^:dynamic *anim-loop-count* (atom 0))
(def ^:dynamic *anim-index* (atom 0))
(def anim-lookup [
                  {:name "ybot-combo" :loop 8 :speed-ratio 1.6 :from 0 :to 2.4 :desc "rumba"}
                  ; {:name "ybot-combo" :loop 1 :speed-ratio 0.8888 :from 2.4 :to 2.73}
                  {:name "ybot-combo" :loop 1 :speed-ratio 1.0 :from 2.4 :to 2.73}
                  {:name "ybot-combo" :loop 1 :speed-ratio 1.2222 :from 2.8 :to 10.9 :desc "head-bang"}
                  {:name "ybot-combo" :loop 1 :speed-ratio 1.0 :from 10.93328 :to 11.2666}
                  {:name "ybot-combo" :loop 4 :speed-ratio 1.6 :from 11.3 :to 15.133 :desc "silly"}
                  {:name "ybot-combo" :loop 1 :speed-ratio 1.0 :from 15.06 :to 15.4}
                  {:name "ybot-combo" :loop 2 :speed-ratio 1.0 :from 15.4 :to 30.8333 :desc "robot"}])
                  ; {:name "ybot-combo" :loop 8 :speed-ratio 1.6 :from 0 :to 2.4 :desc "rumba"}
                  ; {:name "ybot-combo" :loop 1 :speed-ratio 1.0 :from 2.4 :to 2.73}
                  ; {:name "ybot-combo" :loop 2 :speed-ratio 1.2222 :from 2.8 :to 10.9 :desc "head-bang"}])

(defn dummy []
  8)

(defn reset-anim-loop-count[]
  (swap! *anim-loop-count* (fn [loop-count] 0)))

(defn reset-anim-index[]
  (swap! *anim-index* (fn [x] 0)))

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

; (filter #(not (nil? %1)) (map-indexed #(when (= %1 2) %2) b))
(defn set-anim-key-range [anim from-key to-key]
  ; (js-debugger)
  (let [
        ; range (.createRange (str "range-" from-key "-" to-key) from-key to-key)
        ; sub-keys (subvec (.getKeys anim) from-key to-key)
        ; sub-keys (reduce #() (.getKeys anim) from-key to-key)
        sub-keys
                   (filter #(not (nil? %1))
                          (doall
                            (map-indexed #(when (<= from-key (+ %1 1) to-key) %2) (.getKeys anim))))
        sk-2 (concat sub-keys sub-keys)
        ; sk-3 (doall (map #((prn "sk2.to=" (.-to %1))) sk-2))
        ;; note sub-keys, even with doall is still a lazy seq internally.  We need to fully convert
        ;; it into a js-array so bjs can properly deal with it.
        sk-array (into-array sub-keys)
        ; sk-array-2 (concat sk-array (subvec sk-array 0 10))
        ; sk-array-2 (concat sk-array [])
        sk-array-2 (into-array sk-2)]
        ; tmp (js-debugger)]
    ; (prn "twitch-stream.set-anim-key-range: sub-keys=" sub-keys, "count(sub-keys)=" (count sub-keys))
    (prn "count(sub-keys)=" (count sub-keys))
    (prn "count(sk-array-2)=" (count sk-array-2))
    ; (js-debugger)
    (.setKeys anim sk-array-2)))


(defn create-sub-anim-group [ag-orig name from-key to-key]
  (prn "twitch-stream.create-sub-anim-group: entered, ag-orig=" ag-orig)
  (let [ag-new (bjs/AnimationGroup. (str name "-" from-key "-" to-key))
        children (.-children ag-orig)]
    ; (js-debugger)
    (prn "count children=" (count children))
    (doall (for [targetedAnim children]
             (do
               (prn "twitch-stream.create-sub-anim-group: targetedAnim=" targetedAnim)
               (let [target (.-target targetedAnim)
                     tmp (prn "target=" target)
                     sub-anim (.clone (.-animation targetedAnim))
                     tmp2 (prn "sub-anim=" sub-anim)]
                     ; tmp3 (js-debugger)]
                 (set-anim-key-range sub-anim from-key to-key)
                 (.addTargetedAnimation ag-new sub-anim target)))))
                 ; (js-debugger)))))
    (set! (.-name ag-new) "dynamic-anim")
    (set! (.-to ag-new) 4.8)
    (js-debugger)
    ag-new))

; (swap! *twitch-streaming-active* (fn [x] true))
(defn animGroupLoopHandler [ag]
  (swap! *anim-loop-count* (fn [loop-count] (inc loop-count)))
  (prn "twitch-stream: anim-loop-count=" @*anim-loop-count*)
  (let [anim-loop-cnt @*anim-loop-count*
        anim-idx @*anim-index*
        anim (nth anim-lookup anim-idx)
        ag-name (:name anim)
        max-loops (:loop anim)]
    ; (prn "twitch-stream: anim=" anim)
    (prn "twitch-stream: anim-loop-cnt=" anim-loop-cnt ", anim-idx=" anim-idx ", max-loops-" max-loops)
    ; (js-debugger)
    (when (= anim-loop-cnt max-loops)
      (utils/stop-animation (str ag-name "-anim"))
      (swap! *anim-loop-count* (fn [loop-count] 0))
      (swap! *anim-index* (fn [idx-count] (inc idx-count)))
      (prn "twitch-stream: going to next anim, anim-index=" @*anim-index*)
      ; (when (< anim-idx (count anim-lookup)))
      (if (< @*anim-index* (count anim-lookup))
        (do
          (let [next-anim-idx @*anim-index*
                next-anim (nth anim-lookup next-anim-idx)
                next-ag-name (:name next-anim)
                next-speed-ratio (:speed-ratio next-anim)
                next-from (:from next-anim)
                next-to (:to next-anim)]
            (utils/start-animation (str next-ag-name "-anim") next-speed-ratio next-from next-to)
            (prn "twitch-stream.animGroupLoopHandler: isPlaying=" (-> (.getAnimationGroupByName cube-test.main-scene.scene "ybot-combo-anim") .-isPlaying))))
        (do
          (swap! *anim-index* (fn [idx-count] 0)))))))
