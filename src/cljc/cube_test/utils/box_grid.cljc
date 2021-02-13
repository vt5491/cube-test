;;
(ns cube-test.utils.box-grid
  (:require
    [clojure.spec.alpha :as s]
    [clojure.spec.test.alpha :as stest]
    [cube-test.utils.box-grid-spec :as box-grid-spec]))
    ; [cljs.repl :refer-macros [doc]]
    ; [cljs.repl :refer [doc]]))
  ; (:require [cube-test.base :as base]))

(defn do-it [n]
  (+ n 2))
; (s/def ::do-it-args (s/cat :n int?))
; ; (s/def ::do-it-args int?)
;
; (s/def ::do-it-ret int?)
;
; (s/def ::do-it-fn
;   (fn [{:keys [args ret]}]
;     (let [arg (:n args)]
;       ; (println "do-it-fn: args=" args ", arg=" arg ", ret=" ret)
;       ; true)))
;       (= ret (+ arg 1)))))
;
; ; (s/def ::do-it-fn
; ;   (fn [{:keys [arg ret]}]
; ;     nil))
;
; ; (s/fdef box-grid/do-it)
; ; (s/fdef 'cube-test.utils.box-grid/do-it)
; (s/fdef cube-test.utils.box-grid/do-it
; ; (s/fdef `do-it
;         :args ::do-it-args
;         :ret ::do-it-ret
;         :fn ::do-it-fn)
;
; ; (def r (stest/instrument `do-it))
; (def r2 (s/exercise `do-it))

; (s/conform ::ingredient [2 :teaspoon])
(defn print-it [])
  ; (println "hi from print it, doc(map)=" (cljs.repl/doc map)))
  ; (println "hi from print it, doc(map)=" (doc map)))
  ; (println "box-grid.cljc r2=" r2)
  ; (println "box-grid: exercise=" (s/exercise `do-it)))
  ; (println "box-grid: check=" (stest/check `do-it))
  ;; following works
  ; (println "box-grid: check=" (stest/check 'cube-test.utils.box-grid/do-it)))
  ; (println "box-grid: check=" (stest/check 'cube-test.utils.box-grid-spec/do-it))
  ; (println "box-grid: check=" (stest/check box-grid-spec/do-it))
  ; (println "box-grid: abbrev-result=" (stest/abbrev-result 'cube-test.utils.box-grid/do-it))
  ; (println "box-grid: exercise=" (s/exercise 'cube-test.utils.box-grid/do-it)))
  ; (println "box-grid: exercise=" (s/exercise cube-test.utils.box-grid/do-it)))
  ;; following 3 work
  ; (println "box-grid: exercise ::do-it-args=" (s/exercise ::do-it-args))
  ; (println "box-grid: exercise ::do-it-ret=" (s/exercise ::do-it-ret))
  ; (println "box-grid: conform ::do-it-args=" (s/conform ::do-it-args [1])))

; (defrecord Box-grid)
(defn init-box-grid []
  (let [grid {}]
    grid))
