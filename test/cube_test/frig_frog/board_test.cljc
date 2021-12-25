(ns cube-test.frig-frog.board-test
 (:require
  [re-frame.db       :as db]
  [cube-test.db       :as ct-db]
  [cube-test.frig-frog.db       :as ff-db]
  [re-frame.core :refer [dispatch-sync] :as re-frame]
  [cube-test.frig-frog.events :as ff-ev]
  [cube-test.frig-frog.board :as ff-board]
  [cube-test.utils.common :as common-utils]
  ; [clojure.test :as t]))
  [cljs.test :as t]))

(comment
  ;; Don't forget to ctrl-alt-shift-e these lines not ctrl-alt-shift-b
  ;; (because they're under a global comment)
  ;; Also, need to be on left-paren not on end-paren

  ;; switch to cljs repl
  (shadow.cljs.devtools.api/nrepl-select :app)
  ;; run a single test
  (t/test-vars [#'change-abc]))

(t/deftest create-row
  (t/testing "create-row"
    (dispatch-sync [:ff-db/init-db])
    ; (dispatch-sync [:cube-test.frig-frog.events/init-row 0 0])
    (let [r (ff-board/create-row 0 2)]
      (prn "r=" r)
      (t/is (map? r))
      (t/is (vector? (:row-0 r)))
      (t/is (= (count (:row-0 r)) 2)))))
      ; (t/is (contains? r-db :board)))))

(t/deftest init-board
  (t/testing "init-board"
  ; (re-frame/dispatch [:cube-test.frig-frog.events/init-board]))
    (dispatch-sync [:ff-db/init-db])
    (dispatch-sync [:cube-test.frig-frog.events/init-board])
    (let [r-db @db/app-db
          b (:board r-db)]
      ; (prn "b=" b)
      (t/is (contains? r-db :board))
      (t/is (map? (nth b 0)))
      ; (prn "x=" (-> (:row-0 (nth b 0)) (nth 1)))
      (t/is (map? (-> (:row-0 (nth b 0)) (nth 1))))
      (t/is (= (-> (:row-0 (nth b 0)) (nth 1) -> (:tile)) :0-1)))))
      ; (t/is (contains? (:board r-db) :row-0))
      ; (t/is (vector? (get-in r-db [:board :row-0]))))))
    ; (let [tmp (dispatch-sync [:cube-test.frig-frog.events/init-board])
    ;       r-db @db/app-db]
    ;     (prn "r-db=" r-db)
    ;     (t/is (contains? r-db :game-abc)))))

(t/deftest parse-delta
  (t/testing "parse-delta"
    (t/is (= 1 1))
    (let [diff-1 [{:row-0 [{:state 1 :abc 0}]}
                  nil
                  {:row-2 [nil nil {:state 1 :abc 0}]}]
          r (ff-board/parse-delta diff-1)]
      ; (t/is (map? r))
      (prn "r=" r)
      (prn "type r=" (type r))
      (t/is (vector? r))
      (t/is (= 2 (count r)))
      (let [r-1 (first r)
            r-2 (second r)]
        (prn "r-1=" r-1)
        (prn "r-1.info=" (:info r-1))
        (prn "type r-1.info=" (type (:info r-1)))
        (prn "take r-1.info=" (take 1 (:info r-1)))
        (prn "rest r-1.info=" (rest (:info r-1)))
        (prn "doall r-1.info=" (doall (:info r-1)))
        (t/is (map? r-1))
        (t/is (= (:row r-1) 0))
        (t/is (= (:col r-1) 0))))))
        ; (t/is (map? r-2))))))

(t/run-tests 'cube-test.frig-frog.board-test)

(comment
 ;; run a single test
 (t/test-vars [#'create-row])
 ; (t/test-vars [#'init-row])
 (t/test-vars [#'init-board])
 (t/test-vars [#'parse-delta]))

; (assoc {} :a 7 :b 8)
    ; (prn "r0.1" (nth r0 1)))
; (let [db @db/app-db
;       b (:board db)
;       r0 (:row-0 b)
;       r1 (:row-1 b)]
;   (prn "r0=" r0)
;   (prn "r0.0" (nth r0 0))
;   (prn "r1=" r1))
