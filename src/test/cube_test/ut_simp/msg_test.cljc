(ns cube-test.ut-simp.msg-test
  (:require [clojure.test :refer :all]
            [cube-test.ut-simp.msg :as msg]
            [clojure-test :as t]))

(deftest a-test
  (testing "basic dummy test"
    ; (is (= 0 1))
    (is (= msg/dummy 11))))

(def msg-box-0 {::msg/box-id 0 ::msg/msg {::msg/text "abc" ::msg/msg-level :INFO}})

(deftest msg-box-0-basic
  (testing "msg-box-0 has box-id=0"
    (is (= (msg-box-0 ::msg/box-id) 0))))

(deftest inc-msg-level
  (testing "inc-msg-level properly increments msg-level"
    (let [
          result (msg/inc-msg-level msg-box-0)
          ; result 4
          box-id' (result ::msg/box-id)
          msg-level' (get-in result [::msg/msg ::msg/msg-level])]
          ; box-id' 2]
      (println "msg-level=" msg-level')
      (is (= msg-level' :WARN)))))

(comment
 (println *ns*)
;;  (require '[clojure.test :as t])
;;  (require [clojure.test :as t])
 (repl/dir t)
 (t/run-tests 'cube-test.ut-simp.msg-test)
 ,) 
; (deftest inc-msg-level
;   (testing "inc-msg-level properly increments msg-level"
;     (is (= 0 1))))
; (deftest extract-msg-box-num
;   (testing "properly extract the box number from msg-box id"
;     (let [result (msg/extract-msg-box-num msg-box-0)]
;       (is (= result 0)))))
