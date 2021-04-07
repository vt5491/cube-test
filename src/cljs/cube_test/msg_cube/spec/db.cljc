;; This is the spec for anything in the db for 'msg-cube'
(ns cube-test.msg-cube.spec.db
  (:require
   [re-frame.core :as re-frame]
   [clojure.spec.alpha :as s]))

(def default-db
  {
  ;;  :name "re-frame"
   :msgs []
   :msgs-2 []
   :ints [0 2]
   :max-id 0
   :input-id 2})

(def dummy 7)
(s/def ::msgs vector?)
(s/def ::msgs-2 vector?)
(s/def ::ints vector?)
(s/def ::input-id int?)

(s/def ::max-id int?)
(s/def ::name string?)

(s/def ::db-spec (s/keys :req-un [::msgs ::max-id ::name]))
