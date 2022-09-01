
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

; (reg-event-db
;  ::choice-model-loaded-rf
;  (fn [db [_ user-parms meshes particle-systems skeletons anim-groups transform-nodes geometries lights user-cb]]
;    (cc/choice-model-loaded-rf db user-parms meshes particle-systems skeletons anim-groups transform-nodes geometries lights user-cb)))

(reg-event-db
 ::get-choice-idx-by-id
 (fn [db [_ choices id]]
   ; (prn "events.init-choice-carousel:6 parms=" parms "db=" db)
   (cc/get-choice-idx-by-id choices id)
   db))

(rf/reg-event-fx
 ::get-choice-idx-by-id-2
 (fn [cofx [_ id]]
   (let [idx (cc/get-choice-idx-by-id (get-in (:db cofx) [:choice-carousels 0 :choices]) id)]
     (prn "event: idx=" idx))))
   ; {
   ;  :db (:db cofx)}))

; (reg-event-db
;  ::get-choice-idx-by-id-2
;  (fn [db [_ id]]
;    (prn "events.get-choice-idx-by-id-2: db=" db)
;    (cc/get-choice-idx-by-id (get-in db [:choice-carousels 0 :choices]) id)
;    db))

(reg-event-db
 ::init-model-containers
 (fn [db [_ parms]]
   ; (prn "events.init-choice-carousel:6 parms=" parms "db=" db)
   (cc/init-model-containers db)))

(reg-event-db
 ::update-choice
 (fn [db [_ choice-idx new-choice]]
   ; (prn "events.init-choice-carousel:6 parms=" parms "db=" db)
   ; (cc/init-model-containers db)))
   (assoc-in db [:choice-carousels 0 :choices choice-idx] new-choice)))
