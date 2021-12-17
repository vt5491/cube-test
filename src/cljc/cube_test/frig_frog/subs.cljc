(ns cube-test.frig-frog.subs
  (:require
   [re-frame.core :as re-frame :refer [reg-sub subscribe]]
   [cube-test.frig-frog.events :as events]
   [cube-test.frig-frog.scene :as scene]
   [cube-test.utils :as utils]
   [clojure.data :as clj-data]))

(def ^:dynamic *last-board* (atom nil))
; (def ^:dynamic *last-board* (atom {}))

;; extractors
(reg-sub
 :get-board
 (fn [db _]
   (:board db)))

;; computations
(reg-sub
 :board
 :<- [:get-board]
 (fn [db query-v]
   (prn "board has changed")))

(reg-sub
 :board-changed
 :<- [:get-board]
 (fn [board query-v]
   ; (re-frame/dispatch [:cube-test.frig-frog.events/draw-tile 0 0])
   (prn "subs: *last-board*=" @*last-board*)
   (prn "subs: diff=" (first (clj-data/diff board @*last-board*)))
   (prn "subs: board-changed: board=" board ",query-v=" query-v)
   (let [diff (first (clj-data/diff board @*last-board*))]
      (prn "diff=" diff))
   (swap! *last-board* (fn [x] board))))
