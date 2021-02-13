(ns cube-test.ut-simp.msg-box
  (:require
   [re-frame.core :as re-frame]
   [clojure.spec.alpha :as s]))
   ; [cube-test.specs.ut-simp-spec :as mb-spec]))

;; example of how you can refer to "outsider" specs.
; (s/valid? ::mb-spec/msg-box {::mb-spec/id 0 ::mb-spec/msg {::mb-spec/text "abc" ::mb-spec/msg-level :INFO}})

;; specs
(s/def ::text string?)
(s/def ::msg-level #{:INFO :WARN :SEVERE})
(s/def ::id int?)
(s/def ::msg (s/keys :req [::text ::msg-level]))
(s/def ::msg-box (s/keys :req [::id ::msg]))
(s/def ::msg-boxes (s/coll-of ::msg-box))

; (s/valid? ::msg-boxes [{::id 0 ::msg {::text "abc" ::msg-level :INFO}}
;                        {::id 1 ::msg {::text "def" ::msg-level :WARN}}])

(defn add-msg-box [*msg-boxes-atom*]
  (swap! *msg-boxes-atom* (fn [x] [{::id 1 ::msg {::text "abc" ::msg-level :INFO}}]))
  (let [r (s/assert ::msg-boxes @*msg-boxes-atom*)
        r2 (s/valid? ::msg-boxes @*msg-boxes-atom*)]
    (println "*msg-boxes*=" *msg-boxes-atom*)
    (println "add-msg-box: assert=" r ", valid=" r2)))

(defn add-msg-box-2 [db]
  (swap! (db :msg-boxes-atom-2) (fn [x] [{::id 1 ::msg {::text "def" ::msg-level :INFO}}])))

(defn print-msg-boxes [db]
  (println "print-msg-boxes: db=" db)
  (println "print-msg-boxes: msg-boxes=" (db :msg-boxes-atom)))
  ; (println "print-msg-boxes: msg-boxes=" @(db :msg-boxes-atom)))
