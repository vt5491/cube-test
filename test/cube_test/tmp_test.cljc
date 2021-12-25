(ns cube-test.tmp-test
 (:require
   [clojure.test :refer-macros [is testing deftest run-tests] :as t]
   [re-frame.db       :as db]))

(def h {:models
        {:ybot-rumba {:is-loaded true, :is-enabled true, :is-playing false},
         :ybot-head-bang {:is-loaded true, :is-enabled false, :is-playing false}}})

(prn "hi h=" h)

(let [ {{{:keys [:is-enabled]} {:keys :ybot-rumba}} :models} h]
  (prn "val=" models))

(let [{:keys [:models]} h]
  (prn "val=" models))

(let [{models :models} h]
  (prn "val=" models))

;; works
(let [{{:keys [ybot-rumba]} :models} h]
  (prn "val=" ybot-rumba))

;; triple..works
(let [{{{:keys [is-loaded]} :ybot-rumba} :models} h]
  (prn "val=" is-loaded))

;; works too.
(let [{{{is-loaded :is-loaded} :ybot-rumba} :models} h]
  (println "Joe is a" is-loaded "wielding a"))

;; variable key
(def k1 :ybot-head-bang)
(def k2 :ybot-rumba)

(let [{{{:keys [is-enabled]} k2} :models} h]
  (prn "val=" is-enabled))

(def multiplayer-game-state
  {:joe {:class "Ranger"
         :weapon "Longbow"
         :score 100}
   :jane {:class "Knight"
          :weapon "Greatsword"
          :score 140}
   :ryan {:class "Wizard"
          :weapon "Mystic Staff"
          :score 150}})

(let [{{:keys [class weapon]} :joe} multiplayer-game-state]
  (println "Joe is a" class "wielding a" weapon))


(cube-test.beat-club.twitch-stream/beat-sync-factor 72 30 80 :double-note)
(cube-test.beat-club.twitch-stream/beat-sync-factor 244 24 80 :double-note)
(cube-test.beat-club.twitch-stream/beat-sync-factor 44 24 80 :double-note)

(Math/round 3.1)
(Math/round 1.5999)
(clojure.core/with-precision 2 3.1415)

(clojure.core/with-precision 2 :rounding FLOOR (/ 1 3M))
(clojure.core/with-precision 2 :rounding FLOOR 3.1415M)
(format "%.2f" 3.1415)

(Math/pow 2 5)

(def h {:y 0 :z 1})
(def h2 {:a 7 :b 8})

(doall (map #(prn "k=" %1 ",v=" %2)))

(keys h2)

(doseq [k (keys h2)
        v (vals h2)]
  (assoc h k v)
  (println (str k " " v)))
(seq h2)
(doall (map #(prn "k=" (first %1) ",v=" (second %1)) (seq h2)))

(doall (map #(assoc h (first %1) (second %1)) (seq h2)))

(reduce #(do
                             (let [level (first %2)]
                               (assoc %1 level (reduce (fn [a b] (assoc a (first b) (.-name (second b)))) {} (second %2))))
                          {}
                          rubiks-grid))
(reduce #(do
           (let [k (first %2)
                 v (second %2)]
             (prn "k=" k ",v=" v)
             (assoc %1 k v)))
        h
        h2)

(prn h)
(:z h)
(def kw :z)
(prn kw)
(kw h)

(def db {:control-intervals {:toggle-model [{:obj :abc :intervals [1 2 3]}]}})

(:control-intervals db)
(-> db :control-intervals :toggle-model)

(map (fn [x] (prn "x=" x))
     (-> db :control-intervals :toggle-model))

(str 7)

(def a [0 1 2 3 4 5 6 7 8])
(def b ["abc" "def" "ghi" "jkl"])
(range a 1 3)
(subvec a 1 3)

(for [x (range 1 5)]
  (do
    (prn "x=" x)))

(for [x a]
  (do
    (prn "x=" x)))

(count a)

(reduce #(prn "%1=" %1 ",%2=" %2) [] a)

(reduce #(prn "%1=" %1 ",%2=" %2) [] b)

(doseq [[i x] (map-indexed vector items)]
  (println i ":" x))

(map-indexed #(prn "%1=" %1 ",%2=" %2) b)

(map-indexed #((do (when (= %1 2)) ",%2=" %2) b))

(map-indexed #(when (= %1 2) true) b)
(map-indexed #(= %1 2)  b)
(map-indexed #(prn %1)  b)
(map-indexed #(prn %1) b)
(filter #(not (nil? %1)) (map-indexed #(when (= %1 2) %2) b))
(filter #(not (nil? %1)) (map-indexed #((= %1 2) %2) b))

(when true 7)
(<= 3 5 4)
(<= 4 8.6 8.5)

(inc 1)

(nth [0 1 2] 2)
(count [1 2])

(map list [1 2 3])
(def a [1 2 3])
(def a [{:a 1 } {:b 1} {:c 1}])
(list? (first (map list a)))
(map (fn [x] x) a)
(first (map (fn [x] x) a))
(map list a)

(defn is-small? [number]
  (if (< number 100) "yes" "no"))

(def ph "abc")
(def ph nil)

(defn exist-test []
  (prn "now in exist-test")
  (if ph
    (prn "ph exists")
    (prn "ph not exists")))

(exist-test)
(when (not ph) "hi")

(if ph "hi" "bye")
(if ph (prn "hi") "bye")
(prn "abc")

(exist-test)
(common-utils/close? 0.01 6.0 6.06)
(common-utils/relative-difference 6.0 6.06)

(common-utils/relative-difference 5.0 4.00)

(common-utils/close? 0.2 5.0 4.1)
(common-utils/close? 0.1 5.0 5.6)

(type (keys (ns-publics 'clojure.test)))
(count (keys (ns-publics 'clojure.test)))
(count [1 2])
(def a (keys (ns-publics 'clojure.test)))
(def a-s (set (map (fn [x] (name x)) a)))
(def b (keys (ns-publics 'cljs.test)))
(def b-s (set (map (fn [x] (name x)) b)))
(count a)
(count a-s)
(first a-s)
(first b-s)
(type a-s)
(type (doall a-s))
(count b)
; (type (nth a-s 1))
(name (nth a 1))
(filter (fn [x] (= (name x) "are")) a)
(filter (fn [x] (re-matches #"^run.*" (name x))) a)
(re-matches #"^hello.*" "jello-world")
(doall (filter (fn [x] (contains? a-s x)) b-s))
(doall (filter (fn [x] (not (contains? b-s x))) a-s))
(contains? a-s "are")
(contains? #{"are"} "bre")
(doall a-s)
(type (doall b-s))
(contains? [1 2] 3)

(vector? [1 2])
(type [1 2])

;; 2021-12-10 fun with reduce (accumulator)
(+ 1 1)

(def a 7)

(reduce (fn [val accum] (prn "val=" val ",accum=" accum)) [0 1 2] [])

(assoc {:a 7} :a 8)
(conj [1 2] 3)

(doall
  (reduce
          (fn [accum val]
            (do
              (prn "val=" val ",accum=" accum)
              (conj accum (inc val))))
          ; (fn [a b] (inc a))
          ; #(do (prn "1=" %1 ",2=" %2))
          []
          [0 1 2]))

(print "hi")
(prn "hi")

(doall (map (fn [x] (do
                      (prn "hi")
                      (+ x 1)))
            [1 2 3]))

(reduce #(do (prn "1=" %1 ",2=" %2)) {} [1 2 3])
(-> (reduce #(conj %1 [(keyword (str "tile-" %2)) %2]) {} [1 2 3])
    (prn "r=" %1))

(conj [])

(keyword "a")
(range 2 5)

(keyword (str "tile-" 7))
(str 7)

(map-indexed (fn [i v] (prn "i=" i ",v=" v)) [1 2 3])

(conj [1 2 ] 3)
(conj { } [:a 7] [:b 8])

(conj [] {:tile-2 2, :tile-3 3, :tile-1 1})
(seq (into (sorted-map) {:tile-2 2, :tile-3 3, :tile-1 1}))

(seq (into (sorted-map) {:key1 "value1" :key2 "value2"}))

(as-> (reduce #(conj %1 [(keyword (str "tile-" %2)) %2]) {} [1 2 3]) r
    (conj [] r))
    ; (seq (into (sorted-map) r)))
    ; (conj [] ))

(type '([{:tile-0-0 {}, :tile-0-1 {}}] [{:tile-1-0 {}, :tile-1-1 {}}]))
(type (list [{:tile-0-0 {}, :tile-0-1 {}}] [{:tile-1-0 {}, :tile-1-1 {}}]))

(hash-map :a 7 :b 8)
(hash-map :a [{:a 7} {:b 8}])
;; here
(reduce-kv (fn [a i v] (conj a (hash-map (keyword (str "row-" i)) v)))
           {}
           (vector [{:tile-0-0 {}, :tile-0-1 {}}] [{:tile-1-0 {}, :tile-1-1 {}}]))

(conj {} {:a 7})
(conj {} [:a 7] [:b 8])
(type [])
(type (vector))
(type ([1], [2]))
(type (list 1 2))
(type '(1 2))
(type (reduce-kv (fn [a i v] (prn "a=" a ",i=" i ",v=" v)) [] [{:a 7} {:b 8}]))

(use 'clojure.walk)
(postwalk identity [1 2 3])

(type (reduce-kv (fn [a i v] (conj a (hash-map (keyword (str "row-" i)) v)))
                 {}
            (vector [{:tile-0-0 {}, :tile-0-1 {}}] [{:tile-1-0 {}, :tile-1-1 {}}])))


(into [] (list 1 2))
(def h {:a {:a1 7 :a2 8}})
(let [h {:a {:a1 7 :a2 8}}]
  (prn (get-in h [:a :a1]))
  (contains? h :a)
  (contains? (:a h) :a3))


(let [db @db/app-db
      b (:board db)
      r0 (:row-0 b)
      r1 (:row-1 b)]
  (prn "r0=" r0)
  (prn "r0.0" (nth r0 0))
  (prn "r1=" r1))

(let [db @db/app-db
      b (:board-2 db)
      r0 (nth b 0)
      r1 (nth b 1)]
  (prn "board-2" b)
  (prn "r0=" r0)
  (prn "tile-0-0" (nth (:row-0 r0) 0))
  (prn "tile-0-1" (nth (:row-0 r0) 1))
  (prn "r1=" r1))

(type [])
(vector? [])
(conj [] 7)

(hash-map :a 7 :b 8)
(hash-map :a)

(let [h (hash-map :a)
      h2 (assoc h :a 7)]
  (prn "h2=" h2))

(let [h {}
      h2 (assoc h :a 7)
      h3 (assoc h2 :b 8)
      kw (keyword "a")]
  (prn "h3=" h3)
  (prn "h3.a=" (kw h3)))
  ; (prn "h3.a=" (get-in h3 kw)))

; @re-frame.db/app-db
(let [db @re-frame.db/app-db
      b (:board db)]
    (prn "b=" b)
    (prn "b[0]=" (nth b 0))
    (prn "b[1]=" (nth b 1))
    (prn "t[0]=" (-> (:row-0 (nth b 0)) (nth 1)))
    (prn "get-in=" (get-in b [0 :row-0 1])))

(let [a [nil nil {:a 3} nil {:a 5}]
      b [nil 2 nil 3]
      ; tmp2 (filter (-> nil? not) b)
      tmp (filter some? a)
      tmp2 (filter (fn [x] (not (nil? x))) a)]
    (prn "tmp=" tmp)
    (prn "tmp2=" tmp2))
      ; non-n (doall (filter (not nil?) a))])

  ; (prn "non-nil="))
  ; (prn "type non-nil" (type non-nil)))
  ; (prn "count non-nil" (count non-nil)))

(not (nil? nil))
(type "a")

(defn parse-int [s]
  (Integer/parseInt (re-find #"\A-?\d+" s)))

(parse-int "5")

(name :abc)
(read-string "5")
; :row-1
(re-matches #"^([a-z]*)_(cube)_(\d)_(\d)" "abc_cube_1_2")
(-> (re-matches #"row-(\d+)" (name :row-12)) (nth 1) (js/parseInt))

(js/parseInt "5")

(map #(some? %1) [nil 1 nil 3])
(filter #(some? %1) [nil 1 nil 3])

(some? 7)

(conj {:a 7} {:b 8})

(first (vals {:row-0 [{:state 1}]}))
(first (keys {:row-0 [{:state 1}]}))
(into {} [ 7])
(into {} [[:a "a"] [:b "b"]])
(into (hash-map) [[:a "a"] [:b "b"]])

(map (fn [val] (prn "val=" val) val) {:a 7 :b 8})

(conj [[:a 7 :b 8]] [[:d 7 :e 8]])

(let [a [:d 7 :e 9]]
  ; (into (hash-map [[:a 7 :b 8] a]))
  (prn "x=" [a])
  (into (hash-map) [a]))

(map (fn [x] x)  (into [] (map (fn [x] x) [[:a 7] [:b 8] [:c 9]])))

(vector [1 2])

(conj [[:col 2]] (into [](map (fn [x] x)(into [] (map (fn [x] x) [[:state 1] [:abc 0]])))))
(into [] (flatten (conj [[:col 2]] [[:state 1] [:abc 0]])))
(hash-map (into [] (flatten (conj [[:col 2]] [[:state 1] [:abc 0]]))))

(let [hash {:row 0}
      kv-pairs [:col 0 :state 1 :abc 0]
      ; h2 (into {} kv-pairs)]
      h2 (apply hash-map kv-pairs)]
    (prn "seq kv-pairs=" (seq kv-pairs))
    ; (prn "hash-map=" (hash-map :a 7 :b 8))
    ; (prn "hash-map=" (apply hash-map kv-pairs)))
    (conj hash h2))

    ; (prn "h2=" h2))
