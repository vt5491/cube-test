;; We concentrate our test in order to minimize the amount of refreshing.
;; If you update a test or test target, don't forget to re-eval in the respective editor.
;; If you actually save the file, you have to re-eval it (still), and also re-run this 'ns
;; Having the test-runner in the same file as the ut themselves is expensive, because
;; anytime you save the file, you not only have to run the 'ns of the ut file, but also all
;; the definitions in the ut file again.  By having the test runner separated here, if you save
;; the ut file, then running the (ns) here will automatically re-read the entire ut file.
(ns cube-test.test-runner
 (:require
   [clojure.test :refer-macros [is testing deftest run-tests] :as t]
   [cube-test.msg-cube.data.msg :as msg]
   [cube-test.msg-cube.msg-test :as msg-test]
   [cube-test.msg-cube.spec.db :as msg-cube.spec]
  ;;  [cube-test.msg-cube.game-test :as game-test]
   [cube-test.utils-test :as utils-test]
   [cube-test.twizzlers.twizzler-test :as twizzler-test]))

;; hookup
(comment
 (shadow.cljs.devtools.api/nrepl-select :app)
 (clojure.browser.repl/connect "http://localhost:8778")
 ,)

(comment
 (+ 1 1)
 (js/parseInt "5")
 (println msg-test/db)
 msg-test/db
 (println msg/dummy)
 msg/get-by-id
 utils-test/merge-dbs
 ,)

;; Note: if you update msg or msg-test, you have to re-eval the 'ns' statment in this file
;; before re-running the 'run-test' cmd below. Use vi "`a" and then "``" to go back.
;; msg-test
(comment
 (run-tests 'cube-test.msg-cube.msg-test)

 (run-tests 'cube-test.msg-cube.game-test)

 (run-tests 'cube-test.utils-test)

 ;; run a single test
 ;; Note: this doesn't work for me.
 ;; Note: the best thing is to probably just select that single test in the ns
 ;; and "ctrl-, s" it, so only that one test is known to the repl.
 (clojure.test/test-vars [#'cube-test.msg-cube.msg-test/inc-level-with-cap])

 (clojure.test/test-vars [#'cube-test.msg-cube.msg-test/inc-level-with-cap])

 (clojure.test/test-vars [#'cube-test.msg-cube.msg-cube-game-test/extract-id])

 (clojure.test/test-vars [#'cube-test.msg-cube.game-test/dmy-test])

 (clojure.test/test-vars game-test/dmy-test)

 (println game-test/dummy)
 game-test/dummy
 game-test/dmy-test
 (game-test/dmy-test)
 ,)
 
;; twizzlers
(comment
 (clojure.test/test-vars [#'cube-test.twizzlers.twizzler-test/add-twizzler-empty])
 (clojure.test/test-vars [#'cube-test.twizzlers.twizzler-test/add-twizzler-one])

 (run-tests 'cube-test.twizzlers.twizzler-test)
;; (run-tests 'cube-test.utils-test
  
 ,)
 
