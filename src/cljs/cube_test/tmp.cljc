(ns cube-test.tmp
   (:require
     [cube-test.db :as db]))
;    ; [re-frame.core :as re-frame]
;    [babylonjs :as bjs]))

;;hookup
(shadow.cljs.devtools.api/nrepl-select :app)
(clojure.browser.repl/connect "http://localhost:8778")

(defn f1 []
  (let [v1 (bjs/Vector3. 1 2 3)]
    (.-position v1)))


(comment
  (js/parseInt "5")
  ,)

(+ 1 1)

(js/parseInt "7")

(in-ns 'cube-test.tmp)

bd
(def a (+ 1 6))

(def bd {:msgs [
                {:id 1 :text "abc" :level :INFO}
                {:id 2 :text "def" :level :INFO}
                {:id 3 :text "ghi" :level :INFO}]})
a
*ns*
bd

(get bd 1)
(assoc (get bd 1) :id 7)
(map #(do
        (let [id (%1 :id)
              new-id (+ id 1)]
          (println "new-id=" new-id))) (bd :msgs))

;; the best
(map #(do
        (let [id (%1 :id)]
           (if (= id 2)
             (assoc %1 :level :WARN)
             %1)))
     (bd :msgs))

(if (= 1 2)
 (+ 1 1)
 (+ 1 2))

(vec '(1 2 3))

cube-test.db

(db/default-db)
db/default-db

(def ddb {:name "re-frame"})

(def g-db {:a 7 :b 8})
ddb
(assoc ddb :a 7)

(assoc ddb)

(get g-db 0)
(keys g-db)
(let [db ddb]
  (for [k (keys g-db)]))

(reduce #(do 
          (assoc %1)))

(second [:a 7])

(reduce #(do 
           (println "%1=" %1 ", %2=" %2)
           (assoc %1 (first %2) (second %2)))
         ddb g-db)