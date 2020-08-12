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

(def a [1 2 3])
(def mr 0)

(if (> (count a) @*mr*) (swap! *mr* (fn [x] (count a))))

(set! mr 1)
(def ^:dynamic *mr* (atom 0))
(swap! *mr* #(1))
(swap! *mr* (fn [x] 1))

(not nil)

(def a (atom false))

(def s "red-cube-1")

; (re-matches #"hello, (.*)" "hello, world")
(re-matches #"^([a-z]*)-(cube)-(\d)" s)
(def r (re-matches #"^([a-z]*)-(cube)-(\d)" s))

(get r 3)
(keyword "1")

#f, val coll
(reduce #(do
           (prn "1=" %1 ",2=" %2)
           (conj %1 %2))
        []
        [1 2 3])

(conj [] 1)

(assoc {} :a 1)
(reduce #(do
           ; (prn "1=" %1 ",2=" %2)
           (assoc %1 (get %2 0) (get %2 1)))
        {} {:a 1 :b 2})
           ; (assoc %1)))

(map key {:a 1 :b 2})
(key 1)

(get [:a 1] 1)

(assoc-in {} [:a :b] 1)

(defn f []
  (let [a 1]
    [1 2 3]))

(f)

(do (let [r 7]
      r))

(def nest-map {})

(def nest-map (assoc-in nest-map [:front :1] "abc"))

(nest-map :front)

(:front nest-map)

(nest-map :front :1)

(get-in nest-map [:front :1])
