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
   [cube-test.msg-cube.msg-test :as msg-test]))

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
 ,)

;; Note: if you update msg or msg-test, you have to re-eval the 'ns' statment in this file
;; before re-running the 'run-test' cmd below.
(comment
 (run-tests 'cube-test.msg-cube.msg-test)

 ;; run a single test
 (clojure.test/test-vars [#'cube-test.msg-cube.msg-test/inc-level-with-cap])
 ,)
