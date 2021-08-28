(ns cube-test.beat-club.subs
  (:require
   [re-frame.core :as re-frame :refer [reg-sub subscribe]]
   [cube-test.beat-club.events :as events]
   [cube-test.beat-club.scene :as scene]
   [cube-test.utils :as utils]
   [clojure.data :as clj-data]))

(def ^:dynamic *last-models* (atom nil))

;; extractors
(reg-sub
 :get-song-loaded
 (fn [db _]
   (:song-loaded db)))

(reg-sub
 :get-twitch-load-status
 (fn [db _]
   (prn "subs:get-twitch-load-status: extractor driven")
   (:twitch-load-status db)))

(reg-sub
 :models
 (fn [db _]
   (prn "subs:models: extractor driven")
   (:models db)))

; (reg-sub
;  :models.is-enabled
;  (fn [db query-v]
;    (prn "subs:models.is-enabled: extractor driven, query-v=" query-v)
;    ; (:models db)
;    ; (-> db :models :ybot-rumba)
;    (-> db :models :ybot-head-bang)))
;    ; (-> db :models)))

(reg-sub
 :models.ybot-rumba
 (fn [db query-v]
   (prn "subs:models.ybot-rumba: extractor driven, query-v=" query-v)
   (-> db :models :ybot-rumba)))

(reg-sub
 :models.ybot-head-bang
 (fn [db query-v]
   (prn "subs:models.ybot-head-bang: extractor driven, query-v=" query-v)
   (-> db :models :ybot-head-bang)))

(reg-sub
 :models.ybot-head-bang.is-enabled
 (fn [db query-v]
   (prn "subs:models.ybot-head-bang.is-enabled: extractor driven, query-v=" query-v)
   (-> db :models :ybot-head-bang :is-enabled)))

;; computations
(reg-sub
 :song-loaded
 :<- [:get-song-loaded]
 (fn [db query-v]
  (prn "subs: song-loaded entered")))

(reg-sub
 :twitch-load-status
 :<- [:get-twitch-load-status]
 ; (fn [db query-v])
 (fn [load-status query-v]
   ; (prn "subs: twitch-load-status entered, db=" db)
   (prn "subs: twitch-load-status entered, load-status=" load-status)
   ; (when (= (:twitch-load-status db) 2))
   (when (= load-status 2)
     (re-frame/dispatch [:cube-test.beat-club.events/play-track])
     (re-frame/dispatch [:cube-test.beat-club.events/play-song-anim])
     ; (utils/toggle-enabled "ybot-rumba")
     ; (re-frame/dispatch [:cube-test.beat-club.events/start-animation :ybot-rumba 1.6])
     ; ; (utils/toggle-enabled "ybot-head-bang")
     ; (re-frame/dispatch [:cube-test.beat-club.events/start-animation :ybot-head-bang 1.222])
     (utils/toggle-enabled "ybot-combo")
     (re-frame/dispatch [:cube-test.beat-club.events/start-animation :ybot-combo 1.6]))))

(reg-sub
 :model-changed
 ; :<- [:models :ybot-rumba]
 :<- [:models]
 ; (fn [db query-v])
 (fn [models query-v]
   ; (prn "subs:  diff=" (clj-data/diff {:a 7 :b 8} {:a 7}))
   ; (prn "subs: *last-models* (non-ref)=" *last-models*)
   (prn "subs: *last-models*=" @*last-models*)
   (prn "subs: diff=" (first (clj-data/diff models @*last-models*)))
   (prn "subs: model-changed: models=" models ",query-v=" query-v)
   (let [diff (first (clj-data/diff models @*last-models*))]
     (when (and diff (-> diff empty? not))
       (let [ model-kw (first (keys diff))
              property-changed (-> diff model-kw keys first)
              new-val (-> models model-kw property-changed)]
         (prn "subs: changed-model-id=" model-kw
              ",property-changed=" property-changed
              ",new value=" (-> models model-kw property-changed))
         (when (= property-changed :is-enabled)
           (utils/set-enabled (name model-kw) new-val)))
       (swap! *last-models* (fn [x] models))))))

(reg-sub
 :model-changed-rumba
 :<- [:models.ybot-rumba]
 (fn [model query-v]
   (prn "subs: model-changed-rumba: model=" model ",query-v=" query-v)))

(reg-sub
 :model-changed-head-bang
 :<- [:models.ybot-head-bang]
 (fn [model query-v]
   (prn "subs: model-changed-head-bang: model=" model ",query-v=" query-v)))

(reg-sub
 :model-changed.head-bang.is-enabled
 :<- [:models.ybot-head-bang.is-enabled]
 (fn [new-val query-v]
   (prn "subs: model-changed.head-bang.is-enabled: new-val=" new-val ",query-v=" query-v)
   (when new-val
     (utils/set-enabled (name :ybot-head-bang) new-val))))
