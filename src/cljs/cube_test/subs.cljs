(ns cube-test.subs
  (:require
   [re-frame.core :as re-frame :refer [reg-sub subscribe]]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

; (reg-sub
;   :id
;
;   ;; signals function
;   (fn [query-v]
;     [(subscribe [:a]) (subscribe [:b 2])])     ;; <-- these inputs are provided to the computation function
;
;   ;; computation function
;   (fn [[a b] query-v]                  ;; input values supplied in a vector
;       (calculate-it a b)))
;; queries
(re-frame/reg-sub
  :max-id          ;; usage:   (subscribe [:showing])
  (fn [db _]        ;; db is the (map) value stored in the app-db atom
    (:max-id db))) ;; extract a value from the application state

(re-frame/reg-sub
  :msgs          ;; usage:   (subscribe [:showing])
  (fn [db _]        ;; db is the (map) value stored in the app-db atom
    (:msgs db))) ;; extract a value from the application state

(re-frame/reg-sub
  :max-id-and-msgs
  ; :<- [:max-id]
  ; :<- [:msgs])
  ; :<- [:msgs :max-id])
  (fn [db _]
    [(:max-id db) (:msgs db)]))

; (reg-sub
;   :gen-msg-cube
;
;   ;; input signals
;   :<- [:max-id]        ;; means (subscribe [:a] is an input)
;   ; :<- [:b 2]      ;; means (subscribe [:b 2] is an input)
;
;   ;; computation function
;   (fn [[max-id] query-v]
;     (when max-id
;        (re-frame/dispatch [:add-msg-cube max-id]))))

; id= [3 [{:id 2, :text hi2}]]

;; signal flows
(reg-sub
 :gen-msg-cube
 ; :<- [:max-id]
 ; :<- [:max-id]
 (fn [query-v]
   (println "gen-msg-cube: query-v=" query-v)
   ; [(subscribe [:max-id :msgs])])
   ; [(subscribe [:max-id-and-msgs])])
   [(subscribe [:max-id])
    (subscribe [:msgs])])
 (fn [[max-id msgs] query-v]
 ; (fn [args]
 ;   (println "get-msg-cube: args=" args)))
 ; (fn [[max-id] [msgs] query-v]
 ; (fn [[max-id] query-v])
 ; (fn [[[max-id msgs]] query-v]
   (let [msg (-> (filter #(= (%1 :id) max-id) msgs) (first))]
         ; max-id-kw (keyword (str max-id))]
     ; (println "reg-sub: detected :max-id and :msgs change: max-id=" max-id ", msgs=" msgs, ",msg[:max-id]=" (msg max-id-kw))
     (println "reg-sub: detected :max-id and :msgs change: max-id=" max-id ", msgs=" msgs, ",msg=" msg)
     (when (not (empty? msg))
       (re-frame/dispatch [:add-msg-cube msg])))))

   ; (println "reg-sub: detected :max-id and :msgs change: max-id=" max-id ", msgs=" msgs)
   ; (println "reg-sub: detected :max-id and :msgs change: max-id=" max-id ",query-v=" query-v)
   ; (when (and max-id msgs))
   ; (when max-id
   ;   (re-frame/dispatch [:add-msg-cube max-id]))))
   ; (filter #(and (contains? %1 :a) (= (%1 :a) 1)) [{:a 1} {:b 1} ()])
   ; (-> (filter #(and (contains? %1 :a) (= (%1 :a) 1)) [{:a 2} {:b 1} ()]) count)
   ; (let [r (filter #(and (contains? %1 :id) (= (%a :a) max-id)))])
   ;; only add a cube if the new max-id does not already exist

   ;; since max-id and the latest msg are added together, we can distinguish between
   ;; a max-id only update, and a max-id *and* msgs update by seeing if there is a msg
   ;; with the id of the max-id.  A max-id only update will *not* have a corresponding msg.
   ; (when (-> (filter #(and (contains? %1 :id) (= (%1 :id) max-id)) msgs) count (= 1))
   ;   ; (re-frame/dispatch [:add-msg-cube max-id])
   ;   (re-frame/dispatch [:add-msg-cube (msgs (keyword (str max-id)))]))))



(re-frame/reg-sub
 :msgs-cnt
 :<- [:msgs]
 ; (fn [query-v]
 ;   ;; signals fn
 ;   (println "msgs-change: a")
 ;   [(re-frame/subscribe [:msgs])])
 ;   ; [(re-frame/subscribe [:max-id])])
 (fn [[msgs] query-v]
   (str "msgs-count: count msgs=" (count msgs))))

(println "now running cube-test.subs")
; (reg-sub
;   :showing          ;; usage:   (subscribe [:showing])
;   (fn [db _]        ;; db is the (map) value stored in the app-db atom
;     (:showing db))) ;; extract a value from the application state
