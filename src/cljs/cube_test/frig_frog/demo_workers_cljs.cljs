(ns cube-test.frig-frog.demo-workers-cljs)

(def i 0)

(defn inc-i []
  (set! i (+ i 1)))

(defn timedCount []
  (set! i (+ i 2))
  (js/postMessage i)
  (js/setTimeout #(timedCount) 500))

