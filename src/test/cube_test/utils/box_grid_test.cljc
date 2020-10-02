; (ns cube-test.utils.box-grid-test
;   (:require [clojure.test :refer :all]
;             '[clojure.spec.alpha :as s]
;             '[clojure.spec.test.alpha :as stest]
;             [cube-test.utils.box-grid :as box-grid]))

; (ns cube-test.utils.box-grid-test
;   (:require '[clojure.spec.alpha :as s]
;             '[clojure.spec.test.alpha :as stest]))

(ns cube-test.utils.box-grid-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]
            [cube-test.utils.box-grid :as box-grid]))
            ; [cube-test.utils.box-grid-spec :as box-grid-spec]))

; (println  "now in box-grid-test, do-it(6)=" (box-grid/do-it 6))
; (println "stest/instrument=" stest/instrument)
(println "box-grid-test: r2=" (box-grid/print-it))
(println "test: stest/check do-it=" (stest/check 'cube-test.utils.box-grid/do-it))
(println "test: stest/check init-box-grid=" (stest/check 'cube-test.utils.box-grid/init-box-grid))
; (deftest a-test
;   (testing "FIXME, I fail."
;     (is (= 0 0))))
;     ; (is (= dummy-base/abc 8))))
; cube-test.utils.box-grid/do-it
; (stest/instrument ​box-grid/do-it)
; (stest/instrument ​cube-test.utils.box-grid/do-it)
; (stest/check 'cube-test.utils.box-grid/do-it)
