(ns cube-test.tmp-scene.events
  (:require
   [re-frame.core :refer [dispatch dispatch-sync reg-event-db reg-event-fx reg-fx after ] :as rf]
   ; [cube-test.frig-frog.game :as ff.game]
   [cube-test.main-scene :as main-scene]
   [cube-test.tmp-scene.scene :as tmp-scene]))

;;
;; tmp-scene
;;
(reg-event-fx
  :init-tmp-scene
  (fn [cofx _]
    {:fx [(tmp-scene/init)]}))

(reg-event-fx
  :run-tmp-scene
  (fn [cofx _]
    {:fx [(tmp-scene/run-scene)]}))
