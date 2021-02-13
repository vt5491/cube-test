;; Note: the specs for msg-box are now in 'cljs/scenes/ut_simp_scene/msg_box.cljs'
;; I did this because it is very hard (e.g wordy) to reference a spec in one namespace from another
;; as aliasing doesn't seem to work when referencing a spec def

(ns cube-test.specs.ut-simp-spec
  (:require [clojure.spec.alpha :as s]))

(def a 9)
; (s/def ::msg-boxes (s/cat))
(s/def ::text string?)
(s/def ::msg-level #{:INFO :WARN :SEVERE})

; (s/def ::msg {::text string?, ::level ::msg-level})
(s/def ::msg (s/keys :req [::text ::msg-level]))
; (s/def ::msg2 [:text string?])
; (s/def ::msg3 (s/cat :text string?))
; (s/def ::msg4 (s/keys :req [::text]))

(s/valid? ::msg {::text "abc", ::msg-level :INFO})
(s/explain ::msg {::text "abc", ::msg-level :INFO})

(s/def ::msg-box-id keyword?)
(s/def ::id int?)

; (s/def ::msg-box (s/keys :req [::id ::msg]))
(s/def ::msg-box (s/keys :req [::id ::msg]))
; (s/def ::msg-box (s/keys :req [int? ::msg-level]))
; (s/def ::msg-box (s/keys :req [(s/cat int? ::msg)]))
(s/valid? ::msg-box {::id 0 ::msg {::text "abc" ::msg-level :INFO}})
(s/explain ::msg-box {::id 1 ::msg {::text "abc" ::msg-level :INFO}})
(s/assert ::msg-box {::id "1" ::msg {::text "abc" ::msg-level :INFO}})
; (s/explain ::msg-box {0 :INFO})

(s/def ::dmy (s/keys :req [::text ::id]))
(s/explain ::dmy {::text "hi" ::id :0})
(s/explain ::dmy {::id 1 ::text "hi"})
(s/valid? ::dmy {::text "hi" ::msg-box-id :0})
(s/valid? ::dmy {::id :1 ::text "hi"})

(s/def ::dmy2 (s/and keyword? (s/keys :req [::text ::msg-box-id])))
; (s/explain ::dmy2 {::text "hi" :0})
; (s/valid? ::dmy2 {::text "hi" :0})
; (s/def ::msg-boxes (s/keys :))

; (s/explain ::msg2 {:text "abc"})

; (s/explain ::msg2 [:text "abc"])
; (s/valid? ::msg2 1)
; (s/valid? ::msg2 "abc")
; (s/valid? ::msg2 [:text "abc"])
; (s/valid? ::msg3 [:text "abc"])
; (s/explain ::msg3 [:text "abc"])
; (s/explain ::msg3 {:text "abc"})
; ; (s/explain ::msg4 {::text "abc"})
; (s/valid? ::msg4 {::text "abc"})

; (s/valid? ::msg-level :INFOR)
; (s/explain ::msg-level :INFOR)
(s/def ::msg-boxes (s/coll-of ::msg-box))

(s/valid? ::msg-boxes [{::id 0 ::msg {::text "abc" ::msg-level :INFO}}
                       {::id 1 ::msg {::text "def" ::msg-level :WARN}}])
