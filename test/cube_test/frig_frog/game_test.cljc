;; use this to reset the namespace
; (map (partial ns-unalias *ns*) (keys (ns-aliases *ns*)))
;(shadow.cljs.devtools.api/nrepl-select :app)
(ns cube-test.frig-frog.game-test
 (:require
  [re-frame.db       :as db]
  [cube-test.db       :as ct-db]
  [cube-test.frig-frog.db       :as ff-db]
  [re-frame.core :refer [dispatch-sync] :as re-frame]
  [cube-test.frig-frog.events :as ffe]
  [cube-test.frig-frog.game :as ffg]
  [cube-test.utils.common :as common-utils]
  ; [clojure.test :as t]))
  [cljs.test :as t]))

(comment
 (shadow.cljs.devtools.api/nrepl-select :app)

 ;; shouldn't be necessary to run when using a cljs repl.
 ;; but do need to do if running a cljs repl?
 (use 'cube-test.frig-fron.game :reload)
 ,)
; (+ 1 3)
; (count (keys (ns-interns 'cljs.test)))
; (count (keys (ns-publics 'cljs.test)))

(t/deftest dmy-1
  (t/testing "close?"
    (t/is (common-utils/close? 0.1 5.0 5.4))
    (t/is (not (common-utils/close? 0.1 5.0 5.6)))))
;
(t/deftest dmy-2
  (t/testing "dmy"
    (prn "running test.dmy-2")
    (let [r (ffg/dmy)]
      (t/is (= r 8)))))

(t/deftest change-abc
  (t/testing "change-abc"
    (prn "hello")
    (prn "db=" db/app-db)
    ; (prn "db.abc=" (:abc db/app-db))
    ; (dispatch-sync [:ffe/init-game-db])
    (re-frame/dispatch-sync [:cube-test.frig-frog.events/init-game-db])
    ; (let [r-db (dispatch-sync  [:change-abc 8])])
    (let [r-db (dispatch-sync [:cube-test.frig-frog.events/change-abc 8])
          r (:abc @db/app-db)]
          ; r' (dispatch-sync [:cube-test.frig-frog.events/change-abc 8])
      (prn "r-db=" r-db)
      (prn "db/app-db =" (:abc @re-frame.db/app-db))
      ; (t/is (= (:abc r-db) 8))
      (t/is (= r 8)))))

(t/deftest init-game-db
  (t/testing "init-game-db"
    ; (ct-db/init-game-db)
    ; (ct-db/init-game-db-2)
    ; (cube-test.frig-frog.db/init-game-db {})
    ; (cube-test.db/init-game-db-2 {})
    ; (ct-db/init-game-db-2 {})))
    ; (ffg/init-game-db ffg/default-game-db)))
    (let [tmp (dispatch-sync [:cube-test.events/init-game-db ffg/default-game-db])
          r-db @db/app-db]
        (prn "r-db=" r-db)
        (t/is (contains? r-db :game-abc))
        (t/is (:game-abc r-db) 7)
        (t/is (contains? r-db :board)))))


(t/run-tests 'cube-test.frig-frog.game-test)

;; run a single test
(t/test-vars [#'change-abc])
(t/test-vars [#'dmy-2])
(t/test-vars [#'init-game-db])

; (deftest dmy-1
;   (testing "close?"
;     (is (common-utils/close? 0.1 5.0 5.4))
;     (is (not (common-utils/close? 0.1 5.0 5.6)))))
;
; (run-tests 'cube-test.frig-frog.game-test)

; (cljs-test/deftest dmy-1
;   (cljs-test/testing "close?"
;     (cljs-test/is (common-utils/close? 0.1 5.0 5.4))
;     (cljs-test/is (not (common-utils/close? 0.1 5.0 5.6)))))
;
; (cljs-test/deftest dmy-2
;   (cljs-test/testing "dmy"
;     (let [r (ffg/dmy)]
;       (cljs-test/is (= r 7)))))
;
; (cljs-test/run-tests 'cube-test.frig-frog.game-test)
