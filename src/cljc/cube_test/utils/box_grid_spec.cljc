;; Note: you "drive" this ns by simply having a require it for it somewhere e.g.
;; in your test file or in the ns that has the wrapped fn (box-grid in this cases)
;; You do not need to call 'init' or anything.
;; Once this gets driven, it hooks the wrapped fn, even if it's in a different ns.  You don't
;; have to require the foriegn ns either, just explictly reference it on the 's/fdef' statement.
;; If you reference only in test, then you don't get hot-compile when you save.  If you referenc
;; from box_grid, you get hot-compile, but then you'll also include in runtime, which isn't necesarilly
;; bad (unlike test code which you wouldn't want to include in your dev or prod runtime)
(ns cube-test.utils.box-grid-spec
  ; (:require [cube-test.base :as base]))
  (:require [clojure.spec.alpha :as s]))
            ; [cube-test.utils.box-grid :as box-grid]))

(println "box-grid-spec: now being driven")
;; my first real fn spec
; (s/def)
; (s/def ::rand-args (s/cat :n (s/? number?)))
; (s/def ::rand-ret double?)
; (s/def ::rand-fn ​
;(​fn​ [{:keys [args ret]}] ​
;(​let​ [n (or (:n args) 1)] ​  (​cond​ (zero? n) (zero? ret) ​  (pos? n) (and (>= ret 0) (< ret n)) ​  (neg? n) (and (<= ret 0) (> ret n))))
; (s/fdef clojure.core/rand ​  :args ::rand-args ​  :ret  ::rand-ret ​  :fn   ::rand-fn)

(s/def ::do-it-args (s/cat :n int?))

(s/def ::do-it-ret int?)

; (s/def ::do-it-fn
;   (fn [{:keys [arg ret]}]
;     (= ret (+ arg 1))))

(s/def ::do-it-fn
  (fn [{:keys [args ret]}]
    (let [arg (:n args)]
      ; (println "do-it-fn: args=" args ", arg=" arg ", ret=" ret)
      ; true)))
      (= ret (+ arg 1)))))

; (s/fdef box-grid/do-it)
(s/fdef cube-test.utils.box-grid/do-it
        :args ::do-it-args
        :ret ::do-it-ret
        :fn ::do-it-fn)

;; init-box-grid
(s/def ::init-box-grid-args (s/cat))

(s/def ::init-box-grid-ret map?)

(s/def ::init-box-grid-fn
  (fn [{:keys [args ret]}]
    (map? ret)))

(s/fdef cube-test.utils.box-grid/init-box-grid
        :args ::init-box-grid-args
        :ret ::init-box-grid-ret
        :fn ::init-box-grid-fn)

;; vt 2021-02-09
; (s/valid? (s/cat :num number?
;                  :key keyword?) [5 :x])
;
; (s/explain (s/cat :num number?
;             :key keyword?) ["5" :x])
;
; (def b 17)
; (def c 12)
