(ns cube-test.frig-frog.train-test
 (:require
  [re-frame.db       :as db]
  [cube-test.db       :as ct-db]
  [cube-test.frig-frog.db       :as ff-db]
  [re-frame.core :refer [dispatch-sync] :as rf]
  [cube-test.frig-frog.events :as ff-events]
  [cube-test.frig-frog.train :as ff-train]
  [cube-test.utils.common :as common-utils]
  [cljs.test :as t]))

(comment
  ;; Don't forget to ctrl-alt-shift-e these lines not ctrl-alt-shift-b
  ;; (because they're under a global comment)
  ;; Also, need to be on left-paren not on end-paren

  ;; switch to cljs repl
  (shadow.cljs.devtools.api/nrepl-select :app))
;;
;; fixtures
;;
(def train-vec-3 [{:init-row 4, :vx -1, :vy 0, :init-col 7, :length 1, :id :tr-1}
                  {:init-row 5, :vx 1, :vy 0, :init-col 7, :length 1, :id :tr-2}
                  {:init-row 6, :vx 1, :vy 0, :init-col 7, :length 1, :id :tr-3}])


;;
;; tests
;;
(t/deftest init-train
  (t/testing "init-train"
    ; (dispatch-sync [:ff-db/init-db])
    (dispatch-sync [:cube-test.events/seed-db {:n-cols 8 :n-rows 8}])
    ; (dispatch-sync [:cube-test.frig-frog.events/init-frog 0 0])
    (dispatch-sync [:cube-test.frig-frog.events/init-train
                     {:id :train-1 :vx -1 :vy 0 :length 1 :init-col 7 :init-row 4}])
    (let [r-db @db/app-db]
      ; (prn "r-db=" r-db)
      (t/is (contains? r-db :trains))
      (t/is (= (count (:trains r-db)) 1))
      (let [train-1 (get-in r-db [:trains 0])]
        (t/is (map? train-1))
        (t/is (= (:id train-1) :train-1))
        (t/is (= (:length train-1) 1))
        (t/is (= (:vx train-1) -1))
        (t/is (= (:vy train-1) 0))
        (t/is (= (:init-row train-1) 4))
        (t/is (= (:init-col train-1) 7))))
    ;; add a second train
    (dispatch-sync [:cube-test.frig-frog.events/init-train {:id :train-2 :length 3}])
    (let [r-db @db/app-db]
      ; (prn "r-db=" r-db)
      (t/is (contains? r-db :trains))
      (t/is (= (count (:trains r-db)) 2))
      (let [train-2 (get-in r-db [:trains 1])]
        (t/is (map? train-2))
        (t/is (= (:id train-2) :train-2))
        (t/is (= (:length train-2) 3))
        (t/is (= (:vx train-2) 0))
        (t/is (= (:vy train-2) 0))
        (t/is (= (:init-row train-2) 0))
        (t/is (= (:init-col train-2) 0))))))

(t/deftest drop-train-idx
  (t/testing "drop-train-idx"
    ; (dispatch-sync [:ff-db/init-db])
    (dispatch-sync [:cube-test.events/seed-db
                     ; {:trains [{:init-row 4, :vx -1, :vy 0, :init-col 7, :id :tr-1}
                     ;           {:init-row 5, :vx 1, :vy 0, :init-col 7, :id :tr-2}
                     ;           {:init-row 6, :vx 1, :vy 0, :init-col 7, :id :tr-3}]}
                     {:trains train-vec-3}])
    (dispatch-sync [::ff-events/drop-train-idx 1])
    (let [r-db @db/app-db
          trains (:trains r-db)]
      (prn "r-db=" r-db)
      (t/is (contains? r-db :trains))
      (t/is (= (count trains) 2))
      (t/is (= (:id (get trains 0)) :tr-1))
      (t/is (= (:id (get trains 1)) :tr-3)))))

(t/deftest drop-train-id
  (t/testing "drop-train-id"
    (dispatch-sync [:cube-test.events/seed-db {:trains train-vec-3}])
    (dispatch-sync [::ff-events/drop-train-id :tr-2])
    (let [r-db @db/app-db
          trains (:trains r-db)]
      (prn "r-db=" r-db)
      (t/is (contains? r-db :trains))
      (t/is (= (count trains) 2))
      (t/is (= (:id (get trains 0)) :tr-1))
      (t/is (= (:id (get trains 1)) :tr-3)))))

(t/deftest get-train-by-id
  (t/testing "get-train-by-id"
    (dispatch-sync [:cube-test.events/seed-db {:trains train-vec-3}])
    (let [r-db @db/app-db
          trains (:trains r-db)
          ; train (dispatch-sync [::ff-events/get-train-by-id trains :tr-2])
          train (ff-train/get-train-by-id trains :tr-2)]
     (prn "***train=" train)
     (t/is (not (nil? train)))
     (t/is (= (:id train) :tr-2)))))

; (t/deftest update-train-id
;   (t/testing "update-train-id"
;     (dispatch-sync [:cube-test.events/seed-db
;                      {:trains train-vec-3}])
;     (let [r-db @db/app-db
;           r (ff-train/update-train-id :tr-2 (:trains r-db) {:length 3})]
;       (prn "***r=" r)
;       (t/is (= (:length r) 3)))))

(t/deftest update-train-id
  (t/testing "update-train-id"
    (dispatch-sync [:cube-test.events/seed-db
                     {:trains train-vec-3}])
    (dispatch-sync [::ff-events/update-train-id :tr-2 {:length 3}])
    ; (dispatch-sync [::ff-events/drop-train-idx 1])
    (let [r-db @db/app-db
          trains (:trains r-db)]
      (prn "trains=" trains)
      (t/is (= (count trains) 3))
      (t/is (= (get-in trains [1 :length]) 3)))))


(t/run-tests 'cube-test.frig-frog.train-test)

(comment
 ;; run a single test
 (t/test-vars [#'init-train])
 (t/test-vars [#'drop-train-idx])
 (t/test-vars [#'drop-train-id])
 (t/test-vars [#'get-train-id])
 ; (t/test-vars [#'update-train-by-id])
 (t/test-vars [#'update-train-id]))
