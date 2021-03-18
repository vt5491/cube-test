(ns test.cube-test.msg-cube.msg-cube-game-test
 (:require 
  [clojure.test :refer [is testing deftest run-tests]]
  ;; Note: :all doesn't seem to work
  ;; [clojure.test :refer :all]
  [cube-test.msg-cube.msg-cube-game :as game]))
  ;; [clojure.test :as t]]))

;; (ns cube-test.msg-cube.msg-cube-game
;;  (:require
;;   [clojure.test :refer :all]))

(deftest dmy-test
  (testing "basic dummy test"
    (is (= 1 1))
    (is (= 2 2))))

(deftest init-test
  (testing "inits db properly"
    ;; (let [r (game/init nil)])
    (let [r (game/init {})]
    ;; (let [r (game/df)]
      (is (contains? r :msgs))
      (is (contains? r :max-id))
      (is (vector? (r :msgs))))))

(comment
 (shadow.cljs.devtools.api/nrepl-select :app)
 ;; browser repl hookup doesn't work in vscode for some reason.
;;  (clojure.browser.repl/connect "http://localhost:8778")

 (js/parseInt "5")
 *ns*
 ,)

 ;; run with shift-enter not ctrl-enter
 ;; put cursor on the left-paren of 'run-tests' instr itself
 ;; don't forget to shift-enter the updated test if editing.
 ;; Note: have to have a cljs repl active (but not a browser repl) in order to evaluate
 ;; .cljs code (indirectly) in a test.
(comment
;;  (t/run-tests 'cube-test.msg-cube.msg-cube-game)
 (run-tests 'test.cube-test.msg-cube.msg-cube-game-test)

 (game/init)
 (game/df)
 game

 (cube-test.msg-cube.msg-cube-game/df)

 (cube-test.msg-cube.msg-cube-game/df)
 ,)

;; (t/run-tests 'cube-test.msg-cube.msg-cube-game)
