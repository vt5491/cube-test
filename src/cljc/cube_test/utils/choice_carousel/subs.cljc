
(ns cube-test.utils.choice-carousel.subs
  (:require
   [re-frame.core :as rf :refer [reg-sub subscribe]]
   [clojure.data :as clj-data]
   [cube-test.utils.choice-carousel.choice-carousel :as cc]))

(def ^:dynamic *last-choice-carousels* (atom []))
;;
;; extractors
;;
(reg-sub
 :get-choice-carousels
 (fn [db _]
   (prn "subs: now driving get-choice-carousels")
   (get-in db [:choice-carousels])))

; (fn [db query-v])
(reg-sub
 :get-last-selected-idx
 ; (fn [db _])
 ; (fn [db query-v])
 (fn [db [_ idx-path]]
   ; (prn "subs: now driving get-last-selected-idx, query-v=" query-v)
   (prn "subs: now driving get-last-selected-idx, idx-path=" idx-path)
   ; (get-in db [:choice-carousels 0 :last-selected-idx])
   (when idx-path
     (get-in db idx-path))))
   ; (get-in db [:globals 0 :last-selected-idx])))

;;
;; computations
;;
; (defn choice-carousels-changed [choice-carousels query-v]
; (fn [query-v]
;   [(subscribe [:a]) (subscribe [:b 2])])
(reg-sub
  :choice-carousels-changed
  ; :<- [:get-choice-carousels]
  ; :<- [:get-last-selected-idx]
  ; (fn [query-v])
  (fn [[_ idx-path]]
    ; (prn "*query-v=" query-v)
    (prn "*idx-path=" idx-path)
    ; [(subscribe [:get-choice-carousels])(subscribe [:get-last-selected-idx (nth query-v 1)])]
    [(subscribe [:get-choice-carousels])(subscribe [:get-last-selected-idx idx-path])])
  ; :<- [:get-last-selected-idx [:globals :top-scene :last-selected-idx]]
  ; :<- [:get-last-selected-idx (nth query-v 1)]
  ; (fn [choice-carousels last-selected-idx query-v])
  (fn [[choice-carousels last-selected-idx] query-v]
    ; (prn "subs: now in choice-carousels-changed, choice-carousels=" choice-carousels ",last-idx=" last-selected-idx)
    ; (prn "subs.choice-carousels-changed: last-idx=" last-selected-idx ", second query-v=" (nth query-v 1))
    (prn "subs.choice-carousels-changed: last-idx=" last-selected-idx)
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
          ; (prn "cc.subs: diff-full=" diff-full)
          ; (prn "cc.subs: diff-a=" diff-a)
          ; (prn "cc.subs: second diff-a=" (second diff-a))
          ; (prn "cc.subs: diff-b=" diff-b)
          (prn "cc.subs: diff=" diff)
          ; (when diff)
          ;; We denote a "new" choice insertion as any choice that has an ":id"
          ;; We need to distinguish this path because we may add other keys but
          ;; do *not* want to drive the full initialization path.
          ; (when (and diff (get-in diff [:choice :id])))
          (when (and diff (get-in diff [:id]))
            ; (cc/init-meshes (:radius diff) (:choices diff) (:colors diff))
            (prn "subs. calling init-meshes: last-selected-idx=" last-selected-idx)
            (cc/init-meshes (:radius diff) (:choices diff) (:colors diff) last-selected-idx)
            ; (cc/init-models (:choices diff))
            (rf/dispatch [:cube-test.utils.choice-carousel.events/init-model-containers])
            (prn "just dispatched"))

          (swap! *last-choice-carousels* (fn [x] choice-carousels))))))

;; housekeeping
(defn release []
  (swap! *last-choice-carousels* (fn [x] atom [])))
