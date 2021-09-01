(ns cube-test.beat-club.db
  (:require
   [re-frame.core :as re-frame]
   [clojure.spec.alpha :as s]
   [clojure.test.check.generators :as gen]
   [clojure.pprint :refer [pprint]]
   [cube-test.utils :as utils]))

(def default-db
  {
   ; :interval-map {}
   :song-loaded false
   :twitch-streaming-active false
   :twitch-load-status 0
   :models {:dynamic {:anim-factor 1.6}}})

  ;;  :name "re-frame"
   ; ::twizzlers []})
   ; :scenes {
   ;          :space-port {:path "models/space_portal/" :fn "space_portal.glb" :loaded false}
   ;          :hemisferic {:path "models/hemisferic/" :fn "hemisferic.glb" :loaded false}}
   ; :twizzlers []})

(defn init-db [db]
  (utils/merge-dbs db default-db))
