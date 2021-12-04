(ns cube-test.test.ut-simp.msg-test-2
 (:require
     ; [clojure.test :refer :all]
     ; [cube-test.ut-simp.msg :as msg]
     [clojure.test :as t]))

(comment
  (+ 1 1)
  (println *ns*)
 ,)

(deftest a-test
  (t/testing "basic dummy test"
    (t/is (= msg/dummy 11))))

(comment
 (t/run-tests 'cube-test.test.ut-simp.msg-test-2)

 ,)
