(ns cube-test.ut-simp.ut-simp-scene
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.utils.fps-panel :as fps-panel]
   [clojure.spec.alpha :as s]
   [cube-test.ut-simp.msg-box :as msg-box]
   [cube-test.ut-simp.msg-cube-ph :as msg-cube-ph]))
   ; [cube-test.specs.ut-simp-spec :as ut-simp-spec]))

;; structs
;; main data struct.  This is a series of "message boxes" that represent a message.  Each message
;; box is either green=info, yellow=warn, or red=severe.
; (def msg-boxes)
(def ^:dynamic *msg-boxes-atom* (atom []))
; (s-def)

;; inits
(defn init []
  (println "ut-simp-scene/scene.init: entered")
  (let [light (bjs/PointLight. "pointLight" (bjs/Vector3. 0 5 -3) main-scene/scene)]
    (.setEnabled light true))
  (msg-box/add-msg-box *msg-boxes-atom*)
  ; (assoc-in db [:vrubik-state :rots :left-side] 0)
  ; (assoc-in db [:msg-boxes-atom] *msg-boxes-atom*)
  ; (println "init-pre db=" db)
  (re-frame/dispatch [:set-msg-boxes-atom *msg-boxes-atom*])
  ; (println "init-post db=" db)
  ; (assoc-in db [:abc] 7)
  ; (assoc db :abc 7)
  ; (re-frame/dispatch [:add-msg-box *msg-boxes-atom*])
  ; (js-debugger)
  (re-frame/dispatch [:add-msg-cube-ph (get @*msg-boxes-atom* 0)])
  ;; 2nd method
  (re-frame/dispatch [:init-msg-boxes-2])
  (re-frame/dispatch [:add-msg-box-2
                      {::msg-box/id 2 ::msg-box/msg {::msg-box/text "def" ::msg-box/msg-level :INFO}}]))
  ; (re-frame/dispatch [:add-msg-box (db :msg-boxes-atom)])
  ; (println "init: *msg-boxes-atom*=" *msg-boxes-atom*)
  ; (println "init: @*msg-boxes-atom*=" @*msg-boxes-atom*))
  ; (println "init: (db :msg-boxes-atom=)" (db :msg-boxes-atom))
  ; (println "init: (db :abc=)" (db :abc))
  ; (println "init: db=" db))

; {::id 0 ::msg {::text "abc" ::msg-level :INFO}}
; (defn add-msg-box []
;   (swap! *msg-boxes* [{::id 0 ::msg {::text "abc" ::msg-level :INFO}}]))

;; render
(defn render-loop []
  ; (println "ut-simp-scene.render-loop")
  (if (= main-scene/xr-mode "vr")
    (controller/tick)
    (controller-xr/tick))
  (fps-panel/tick main-scene/engine)
  (.render main-scene/scene))

(defn run-scene []
  (println "ut-simp-scene.run-scene: entered")
  (.runRenderLoop main-scene/engine (fn [] (render-loop))))
