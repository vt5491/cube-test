(ns cube-test.twizzlers.game
  (:require
   [re-frame.core :as re-frame]
   ; [cube-test.msg-cube.spec.db :as msg-cube.spec]
   [cube-test.utils :as utils]
   [cube-test.main-scene :as main-scene]
   [cube-test.twizzlers.scene :as twizzlers-scene]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.utils.fps-panel :as fps-panel]))
   ; [cube-test.twizzlers.events :as twizzler-events]))

(def ^:dynamic *a* (atom 10))
; (def ^:dynamic *a* (atom []))

(defn update-atom []
  (swap! *a* inc))
  ; (swap! *a* (fn [x] (+ x 1)))
  ; (swap! *a* (fn [x] (conj x 1))))

(defn render-loop []
  (if (= main-scene/xr-mode "vr")
    (controller/tick)
    (controller-xr/tick))
  (if fps-panel/fps-pnl
    (fps-panel/tick main-scene/engine))
  (twizzlers-scene/tick)
  (.render main-scene/scene))

(defn run-game []
  ; (run-render-loop)
  (.stopRenderLoop main-scene/engine)
  (.runRenderLoop main-scene/engine (fn [] (render-loop))))

(defn init []
  (println "twizzlers.game.init: entered")
  ; (re-frame.core/set-loggers!  {:warn utils/rf-override-logger})
  ; (re-frame.core/set-loggers!  {:warn utils/rf-odoyle-warn-override-logger})
  (re-frame/console :warn "console msg from twizzlers.game.init"))
  ; (re-frame/dispatch [::twizzler-events/init-scene])
  ; (re-frame/dispatch [:cube-test.twizzlers.events/init-scene])
  ;; Note: we init the scene only through db updates.  We do not explicitly call.
  ;vt-x (re-frame/dispatch [:cube-test.twizzlers.events/init-scene]))
  ; (assoc))
