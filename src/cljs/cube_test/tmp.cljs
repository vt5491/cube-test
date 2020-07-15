; (ns cube-test.tmp
;   (:require
;    [re-frame.core :as re-frame]
;    [babylonjs :as bjs]))

(+ js/Math.PI 1)

(+ 1 1)

(js/setTimeout (fn [] (prn "hi")) 2000)

; (let [x (future (js/setTimeout #() 2000) (+ 41 1))] [@x @x])
(def f (fn [] (js/setTimeout (fn [] (prn "hi")) 2000)))

(for [i (range 6)] [i])

(def f (for [i (range (rand 6))] [i]))

(def a (for [i (range 6)] [i]))

(get 0 a)

(prn "val=" (get-in a [1]))

(nth a 1)

(map #(do))

(accum [0] [1])

(reduce #(conj [] (nth %1 0)) [1] [2])

(reduce #(prn %1) [1 2])

(conj [] 1 2)
(conj [] [1] [2])

(reduce #(conj))

(reduce #(cons %2 %1) [1 2 3] [4 5 6] [8 9])
(reduce into [] a)
