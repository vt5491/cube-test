;; Note: this is '.cljs' file and it's using 'cljs.test' (not 'clojure.test')
(ns cube-test.base-test-2
; (ns cube-test
  ; (:require [cljs.test :refer-macros [deftest is testing run-tests]])
  (:require [cljs.test :refer (deftest is testing run-tests)]))
  ; (:require [cljs.test :as ct])
  ; (:require [cljs.test :as ct :refer (deftest is testing run-tests update-current-env!)])
  ; (:require [cljs.test :as ct :refer (deftest is testing run-tests)]
  ; (:require [clojure.test :refer :all]))
  ; (:require [cljs.test :refer :all]))

(println  "now in src/test/cube_test/base_test_2.cljs")

(deftest a-test
  (testing "FIXME, I fail."))
    ; (is (= 0 1))
