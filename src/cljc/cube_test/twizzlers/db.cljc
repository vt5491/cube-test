(ns cube-test.twizzlers.db
  (:require
   [re-frame.core :as re-frame]
   [clojure.spec.alpha :as s]
   [cube-test.utils :as utils]))

(def default-db
  {
  ;;  :name "re-frame"
   :twizzlers []})
   ; :msgs-2 []
   ; :ints [0 2]
   ; :max-id 0
   ; :input-id 2})

(defn init-db [db]
  ; (utils/merge-dbs db cube-test.msg-cube.spec.db/default-db)
  (utils/merge-dbs db default-db))

;; specs
; (s/def ::twizzlers vector?)
(s/def ::twizzlers vector?)

(s/def ::db-spec (s/keys :req-un [::twizzlers]))
