(ns cube-test.twizzlers.rules
 (:require
  [re-frame.core :as re-frame]
  [odoyle.rules :as o]))

(def rules
  (o/ruleset
    {
     ::print-time
     [:what
      [::time ::total tt]
      :then
      (println "upate time rule:" tt)]

     ::twiz-cnt
     [:what
      [::twiz-cnt ::new-cnt n]
      :then
      (if (> n 2)
        (prn "twiz count > 2 rule fired!!!")
        (prn "twiz count not exceeded"))]}))

;; create session and add rule
(def ^:dynamic *session
  (atom (reduce o/add-rule (o/->session) rules)))

(defn update-time []
  (swap! *session
         (fn [session]
           (-> session
               (o/insert ::time ::total 100)
               o/fire-rules))))

(defn update-twiz-cnt [new-cnt]
  (swap! *session
         (fn [session]
           (-> session
               (o/insert ::twiz-cnt ::new-cnt new-cnt)
               o/fire-rules))))

(defn query-twiz-cnt []
  (prn "rules: twiz-cnt=" (o/query-all @*session ::twiz-cnt)))

