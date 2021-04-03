(ns cube-test.msg-cube.msg-test
 (:require
   [clojure.test :refer-macros [is testing deftest run-tests] :as t]
  ; [clojure.test :as t]
   [cube-test.msg-cube.data.msg :as msg]))

;; hookup
(comment
 (shadow.cljs.devtools.api/nrepl-select :app)
 ,)

;; set up
(def db {:msgs [
                  {:id 1 :text "abc" :level :INFO}
                  {:id 2 :text "def" :level :INFO}
                  {:id 3 :text "ghi" :level :INFO}]})

;; tests
(deftest get-by-id 
 (testing "get-by-id 2 returns the second vector in :msgs"
;;  (println "hi from get-by-id test"))
   (let [r (msg/get-by-id 2 (db :msgs))]
    (println "test: r=" r)
    (is (map? r))
    (is (= (r :id) 2))
    (is (= (r :text) "def")))))

(deftest set-level-cnt
 (testing "set-level doesn't alter msg count"
   (let [r (msg/set-level 1 :WARN (db :msgs))]
      (println "*r=" r)
      ;; (println "r@:msgs=" (r :msgs))
      ;; (println "count r@:msgs=" (count (r :msgs)))
      ;; (println "hi8")
      (is (= (count r)) (count (db :msgs))))))

(deftest set-level-warn-1
 (testing "set-level changes :id 1 from :INFO to :WARN"
   (let [r (msg/set-level 1 :WARN (db :msgs))]
     (println "test-r: r=" r)
     (println "new-l=" (:level (first r)))
     (is (= (:level (first r)) :WARN))
     (is (= (:level (second r)) :INFO)))))

(deftest inc-level-no-cap
 (testing "inc-level changes :id 1 from :INFO to :WARN"
   (let [r (msg/inc-level 1 (db :msgs))]
    (is (= (:level (first r)) :WARN)))))

(deftest inc-level-with-cap
 (testing "inc-level changes :id 1 from :INFO to :WARN"
   (let [new-msgs (assoc (db :msgs) 1 {:id 2 :level :SEVERE :text "def"})
         r (msg/inc-level 2 new-msgs)]
    ;; (println "r=" r)
    (is (= (:level (second r)) :SEVERE)))))

;; misc
(comment
 (+ 1 1)
 (js/parseInt "5")
 *ns*
 db
 (t/run-tests 'cube-test.msg-cube.msg-test)
 (run-tests 'cube-test.msg-cube.msg-test)
 (msg/set-level 1 :WARN db)
 ,)
