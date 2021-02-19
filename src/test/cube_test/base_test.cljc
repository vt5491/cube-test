;; run via
;; lein.bat test cube-test.base-test
;; from the root dir of the project (I think actually anywhere, but test from root)
;;
;; note: tester and testee files are both .cljc
;; note2: this is using 'clojure.test' not 'cljs.test'
(ns cube-test.base-test
; (ns cube-test
  ; (:require [cljs.test :refer-macros [deftest is testing run-tests]])
  ; (:require [cljs.test :refer (deftest is testing run-tests)])
  ; (:require [cljs.test :as ct])
  ; (:require [cljs.test :as ct :refer (deftest is testing run-tests update-current-env!)])
  ; (:require [cljs.test :as ct :refer (deftest is testing run-tests)]
  ;; note: following works
  (:require [clojure.test :refer :all]
  ; (:require [cljs.test :refer :all])
  ; (:require [cljs.test :refer-macros [async deftest is testing]])
  ; (:require [cljs.test :refer [async deftest is testing update-current-env!]]
             ; :refer [*current-env*]]
            ; [cube-test.base :as base]))
            [cube-test.dummy-base :as dummy-base]))
            ; [cube-test.base :refer :all]))
(comment
 (println "hi")
 (js/parseInt "5")
 ,)

(println  "now in src/test/cube_test/base_test.cljc mutha2")
(println "now in base_test.cljc, base.abc=" dummy-base/abc)
; (println "*current-env*=" *current-env*)
; (println "async=" async)
; (println "now in base_test.cljs, base.scale-factor=" base/scale-factor)

; (ct/deftest a-test)
(deftest a-test
  (testing "FIXME, I fail."
    ; (is (= 0 1))
    (is (= dummy-base/abc 8))))
