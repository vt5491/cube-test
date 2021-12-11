(ns cube-test.frig-frog.subs
  (:require
   [re-frame.core :as re-frame :refer [reg-sub subscribe]]
   [cube-test.frig-frog.events :as events]
   [cube-test.frig-frog.scene :as scene]
   [cube-test.utils :as utils]
   [clojure.data :as clj-data]))

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
