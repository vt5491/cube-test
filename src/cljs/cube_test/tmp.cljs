(ns cube-test.tmp)
;   (:require
;    [re-frame.core :as re-frame]
;    [babylonjs :as bjs]))
  ; (:require [clojure.spec.alpha :as s]
  ;          [clojure.spec.test.alpha :as stest]
  ;          [cube-test.utils.box-grid :as bg]))
  ; (require 'cube-test.utils.box-grid :as bg))

; hook-up
(shadow.cljs.devtools.api/nrepl-select :app)
(clojure.browser.repl/connect "http://localhost:8778")
;
; (+ js/Math.PI 1)
;
(+ 1 2)
;
; (js/setTimeout (fn [] (prn "hi")) 2000)
;
; ; (let [x (future (js/setTimeout #() 2000) (+ 41 1))] [@x @x])
; (def f (fn [] (js/setTimeout (fn [] (prn "hi")) 2000)))
;
; (for [i (range 6)] [i])
;
; (def f (for [i (range (rand 6))] [i]))
;
; (def a (for [i (range 6)] [i]))
;
; (get 0 a)
;
; (prn "val=" (get-in a [1]))
;
; (nth a 1)
;
; (map #(do))
;
; (accum [0] [1])
;
; (reduce #(conj [] (nth %1 0)) [1] [2])
;
; (reduce #(prn %1) [1 2])
;
; (conj [] 1 2)
; (conj [] [1] [2])
;
; (reduce #(conj))
;
; (reduce #(cons %2 %1) [1 2 3] [4 5 6] [8 9])
; (reduce into [] a)
;
; (def a [1 2 3])
; (def mr 0)
;
; (if (> (count a) @*mr*) (swap! *mr* (fn [x] (count a))))
;
; (set! mr 1)
; (def ^:dynamic *mr* (atom 0))
; (swap! *mr* #(1))
; (swap! *mr* (fn [x] 1))
;
; (not nil)
;
; (def a (atom false))
;
; (def s "red-cube-1")
;
; ; (re-matches #"hello, (.*)" "hello, world")
; (re-matches #"^([a-z]*)-(cube)-(\d)" s)
; (def r (re-matches #"^([a-z]*)-(cube)-(\d)" s))
;
; (get r 3)
; (keyword "1")
;
; #f, val coll
; (reduce #(do
;            (prn "1=" %1 ",2=" %2)
;            (conj %1 %2))
;         []
;         [1 2 3])
;
; (conj [] 1)
;
; (assoc {} :a 1)
; (reduce #(do
;            ; (prn "1=" %1 ",2=" %2)
;            (assoc %1 (get %2 0) (get %2 1)))
;         {} {:a 1 :b 2})
;            ; (assoc %1)))
;
; (map key {:a 1 :b 2})
; (key 1)
;
; (get [:a 1] 1)
;
; (assoc-in {} [:a :b] 1)
;
; (defn f []
;   (let [a 1]
;     [1 2 3]))
;
; (f)
;
; (do (let [r 7]
;       r))
;
; (def nest-map {})
;
; (def nest-map (assoc-in nest-map [:front :1] "abc"))
;
; (nest-map :front)
;
; (:front nest-map)
;
; (nest-map :front :1)
;
; (get-in nest-map [:front :1])
;
; (def m (assoc-in {} [:top :a] 1))
;
; (def m2 (assoc-in m [:top :b] 2))
;
; (def m3 (assoc-in m2 [:bottom :a] 11))
;
; (def m4 (assoc-in m3 [:bottom :b] 12))
;
; (reduce #(do (assoc %2 :val %1)) {} m4)
; (assoc m4 :c 7)
;
; (reduce #(do (prn "1=" %1 ",2=" %2)) {} m4)
;
; (reduce #(do (assoc %1 (first %2) (second %2))) {} m4)
;
; (key [:top])
; (def a [:top {:a 1, :b 2}])
; (type a)
; (first a)
; (count a)
; (count (second a))
;
; (reduce #(do (prn "hi")
;            (assoc %1 (first %2) 1)) {} m4)
;
; (reduce #(prn "hi"
;            (assoc %1 (first %2) 1)) {} m4)
;
; (def b {:a (js-obj "a" 7 "b" 8)})
; (second [:top {:a 1, :b 2}])
;
; (= :rear (first [:mid]))
;
; (assoc-in {} [:front :1] 7)
;
; (type {})
; (type ())
; (type (1 2))
; (def l (list 1 2 3))
; (count l)
; (first l)
; ({:mid {:4 #object[t$jscomp$0 Name: blue_cube_4, isInstance: YES, # of submeshes: 1, n vertices: 24, parent: rubiks-cube, animation[0]: Name: anim, property: rotationQuaternion, datatype: Quaternion, nKeys: 2, nRanges: 0]}} {:mid {:7 #object[t$jscomp$0 Name: blue_cube_7, isInstance: YES, # of submeshes: 1, n vertices: 24, parent: rubiks-cube, animation[0]: Name: anim, property: rotationQuaternion, datatype: Quaternion, nKeys: 2, nRanges: 0]}} {:front {:4 #object[t$jscomp$0 Name: blue_cube_1, isInstance: YES, # of submeshes: 1, n vertices: 24, parent: rubiks-cube, animation[0]: Name: anim, property: rotationQuaternion, datatype: Quaternion, nKeys: 2, nRanges: 0]}})
;
; (def r {:mid {:4 #object[t$jscomp$0 Name: blue_cube_4, isInstance: YES, # of submeshes: 1, n vertices: 24, parent: rubiks-cube, animation[0]: Name: anim, property: rotationQuaternion, datatype: Quaternion, nKeys: 2, nRanges: 0]}}) {:mid {:7 #object[t$jscomp$0 Name: blue_cube_7, isInstance: YES, # of submeshes: 1, n vertices: 24, parent: rubiks-cube, animation[0]: Name: anim, property: rotationQuaternion, datatype: Quaternion, nKeys: 2, nRanges: 0]}} {:front {:4 #object[t$jscomp$0 Name: blue_cube_1, isInstance: YES, # of submeshes: 1, n vertices: 24, parent: rubiks-cube, animation[0]: Name: anim, property: rotationQuaternion, datatype: Quaternion, nKeys: 2, nRanges: 0]}} {:mid {:8 #object[t$jscomp$0 Name: blue_cube_8, isInstance: YES, # of submeshes: 1, n vertices: 24, parent: rubiks-cube]}} {:mid {:9 #object[t$jscomp$0 Name: blue_cube_9, isInstance: YES, # of submeshes: 1, n vertices: 24, parent: rubiks-cube]}} {:mid {:2 #object[t$jscomp$0 Name: blue_cube_2, isInstance: YES, # of submeshes: 1, n vertices: 24, parent: rubiks-cube]}} {:mid {:5 #object[t$jscomp$0 Name: blue_cube_5, isInstance: YES, # of submeshes: 1, n vertices: 24, parent: rubiks-cube]}} {:mid {:3 #object[t$jscomp$0 Name: blue_cube_3, isInstance: YES, # of submeshes: 1, n vertices: 24, parent: rubiks-cube]}} {:mid {:6 #object[t$jscomp$0 Name: blue_cube_6, isInstance: YES, # of submeshes: 1, n vertices: 24, parent: rubiks-cube]}}
;
; (count r)
; (def r {})
;
; (def s (assoc-in r [:front :1] "hi"))
; (def s2 (assoc-in s [:front :2] "hi2"))
;
; (def m {:a 1 :b 2})
;
; (def r (map (fn [x]
;               (println "x=" x))
;             m))
;
; (count r)
;
; (doseq m (fn [x]
;            (println "x=" x)))
; (doseq [x m] (println "x=" x))
;
; (def a 7)
; (condp = a
;   ;6 (println "hi")
;   (println "bye"))
;
; (def a {:a 1 :b 2})
;
; (reduce (fn [accum b]
;           (-> accum
;               (assoc-in [:abc (first b)] (second b))
;               (assoc-in [:def (first b)] (second b))
;               (assoc-in [:abc :ghi] 7)
;               (assoc-in [:abc :b] 12)))
;
;         {} a)
; (reduce (fn [accum b]
;           (println "b=" b))
;         {} a)
; (assoc {} :a 7)
;
; (map :a 1)
; {:a 1}
;
; (keyword (str 1))
; ;; 2020-08-14
; (def a {:0 "abc" :1 "def" :2 "ghi"})
;
; (let [idxs [:0 :2]]
;   (doseq [i idxs] (prn (a i))))
;
; (contains? a :0)
;
; (contains? a :7)
;
; (if (contains? a :7)
;   (do (prn "found"))
;   (do (prn "not found")))
;
; (def b [:0 :3 :6 :9 :15 :18 :21 :24])
;
; (contains? b 8)
; (some :1 b)
; (find b 2)
;
; (def c {:0 :3 :6 :9 :15 :18 :21 :24})
; (contains? c 0)
; (find c :3)
;
; ;; 2020-08-17
; (reduce (fn [x y]
;           ; (prn "y=" y)
;           (let [k (first y)
;                 v (second y)]))
;             ; (assoc x (keyword v) (str k))
;             ; (prn "hi")
;             ; (assoc x :a 7)))
;             ; (condp = k
;             ;   :0 (assoc x :a 7)
;             ;   (assoc x :b 8))))
;         {} a)
;
; (str :a)
;
; (map (fn [x]
;        (println "x=" x)
;        (first x)
;        (second x))
;      a)
;
; (def v [1 2 3])
;
; (map #(do
;         (+ %1 1)
;         (+ %1 2)) v)
;
; (map #(do
;         (+ %2 1)
;         (+ %2 2)) a)
;
; (map (fn [x] (+ x 1)) v)
;
; (map #(+ %1 1) v)
;
; (map #(do (+ %1 1)) v)
;
; (keys a)
;
; (def r (map (fn [x]
;               (prn "x=" x)
;               (first x))
;             {:a 1 :b 2 :c 3}))
;
; (map (fn [x] (prn "x=" x)) {:a 1, :b 2, :c 3})
;
; (doseq [i [1 2 3]] (fn [x] (prn "x=" x)))
;
; (doseq [i [1 2 3]] (do (prn "x=" i) (prn "y=" (+ i 1))))
;
; (let [x 3
;       y 4]
;   (<< "~{x} plus ~{y} equals ~(+ x y)."))
; (sorted-map)
;
; (into (sorted-map) [ 1 3 2])
; (into () '(1 2 3))
; (into [] '(1 2 3))
; (into {} '(1 2 3))
; (into {} {:a 1 :b 2 :c 3})
; (into [] {:a 1 :c 3 :b 2})
; (into (sorted-map ){:a 1 :c 3 :b 2})
;
; (into (sorted-map-by (fn [key1 key2]
;                        (compare (get results key2)
;                                 (get results key1))))
;       {:1 :a :10 :k :2 :b})
; (into (sorted-map-by =) {:1 :a :10 :k :2 :b})
;
; (re-matches #"\\:\\d\{1,2\}" (str :7))
;
; (re-matches #"^abc" "abcd")
; (re-find #"^abc" "abcd")
; (re-find #"^abc" "bcd")
; (re-find #"(:)(\d{1,2})" ":7")
;
; (-> (re-find #"(:)(\d{1,2})" (str :17)) (nth 2))
;
; (into (sorted-map-by (fn [key1 key2]
;                        (prn "key1=" key1 ",key2=" key2)))
;       {:1 :a :10 :k :2 :b})
;
; (def m {:1 :a :10 :k :2 :b})
;
; (get m :1)
;
; (compare :a :d)
;
; (into (sorted-map-by (fn [key1 key2])))
;
; (cube-test.utils/keyword-to-int :17)
;
; (prn cube-test.base/scale-factor)
;
; (ns user
;   (:require [cube-test.base :as base]))
; (+1 1)
; (require '[cube-test.base :as base])
; (ns user2 (:require [cube-test.base :as base]))
;
; (defn kw-to-int [kw]
;   (-> (re-find #"^:(\d{1,3})" (str kw)) (nth 1) (int)))
;
; (kw-to-int :17)
; (js/parseInt "17")
; (-> (re-find #"^:(\d{1,3})" :17))
;
; (into (sorted-map-by (fn [k1 k2]
;                        (let [num-1 (kw-to-int k1)
;                              num-2 (kw-to-int k2)]
;                          (prn "num-1=" num-1 ", num-2" num-2)
;                          (compare num-1 num-2))))
;       m)
;
; (compare 2 10)
;
; (assoc-in {} [:a :b] 7)
; (assoc-in nil [:a :b] 7)
; (def m {:a 1, :b {:c 7}})
;
; (m :a)
; (m [:b :c])
; (get-in m [:b :c])
;
; (assoc {} :a 7)
; (assoc {} :a 7 :b 8)
; (assoc {} :a 7, :b 8)
;
; (map #(+ 1 %1) [1 2 3])
; (map #(prn "1=" %1) [1 2 3])
; (map #(prn "1=" %1) {:a 1, :b 2})
;
; # 2020-08-20
; (def m {:1 7, :2 8, :3 9})
; (def m2 {:1 7, :2 8, :29 9})
;
; (filter (fn [x] (prn "x=" x)) m)
; (filter (fn [x] (= (second x) 8)) m)
;
; (contains? m :1)
; (contains? m2 :28)
;
; (contains? [:1 7] :1)
; (contains? [:1 7] 7)
;
; (first [:a 7])
;
; (contains? [:3 :a] :3)
;
; (some?)
;
; (some #{:a} [:a :b])
; (some #{:1 :2} [:3 :mesh])
; (contains? #{:1 :2} [:1 :mesh])
; (some [:1 :2] [:3 :mesh])
; (some :0 [:0])
; (some #{:0} [:0])
; #{:a}
; # 2020-08-21
; (map (fn [kv-pair]
;        kv-pair)
;      m)
;
; (get m :1)
; (get :1 m)
;
; (assoc m :1 17)
;
; (def m2 {:cell {:a 7 :b 8}})
;
; (-> (get m2 :cell) (assoc :a 17) (assoc :b 18))
;
; (doseq [x m] (do
;               (println "x=" x)
;               (let [k (first x)
;                     v (second x)]
;                 (assoc m k (+ v 1)))))
; (get [:a :b] 1)
;
; (def p false)
; (set! p true)
; (atom f false)
; (def ^:dynamic *flag* (atom false))
;
; (swap! *flag* (fn [x] true))
; (swap! *flag* (fn [x] false))
;
; @*flag*
;
; (get-in m2 [:cell :a])
;
; (not (nil? (get-in m2 [:cell :c])))
;
; (+ 1 1)
;
; (def m {:a 1, :b 2, :c 3})
;
; (def r (filter (fn [kv-pair]
;                  (let [k (first kv-pair)
;                        v (second kv-pair)]
;                    (> v 1))) m))
;
; (type (doall r))
;
; (map (fn [kv-pair]
;        (prn "hi")
;        (prn "kv-pari"))
;      m)
;
; (def m2 {:g 7 :h 8 :i 9})
;
; (merge  m m2)
; (map (fn [x] (merge m x)) m2)
;
; (reduce (fn [accum kv-pair]
;           (let [k (first kv-pair)
;                 v (second kv-pair)]
;             (assoc accum k v)))
;         {} (merge m m2))
;
; (def m {:1 {:a 7}, :2 {:b 8}})
;
; (def k :abc)
;
; {k 7}
; (m)
; [:k]
;
; (map (fn [kv-pair]
;        (println kv-pair)
;        (let [k (first kv-pair)
;              v (second kv-pair)]
;          [k (assoc v :frame-cnt 30)]))
;      m)
;
; (def ^:dynamic *a* (atom {:1 {:a 7 :b 8}, :2 {:a 17 :b 18}}))
;
; (swap! *a* (fn [x] println "x=" x))
;
; (swap! *a* (fn [x] 7))
;
; (map (fn [kv]
;        (println "kv=" kv))
;      @*a*)
;
; (dec 7)
;
; (map (fn [kv]
;        (let [k (first kv)
;              v (second kv)
;              b (get-in v [:b])]
;          [k (assoc v :b (dec b))]))
;      @*a*)
;
; (assoc [:1 {:a 7}] :1 7)
; (first [:1 {:a 7}])
; (second [:1 {:a 7}])
;
; (def a 7)
; (when (and a (println "hi")))
;
; (count)
; (when 0
;   (println "hi"))
;
; (get m :2)
;
; (every? nil? [nil 7])
;
; (def v [:1 :2 :3])
;
; (reduce (fn [accum keyword]
;           (assoc accum keyword nil))
;         {} v)
;
; (interleave v [nil nil nil])
;
; (r)
;
; (find m :1)
;
; (get #{v} :1)
;
; (contains? v :1)
;
; (def k :1)
; (some #{k} v)
;
; (assoc {} :a 7 :b 8)
;
; (apply (fn
;          ([x]
;           (println "x=" x))
;          ([x y]
;           (println "x+y=" (+ x y)))) [7 8])
;
; (apply (fn
;          ([x]
;           (println "x=" x))
;          ([x y]
;           (println "x+y=" (+ x y)))) [7 8])
;
; (count "abc")
; (apply count ["abc"])
;
; (let [f (fn ([x] (f x 2))
;           ([x y] (+ x y)))])
;
; (my-f fn ([x] (my-f x 2)
;           ([x y] (+ x y))))
;
; (apply (fn my-f [x y]
;          (+ x y)) [1 2])
;
; (apply (fn my-f
;          ([x] (my-f x 3))
;          ([x y]
;           (+ x y)))
;   [1])
; ;; fun with specs
; (s/conform even? 1001)
;
; (s/valid? even? 3)
;
; (s/valid? (fn [x] (> x 5)) 1)
; (s/valid? #(> %1 5) 10)
;
; (s/def ::date inst?)
;
; (s/def ::suit #{:club :diamond :heart :spade})
; (s/def ::vt-suit #{:club :diamond :heart :spade})
; (s/def ::black #{:club :spade})
; (s/def ::black-suit (and ::suit ::black))
; (s/valid? ::suit :lub)
; (s/valid? ::vt-suit :heart)
;
; (s/valid? (s/and ::suit ::black) :club)
; (s/valid? ::black-suit :club)
; (s/conform ::black-suit :club)
; (s/explain-data ::black-suit :club)
;
; (s/doc ::black-suit)
; (defrecord Person [first-name last-name email phone])
;
; (:first-name Person)
;
; (def p (user.Person "joe" "elliot" "abc"))
; (->Person "Bugs" nil nil nil)
; (def p (->Person "joe" "elliot" "abc" "123"))
; (def q (Person. "joe2" "elliot" "abc" nil))
;
; (:first-name p)
; (:first-name q)
;
; ; (defrecord Vt-point [:x-disp :y-disp])
; (defrecord Vt-point [x y])
;
; (s/def ::x-disp int?)
; (s/def ::y-disp int?)
;
; (s/def ::two-point (s/cat :x ::x-disp :y ::y-disp))
;
; (s/valid? ::two-point [1 2])
; (s/conform ::two-point [1 2])
;
; (def p (->Vt-point 1 2))
; (:x p)
;
; (s/valid? ::two-point p)
;
; (cube-test.utils.box-grid/do-it 2)
;
; (s/conform :cube-test.utils.box-grid.do-it-ret [1])
; (require 'cube-test.utils.box-grid)
;
; (cube-test.utils.box-grid/do-it 1)
; (:cube-test.utils.box-grid.do-it-args)
; (s/conform :cube-test.utils.box-grid/do-it-args [1])
; (s/conform :cube-test.utils.box-grid/do-it-args 1)
;
; (map? {:a 7})
; (seq? {:a 7})
;
; (doc 'cube-test.utils.box-grid/do-it)
; (cljs.repl/doc map)
; (cljs.repl/doc map)
; (doc map)
; (s/explain :cube-test.utils.box-grid/do-it-args [1])
; (s/explain :cube-test.utils.box-grid/do-it-args 1)
;
; cube-test.base/dummy
; cube-test.base/top-level-scene
;
; (println "dummy=" cube-test.base/dummy)
;
; (js/parseInt "6")
;; two command to start a cljs browser repl
(do
  (shadow.cljs.devtools.api/nrepl-select :app)
  (clojure.browser.repl/connect "http://localhost:8778"))

(in-ns 'cube-test.msg-cube.msg-cube-scene)

gui-plane

(when cube-test.msg-cube.msg-cube-scene/gui-plane
  (.dispose cube-test.msg-cube.msg-cube-scene/gui-plane))

(def a (cube-test.main-scene.scene/getMeshByID "sphere"))

cube-test.msg-cube.msg-cube-scene/top-plane

(+ 1 1)
(+ 1 2)

;; destructuring test
(defn move [{:keys [body direction] :as snake} & grow]
 (println "snake=" body))

(move {:body :direction})


(defn gen-m [{:keys [id level text] :as msg}]
  (println "msg=" msg)
  (println "id=" id))

; (defn gen-m [{:keys [id level text] :as msg}]
;   (println "msg=" msg))

(gen-m {:id 7})

(defn gen-m2 [{:keys [id level text] :as msg}]
  (println "id=" (msg :id)))

(gen-m2 {:id 7})

(def my-msg {:id 7 :text "hi"})

my-msg
; (defn head-outside-bounds? [[head-x head-y]])
(defn fa [[a b]]
  (println "a=" a))

(fa [[1 2]])

(defn fb [a]
  (prn "a=" a))

(fb 1)
(defn fc [x]
  7)

(def a (conj (conj [] {:a 7}) {:a 8}))

(println "val=" cube-test.msg-cube.msg-cube-game/msgs)

(def m {})

(assoc m :a 7 :b 8)

;; play with msg extraction

(def bd {:msgs [
                {:id 1 :text "abc" :level :INFO}
                {:id 2 :text "def" :level :INFO}
                {:id 3 :text "ghi" :level :INFO}]})

(-> (bd :msgs) (get 1))
(-> (bd :msgs) (count))

(pos? -7)
(while pos? [-1 -2 3 -1])

(def a (atom 10))

(doseq [x [1 2 3]]
  (println x))

(find {:a 1 :b 2} :c)

(find [{:a 1} {:b 2}] :a)

(contains? {:a 1 :b 2} :a)

(contains? [{:a 1 :b 2} {:a 11 :b 21}] [:a 11])

(defn get-by-id [db id]
  "extract the msg map from db :msgs by the msg id")

(contains? (bd :msgs))

(bd :msgs)

; #(+ %1 %2)
(map #(println "1=" %1) [1 2 3])

(map #(do
        (let [a 3]
          (+ %1 a))) [1 2 3])

(assoc {:a 1} :a 2)

(def a (+ 1 6))

(map #(do
        (let [id (%1 :id)
              new-id (+ id 1)]
          (println "new-id=" new-id))) (bd :msgs))
          ; (assoc %1 :id new-id))) bd)

*ns*

(js/parseInt "5")

(first '({:a 7}))
(first '(1 2 3))

;; 2023-05-26 first calva repl
;; Note: use joyride, not babashka or npp
;; (defn gen-m [{:keys [id level text] :as msg}])
(defn my-f1 [{:keys [a b c] :as vals}]
  (prn "a=" (:a vals) ",b=" (:b vals)))

(my-f1 {:a 4 :b 5})

(def m2 {:a 7 :b 8})
(:a m2)

(defn my-f2 [s {:keys [a b] :as vals}]
  (prn "s=" s ",a=" (:a vals)))

(my-f2 "hi" {:a 7 :b 8})
(my-f2 "hi" {:b 7})
(my-f2 "hi")

(defn my-f3 
  ([s] (my-f3 s {:a 1}))
  ([s {:keys [a b] :as vals}]
   (prn "s=" s ",a=" (:a vals))))

(my-f3 "hi")

(defn my-f4
  ([s] (my-f4 s 6))
  ([s t] 
   (prn "s=" s ",t=" t)))

(my-f4 4)
(my-f4 4 7)