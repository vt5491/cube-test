
(ns cube-test.utils.choice-carousel.events
  (:require
   [re-frame.core :refer [dispatch dispatch-sync reg-event-db reg-event-fx reg-fx after ] :as rf]
   [cube-test.utils.choice-carousel.choice-carousel :as cc]))


(reg-event-db
 ::init-choice-carousel
 (fn [db [_ parms]]
   ; (prn "events.init-choice-carousel:6 parms=" parms "db=" db)
   (cc/init parms db)))
   ; db))

(reg-event-db
 ::choice-model-loaded-rf
 (fn [db [_ user-parms meshes particle-systems skeletons anim-groups transform-nodes geometries lights user-cb]]
   (cc/choice-model-loaded-rf db user-parms meshes particle-systems skeletons anim-groups transform-nodes geometries lights user-cb)))
