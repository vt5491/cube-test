(ns cube-test.frig-frog.game
  (:require
   [re-frame.core :as re-frame]
   ; [cube-test.msg-cube.spec.db :as msg-cube.spec]
   ; [cube-test.utils :as utils]
   [cube-test.main-scene :as main-scene]
   ; [cube-test.frig-frog.scene :as frig-frog-scene]
   ; [cube-test.frig-frog.db :as frig-frog-db]
   [cube-test.controller :as controller]
   [cube-test.controller-xr :as controller-xr]
   [cube-test.utils.fps-panel :as fps-panel]
   [cube-test.utils.common :as common]))

(def default-game-db
  {:game-abc 7
   :n-cols 2 :n-rows 3
   ; :n-cols 10 :n-rows 10
   :board {}})

(defn dmy []
  8)

; (defn change-abc [new-val db]
(defn change-abc [db new-val]
  (prn "game.change-abc: new-val=" new-val)
  (prn "game.change-abc: db=" db)
  (prn "game.change-abc: db.abc=" (db :abc))
  (assoc db :abc new-val))

(defn render-loop []
  (if (= main-scene/xr-mode "vr")
    (controller/tick)
    (controller-xr/tick))
  (if fps-panel/fps-pnl
    (fps-panel/tick main-scene/engine))
  ; (beat-club-scene/tick)
  (.render main-scene/scene))

(defn run-game []
  (.runRenderLoop main-scene/engine (fn [] (render-loop))))

(defn init-game-db [game-db]
  ; (re-frame/dispatch [:common/init-game-db d])
  (re-frame/dispatch [:cube-test.events/init-game-db default-game-db]))
  ; default-game-db)

(defn init []
  (println "frig-frog.game.init: entered")
  (re-frame/console :warn "console msg from frig-frog.game.init")
  ; (re-frame/dispatch [:frig-frog-db/init-game-level-db])
  ; (re-frame/dispatch [:cube-test.frig-frog.events/init-game-db])
  (init-game-db default-game-db)
  (re-frame/dispatch [:cube-test.frig-frog.events/init-board]))
  ; (re-frame/dispatch [:cube-test.events/init-game-db default-game-db]))
