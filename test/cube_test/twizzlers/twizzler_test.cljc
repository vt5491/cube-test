(ns cube-test.twizzlers.twizzler-test
 (:require
   [clojure.test :refer-macros [is testing deftest run-tests] :as t]
   [cube-test.twizzlers.db :as db]
   [cube-test.twizzlers.twizzler :as twizzler]
   [clojure.spec.alpha :as s]))

(comment
  (+ 1 1)
  (s/valid? :cube-test.twizzlers.db/twizzler {:cube-test.twizzlers.db/id 1})
  (s/valid? :db/twizzler {:cube-test.twizzlers.db/id 1})
  ;; following works
  (s/valid? ::db/twizzler {::db/id 1})
  (s/valid? :cube-test.twizzlers.db/twizzler {:db/id 1})
  ;; this also works
  (s/valid? :cube-test.twizzlers.db/twizzler {:cube-test.twizzlers.db/id 1})

  (prn "dummy=" db/dummy) 
  (prn "id" :db/id) 
  (prn "twizzler" :db/twizzler) 
 ,)

(def test-db-1 {::db/twizzlers [{::db/id 0}]})

(deftest add-twizzler-empty
 (testing "add-twizzler adds correctly to empty db"
   (let [r (twizzler/add-twizzler {})
         first-twz (first (r ::db/twizzlers))]
       (println "*r=" r)
       (println "first r=" first-twz) 
       (prn "valid=" (s/valid? ::db/twizzler first-twz))
       (is (= (count r)) 1)
       (is (s/valid? ::db/twizzler first-twz)))))
       ;; (is (s/valid :cube-test.twizzlers/t)))))
       ;; (is))))

(deftest add-twizzler-one
 (testing "add-twizzler adds correctly to db with one existing entry"
   (let [r (twizzler/add-twizzler test-db-1)
         second-twz (second (r ::db/twizzlers))]
       (println "*r=" r)
       (println "test-db-1=" test-db-1)
       (println "second r=" second-twz) 
       (prn "valid=" (s/valid? ::db/twizzler second-twz))
       (is (= (count r)) 2)
       (is (s/valid? ::db/twizzler second-twz)))))