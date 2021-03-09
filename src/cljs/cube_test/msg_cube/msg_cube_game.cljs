(ns cube-test.msg-cube.msg-cube-game
  (:require
   [re-frame.core :as re-frame]
   [cube-test.msg-cube.msg-cube-scene :as msg-cube-scene]))

(defn init []
  (println "msg-cube-game.init: entered")
  ; (js-debugger)
  (re-frame/dispatch [:init-msg-cube-scene]))

(defn run []
  (println "msg-cube-game.run: entered")
  (re-frame/dispatch [:run-msg-cube-scene]))
  ; (msg-cube-scene/run-scene))
