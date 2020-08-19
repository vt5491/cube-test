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

(def m (assoc-in {} [:top :a] 1))

(def m2 (assoc-in m [:top :b] 2))

(def m3 (assoc-in m2 [:bottom :a] 11))

(def m4 (assoc-in m3 [:bottom :b] 12))

(reduce #(do (assoc %2 :val %1)) {} m4)
(assoc m4 :c 7)

(reduce #(do (prn "1=" %1 ",2=" %2)) {} m4)

(reduce #(do (assoc %1 (first %2) (second %2))) {} m4)

(key [:top])
(def a [:top {:a 1, :b 2}])
(type a)
(first a)
(count a)
(count (second a))

(reduce #(do (prn "hi")
           (assoc %1 (first %2) 1)) {} m4)

(reduce #(prn "hi"
           (assoc %1 (first %2) 1)) {} m4)

(def b {:a (js-obj "a" 7 "b" 8)})
(second [:top {:a 1, :b 2}])

(= :rear (first [:mid]))

(assoc-in {} [:front :1] 7)

(type {})
(type ())
(type (1 2))
(def l (list 1 2 3))
(count l)
(first l)
({:mid {:4 #object[t$jscomp$0 Name: blue_cube_4, isInstance: YES, # of submeshes: 1, n vertices: 24, parent: rubiks-cube, animation[0]: Name: anim, property: rotationQuaternion, datatype: Quaternion, nKeys: 2, nRanges: 0]}} {:mid {:7 #object[t$jscomp$0 Name: blue_cube_7, isInstance: YES, # of submeshes: 1, n vertices: 24, parent: rubiks-cube, animation[0]: Name: anim, property: rotationQuaternion, datatype: Quaternion, nKeys: 2, nRanges: 0]}} {:front {:4 #object[t$jscomp$0 Name: blue_cube_1, isInstance: YES, # of submeshes: 1, n vertices: 24, parent: rubiks-cube, animation[0]: Name: anim, property: rotationQuaternion, datatype: Quaternion, nKeys: 2, nRanges: 0]}})

(def r {:mid {:4 #object[t$jscomp$0 Name: blue_cube_4, isInstance: YES, # of submeshes: 1, n vertices: 24, parent: rubiks-cube, animation[0]: Name: anim, property: rotationQuaternion, datatype: Quaternion, nKeys: 2, nRanges: 0]}}) {:mid {:7 #object[t$jscomp$0 Name: blue_cube_7, isInstance: YES, # of submeshes: 1, n vertices: 24, parent: rubiks-cube, animation[0]: Name: anim, property: rotationQuaternion, datatype: Quaternion, nKeys: 2, nRanges: 0]}} {:front {:4 #object[t$jscomp$0 Name: blue_cube_1, isInstance: YES, # of submeshes: 1, n vertices: 24, parent: rubiks-cube, animation[0]: Name: anim, property: rotationQuaternion, datatype: Quaternion, nKeys: 2, nRanges: 0]}} {:mid {:8 #object[t$jscomp$0 Name: blue_cube_8, isInstance: YES, # of submeshes: 1, n vertices: 24, parent: rubiks-cube]}} {:mid {:9 #object[t$jscomp$0 Name: blue_cube_9, isInstance: YES, # of submeshes: 1, n vertices: 24, parent: rubiks-cube]}} {:mid {:2 #object[t$jscomp$0 Name: blue_cube_2, isInstance: YES, # of submeshes: 1, n vertices: 24, parent: rubiks-cube]}} {:mid {:5 #object[t$jscomp$0 Name: blue_cube_5, isInstance: YES, # of submeshes: 1, n vertices: 24, parent: rubiks-cube]}} {:mid {:3 #object[t$jscomp$0 Name: blue_cube_3, isInstance: YES, # of submeshes: 1, n vertices: 24, parent: rubiks-cube]}} {:mid {:6 #object[t$jscomp$0 Name: blue_cube_6, isInstance: YES, # of submeshes: 1, n vertices: 24, parent: rubiks-cube]}}

(count r)
(def r {})

(def s (assoc-in r [:front :1] "hi"))
(def s2 (assoc-in s [:front :2] "hi2"))

(def m {:a 1 :b 2})

(def r (map (fn [x]
              (println "x=" x))
            m))

(count r)

(doseq m (fn [x]
           (println "x=" x)))
(doseq [x m] (println "x=" x))

(def a 7)
(condp = a
  ;6 (println "hi")
  (println "bye"))

(def a {:a 1 :b 2})

(reduce (fn [accum b]
          (-> accum
              (assoc-in [:abc (first b)] (second b))
              (assoc-in [:def (first b)] (second b))
              (assoc-in [:abc :ghi] 7)
              (assoc-in [:abc :b] 12)))

        {} a)
(reduce (fn [accum b]
          (println "b=" b))
        {} a)
(assoc {} :a 7)

(map :a 1)
{:a 1}

(keyword (str 1))
;; 2020-08-14
(def a {:0 "abc" :1 "def" :2 "ghi"})

(let [idxs [:0 :2]]
  (doseq [i idxs] (prn (a i))))

(contains? a :0)

(contains? a :7)

(if (contains? a :7)
  (do (prn "found"))
  (do (prn "not found")))

(def b [:0 :3 :6 :9 :15 :18 :21 :24])

(contains? b 8)
(some :1 b)
(find b 2)

(def c {:0 :3 :6 :9 :15 :18 :21 :24})
(contains? c 0)
(find c :3)

;; 2020-08-17
(reduce (fn [x y]
          ; (prn "y=" y)
          (let [k (first y)
                v (second y)]))
            ; (assoc x (keyword v) (str k))
            ; (prn "hi")
            ; (assoc x :a 7)))
            ; (condp = k
            ;   :0 (assoc x :a 7)
            ;   (assoc x :b 8))))
        {} a)

(str :a)

(map (fn [x]
       (println "x=" x)
       (first x)
       (second x))
     a)

(def v [1 2 3])

(map #(do
        (+ %1 1)
        (+ %1 2)) v)

(map #(do
        (+ %2 1)
        (+ %2 2)) a)

(map (fn [x] (+ x 1)) v)

(map #(+ %1 1) v)

(map #(do (+ %1 1)) v)

(keys a)

(def r (map (fn [x]
              (prn "x=" x)
              (first x))
            {:a 1 :b 2 :c 3}))

(map (fn [x] (prn "x=" x)) {:a 1, :b 2, :c 3})

(doseq [i [1 2 3]] (fn [x] (prn "x=" x)))

(doseq [i [1 2 3]] (do (prn "x=" i) (prn "y=" (+ i 1))))

(let [x 3
      y 4]
  (<< "~{x} plus ~{y} equals ~(+ x y)."))
(sorted-map)

(into (sorted-map) [ 1 3 2])
(into () '(1 2 3))
(into [] '(1 2 3))
(into {} '(1 2 3))
(into {} {:a 1 :b 2 :c 3})
(into [] {:a 1 :c 3 :b 2})
(into (sorted-map ){:a 1 :c 3 :b 2})

(into (sorted-map-by (fn [key1 key2]
                       (compare (get results key2)
                                (get results key1))))
      {:1 :a :10 :k :2 :b})
(into (sorted-map-by =) {:1 :a :10 :k :2 :b})

(re-matches #"\\:\\d\{1,2\}" (str :7))

(re-matches #"^abc" "abcd")
(re-find #"^abc" "abcd")
(re-find #"^abc" "bcd")
(re-find #"(:)(\d{1,2})" ":7")

(-> (re-find #"(:)(\d{1,2})" (str :17)) (nth 2))
