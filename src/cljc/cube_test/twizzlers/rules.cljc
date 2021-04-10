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

     ; ::keys
     ; [:what
     ;  [::keys ::pressed pressed]]}))

     ; ::x
     ; [:what
     ;  [::time ::pressed pressed]
     ;  :then
     ;  (println "fuck")]}))
     ::twiz-cnt
     [:what
      [::twiz-cnt ::new-cnt n]
      :then
      (if (> n 2)
        (prn "twiz count > 2 rule fired!!!")
        (prn "twiz count not exceeded"))]}))
      ; (println "hi")]}))


      ; (when (> n 2)
      ;   (prn "msg count > 2 rule fired!!!"))]}))

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
