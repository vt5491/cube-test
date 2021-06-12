;; events is refer to many
;; We need to access some util methods through re-frame to avoid
;; circular references, such as main-scene trying to directly call utils.
(ns cube-test.utils.events
  (:require
   [re-frame.core :refer [reg-event-db reg-event-fx reg-fx after ] :as re-frame]
   [cube-test.utils :as utils]))

; (def a 7)
; (re-frame/reg-event-fx
;  ::get-xr-camera
;  (fn [_]
;    (prn "utils.events.get-xr-camera: entered")
;    (prn "utils.events.get-xr-camera: result=" (utils/get-xr-camera))
;    (prn "utils.events.get-xr-camera: result2=" (utils/kw-to-int :17))
;    ; (utils/get-xr-camera)
;    (utils/kw-to-int :17)))

(re-frame/reg-event-fx
 ; :init-twizzlers-scene
 ::kw-to-int
 ; (fn [cofx _])
 ; :keys [db] :as cofx
 ; (fn [{:keys [event] :as e} _])
 ; (fn [{oe :original-event, db :db} event])
 (fn [{oe :original-event, db :db} [e-name e-kw]]
   ; (prn "::events.kw-to-int: cofx=" cofx)
   ; (prn "::events.kw-to-int: cofx=" cofx)
   (prn "::events.kw-to-int: oe=" oe)
   (prn "::events.kw-to-int: db=" db)
   ; (prn "::events.kw-to-int: event=" event)
   (prn "::events.kw-to-int: e-name=" e-name ", e-kw" e-kw)
   ; (utils/kw-to-int (:db cofx))
   (utils/kw-to-int e-kw)))
   ; cofx))

(reg-event-fx
; (reg-fx
 ::get-xr-camera
 ; (fn [cofx [_]])
 (fn [cofx event]
   (println "utils.events.get-xr-camera: cofx=" cofx)
   (println "utils.events.get-xr-camera: event=" event)
   ; (prn "utils.events.get-xr-camera: result2=" (utils/kw-to-int :18))
   ; {:fx [(utils/get-xr-camera)]}
   ; {:fx [(utils/kw-to-int :18)]}
   ; {:fx [[:kw (utils/kw-to-int :18)]]}
   ; {:fx [[:dispatch [::kw-to-int :18]]]}
   {:kw (utils/kw-to-int :18)}))
   ; (utils/kw-to-int :18)))
