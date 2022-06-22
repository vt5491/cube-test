;; rules is referred by many, refer to few.
(ns cube-test.frig-frog.rules
 (:require
  [re-frame.core :as re-frame]
  [odoyle.rules :as o]))

; (declare rules)
(declare query-train-id-cnt)

;;
;; rule set
;;
(def rules
  (o/ruleset
    {
      ::train-id-cnt
        [:what
          [::train-id-cnt ::new-cnt n]]
      ::frog
        [:what
          [::frog ::x x]
          [::frog ::y y]]}))

;;
;; session
;;
(def ^:dynamic *session
  (atom (reduce o/add-rule (o/->session) rules)))

;;
;; commands
;;

;;
;; frog
;;
(defn init-frog []
  (swap! *session
    (fn [session]
      (-> session
          (o/insert ::frog ::x 3)
          (o/insert ::frog ::y 7)
          o/fire-rules))))

; (defn move-frog [x y]
;   (swap! *session
;     (fn [session]
;       (-> session
;           (o/insert ::train-id-cnt ::new-cnt new-cnt)
;           o/fire-rules))))

;;
;; train
;;
(defn update-train-id-cnt [new-cnt]
  (swap! *session
    (fn [session]
      (-> session
          (o/insert ::train-id-cnt ::new-cnt new-cnt)
          o/fire-rules))))

(defn inc-train-id-cnt []
  (swap! *session
    (fn [session]
      (let [
            ; abc (query-train-id-cnt)
            ; tmp-2 (prn "inc-train-id-cnt: abc=" abc)
            old-cnt (-> (query-train-id-cnt) (first) (:n))
            tmp (prn "inc-train-id-cnt: old-cnt=" old-cnt)]
        (-> session
            (o/insert ::train-id-cnt ::new-cnt (+ old-cnt 1))
            o/fire-rules)))))

;;
;; queries
;;
(defn query-all-rules []
  (let [r (o/query-all @*session)]
    (prn "rules: r=" r)
    r))

(defn query-frog []
  ; (prn "rules: train-id-cnt=" (o/query-all @*session ::train-id-cnt))
  (let [frg (o/query-all @*session ::frog)]
    (prn "rules: frog.x=" frg)
    frg))

(defn query-train-id-cnt []
  ; (prn "rules: train-id-cnt=" (o/query-all @*session ::train-id-cnt))
  (let [cnt (o/query-all @*session ::train-id-cnt)]
    (prn "rules: train-id-cnt=" cnt)
    cnt))
