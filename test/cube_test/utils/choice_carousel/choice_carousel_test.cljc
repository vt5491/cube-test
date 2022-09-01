(ns cube-test.utils.choice-carousel.choice-carousel-test
 (:require
  [clojure.test :refer [is testing deftest run-tests]]
  [clojure.test :as t]
  [re-frame.db :as db]
  [re-frame.core :refer [dispatch-sync] :as rf]
  ; [cube-test.utils.choice-carousel.choice-carousel :as cc]
  [cube-test.utils.choice-carousel.choice-carousel :as cc]
  [cube-test.utils.choice-carousel.events :as cc-events]))

  ;; example 'choice-carousel' shape:
  ; [:choice-carousels
  ;  [
  ;   {:id :cc-test :choices [
  ;                           {:id :ff}
  ;                           {:id :cube-spin}
  ;                           {:id :face-slot}]}]]))

(t/deftest init-test
  (t/testing "init"
    ; (dispatch-sync [:cube-test.events/seed-db {:n-cols 8 :n-rows 8}])
    (dispatch-sync [:cube-test.events/seed-db {}])
    (let [
          i-db @db/app-db
          ; r (cc/init {:id :test-cc, :abc 7} r-db)
          choices [{:id :ff :model-file "models/top_scene/sub_scenes/ff_scene.glb"} {:id :cube-spin} {:id :face-slot}]
          parms {:id :test-cc, :radius 16.0 :choices choices}
          ; r (dispatch-sync [::cc-events/init-choice-carousel parms i-db])
          r (dispatch-sync [:cube-test.utils.choice-carousel.events/init-choice-carousel parms i-db])
          r-db @db/app-db]
      ; (prn "r=" r)
      (prn "r-db=" r-db)
      ; (t/is (= r 7))
      (let [r-ccs (:choice-carousels r-db)
            _ (prn "test: r-ccs=" r-ccs)
            r-1 (first r-ccs)
            _ (prn "test: r-1=" r-1)
            ; r-id (get r-1 :id)
            r-id (:id r-1)
            _ (prn "test: r-id=" r-id)
            r-radius (:radius r-1)
            _ (prn "test: r-radius=" r-radius)
            ; r-choices (get-in r-1 :choices)
            r-choices (:choices r-1)
            _ (prn "test: r-choices=" r-choices)]
        ; (t/is (= (get-in r-db [:test-cc :val]) 7))
        (t/is (= (count r-choices) 3))
        (t/is (= r-radius 16.0))
        (t/is (exists? (:model-file (first choices))))))))

(t/deftest get-choice-idx-by-id-test
  (t/testing "get-choice-idx-by-id")
  (let [choices [{:id :abc :val 7}{:id :def :val 8}{:id :ghi :val 9}]
        r-abc (cc/get-choice-idx-by-id choices :abc)
        r-ghi (cc/get-choice-idx-by-id choices :ghi)
        r-xyz (cc/get-choice-idx-by-id choices :xyz)]
    (t/is (= r-abc 0))
    (t/is (= r-ghi 2))
    (t/is (= r-xyz nil))))

(comment
  (+ 1 1)
  ;; switch to cljs repl
  (shadow.cljs.devtools.api/nrepl-select :app)
  ;; Note: test names must be different name than the thing
  ;; it's testing e.g. add a "-test" suffix at the end to
  ;; get around this.
  ; (common/unique-rnd-seq 8 4)
  ; (common/round-places 3.1415 2)
  ;; run this if you edit the main source code
  ;; Note: just make sure some of your non-test code is referencing
  ;; the module somewhere, then it should automatically be refreshed.
  ;; (including cc.events and cc.subs)
  (use 'cube-test.utils.choice-carousel.choice-carousel' :reload)
  (use 'cube-test.top-scene.top-scene' :reload)
  ;; run a single test
  ;; make sure to shift-ctrl-alt-b the test first
  (t/test-vars [#'init-test])
  (t/test-vars [#'get-choice-idx-by-id-test]))
