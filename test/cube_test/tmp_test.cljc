(ns cube-test.tmp-test
 (:require
   [clojure.test :refer-macros [is testing deftest run-tests] :as t]
   [re-frame.db       :as db]
   [cube-test.main-scene :as main-scene]
   [babylonjs :as bjs]
   [babylonjs-gui :as bjs-gui]
   [promesa.core :as p]))
   ; [cube-test.frig-frog.utils.common :as common]))

(comment
  ;; Don't forget to ctrl-alt-shift-e these lines not ctrl-alt-shift-b
  ;; (because they're under a global comment)
  ;; Also, need to be on left-paren not on end-paren

  ;; switch to cljs repl
  (shadow.cljs.devtools.api/nrepl-select :app))

(def h {:models
        {:ybot-rumba {:is-loaded true, :is-enabled true, :is-playing false},
         :ybot-head-bang {:is-loaded true, :is-enabled false, :is-playing false}}})

(prn "hi h=" h)
(print h)
(prn "hi")

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
(conj {:a 1 :b 2} {:c 3 :d 4})
(conj {:c 3 :d 4} {:a 1 :b 2})
(conj {:c 3 :d 4} {:a 1 :b 2} {:e 7})
(get-in {:a 1 :b {:g 1 :h 2} :c 3} [:b :h])

(let [ds [{:row 0, :col 0, :abc -15, :state 15} {:row 2, :col 2, :abc -1, :state 1}]]
      ; {:keys [name location description]} ds]
  (map (fn [{:keys [row col]}]
         (prn "row=" row)
         row) ds))

(defn ds1 [{:keys [row col]}]
  (prn "row=" row))

(defn ds2 [val]
  (prn "ds2: val=" val))

(ds1 {:row 0, :col 0, :abc -15, :state 15})
(ds2 {:row 0, :col 0, :abc -15, :state 15})

; (let [dseq (seq [{:row 0, :col 0, :abc -15, :state 15} {:row 2, :col 2, :abc -1, :state 1}])])
(let [dseq [{:row 0, :col 0, :abc -15, :state 15} {:row 2, :col 2, :abc -1, :state 1}]]
  (prn "dseq=" dseq)
  ; (doseq [x dseq])
  (doseq [{:keys [row col]} dseq]
    (prn "row=" row)))

  ; (doseq dseq (fn [x] (prn "x=" x))))
  ; (doseq dseq (prn "hi")))

(doseq [ x (seq [1 2 3])] (prn "x=" x))
(doseq [ x (seq [1 2 3])] (conj [] x))
(map (fn [x] (inc x))
     (seq [1 2 3]))

(doseq [x [1 2 3]
        y (seq [4 5 6])]
  (prn "x=" x)
  (prn "y=" y))

(seq [1 2 3])

(doseq [x [-1 0 1]
        y [1  2 3]]
  (prn (* x y)))

(flatten [[:col 0 :tile :0-0] [:col 1 :tile :0-1]])

(doseq [ x [[:col 0 :tile :0-0] [:col 1 :tile :0-1]]]
  (prn "x=" x))

(let [r (doseq [x [-1 0 1]
                y [1  2 3]
                a []]
          (prn "x=" x)
          (prn "a=" (conj a (* x y))))]
  (prn "r=" r)
  r)

(let [a []]
  (doseq [x [-1 0 1]
          y [1  2 3]]
    (prn (* x y))
    (prn "a=" (conj a (* x y)))))

(reduce (fn [a v]
          (prn "v=" v ",a=" a)
          (conj a v))
        []
        [0 1 2])

(reduce (fn [a v]
          (prn "val=" v ",accum=" a)
          (prn "col=" (:col v))
          (conj a (:col v)))
        []
        [{:col 0 :tile :0-0} {:col 1 :tile :0-1}])

(conj [] 0)
(conj {} [:a 7] [:b 8])
(conj {} [:a 7] {:b 8 :c 9})
(conj [] :a 7 :b 8)
(nth [0 1 2] 2)
(conj {} [:col 0 :tile :0-0])
(conj {} [:col 0] [:tile :0-0])
(conj {} [[:col 0 :tile :0-0]])
(array-map [:col 0 :tile :0-0])
(array-map [:col 0])
(let [r (array-map :a 10)]
  (prn "r=" r))
(array-map :a 10 :b 11)
(conj {:x 1} (apply array-map [:a 10 :b 11]))
(let [a [:a 7 :b 8]
      h {:a 7 :b 8}
      hf (flatten h)
      af (flatten a)
      am (apply array-map a)]
  (prn "am=" am))
  ; (prn "hf=" hf)
  ; (prn "af=" af))

(reduce-kv (fn [a i v]
             (prn "a=" a ",i=" i ",v=" v)
             ; (conj a (conj [] (first v) (second v) (nth v 2) (nth v 3)))
             ; (conj a (doall (conj []  (nth v 2) (nth v 3) (first v)))))
             ; (conj a (conj [] (flatten v))))
             (let [flat-v (into [] (flatten v))
                   tmp (count flat-v)]
               (prn "count flat-v=" tmp)
               (prn "flat-v=" flat-v)
               (conj a flat-v)))
             ;   (doseq [x flat-v]))
             ; (conj a (flatten v)))
          []
          [[:col 0 :tile :0-0] [:col 1 :tile :0-1] [:col 2 :tile :0-2]])

(let [am [:a 7 :b 8]
      am2 (apply array-map (conj  am :c))]
  (prn "am2=" am2))

(into (hash-map) (array-map :a 7))
(array-map :a 7 :b 8)
(vector 1 2)
(into (hash-map) (flatten (vector :a 7)))

(into (array-map) (vector :a 7))
(reduce conj {} [[:a 1] [:b 2 :c 3]])
(apply hash-map (flatten [:a 7 :b 8 :c 9]))

(let [a [[:row 0 :col 0 :tile :0-0] [:row 0 :col 1 :tile :0-1]]]
  ; (into [] (map (fn [x] (apply hash-map (flatten x))) a)))
  (->> (map (fn [x] (apply hash-map (flatten x))) a)
      (into []) second))
  ; (apply hash-map (flatten a)))

(type 76)
(take 1 (map
               (fn [x] x)
               [1 2 3]))

(count [[:row 0 :col 0 :state 1 :abc 0]] [[:row 2 :col 2 :state 1 :abc 0]])
(count (seq [[[:a 7]] [[:b 8]]]))
((seq [[:a 7]]))

(let [r
      (->> (reduce (fn [a v]
                     (prn "v=" v)
                     (prn "first v=" (first v))
                     (prn "a=" a)
                     (conj a (first v)))
                   []
                   (seq [[[:a 7]] [[:b 8]]]))
       (into []))]
  (prn "r=" r)
  (prn "first r=" (first r)))
                 ; (first))
(range 3)
(let [a [[:a 1] [:b 2] [:c 3]]]
  (map-indexed (fn [i x](nth a i)) (range 3)))

(loop [a [] x 3]
  (if (>= x 0)
    (do
      (prn "x=" x)
      (recur (conj a x) (dec x)))
    a))

(+ 1 1)
(doseq (= :a) (keys {:a 1 :b 2}))
(doseq [{:keys [:a :b]} {:z 1 :b 2}]
  (prn "a=" :a))
(doseq [ x (keys {:a 1 :b 2})]
  (prn "x=" x)
  (some? (= x :b)))
(filter #(= %1 :c) (keys {:a 1 :b 2}))

(nil? nil)
(nil? 7)
(nil? ())

(assoc {} :a 7 :b 8)
(let [h {}
      tmp (assoc h :frog {})
      tmp-2 (assoc-in h [:frog :a] 7)
      tmp-3 (assoc-in tmp-2 [:frog :b] 8)]
  (prn "tmp=" tmp)
  (prn "tmp-2=" tmp-2)
  (prn "tmp-3=" tmp-3))

(-> (assoc-in {} [:a] 7) (assoc-in [:b] 8))

(let [h {:a 7 :b 8 :c 9}]
  ; (prn (get-in h [:a])))
  (conj (hash-map :x (get-in h [:a])) (hash-map :y (get-in h [:b]))))

(:a {:a 7 :b 8})
(let [a 7]
  (when a (prn "hi")))

(map (fn [x] (inc x)) [1 2])
(map #(inc %1) [1 2])
(map #(identity 7) [1 2])

(identity 7)

(let [h {:a 7 :b 8 :c 9}
      row (or (:a h))
      col (or (:d h) -1)]
  (prn "row=" row)
  (prn "col=" col))

(let [a -7
      b 8]
  (* b (if (neg? a)
         -1
         1)))

(let [v (bjs/Vector3. 0 0 1)
      v2 (.multiply v)]
  (prn "v=" v))

(+ 1 1)
(range 6)

(bjs/Color3.Red)
(bjs/Color3. 1 2 3)
(bjs/Color3. 0xff 2 3)
(-> (bjs/Angle.FromDegrees 180) (.-radians))
(.-radians (bjs/Angle. 180))
; BABYLON.Angle.FromDegrees(45).radians();
(-> (bjs/Angle.FromDegrees 10) (.radians))

(+ 1 1)
(let [a nil]
  (when (not (nil? a))
    (+ 1 2)))

(quot 8 2)

(let [h {:a [0 1 2]}]
  (get-in h [:a 1]))

(let [db {:a [:x :y :z]}
      v (:a db)
      ; v2 (map-indexed #(do (prn "1=" %1)(prn "2=" %2)) v)
      ; v2 (doall (map-indexed #(prn "1=" %1) v))
      ; v2 (doall (map-indexed #(do (prn "1=" %1)(prn "%2=" %2)) v))
      v3 (doall (map-indexed #(when-not (= %1 1) %2) v))
      v4 (keep-indexed #(when-not (= %1 1) %2) v)]
      ; v2 (map-indexed #(prn "1=" %1) v)]
  (prn "v3=" v3)
  (prn "v4=" v4)
  (assoc db :a v))

(let [db {:a [:x :y :z]}
      v (filter #(not (= %1 :y)) (:a db))]
    (prn "v=" v))

(get [0 1 2] 1)

(let [a [{:a 1 :id :a1} {:b 2 :id :b1}{:c 3 :id :c1}]
      r (into [] (filter (fn [x] (= (:id x) :b1)) a))]
  (prn "r=" r))

(defn f-x [[a b]]
  (prn "a=" a ",b=" b))

(f-x [ 1 2])
(apply f-x '([3 4]))
(apply #(count %1) '([3 4]))
(apply (fn [[a b]](+ a b)) '([3 4]))
(map (fn [[a b]] (+ a b)) [[3 4] [7 9]])

(apply + '(1 2))

(let [h {:a 1 :b 2 :c 3}
      updates {:b 12}
      new-h (doall (map #(prn "x=" %1) updates))
      ; new-h2 (doall (map (fn [[k v]] (prn "k=" k ",v=" v)) updates))
      new-h2 (doall (map (fn [[k v]] (if (get-in updates [k])
                                       [k (get-in updates [k])]
                                       [k v]))
                         h))
      new-h3 (into {} new-h2)]
  (prn "new-h2=" new-h2)
  (prn "new-h3=" new-h3))

(let [k :a
      h {:a 1 :b 2}
      r (get-in h [k])]
  r)

(conj [{:a 1 :id :1}] {:b 1 :id :2})
(seq? [1 2])
(seq? 1)

(assoc [0 1 2 3] 2 7)

(name :abc)
(dotimes [i 3] (prn "abc")(prn "i=" i))
(str "abc" "def")
(some? nil)
(some? 7)

(-> cube-test/main-scene)
(let [scene main-scene/scene
      m (.getMeshByID scene "tr-1-0")]
    (prn "m=" m)
    (-> m (.-metadata) (.-vx)))

(-> (re-find #"^(tr-\d{1,4})-(\d{1,4})" "tr-0-1"))
(-> (re-find #"^(tr-\d{1,4})-(\d{1,4})" "tr-666-14") (second) (keyword))

(* 8 1.2)
(def a 7)
(prn "a=" a)

(let [o (js-obj "a" 7 "b" 8)]
  (set! o -a 9)
  (prn o))

(mod 7 5)

(.then (js/Promise.resolve 42)
       #(js/console.log "cljs.promise: val= "%))

(.then (js/Promise.resolve 43)
       #(prn "cljs.promise: val= "%))
  ; function sleep (time) {}
  ; return new Promise((resolve) => setTimeout(resolve, time)));

(defn sleep-t [time]
  (js/Promise. #(js/setTimeout %1 time)))

(defn sleep-t2 [time]
  (js/Promise. (fn [resolve] (js/setTimeout resolve time))))

(defn sleep-t3 [time]
  (prn "sleep-t3: time=" time)
  (p/promise (fn [resolve reject]
               ; (js/setTimeout #(js/Promise.resolve 1) time)
               ; (js/setTimeout #(resolve 1) time)
               (js/setTimeout #(prn ":resolved") 5000))))

  ; (js/Promise. (fn [resolve] (js/setTimeout resolve time))))

(defn get-hi []
  "aloha there")

(get-hi)
(-> (sleep-t 1000) (.then #(get-hi)))

(.then (sleep-t 5000) (prn "hello"))
(.then (sleep-t2 5000) (prn "hello"))

; works
(js/setTimeout #(prn "loaded2") 6000)

(.then (sleep-t3 5000) (prn "abc"))

(-> (sleep-t3 5000) (p/then #(prn "def")))

(p/then #(prn "def") (sleep-t3 3000))

; function sleep (time) {}
;   return new Promise((resolve) => setTimeout(resolve, time));
(defn sleep-t4 [time]
  (p/promise (fn [resolve]
               (prn "hi")
               (js/setTimeout resolve time))))

(-> (sleep-t4 6000) (p/then #(prn (+ 1 1))))

(p/then)

(defn sleep-t5 [f ms]
  (js/setTimeout f ms))

(sleep-t5 #(prn "hi") 5000)

(+ 1 1)
(name :abc)
(let [h {:db {:abc 7}}]
  (prn "abc=" (-> h :db :abc)))

  ; let result = 0;
  ; for (var i = Math.pow(baseNumber, 7)); i >= 0; i--) {
  ;   result += Math.atan(i) * Math.tan(i));
  ; ;
(js/Math.pow 2 7)

(let [r 0
      big-num (js/Math.pow 10 9)]
    (prn "big-num=" big-num)
    (doall (dotimes [i big-num]
                    (+ r (* (js/Math.atan i) (js/Math.tan i))))))
    ; (prn "r=" r))

;       (set! r) ()))
;
; (dotimes [i 5] (println "i is" i))

(reduce (fn [a v] (+ a (* (js/Math.atan v) (js/Math.tan v))))
        0
        (range (js/Math.pow 10 7)))

(range 5)

(count [1 2])

(let [a [1 2 3]]
  (-> a (nth 2)))

(prn "r=" js/self.document)
(conj [1 2] 3)

(let [h {:trains []}
      trains (:trains h)]
  (assoc h :trains (conj trains {:a 7 :b 8})))

(complement true)
(not true)
(-> js/self .-document (cljs.core/undefined?))
(-> js/self .-document undefined?)
(defn w? []
  (-> js/self .-document cljs.core/undefined?))

(cljs.core/undefined? 7)
(-> js/self .-document)

(w?)
(not (w?))
(complement w?)

(let [h (clj->js {:msg "hi" :abc 7})
      h2 (js->clj h)]
  (prn "h=" h)
  (prn "h2=" h2)
  (prn "msg=" (:msg h2))
  (prn "abc=" (:abc h2))
  (prn "abc=" (get h2 "abc"))
  (prn "keys=" (keys h2)))

;; 2022-06-14
(def a {"id-stem" "tr-1", "length" 4})
(def b {"id-stem" "tr-1", "length" 4})
(def c {:a 7 :b 8})

(get a "id-stem")
(get a "length")

(js->clj "hi")
(js->clj a :keywordize-keys true)
(keys a)
(.parse js/JSON a)

(prn a)
(prn c)

(ns cube-test.tmp-test)

(map #(prn %1) (vals a))

(hash-map :a 7 :b 8)
(hash-map "a" 7 "b" 8)
(zipmap (keys a) (vals a))

(keyword "a")

(zipmap (map #(keyword %1) (keys a)) (vals a))

(array-map a)
(array-map {:a 7 :b 8})

(map #(prn %1) a)
(map #(array-map %1) a)

(js-obj "a" 7)

(+ 1 2)

(def db {:trains [{:id "tr-1" :a 7} {:id "tr-2" :a 8} {:id "tr-3" :a 9}]})

(let [trains (:trains db)]
  (vec (remove #(= (:id %1) "tr-2") trains)))

(let [trains (:trains db)]
  ; (prn trains)
  (map #(remoe "hi") trains))

(let [a [{:n 1}]]
  (prn (-> (first a) (:n))))

(let [id "ball"]
  (nth (re-matches #"^([a-z]*)[-]*\d*" id) 1))

(let [id "cube-test.frig-frog.rules/player-1"
      id-2 "ball"
      id-3 "ball-1"
      re #"^([a-z\.\/\-]+)([\-]*)(\d*)"
      r (re-matches re id)
      r2 (re-matches re id-2)
      r3 (re-matches re id-3)
      r4 (re-matches #"^cube-test.frig-frog.rules/player.*" id)]
  (prn "r=" r)
  (prn "r2=" r2)
  (prn "r3=" r3)
  (prn "r4=" r4))

(let [a "abc-def"
      b "abc-ghi"
      c "def-abc"
      re #"^abc.*"]
  (prn (re-matches re a))
  (prn (re-matches re b))
  (prn (re-matches re c)))

(let [a [{:x 6, :y 0, :vx 0, :vy 0}]]
  (prn (-> (first a) (:vx))))

(not nil)
(Math/abs -0.5)

    ; // Load a GUI from a URL JSON.
    ; let advancedTexture = BABYLON.GUI.AdvancedDynamicTexture.CreateFullscreenUI("GUI", true, scene);
    ; let loadedGUI = await advancedTexture.parseFromURLAsync("https://doc.babylonjs.com/examples/ColorPickerGui.json"));

(+ 1 1)
(def v (bjs/Vector3. 2 5 4))
(.-x v)
(def v2 (bjs/Vector3. 3 5 4))

(def adv-t (bjs-gui/AdvancedDynamicTexture.CreateFullscreenUI "GUI" true cube-test.main-scene.scene))
(prn "scene=" cube-test.main-scene.scene)
; (set! engine (bjs/Engine. canvas true))
(prn "a=" bjs-gui/AdvancedDynamicTexture.CreateFullscreenUI)
(prn "a=" BABYLON/GUI)
(p/then #(prn "def"))

(def loaded-gui (-> adv-t
                    (.parseFromURLAsync "models/frig_frog/guiTexture.json")
                    (p/then #(prn "loaded 1=" %1))))

(prn "lg=" loaded-gui)
(prn "hi")
(js/setTimeout #(prn "loaded2") 6000)
(js/setTimeout #(do (prn "loaded1")(prn "loaded2")) 6000)

(defn destr [& {:keys [a b] :as opts}]
  [a b opts])

(destr :a 1)

(destr {:a 1 :b 2})

(defn in?
  "true if coll contains elm"
  [coll elm]
  (some #(= elm %) coll))

(let [a [:board-changed :prfx :btm]]
  ; (filterv #(= %1 :prfx) a)
  (prn (second a)))
  ; (keep-indexed #(prn "1=" %1 ",2=" %2) a))
  ; (map-indexed #(prn "1=" %1 ",2=" %2) a)
  ; (map-indexed #(= %2 :prfx) a))
  ; (map-indexed #(if (= %2 :prfx)
                  ; "hi"
                  ; false a))
  ; (filterv #(prn %1) a))

(= :prfx :prfx)
(keep-indexed #(if (odd? %1) %2) [:a :b :c :d :e])

(name :abc)

(nil? {})
(empty? {:a 7}

 (fn [board query-v]
   (let [diff-full (clj-data/diff board @*last-board*)
         diff-a (first diff-full)
         diff-b (second diff-full)
         diff-c (nth diff-full 2)]
     (let [[row0 row1 row2 ] diff-a]
          (let [changed-tiles (filter some? diff-a)]))
     (let [tile-deltas (ff-board/parse-delta-2 diff-a)]
       (doseq [{:keys [row col abc state]} tile-deltas]
         (rf/dispatch [::ff-events/draw-tile row col]))))
   (swap! *last-board* (fn [x] board))))

(let [a (-> (re-matches #"^(.*)-.*" "player") second)]
  (case a
    "btm" (prn "bottom")
    nil (prn "catch-all")))

(-> (re-matches #"^(.*)-.*" "player") second)
:cube-test.frig-frog.rules/player

(let [a :cube-test.frig-frog.rules/player]
  (prn (name a)))
  ; (prn (str (symbol a)))
  ; (prn (namespace a))
  ; (re-matches #".*/player$" (str (symbol a))))

(name :def/abc)
(name :cube-test.frig-frog.rules/player)

(case "def"
     "abc" (prn "hi")
     "def" (prn "bye"))

(let [mesh-id "btm-ball-1"]
  (re-matches #"^([^-]*)-.*" mesh-id))

(let [id :cube-test.frig-frog.rules/btm-player]
  (prn (name id))
  (re-matches #"^([^-]*)-.*" (str (symbol id))))

(defn gen-mesh-id-from-rule-id
  ([id] (gen-mesh-id-from-rule-id id nil))
  ([id sub-id]
   (let [kind (second (re-matches #"^.*/(.*)" (str id)))]
     (if sub-id
       (str kind "-" sub-id)
       kind))))

(def a 7)
(ns fuck-it)
(defn abc [])

(gen-mesh-id-from-rule-id :cube-test.frig-frog.rules/btm-player 1)
clj꞉cube-test.tmp-test꞉> 
(+ (let [f #(if (string? %) (str "\"" % "\"") %)] (js/console.clear) #?(:cljs (apply js/console.log["\n" (f  (quote 1)) "\n" "\n" "=>" (f 1) "\n "])) 1) #_"AYCl3RM590NZaiJ" 1)
(+ 1 1)
(int 1.2)
(int (/ 5 1.2))


(let [r (for [i (range (rand 8))] [i])
      rots (reduce into [] r)]
    (prn "r=" r)
    (prn rots))

(rand 8)
(for)
(range 8)
(nth (range 8) 0)

(rand 5 8)
(let [a])
(repeat 5 8)
(range 7 0 -1)
(range 0 8)
(range 8)
(int (rand 3))

;; good-use
(defn remove-from-vec [v idx]
  ; (prn "remove-from-vec: idx=" idx)
  (let [
        ; rows (range 8)
        ; rows-2 (map #(if (= %1 idx)))
        rows-2 (map-indexed
                (fn [i itm] (if (= i idx)
                              nil
                              itm))
                v)
        rows-3 (filter #(not (= %1 nil)) rows-2)]
    ; (prn "remove-from-vec: rows-3=" rows-3)
    rows-3))

; (let [idx-2 (int (rand 8))]
;   (prn "idx-2=" idx-2)
;   (remove-from-vec (range 8) 2))
;; good
(let [idx (int (rand 7))
      rows (range 8)
      pick (nth rows idx)
      rows-r (remove-from-vec rows idx)
      idx-2 (int (rand 6))
      pick-2 (nth rows-r idx-2)
      rows-r-2 (remove-from-vec rows-r idx-2)]
  (prn "pick=" pick "row-r=" rows-r)
  (prn "pick-2=" pick-2 "row-r-2=" rows-r-2))

; (let [picks []])
(def picks [])

;; good-use
(let [a []]
  (set! picks [])
  (reduce (fn [acc v]
            (prn "acc=" acc ",v=" v)
            (do
              (let [idx (int (rand v))
                    pick (nth acc idx)]
                (prn "pick=" pick)
                (set! picks (conj picks pick))
                (remove-from-vec acc idx))))
          (range 8)
          (range 8 4 -1))
          ; [(int (rand 8)) (int (rand 7)) (int (rand 6))])
  (prn "picks=" picks))

(reduce (fn [acc v] (do
                      (prn "acc=" acc ",val=" val)
                      (conj acc v)))
        []
        (range 5))

(conj [] 1)
(range 7 0 -1)

(reduce (fn [val accum] (prn "val=" val ",accum=" accum)) [0 1 2] (range 5))
(reduce)
(map #())
(let [rows (range 8)]
    rows-2 (map #(if (= %1 idx)
                   nil
                   %1)
                rows)
    rows-3 (filter #(not (= %1 nil)) rows-2)
  (prn "result=" rows-3))

(let [rows (range 8)
      y-rnd (map #(-> (rand %1) (int)) (repeat 4 8))
      y-rnd-2 (map
               #(let [rnd-idx (rand %1)
                      rnd-row (nth rows rnd-idx)]))]
  (prn "rows=" rows)
  (prn "y-rnd=" y-rnd))

(let [s #{1 2 3}
      t (conj s 4)]
  (prn "count t=" (count t)))

(def urs-seq #{})
(def urs-idx 0)
"generate a non-repeating set of random numbers
from the specified range for a length of n
(unique-rnd-seq 8 4) -> #{2 3 5 6}
(unique-rnd-seq 10 18 3) -> #{11 13 18} "
(defn unique-rnd-seq
  ([range-max n] (unique-rnd-seq 0 range-max n))
  ([range-min range-max n]
   (set! urs-seq #{})
   (set! urs-idx 0)
   (prn "rang-max=" range-max "range-min=" range-min ",n=" n)
   (while (and (< (count urs-seq) n) (< urs-idx (* range-max 3)))
     (set! urs-seq (conj urs-seq (+ (rand-int (- range-max range-min)) range-min)))
     (set! urs-idx (+ urs-idx 1))
     (prn "loop: urs-seq=" urs-seq ", urs-idx=" urs-idx))
   urs-seq))
   ; (let [range (range range-min range-max)])
   ; (map-indexed
   ;   (fn [i itm]))))
(unique-rnd-seq 8 4)
(unique-rnd-seq 8 16 4)

urs-seq
(take 5 (repeatedly #(rand-int 8)))

(map-indexed
 (fn [i x]))

(filter
 #(do
    (prn "%1=" %1)
    (< %1 10))
  (range 20))

(let [x 0]
  (set! urs-seq #{})
  (some #(do
           (set! urs-seq (conj urs-seq %1))
           (>= (count urs-seq) 5))
    (take 20 (repeatedly #(rand-int 8))))
  urs-seq)

(loop [x 10 y 7]
  (when (> x 1)
    (println x "," y)
    (recur (- x 2) (- y 1))))

(loop [rnd-stream (take 20 (repeatedly #(rand-int 8)))
       uniq-seq #{}
       idx 0]
  (if (and (< (count uniq-seq) 4) (< idx 30))
    (recur rnd-stream (conj uniq-seq (nth rnd-stream idx)) (inc idx))
    uniq-seq))

(rand-int 9)

(every? (< 8) #{0 4 3 5})
(every? #(< % 3) #{0 4 3 5})

(nth (seq #{1 2 3}) 1)

(let [s #{1 2 3}]
  (map-indexed (fn [i x]
                 (prn "i=" i ",x=" x)
                 (prn "s-i=" (nth (seq s) i)))
       #{2 4 8}))

(keyword (str "abc" "-" (+ 1 1)))
(neg? -7)

(let [v1 (bjs/Vector3. 1 1 -1)
      _ (.scaleInPlace v1 0.3)]
  (prn "v1=" v1))

(-> 2 * (+ 4 3))

(-> 2 (* (+ 4 3)))

(def a 0)
(+ a 1)

(let [a 0
      b (or nil (+ a 2))]
  (prn "b=" b))

(let [h {}
      h2 (assoc h :a 7)]
  (prn "h2=" h2))

(let a []
  (c))

(name :abc)

; [{:id :ff}
;  {:id :cube-spin}]
(str :ff :abc)
(hash-map :a 7)

(let [app-ids [:ff :cube-spin :face-slot :vrubik :get-cube :twizzlers :beat-club]
      r (vec (map #(hash-map :id %) app-ids))]
      ; r (doall (map #(str %) app-ids))]
      ; r2 (doall (map (fn [x] {:id x})))]
  (prn "r=" r))
  ; (prn "r2=" r2))

(let [h {:a [1 2 3] :b (count (:a h))}]
  (prn h))

(name :ff)
(nth [0 1 2] 1)

(map #(+ %1 %2) [1 2 3] [2 1 0])

(defn get-path-file [file-name]
  (+ 1 1))

(get-path-file "/abc/def/ghi.txt")

(re-seq #"([/\w]*)/([\w]*)" "abc/def/ghi.txt")
(re-matches #"([a-z]*)" "abc/def/ghi.txt")
(re-matches #"^abc" "abc/def/ghi.txt")
(re-matches #"^abc" "abcdef")
(re-seq #"\w+" "mary had a little lamb")
(+ 1 1)
(re-matches #"^hello, (.*)" "hello, world")
(re-matches #"^([\w]*,) (.*)" "hello9, world")
(re-matches #"^([\w/]*)/([\w\.]*)$" "abc/def/ghi.txt")

(name :ff)
(str (name :ff) "def")
(str :ff)

(get-in {:abc {:def 7}} [:abc :def])
(mod 7 7)
(dec 5)
(count [ 1 2 3])
(re-matches #".*root.*" "ff-soot")

(assoc [0 1 2] 1 3)
(let [h {:abc {:def 7}}
      h2 {:abc [{:def 7}]}]
  ; (assoc-in h [:abc :def] 8)
  (assoc-in h2 [:abc 0 :def] 9))
  ; (filter))
  ; (assoc-in h [:abc] 7))
; (range 4)
; (filter #(= %1 2) [1 2 3 2])
(->> [0 1 2] (map #(inc %1)))
(map #(inc %1) [0 1 2])
(->> [{:id 1} {:id 2} {:id 2}]
    ; (map-indexed #(if (= (:id %2) 2)))
    (map-indexed (fn [i x]
                   (if (= (:id x) 7)
                     i
                     nil)))
    (filter #(not (nil? %)))
    (first))

; (filter #(not (nil? %)) (map-indexed #(if ())))
(defn f1 []
  (prn "hi"))

(defn f2 []
  (prn " world"))


(f2)
(comp f1 f2)

(defn f3 [p1]
  (prn "msg is:") (p1))

(f3 f1)
(f3 (comp f1 f2))
(f3 #(-> f1 f2))
(f3 #(do (f1) (f2)))
