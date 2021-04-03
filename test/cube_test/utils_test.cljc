(ns cube-test.utils-test
 (:require 
  [clojure.test :refer [is testing deftest run-tests]]
  [cube-test.utils :as utils]))

(deftest dmy-test
  (testing "basic dummy test"
    (is (= 1 1))
    (is (= 2 2))))

(deftest merge-dbs
  (testing "merge-dbs properly merges two maps"
    (let [r (utils/merge-dbs {:a 1 :b 2} {:c 7 :d 8})]
     (println "r=" r)
     (is (map? r))
     (is (= (count (keys r)) 4)))))      

 