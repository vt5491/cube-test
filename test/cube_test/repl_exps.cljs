(ns cube-test.repl-exps
 (:require
   ; [clojure.test :refer-macros [is testing deftest run-tests] :as t]
   ; [re-frame.db       :as db]
   [cube-test.main-scene :as main-scene]
   [babylonjs :as bjs]))
   ; [promesa.core :as p]))

(comment
  ;; Don't forget to ctrl-alt-shift-e these lines individually and not ctrl-alt-shift-b
  ;; the entire comment (because they're under a global comment)
  ;; Also, need to be on left-paren not on end-paren

  ;; switch to cljs repl
  (shadow.cljs.devtools.api/nrepl-select :app))

(+ 1 2)
(+ js/Math.PI 1)
(js/parseInt "5")
(clj->js {:a 7 :b 8})

(let [h {:a 7 :b 8}]
  (js-obj h)
  (clj->js h))

(js/Object.keys (js-obj "a" 7 "b" 8))
(js/Object.keys (clj->js {:a 7 :b 8}))
