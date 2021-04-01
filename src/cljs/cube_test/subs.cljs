(ns cube-test.subs
  (:require
   [re-frame.core :as re-frame :refer [reg-sub subscribe]]
   ; [cube-test.db :as db]
   [re-frame.db :as rf-db]
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
    ; [(:max-id db)]))

(re-frame/reg-sub
  :max-id-with-msgs
  (fn [db _]
    ; (:max-id db)
    (println ":max-id-with-msgs: db=" db)
    [(:max-id db) (:msgs db)]))

;; This is just a better-named version of 'max-id-with-msgs'
(re-frame/reg-sub
  :max-id-and-msgs
  (fn [db _]
    ; (:max-id db)
    (println ":max-id-and-msgs: db=" db)
    [(:max-id db) (:msgs db)]))

(reg-sub
  :msgs
  ; (fn [db _])
  (fn [db query-v]
    (println "reg-sub:msgs: db=" db ",query-v=" query-v)
    (:msgs db)))


(reg-sub
  :msgs-2
  (fn [db query-v]
    (println "reg-sub:msgs-2: query-v=" query-v)
    (:msgs-2 db)))

(reg-sub
  :ints
  (fn [db query-v]
    (println "reg-sub:ints: query-v=" query-v)
    (:ints db)))

(reg-sub
  :input-id
  (fn [db query-v]
    (:input-id db)))
; (re-frame/reg-sub
;   :max-id-and-msgs
;   ; :<- [:max-id]
;   ; :<- [:msgs])
;   ; :<- [:msgs :max-id])
;   (fn [db _]
;     (println ":max-id-and-msgs: db=")
;     [(:max-id db) (:msgs db)])
;   (fn [[max-id msgs] query-v]
;     (println ":max-id-and-msgs: max-id=" max-id ", msgs=" msgs)))

;; signal flows
(reg-sub
  :msgs-cnt-2
  ; (fn [db _])
  (fn [db query-v]
    (println "reg-sub:msgs-cnt: db=" db ",query-v=" query-v)
    ; (count (:msgs db))
    [(count (:msgs db)) (:msgs db)]))
    ; [(count (:msgs db)) (:msgs db) (:max-id db)]))

(reg-sub
 :scene-msgs-cnt
 :<- [:msgs-cnt-2]
 ; (fn [[msgs-cnt]])
 (fn [[msgs-cnt msgs]]
 ; (fn [[msgs-cnt msgs max-id]]
   ; (println ":scene-msgs-cnt: msgs-cnt=" msgs-cnt)
   (println ":scene-msgs-cnt: msgs-cnt=" msgs-cnt ", msgs=" msgs)
   ; (println ":scene-msgs-cnt: msgs-cnt=" msgs-cnt ", msgs=" msgs ", max-id=" max-id)
   [[2]]))

(reg-sub
 :add-scene-msg-cube
 ; :<- [:max-id]
 :<- [:max-id-and-msgs]
 ; :<- [:max-id-with-msgs]
 ; (fn [db query-v]
 ;   (println ":add-scene-msg-cube fn1: db=" db ", query-v" query-v)
 ;   (let [db rf-db/app-db
 ;         msgs (:msgs @db)]
 ;     (println "add-scene-msg-cube fn1a msgs=" msgs)
 ;     [(subscribe [:max-id] @db)]))
   ; [(subscribe [:max-id])]
   ; (subscribe [:max-id])
   ; (subscribe [:max-id-with-msgs]))
 ; (fn [db [_ query-v]]
 ; (fn [x]
 ;   (println ":add-scene-msg-cube: x=" x)
 ;   [1 2])
 ; (fn [db query-vec]
 ; (fn [[max-id] msgs]
 (fn [[max-id msgs]]
  (println ":add-scene-msg-cube: max-id=" max-id ", msgs=" msgs)
  ;; the max-id msg is the "hot-spot" msg we are interested in.
  (let [ msg (-> (filter #(= (%1 :id) max-id) msgs) (first))]
    (when (not (empty? msg))
      (re-frame/dispatch [:msg-cube.scene.add-msg-cube msg])))))

 ; (fn [max-id]
 ; ; (fn [max-id msgs]
 ;   ; (println ":add-scene-msg-cube: db=" db ", query-vec" query-vec)))
 ;   ; (println ":add-scene-msg-cube: max-id=" max-id ", msgs=" msgs)
 ;   (println ":add-scene-msg-cube: max-id=" max-id)
 ;   ; (let [db @db/default-db])
 ;   (let [db rf-db/app-db
 ;         ; msgs (:msgs @db)
 ;         msgs [1 2]]
 ;     (println ":add-scene-msg-cube fn2: max-id=" max-id ",db=" @db ", msgs=" msgs)
 ;     [[1]])))



; (reg-sub
;   :add-msg-cube
;   (fn [db query-v]
;     (println ":add-msg-cube: db=" db ", query-v=" query-v)
;     [(subscribe [:max-id])
;      (db :msgs)])
;  (fn [[max-id msgs] query-v]
;    (println ":add-msg-cube: max-id=" max-id ", msgs=" msgs)))

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
   (println ":gen-msg-cube: max-id=" max-id ", msgs=" msgs)
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
    (let [msg (msg/get-by-id id (:msgs db))]
      [msg])))

(reg-sub
 :msg-changed-by-id
 ; :<- [:msgs]
 ; (fn [query-v])
 (fn [[_ id]]
   (println "abc")
   (println "reg-sub:msg-changed-by-id id=" id)
   (subscribe [:msg-by-id id]))
 (fn [[msg] [_ id]]
   (println "reg-sub:msg-changed-by-id: *msg-changed-by-id: msg=" msg ", id=" id)
   (when msg
     (re-frame/dispatch [:msg-cube.update-msg-cube id (msg :level)]))
   ; ["result"]
  nil))

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
