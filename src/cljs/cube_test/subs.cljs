(ns cube-test.subs
  (:require
   [re-frame.core :as re-frame :refer [reg-sub subscribe]]
   [cube-test.msg-cube.data.msg :as msg]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

;; queries
(re-frame/reg-sub
  :max-id
  (fn [db _]
    (:max-id db)))

(reg-sub
  :msgs
  ; (fn [db _])
  (fn [db query-v]
    (println "reg-sub:msgs: query-v=" query-v)
    (:msgs db)))

(reg-sub
  :msgs-2
  (fn [db query-v]
    (println "reg-sub:msgs-2: query-v=" query-v)
    (:msgs-2 db)))

(re-frame/reg-sub
  :max-id-and-msgs
  ; :<- [:max-id]
  ; :<- [:msgs])
  ; :<- [:msgs :max-id])
  (fn [db _]
    [(:max-id db) (:msgs db)]))

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
   (println ":gen-msg-cube: msgs=" msgs)
 ; (fn [args]
 ;   (println "get-msg-cube: args=" args)))
 ; (fn [[max-id] [msgs] query-v]
 ; (fn [[max-id] query-v])
 ; (fn [[[max-id msgs]] query-v]
   (let [msg (-> (filter #(= (%1 :id) max-id) msgs) (first))]
         ; max-id-kw (keyword (str max-id))]
     ; (println "reg-sub: detected :max-id and :msgs change: max-id=" max-id ", msgs=" msgs, ",msg[:max-id]=" (msg max-id-kw))
     (println "reg-sub:gen-msg-cube: detected :max-id and :msgs change: max-id=" max-id ", msgs=" msgs, ",msg=" msg)
     (println "hi there")
     (when (not (empty? msg))
       (re-frame/dispatch [:add-msg-cube msg]))
     (println "hi there 2"))))

(reg-sub
 :gen-msg-cube-2
 :<- [:max-id]
 :<- [:msgs-2]
 ; (fn [query-v]
 ;   (println "gen-msg-cube-2: query-v=" query-v)
 ;   [(subscribe [:msgs-2])])
 (fn [[max-id msgs-2] query-v]
   (println ":gen-msg-cube-2: max-id=" max-id ",msgs-2=" msgs-2)
   (let [ msg-2 (-> (filter #(= (%1 :id) max-id) msgs-2) (first))]
     (println "reg-sub:gen-msg-cube: detected :max-id and :msgs change: max-id=" max-id ", msgs-2=" msgs-2, ",msg-2=" msg-2)
     (println "hi there a")
     (when (not (empty? msg-2))
       (re-frame/dispatch [:add-msg-cube-2 msg-2]))
     (println "hi there 2a"))))

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

(re-frame/reg-sub
 :msgs-level
 :<- [:msgs]
 (fn [[msgs] query-v]
   (println "msgs-level: query-v=" query-v)
   (str "msgs-level: query-v=" query-v)))

;; Query
(reg-sub
  :msg-by-id
  ; (fn [db _])
  (fn [db [_ id]]
    (println "reg-sub:msg-by-id")
    (println "reg-sub:msg-by-id: id=" id)
    (msg/get-by-id id (:msgs db))))
    ; (:msgs db)))

(reg-sub
 :msg-changed-by-id
 ; :<- [:msgs]
 ; (fn [query-v])
 (fn [[_ id]]
   ; [(subscribe [:msgs])]
   ; (println "reg-sub:msg-by-id query-v=" query-v)
   (println "abc")
   (println "reg-sub:msg-changed-by-id id=" id)
   ; (subscribe [:msgs 1])
   ; (subscribe [:msgs id]))
   ; (subscribe [:msg-by-id id]))
   (subscribe [:msg-by-id 1]))
 ; (fn [[msgs] query-v])
 ; (fn [msgs query-v])
 (fn [[msg] [_ id]]
   (println "reg-sub:msg-changed-by-id: *msg-changed-by-id: msg=" msg ", id=" id)))
   ; (re-frame/dispatch [:msg-cube.update-msg-cube id])))
   ; (str "msg-by-id: id=" id)))

(reg-sub
  :msg-by-id-2
  (fn [db [_ id]]
    (println "reg-sub:msg-by-id-2: id=" id)
    (let [msg (msg/get-by-id id (db :msgs-2))]
      (println "reg-sub: msg-by-id-2: msg=" msg)
      ; msg
      ; [1 2]
      ;; Note: Definitely need to return your result in a vector.
      [msg])))
    ; (msg/get-by-id id (:msgs-2 db))))

(reg-sub
 :msg-changed-by-id-2
 ; :<- [:msgs]
 ; (fn [query-v])
 (fn [[_ id]]
   (println "abc")
   (println "reg-sub:msg-changed-by-id-2 id=" id)
   ; (subscribe [:msg-by-id-2 2])
   (subscribe [:msg-by-id-2 id]))
 (fn [[msg] [_ id]]
   (println "reg-sub:msg-changed-by-id-2: *msg-changed-by-id: msg=" msg ", id=" id)
   (when msg
     (re-frame/dispatch [:msg-cube.update-msg-cube id (msg :level)]))
   ["result"]))
