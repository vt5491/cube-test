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

(t/deftest unique-rnd-seq-test
  (t/testing "unique-rnd-seq")
  (let [r (common/unique-rnd-seq 8 4)]
    (prn "r=" r)
    (t/is (set? r))
    (t/is (= (count r) 4))
    (t/is (every? #(< % 8) r)))
  (let [r (common/unique-rnd-seq 8 16 5)]
    (prn "r=" r)
    (t/is (set? r))
    (t/is (= (count r) 5))
    (t/is (and (every? #(< % 16) r) (every? #(>= % 8) r)))))

(t/deftest extract-path-file-test
  (t/testing "extract-path-file")
  (let [
        r (common/extract-path-file "/abc/def/ghi.txt")
        ; _ (prn "r=" r)
        ; r {:path 7 :file 8}
        path (:path r)
        file (:file r)]
    (prn "path=" path)
    (prn "file=" file)
    (t/is (= path "/abc/def/"))
    (t/is (= file "ghi.txt"))))

(t/run-tests 'cube-test.utils.common-test)

(comment
  (+ 1 1)
  ;; Note: test names must be different name than the thing
  ;; it's testing e.g. add a "-test" suffix at the end to
  ;; get around this.
  (common/unique-rnd-seq 8 4)
  (common/round-places 3.1415 2)
  ;; run this if you edit the main source code
  (use 'cube-test.utils.common :reload)
  ;; run a single test
  ;; make sure to shift-ctrl-alt-b the test first
  (t/test-vars [#'round-places-test])
  (t/test-vars [#'cube-test.utils.common-test.round-places-test])
  (clojure.test/test-vars [#'cube-test.utils.common-test/round-places-test])
  (t/test-vars [#'idx-of-id])
  (t/test-vars [#'unique-rnd-seq-test])
  (t/test-vars [#'extract-path-file-test]))
