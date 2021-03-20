;; This is the spec for anything in the db for 'msg-cube'
(ns cube-test.msg-cube.spec.db
  (:require
   [re-frame.core :as re-frame]
   [clojure.spec.alpha :as s]))

(s/def ::msgs vector?)

(s/def ::max-id int?)
