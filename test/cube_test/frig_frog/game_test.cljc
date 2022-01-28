;; use this to reset the namespace
; (map (partial ns-unalias *ns*) (keys (ns-aliases *ns*)))
;(shadow.cljs.devtools.api/nrepl-select :app)
(ns cube-test.frig-frog.game-test
 (:require
  [re-frame.db       :as db]
  [cube-test.db       :as ct-db]
  [cube-test.frig-frog.db       :as ff-db]
  [re-frame.core :refer [dispatch-sync] :as re-frame]
  [cube-test.frig-frog.events :as ff-e]
  [cube-test.frig-frog.game :as ff-g]
  [cube-test.utils.common :as common-utils]
  ; [clojure.test :as t]))
  [cljs.test :as t]))

(comment
 (shadow.cljs.devtools.api/nrepl-select :app)

 ;; shouldn't be necessary to run when using a cljs repl.
 ;; but do need to do if running a cljs repl?
 (use 'cube-test.frig-frog.game :reload)
 ,)
; (+ 1 3)
; (count (keys (ns-interns 'cljs.test)))
; (count (keys (ns-publics 'cljs.test)))

;; fixtures
(defn init-test-db []
  (re-frame/dispatch-sync [:cube-test.frig-frog.events/init-game-db]))

;; tests
(t/deftest dmy-1
  (t/testing "close?"
    (t/is (common-utils/close? 0.1 5.0 5.4))
    (t/is (not (common-utils/close? 0.1 5.0 5.6)))))
;
(t/deftest dmy-2
  (t/testing "dmy"
    (prn "running test.dmy-2")
    (let [r (ff-g/dmy)]
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
    (let [tmp (dispatch-sync [:cube-test.events/init-game-db ff-g/default-game-db])
          r-db @db/app-db]
        (prn "r-db=" r-db)
        (t/is (contains? r-db :game-abc))
        (t/is (= (:game-abc r-db) 7))
        (t/is (contains? r-db :board))
        (t/is (contains? r-db :active-scene))
        (t/is (= (:active-scene r-db) :ff-l1)))))

(t/deftest toggle-dev-mode
  (t/testing "toggle-dev-mode"
    (dispatch-sync [:cube-test.events/seed-db {:x 7}])
    (dispatch-sync [:cube-test.frig-frog.events/toggle-dev-mode])
    (let [r-db @db/app-db]
        ; (prn "r-db=" r-db)
        (t/is (contains? r-db :dev-mode))
        (t/is (= (:dev-mode r-db) true)))
    (dispatch-sync [:cube-test.events/seed-db {:y 8 :dev-mode true}])
    (dispatch-sync [:cube-test.frig-frog.events/toggle-dev-mode])
    (let [r-db @db/app-db]
        ; (prn "r-db=" r-db)
        (t/is (contains? r-db :dev-mode))
        (t/is (= (:dev-mode r-db) false)))
    (dispatch-sync [:cube-test.events/seed-db {:dev-mode false}])
    (dispatch-sync [:cube-test.frig-frog.events/toggle-dev-mode])
    (let [r-db @db/app-db]
        ; (prn "r-db=" r-db)
        (t/is (contains? r-db :dev-mode))
        (t/is (= (:dev-mode r-db) true)))))
    ; ; (dispatch-sync [:ff-db/seed-db {} {:y 8}])
    ; (dispatch-sync [:cube-test.events/seed-db {:y 8}])
    ; (dispatch-sync [:cube-test.frig-frog.events/seed-test-db :dev-mode true])
    ; (let [;db (dispatch-sync [:cube-test.frig-frog.events/seed-test-db :dev-mode true])
    ;       r-db @db/app-db]
    ;   (prn "r-db 1=" r-db))
    ; (dispatch-sync [:ff-db/init-db])
    ; (dispatch-sync [:cube-test.frig-frog.events/seed-test-db :x 2])
    ; (let [;db (dispatch-sync [:cube-test.frig-frog.events/seed-test-db :x 2])
    ;       r-db @db/app-db]
    ;   (prn "r-db 2=" r-db))))
    ; (let [;tmp (dispatch-sync [:cube-test.frig-frog.events/toggle-dev-mode])
    ;       tmp (init-test-db)
    ;       tmp-2 (dispatch-sync [:cube-test.frig-frog.events/toggle-dev-mode])
    ;       r-db @db/app-db]
    ;     (prn "r-db=" r-db)
    ;     (t/is (contains? r-db :dev-mode)))
    ; (let [db (-> {}
    ;              (assoc :x 7)
    ;              (assoc :y 8))
    ;       ; r-db (dispatch-sync [:cube-test.frig-frog.events/toggle-dev-mode db])]
    ;       ; r-db (toggle-dev-mode db [:cube-test.frig-frog.events/toggle-dev-mode])
    ;       ; r-db (toggle-dev-mode db [:cube-test.frig-frog.events/toggle-dev-mode])
    ;       ; r-db (cube-test.frig-frog.events/toggle-dev-mode db [:cube-test.frig-frog.events/toggle-dev-mode])
    ;       r-db (ff-e/toggle-dev-mode db [:cube-test.frig-frog.events/toggle-dev-mode])]
    ;       ; r-db (toggle-dev-mode)]
    ;     (prn "db=" db)
    ;     (prn "r-db=" r-db))))

(t/run-tests 'cube-test.frig-frog.game-test)

(comment
 ;; run a single test
 (t/test-vars [#'change-abc])
 (t/test-vars [#'dmy-2])
 (t/test-vars [#'init-game-db])
 (t/test-vars [#'toggle-dev-mode]))
