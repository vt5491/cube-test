
(ns cube-test.utils.choice-carousel.subs
  (:require
   [re-frame.core :as rf :refer [reg-sub subscribe]]
   [clojure.data :as clj-data]
   [cube-test.utils.choice-carousel.choice-carousel :as cc]))

; (def ^:dynamic *last-choice-carousels* (atom nil))
(def ^:dynamic *last-choice-carousels* (atom []))
;;
;; extractors
;;
(reg-sub
 :get-choice-carousels
 (fn [db _]
   (prn "subs: now driving get-choice-carousels")
   (get-in db [:choice-carousels])))

;;
;; computations
;;
; (defn choice-carousels-changed [choice-carousels query-v]
(reg-sub
  :choice-carousels-changed
  :<- [:get-choice-carousels]
  (fn [choice-carousels query-v]
    (prn "subs: now in choice-carousels-changed, choice-carousels=" choice-carousels)
    (when (and choice-carousels (not (empty? choice-carousels)))
        (let [
              ; last-ccs @cube-test.utils.choice-carousel.choice-carousel.*last-choice-carousels*
              last-ccs @*last-choice-carousels*
              diff-full (clj-data/diff choice-carousels last-ccs)
              diff-a (first diff-full)
              diff-b (second diff-full)
              ;; empirically determined that the following two
              ;; are where the things we're interested in reside.
              diff-new (first diff-a)
              diff-delta (second diff-a)
              diff (or diff-new diff-delta)]
          (prn "cc.subs: diff-full=" diff-full)
          (prn "cc.subs: diff-a=" diff-a)
          (prn "cc.subs: second diff-a=" (second diff-a))
          (prn "cc.subs: diff-b=" diff-b)
          (prn "cc.subs: diff=" diff)
          (when diff
            (cc/init-meshes (:radius diff) (:choices diff)))

          (swap! *last-choice-carousels* (fn [x] choice-carousels))))))
