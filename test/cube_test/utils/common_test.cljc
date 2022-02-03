(ns cube-test.utils.common-test
 (:require
  [clojure.test :refer [is testing deftest run-tests]]
  [clojure.test :as t]
  [cube-test.utils.common :as common]))

(t/deftest round-places-test
  (t/testing "round-places"
    (let [r (common/round-places 3.1415 2)]
      (prn "r=" r ",r=3.14 =" (== r 3.14), ",float? r=" (float? r))
      (t/is (= r (float 3.14))))))
      ; (t/is (= (format "%.2f" r) "3.14")))))

(t/deftest idx-of-id
  (t/testing "index of id")
  (let [v [{:a 1 :id :x-0}, {:b 2 :id :x-3}, {:c 3 :id :x-2}]
        r (common/idx-of-id v :x-3)]
      (prn "r=" r)
      (t/is (= r 1))
  ;; no match cond
    (let [r2 (common/idx-of-id v :x-5)]
      (prn "r2=" r2)
      (t/is (nil? r2)))
  ;; duplicate match cond -- should return the first match only
    (let [v2 (conj v {:d 4 :id :x-3})
          r3 (common/idx-of-id v2 :x-3)]
      (prn "r3=" r3)
      (t/is (not (seq? r3)))
      (t/is (= r3 1)))))

(t/run-tests 'cube-test.utils.common-test)

(comment
 ;; run a single test
 (t/test-vars [#'round-places-test])
 (t/test-vars [#'idx-of-id]))
