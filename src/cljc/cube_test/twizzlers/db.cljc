(ns cube-test.twizzlers.db
  (:require
   [re-frame.core :as re-frame]
   [clojure.spec.alpha :as s]
   [clojure.test.check.generators :as gen]
   [clojure.pprint :refer [pprint]]
   [cube-test.utils :as utils]))

(def dummy 7)

(comment
  (+ 1 1)
  (print {:a 7, :b 8})

  (pprint (gen/generate (s/gen ::first-name)))

  (pprint (gen/generate (s/gen ::last-name)))

  (pprint (gen/generate (s/gen ::person)))

  dummy
  ,)

(def default-db
  {
  ;;  :name "re-frame"
   ::twizzlers []})
   ; :msgs-2 []
   ; :ints [0 2]
   ; :max-id 0
   ; :input-id 2})

(defn init-db [db]
  ; (utils/merge-dbs db cube-test.msg-cube.spec.db/default-db)
  (utils/merge-dbs db default-db))

;; specs

;; Individual twizzler
;; (s/def ::twizzler-id int?)

;; (s/def :twz/id int?)

(s/def ::id int?)

;; (s/def ::twizzler
;;  (s/keys :req [::twizzler-id]))

(s/def ::twizzler
 (s/keys :req [::id]))

;; (s/def ::twz
;;  (s/keys :req [:twz/id]))

; (s/def ::db-spec (s/keys :req-un [::twizzlers]))
(s/def ::db-spec (s/keys :req [::twizzlers]))

;; collective twizzlers
; (s/def ::twizzlers vector?)
;; (s/def ::twizzlers vector?)
;; (s/def ::twizzlers (s/coll-of ::twizzler? :kind vector?))
(s/def ::twizzlers (s/coll-of ::twizzler :kind vector?))

;; example from david nolen talk
;; (s/def ::first-name
;;  (s/with-gen string?
;;   #(s/gen #{"Steve" "Sam" "Gretchen"})))

;; (s/def ::last-name
;;  (s/with-gen string?
;;   #(s/gen #{"Adams" "Turner" "Black"})))

;; (s/def ::person
;;  (s/keys :req [::first-name ::last-name]))
(comment
 ;;  (s/valid? ::msg {::text "abc", ::msg-level :INFO}))
;;  (s/valid? ::twizzler {:twizzler-id 1})
;;  (s/valid? ::twizzler {::twizzler-id 1})
 (s/valid? ::twizzler {::id 1})

;;  (s/valid? ::twz {:twz/id 1})
 ,)
