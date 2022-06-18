(ns cube-test.twizzlers.subs
  (:require
   [re-frame.core :as re-frame :refer [reg-sub subscribe]]))

;;
;; queries
;;
;; fully qualified
; (re-frame/reg-sub
;   ::twizzlers
;   (fn [db _]
;     (prn "twizzlers.subs: twizzlers query running")
;     (::twizzlers db)))

;;
;; extractors
;;
(reg-sub
  :twizzlers
  (fn [db _]
    (prn "twizzlers.subs: twizzlers query running, twizzlers=" (db :twizzlers))
    (db :twizzlers)))
    ; (:twizzlers db)))

;;
;; computations
;;
(reg-sub
 :gen-twiz-cube
 :<- [:twizzlers]
 ; (fn [query-v]
 ;   (println "reg-sub.twiz-cnt: query-v=" query-v
 ;    (subscribe [:twizzlers])))
 ; (fn [[twizzlers] query-v])
 (fn [twizzlers query-v]
   (println "reg-sub:gen-twiz-cube, twizzlers=" twizzlers)
   (let [twiz (last twizzlers)]
     (re-frame/dispatch [:add-twiz-cube twiz]))))

(reg-sub
 :twiz-cnt
 :<- [:twizzlers]
 ; (fn [[twizzlers] query-v])
 (fn [twizzlers query-v]
   (print "twiz-cnt: twizzlers=" twizzlers)
   (when twizzlers
     (println "reg-sub: twizzlers=" twizzlers)
     (prn "twiz-cnt: count twizzlers=" (count twizzlers))
     ; (re-frame/dispatch [:cube-test.twizzlers.events/update-time])
     ; (re-frame/dispatch [:dummy])
     (re-frame/dispatch [:cube-test.twizzlers.events/update-twiz-cnt (count twizzlers)])
     (count twizzlers))))
