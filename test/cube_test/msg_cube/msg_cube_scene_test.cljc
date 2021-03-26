;; Note: this has to .cljs if it loads .cljs file on the backend e.g.
;; a .cljc cannot "launder" a .cljs file, at least in vscode
;; Note: no not true
(ns cube-test.msg-cube.msg-cube-scene-test
 (:require
   [clojure.test :refer-macros [is testing deftest run-tests] :as t]
  ; [clojure.test :as t]
  [cube-test.msg-cube.data.msg :as msg]))
  ;; [cube-test.msg-cube.data.msg :as msg]))

;; (def msg-box-0 {::msg/box-id 0 ::msg/msg {::msg/text "abc" ::msg/msg-level :INFO}})

(deftest dmy-test
  (testing "basic dummy test"
    (is (= 1 1))
    (is (= 2 2)))

 (deftest extract-id
   (t/testing "properly extract the id from mesh-name or mesh-id"
      (let [result (msg/extract-id "mc-5")]
        (t/is (= result 5)))
      (let [result (msg/extract-id "mc-15")]
        (t/is (= result 15))))))


;; hookup
(comment
 (shadow.cljs.devtools.api/nrepl-select :app)
 ;; browser repl hookup doesn't work in vscode for some reason.
 (clojure.browser.repl/connect "http://localhost:8778"))

(comment
;;  (t/run-tests 'cube-test.msg-cube.msg-cube-game)
 (+ 1 1)
 (msg/gen)
 msg/dummy
 ;   (-> (re-find #"msg-box-(\d+)" "msg-box-0") (get  1) (js/parseInt)))
 *ns*
 :abc
 (js/parseInt "5")
 (require '[clojure.repl :as repl])
 (require '[cljs.repl :as repl])
 (repl/dir clojure.string)

 (-> (re-find #"-(\d+)$" "mc-5") (get 1) (js/parseInt))

 (run-tests 'cube-test.msg-cube.msg-cube-scene-test)
 (t/run-tests 'cube-test.msg-cube.msg-cube-scene-test))
 
