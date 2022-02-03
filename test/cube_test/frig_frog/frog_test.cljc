(ns cube-test.frig-frog.frog-test
 (:require
  [re-frame.db       :as db]
  [cube-test.db       :as ct-db]
  [cube-test.frig-frog.db       :as ff-db]
  [re-frame.core :refer [dispatch-sync] :as re-frame]
  [cube-test.frig-frog.events :as ff-ev]
  [cube-test.frig-frog.frog :as ff-frog]
  [cube-test.utils.common :as common-utils]
  [cljs.test :as t]))

(comment
  ;; Don't forget to ctrl-alt-shift-e these lines not ctrl-alt-shift-b
  ;; (because they're under a global comment)
  ;; Also, need to be on left-paren not on end-paren

  ;; switch to cljs repl
  (shadow.cljs.devtools.api/nrepl-select :app)
  ;; run a single test
  (t/test-vars [#'change-abc]))

(t/deftest dummy
  (t/testing "dummy"
    ; (dispatch-sync [:ff-db/init-db])
    ; (dispatch-sync [:cube-test.frig-frog.events/init-row 0 0])
    (let [r (ff-frog/dummy 0 2)]
      (prn "r=" r)
      (t/is (= r 7)))))


(t/deftest init-frog
  (t/testing "init-frog"
    ; (dispatch-sync [:ff-db/init-db])
    (dispatch-sync [:cube-test.events/seed-db {:n-cols 8 :n-rows 8}])
    ; (dispatch-sync [:cube-test.frig-frog.events/init-frog 0 0])
    (dispatch-sync [:cube-test.frig-frog.events/init-frog])
    (let [r-db @db/app-db
          frog (:frog r-db)]
      (prn "r-db=" r-db)
      (prn "frog=" frog)
      (t/is (= (:row frog) 0))
      ; (t/is (= (:col frog) 0))
      (t/is (= (:col frog) 3)))))
      ; (t/is (= (:last-row frog) 0))
      ; (t/is (= (:last-col frog) 0)))))

(t/deftest draw-frog
  (t/testing "draw-frog"
    (let [r (ff-frog/draw-frog 0 0)]
      (prn "r=" r))))

(t/deftest move-frog
  (t/testing "move-frog"
    (let [r-db @db/app-db
          r (ff-frog/move-frog 1 2 r-db)
          frog (:frog r)]
      (prn "r-db=" r-db)
      (prn "r=" r)
      (prn ":frog db=" (:frog r-db))
      (prn "frog=" frog)
      (t/is (= (:row frog) 1))
      (t/is (= (:col frog) 2)))))

(t/run-tests 'cube-test.frig-frog.frog-test)

(comment
 ;; run a single test
 (t/test-vars [#'dummy])
 (t/test-vars [#'init-frog])
 (t/test-vars [#'draw-frog])
 (t/test-vars [#'move-frog]))
