(ns cube-test.tmp-test)

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
;; 2021-12-04
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
