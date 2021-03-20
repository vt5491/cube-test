;; Note: this file is explicitly a '.cljc' for now, as it should be "pure" functional and
;; not have any 'js/' dependencies.
(ns cube-test.msg-cube.data.msg
  (:require
   [re-frame.core :as re-frame]
   [clojure.spec.alpha :as s]))

;; Sample
{:id 1, :level :INFO, :text "hello"}

(defn gen [{:keys [id level text] :as msg}]
  {:id id, :level level, :text text})
