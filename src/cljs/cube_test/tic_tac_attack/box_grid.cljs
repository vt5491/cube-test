(ns cube-test.tic-tac-attack.box-grid
  ; (:require-macros [cube-test.macros :as macros])
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   ; [babylonjs-gui :as bjs-gui]
   ; [cube-test.controller :as controller]
   ; [cube-test.controller-xr :as controller-xr]
   ; [cube-test.utils.fps-panel :as fps-panel]
   [cube-test.base :as base]))

(def tmp)
(def tmp2)

(defn init-grid [db grid-key])
  ; (assoc db :board-status {:first-pick-index nil, :first-pick nil, :second-pick-index nil, :second-pick nil}))

(defn print-grid [grid]
  (println grid))
; (defn init-rubiks-grid [db])
; (defn init-rubiks-grid-old []
;   (let [rubiks-node (.getNodeByName main-scene/scene "rubiks-cube")
;         cubes (.getChildMeshes rubiks-node)]
;     ; (js-debugger)
;     ;(re-matches #"^([a-z]*)-(cube)-(\d)" s)
;     (println "init-rubiks-grid: size=" (count cubes))
;     ; (println "init-grid: cubes=" cubes)
;     (set! tmp (doall (reduce #(do
;                                 (let [name (.-name %2)
;                                       parse (re-matches #"^([a-z]*)_(cube)_(\d)" name)
;                                       color (get parse 1)
;                                       num (get parse 3)]
;                                     (println "init-rubiks-grid: name=" name ",color=" color ",num=" num)
;                                     (condp = color
;                                        "red" (assoc-in %1 [:red (keyword num)] %2)
;                                        "blue" (assoc-in %1 [:blue (keyword num)] %2)
;                                        "green" (assoc-in %1 [:green (keyword num)] %2))))
;                              {} cubes))))
;   tmp)
  ;   (println "tmp=" tmp))
  ; (set! tmp2 (assoc db :rubiks-grid tmp)))
  ; (js-debugger))
;;defunct
; (defn init-rubiks-grid []
;   (let [rubiks-node (.getNodeByName main-scene/scene "rubiks-cube")
;         cubes (.getChildMeshes rubiks-node)
;         ; (js-debugger)
;         ;(re-matches #"^([a-z]*)-(cube)-(\d)" s)
;         ; (println "init-rubiks-grid: size=" (count cubes))
;         ; (println "init-grid: cubes=" cubes)
;         grid  (doall (reduce #(do
;                                (let [name (.-name %2)
;                                      parse (re-matches #"^([a-z]*)_(cube)_(\d)" name)
;                                      color (get parse 1)
;                                      num (get parse 3)]
;                                    ; (println "init-rubiks-grid: name=" name ",color=" color ",num=" num)
;                                    (condp = color
;                                       "red" (assoc-in %1 [:front (keyword num)] %2)
;                                       "blue" (assoc-in %1 [:mid (keyword num)] %2)
;                                       "green" (assoc-in %1 [:rear (keyword num)] %2))))
;                             {} cubes))]
;     ; (println "init-rubiks-grid: grid=" grid)
;     grid))

;; red 1-9 -> 0-8
;; blue 1-9 -> 9-17
;; green 1-9 -> 18-26
(defn init-vrubik-grid []
  (let [rubiks-node (.getNodeByName main-scene/scene "vrubik-cube")
        cubes (.getChildMeshes rubiks-node)
        grid  (doall (reduce #(do
                               (let [ name (.-name %2)
                                     parse (re-matches #"^([a-z]*)_(cube)_(\d)" name)
                                     color (get parse 1)
                                     num-str (get parse 3)]
                                     ; num-int (js/parseInt num-str)]
                                     ; num-kw (-> num-str js/parseInt (- 1) (str) (keyword))]
                                   ; (println "init-rubiks-grid: name=" name ",color=" color ",num=" num)
                                   (condp = color
                                      "red" (assoc-in %1 [(-> num-str js/parseInt (- 1) (str) (keyword)) :mesh] %2)
                                      "blue" (assoc-in %1 [(-> num-str js/parseInt (+ 8) (str) (keyword)) :mesh] %2)
                                      "green" (assoc-in %1 [(-> num-str js/parseInt (+ 17) (str) (keyword)) :mesh] %2))))
                            {} cubes))]
    ; (println "init-vrubik-grid: grid=" grid)
    grid))
