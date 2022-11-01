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

(defn init-db [db]
  (utils/merge-dbs db default-db))

(defn seed-db [db seed-db]
  (utils/merge-dbs db seed-db))

(defn init-game-db [db]
  (prn "frig-frog.db: init-game-db: entered"))
