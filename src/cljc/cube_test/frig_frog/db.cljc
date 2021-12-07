(ns cube-test.frig-frog.db
  (:require
   [re-frame.core :as re-frame]
   [clojure.spec.alpha :as s]
   [clojure.test.check.generators :as gen]
   [clojure.pprint :refer [pprint]]
   [cube-test.utils :as utils]))

(def default-game-db
  {
    :game-abc 7})

(def default-db
  {
    :abc 7})
   ; :interval-map {}
   ; :song-loaded false
   ; :twitch-streaming-active false
   ; :twitch-load-status 0
   ; :models {:dynamic {:anim-factor 1.6}}})

  ;;  :name "re-frame"
   ; ::twizzlers []})
   ; :scenes {
   ;          :space-port {:path "models/space_portal/" :fn "space_portal.glb" :loaded false}
   ;          :hemisferic {:path "models/hemisferic/" :fn "hemisferic.glb" :loaded false}}
   ; :twizzlers []})

(defn init-db [db]
  (utils/merge-dbs db default-db))

(defn init-game-db [db]
  (prn "frig-frog.db: init-game-db: entered"))
  ; (utils/merge-dbs db default-game-db))
