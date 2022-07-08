(ns cube-test.subs
  (:require
   [re-frame.core :as re-frame :refer [reg-sub subscribe]]
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

(re-frame/reg-sub
  :max-id-with-msgs
  (fn [db _]
    [(:max-id db) (:msgs db)]))

;; This is just a better-named version of 'max-id-with-msgs'
(re-frame/reg-sub
  :max-id-and-msgs
  (fn [db _]
    [(:max-id db) (:msgs db)]))

(reg-sub
  :msgs
  (fn [db query-v]
    (:msgs db)))


(reg-sub
  :msgs-2
  (fn [db query-v]
    (:msgs-2 db)))

(reg-sub
  :ints
  (fn [db query-v]
    (:ints db)))

(reg-sub
  :input-id
  (fn [db query-v]
    (:input-id db)))

;; signal flows
(reg-sub
  :msgs-cnt-2
  (fn [db query-v]
    [(count (:msgs db)) (:msgs db)]))

(reg-sub
 :scene-msgs-cnt
 :<- [:msgs-cnt-2]
 (fn [[msgs-cnt msgs]]
   [[2]]))

(reg-sub
 :add-scene-msg-cube
 :<- [:max-id-and-msgs]
 (fn [[max-id msgs]]
  ;; the max-id msg is the "hot-spot" msg we are interested in.
  (let [ msg (-> (filter #(= (%1 :id) max-id) msgs) (first))]
    (when (not (empty? msg))
      (re-frame/dispatch [:msg-cube.scene.add-msg-cube msg])))))

(reg-sub
 :gen-msg-cube
 (fn [query-v]
   [(subscribe [:max-id])
    (subscribe [:msgs])])
 (fn [[max-id msgs] query-v]
   (let [msg (-> (filter #(= (%1 :id) max-id) msgs) (first))]
         ; max-id-kw (keyword (str max-id))]
     (when (not (empty? msg))
       (re-frame/dispatch [:add-msg-cube msg])))))

(reg-sub
 :gen-msg-cube-2
 :<- [:max-id]
 :<- [:msgs-2]
 (fn [[max-id msgs-2] query-v]
   (let [ msg-2 (-> (filter #(= (%1 :id) max-id) msgs-2) (first))]
     (when (not (empty? msg-2))
       (re-frame/dispatch [:add-msg-cube-2 msg-2])))))

(re-frame/reg-sub
 :msgs-cnt
 :<- [:msgs]
 (fn [[msgs] query-v]
   (str "msgs-count: count msgs=" (count msgs))))

(re-frame/reg-sub
 :msgs-level
 :<- [:msgs]
 (fn [[msgs] query-v]
   (str "msgs-level: query-v=" query-v)))

;; Query
(reg-sub
  :msg-by-id
  (fn [db [_ id]]
    (let [msg (msg/get-by-id id (:msgs db))]
      [msg])))

(reg-sub
 :msg-changed-by-id
 (fn [[_ id]]
   (subscribe [:msg-by-id id]))
 (fn [[msg] [_ id]]
   (when msg
     (re-frame/dispatch [:msg-cube.update-msg-cube id (msg :level)]))
  nil))

(reg-sub
  :msg-by-id-2
  (fn [db [_ id]]
    (let [msg (msg/get-by-id id (db :msgs-2))]
      ;; Note: Definitely need to return your result in a vector.
      [msg])))

(reg-sub
 :msg-changed-by-id-2
 (fn [[_ id]]
   (subscribe [:msg-by-id-2 id]))
 (fn [[msg] [_ id]]
   (when msg
     (re-frame/dispatch [:msg-cube.update-msg-cube id (msg :level)]))
   ["result"]))
