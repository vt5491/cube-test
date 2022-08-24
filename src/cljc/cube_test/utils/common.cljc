;; This is for common utils that can be written in .cljc
;; For .cljs specifics utils, refer to 'cube-test.utils
(ns cube-test.utils.common)

;; This gives percentage difference with respect to the biggest of the two.
;; e.g (relavtive-difference 5 4) => 0.2
(defn relative-difference ^double [^double x ^double y]
  (/ (Math/abs (- x y))
     (max (Math/abs x) (Math/abs y))))

;; e.g is the second number to with with 10% of the first (0.5 in this case)
;; (common-utils/close? 0.1 5.0 5.5) => true
;; (common-utils/close? 0.1 5.0 5.6) => false
(defn close? [tolerance x y]
  (< (relative-difference x y) tolerance))

;; works with math.numeric-tower which won't work in a js environment.
(defn round-places [number decimals]
  (let [factor (Math/pow 10 decimals)]
    (float (/ (Math/round (* factor number)) factor))))

(defn merge-dbs [db1 db2]
  "Merge two maps into one"
  (reduce #(do
             (assoc %1 (first %2) (second %2)))
          db1 db2))

(defn idx-of-id
  "Given a vector of hashes (each with an :id key), return the (first occurence)
   index that matches the supplied id.
   Example:
   (idx-of-id [{:a 1 :id :1},{:b 2 :id :3},{:c 3 :id :2},{:d 4 :id :3}] :3) -> 1"
  [vec id]
  (let [tmp (map-indexed #(if (= (:id %2) id)
                            %1
                            nil)
                          vec)
        tmp-2 (filter #(not (nil? %1)) tmp)]
    (first  tmp-2)))

(defn idx-of-id-stem
  "Given a vector of hashes (each with an :id-stem key), return the (first occurence)
   index that matches the supplied id.
   Example:
   (idx-of-id-stem [{:a 1 :id-stem :1},{:b 2 :id-stem :3},{:c 3 :id-stem :2},{:d 4 :id-stem :3}] :3) -> 1"
  [vec id-stem]
  (let [tmp (map-indexed #(if (= (:id-stem %2) id-stem)
                            %1
                            nil)
                          vec)
        tmp-2 (filter #(not (nil? %1)) tmp)]
    (first  tmp-2)))

;; get all the meshes that start with the given stem-id
;; e.g. return [(mesh tr-1-0) (mesh tr-1-1)] for stem-id "tr-1"
(defn get-meshes-by-stem [stem-id])

(defn gen-mesh-id-from-rule-id
  ([id] (gen-mesh-id-from-rule-id id nil))
  ([id sub-id]
   (let [kind (second (re-matches #"^.*/(.*)" (str id)))]
     (if sub-id
       (str kind "-" sub-id)
       kind))))

(defn in?
  "true if coll contains elm"
  [coll elm]
  (some #(= elm %) coll))

"generate a non-repeating set of random numbers
from the specified range for a length of n
(unique-rnd-seq 8 4) -> #{2 3 5 6}
(unique-rnd-seq 10 18 3) -> #{11 13 18} "
(defn unique-rnd-seq
  ([range-max n] (unique-rnd-seq 0 range-max n))
  ([range-min range-max n]
  ;; note: n * 10 is just a heuristic amount to prevent run away loops
   (let [limit (* n 10)]
     (loop [rnd-stream (take limit (repeatedly #(rand-int (- range-max range-min))))
            uniq-seq #{}
            idx 0]
       (if (and (< (count uniq-seq) n) (< idx limit))
         (recur rnd-stream (conj uniq-seq (+ (nth rnd-stream idx) range-min)) (inc idx))
         uniq-seq)))))

; "given a path like 'abc/def/ghi.txt' return:
;  {:path 'abc/def/' :file 'ghi.txt'}"
(defn extract-path-file [file-name]
  (let [r (re-matches #"^([\w/]*/)([\w\.]*)$" file-name)]
    (if r
      {:path (nth r 1) :file (nth r 2)}
      nil)))
