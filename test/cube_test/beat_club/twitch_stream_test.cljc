(ns cube-test.beat-club.twitch-stream-test
 (:require
   ;;Note: you can only load .clj or .cljc files, not .cljs files.
 ;   [clojure.test :refer-macros [is testing deftest run-tests] :as t]))
   ; (:require
   ;  [clojure.test :refer :all]
   ;  [clojure.test :as t])
  ; (:require [cljs.test :refer (deftest is)]))
  ; (:require [cljs.test :refer (deftest is testing) :as u]))
  ; (:require [cljs.test :refer :all :as u])
  ; [clojure.test :refer-macros [is testing deftest run-tests] :as t]))
  ; (:require [cljs.test :refer-macros [deftest is testing run-tests]])
  ; (:require [clojure.test :as t])
  ; [cube-test.utils :as utils]
  [cube-test.utils.common :as common-utils]
  [cube-test.beat-club.twitch-stream :as ts]
  [clojure.test :as t]))
  ; [cljs.test :as t]))

; (common-utils/close? 0.0001 100 99.99)
; (def ish? (partial common-utils/close? 0.0001))
; (ish? 100 99.999)
; (:use 'clojure.test)
; (+ 1 1)
; (prn "hi")
; (clojure.test/is (= 2 (+ 1 1)))
; (t/is (= 2 (+ 1 1)))
; (deftest test-numbers
;   (is (= 1 1)))
;
; (deftest t1
;   (is (= 2 (+ 1 1))))
; ; (are [x y] (= x y))
;
; (deftest a-failing-test
;   (is (= 1 1)))
;
; (t/deftest dmy-test
;   (t/testing "twitch-stream dummy test"
;     (t/is (= 1 1))
;     (t/is (= 2 2))))

(t/deftest dummy-t
  (t/testing "dummy"
    ; (let [r (ts/dummy)])
    (let [r (cube-test.beat-club.twitch-stream/dummy)]
      ; (prn "r=" r)
      (t/is (= r 8)))))

; (+ 1 1)
; (prn "hi")
; (ts/beat-sync-factor 244 24 80 :double-note)
; 1.33
; (ts/beat-sync-factor 48 24 80 :double-note)

(t/deftest beat-sync-factor-test
  (t/testing "beat-sync-factor"
    (let [r (ts/beat-sync-factor 72.0 30.0 80 :double-note)
          r2 (ts/beat-sync-factor 72.0 30.0 80 :quarter-note)]
      ; (t/is (= r 1.6))
      (prn "beat-sync-factor-test: r=" r ",r2=" r2)
      (t/is (ish? r 1.6))
      (t/is (ish? r2 3.2)))))

  ; (comment
(t/run-tests 'cube-test.beat-club.twitch-stream-test)
(use 'cube-test.beat-club.twitch-stream :reload)
 ; ,)
