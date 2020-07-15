(ns cube-test.face-slot.rotor
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [cube-test.scenes.face-slot-scene :as face-slot-scene]))

(def rot-action-stem "rot_n1")

; "rot_n1.2-3.eyes"
; (re-pattern (str "^(" stm ")(\\d-\\d)\\.(.*)"))
(defn slot-rotor-loaded [new-meshes particle-systems skeletons anim-groups hlq user-cb]
  ; (js-debugger)
  ; (println "anim-groups=" anim-groups)
  (let [hlq-str (name hlq)]
    (doall (map #(do
                   ; (when (= (.-name %1) "rotor"))
                   (when (re-matches #".*rotor.*" (.-name %1))
                     (set! (.-name %1) (str hlq-str "-" (.-name %1)))
                     (set! (.-id %1) (str hlq-str "-" (.-id %1)))
                     (condp = hlq
                       :top (set! face-slot-scene/top-rotor-uniq-id (.-uniqueId %1))
                       :mid (set! face-slot-scene/mid-rotor-uniq-id (.-uniqueId %1))
                       :bottom (set! face-slot-scene/bottom-rotor-uniq-id (.-uniqueId %1)))))
                new-meshes))
    (doall (map #(do
                   ; (prn "animation-name=" %1)
                   (let [ag-name (.-name %1)]
                     (when (re-matches #".*rotorAction.*" ag-name)
                       ;; normalize "rotorAction.5-4.007" to "rotorAction.5-4", for instance.
                       (set! (.-name %1) (clojure.string/replace ag-name #"\.\d\d\d$" "")))
                     (when (re-matches (re-pattern (str ".*" rot-action-stem ".*")) ag-name)
                     ;; normalize the action name.
                     ;; e.g. convert "rot_n1.2-3.eyes" to "rot_n1.2-3".
                       (let [re (re-pattern (str "^(" rot-action-stem ")(\\.\\d-\\d)\\.(.*)"))
                             match-array (re-matches re ag-name)
                             new-name (str (get match-array 1) (get match-array 2))]
                         ; (println "new-name=" new-name)
                         (set! (.-name %1) new-name)))))
                       ; (-> (.-onAnimationEndObservable %1) (.add (fn [] (println "animation " (.-name %1) " ended"))))
                       ; (-> (.-onAnimationGroupEndObservable %1) (.add (fn [] (println "animation group " (.-name %1) " ended"))))
                       ; (-> (.-onAnimationGroupLoopObservable %1)
                       ;     (.add
                       ;      (fn []
                       ;        (println "animation group loop" (.-name %1) " ended")
                       ;        (.stop %1)))))))
                ; (.-animationGroups main-scene/scene)
                anim-groups))
    ; (let [rotor-anim-0-7 (-> main-scene/scene (.getAnimationGroupByName (str rot-action-stem ".0-7")))
    ;       rotor-anim-7-6 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.7-6"))
    ;       rotor-anim-6-5 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.6-5"))
    ;       rotor-anim-5-4 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.5-4"))
    ;       rotor-anim-4-3 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.4-3"))
    ;       rotor-anim-3-2 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.3-2"))
    ;       rotor-anim-2-1 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.2-1"))
    ;       rotor-anim-1-0 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.1-0"))
    ;       ;; fwd anims
    ;       rotor-anim-7-0 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.7-0"))
    ;       rotor-anim-0-1 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.0-1"))
    ;       rotor-anim-1-2 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.1-2"))
    ;       rotor-anim-2-3 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.2-3"))
    ;       rotor-anim-3-4 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.3-4"))
    ;       rotor-anim-4-5 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.4-5"))
    ;       rotor-anim-5-6 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.5-6"))
    ;       rotor-anim-6-7 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.6-7"))])
    (let [rotor-anim-0-7 (-> main-scene/scene (.getAnimationGroupByName (str rot-action-stem ".0-7")))
          rotor-anim-7-6 (-> main-scene/scene (.getAnimationGroupByName (str rot-action-stem ".7-6")))
          rotor-anim-6-5 (-> main-scene/scene (.getAnimationGroupByName (str rot-action-stem ".6-5")))
          rotor-anim-5-4 (-> main-scene/scene (.getAnimationGroupByName (str rot-action-stem ".5-4")))
          rotor-anim-4-3 (-> main-scene/scene (.getAnimationGroupByName (str rot-action-stem ".4-3")))
          rotor-anim-3-2 (-> main-scene/scene (.getAnimationGroupByName (str rot-action-stem ".3-2")))
          rotor-anim-2-1 (-> main-scene/scene (.getAnimationGroupByName (str rot-action-stem ".2-1")))
          rotor-anim-1-0 (-> main-scene/scene (.getAnimationGroupByName (str rot-action-stem ".1-0")))
          ;; fwd anims
          rotor-anim-7-0 (-> main-scene/scene (.getAnimationGroupByName (str rot-action-stem ".7-0")))
          rotor-anim-0-1 (-> main-scene/scene (.getAnimationGroupByName (str rot-action-stem ".0-1")))
          rotor-anim-1-2 (-> main-scene/scene (.getAnimationGroupByName (str rot-action-stem ".1-2")))
          rotor-anim-2-3 (-> main-scene/scene (.getAnimationGroupByName (str rot-action-stem ".2-3")))
          rotor-anim-3-4 (-> main-scene/scene (.getAnimationGroupByName (str rot-action-stem ".3-4")))
          rotor-anim-4-5 (-> main-scene/scene (.getAnimationGroupByName (str rot-action-stem ".4-5")))
          rotor-anim-5-6 (-> main-scene/scene (.getAnimationGroupByName (str rot-action-stem ".5-6")))
          rotor-anim-6-7 (-> main-scene/scene (.getAnimationGroupByName (str rot-action-stem ".6-7")))]
      (.stop rotor-anim-0-7)
      (.stop rotor-anim-7-6)
      (.stop rotor-anim-6-5)
      (.stop rotor-anim-5-4)
      (.stop rotor-anim-4-3)
      (.stop rotor-anim-3-2)
      (.stop rotor-anim-2-1)
      (.stop rotor-anim-1-0)
      ;; fwd anims
      (.stop rotor-anim-7-0)
      (.stop rotor-anim-0-1)
      (.stop rotor-anim-1-2)
      (.stop rotor-anim-2-3)
      (.stop rotor-anim-3-4)
      (.stop rotor-anim-4-5)
      (.stop rotor-anim-5-6)
      (.stop rotor-anim-6-7)
      ;; bwd animations
      (set! (.-name rotor-anim-0-7) (str hlq-str "-" (.-name rotor-anim-0-7)))
      (set! (.-name rotor-anim-7-6) (str hlq-str "-" (.-name rotor-anim-7-6)))
      (set! (.-name rotor-anim-6-5) (str hlq-str "-" (.-name rotor-anim-6-5)))
      (set! (.-name rotor-anim-5-4) (str hlq-str "-" (.-name rotor-anim-5-4)))
      (set! (.-name rotor-anim-4-3) (str hlq-str "-" (.-name rotor-anim-4-3)))
      (set! (.-name rotor-anim-3-2) (str hlq-str "-" (.-name rotor-anim-3-2)))
      (set! (.-name rotor-anim-2-1) (str hlq-str "-" (.-name rotor-anim-2-1)))
      (set! (.-name rotor-anim-1-0) (str hlq-str "-" (.-name rotor-anim-1-0)))
      ;; fwd animations
      (set! (.-name rotor-anim-7-0) (str hlq-str "-" (.-name rotor-anim-7-0)))
      (set! (.-name rotor-anim-0-1) (str hlq-str "-" (.-name rotor-anim-0-1)))
      (set! (.-name rotor-anim-1-2) (str hlq-str "-" (.-name rotor-anim-1-2)))
      (set! (.-name rotor-anim-2-3) (str hlq-str "-" (.-name rotor-anim-2-3)))
      (set! (.-name rotor-anim-3-4) (str hlq-str "-" (.-name rotor-anim-3-4)))
      (set! (.-name rotor-anim-4-5) (str hlq-str "-" (.-name rotor-anim-4-5)))
      (set! (.-name rotor-anim-5-6) (str hlq-str "-" (.-name rotor-anim-5-6)))
      (set! (.-name rotor-anim-6-7) (str hlq-str "-" (.-name rotor-anim-6-7)))))
  (when user-cb (user-cb)))


(defn load-rotor [path file hlq user-cb]
  (println "rotor.load-rotor: path=" path ", file=" file)
  (.ImportMesh bjs/SceneLoader ""
               path
               file
               main-scene/scene
               #(slot-rotor-loaded %1 %2 %3 %4 hlq user-cb)))


(defn anim-bwd [hlq start-face]
  ; (println "anim-bwd: start-face=" start-face)
  (let [hlq-str (name hlq)]
    (condp = start-face
      ; 0 (.play (-> main-scene/scene (.getAnimationGroupByName (str hlq-str "-" "rotorAction.0-7"))))
      ; 7 (.play (-> main-scene/scene (.getAnimationGroupByName (str hlq-str "-" "rotorAction.7-6"))))
      ; 6 (.play (-> main-scene/scene (.getAnimationGroupByName (str hlq-str "-" "rotorAction.6-5"))))
      ; 5 (.play (-> main-scene/scene (.getAnimationGroupByName (str hlq-str "-" "rotorAction.5-4"))))
      ; 4 (.play (-> main-scene/scene (.getAnimationGroupByName (str hlq-str "-" "rotorAction.4-3"))))
      ; 3 (.play (-> main-scene/scene (.getAnimationGroupByName (str hlq-str "-" "rotorAction.3-2"))))
      ; 2 (.play (-> main-scene/scene (.getAnimationGroupByName (str hlq-str "-" "rotorAction.2-1"))))
      ; 1 (.play (-> main-scene/scene (.getAnimationGroupByName (str hlq-str "-" "rotorAction.1-0"))))
      0 (.play (-> main-scene/scene (.getAnimationGroupByName (str hlq-str "-" rot-action-stem ".0-7"))))
      7 (.play (-> main-scene/scene (.getAnimationGroupByName (str hlq-str "-" rot-action-stem ".7-6"))))
      6 (.play (-> main-scene/scene (.getAnimationGroupByName (str hlq-str "-" rot-action-stem ".6-5"))))
      5 (.play (-> main-scene/scene (.getAnimationGroupByName (str hlq-str "-" rot-action-stem ".5-4"))))
      4 (.play (-> main-scene/scene (.getAnimationGroupByName (str hlq-str "-" rot-action-stem ".4-3"))))
      3 (.play (-> main-scene/scene (.getAnimationGroupByName (str hlq-str "-" rot-action-stem ".3-2"))))
      2 (.play (-> main-scene/scene (.getAnimationGroupByName (str hlq-str "-" rot-action-stem ".2-1"))))
      1 (.play (-> main-scene/scene (.getAnimationGroupByName (str hlq-str "-" rot-action-stem ".1-0")))))))

(defn anim-fwd [hlq start-face]
  (let [hlq-str (name hlq)]
    ; (println "anim-fwd: start-face=" start-face)
    (condp = start-face
      0 (.start (-> main-scene/scene (.getAnimationGroupByName (str hlq-str "-" rot-action-stem ".0-1"))))
      1 (.start (-> main-scene/scene (.getAnimationGroupByName (str hlq-str "-" rot-action-stem ".1-2"))))
      2 (.start (-> main-scene/scene (.getAnimationGroupByName (str hlq-str "-" rot-action-stem ".2-3"))))
      3 (.start (-> main-scene/scene (.getAnimationGroupByName (str hlq-str "-" rot-action-stem ".3-4"))))
      4 (.start (-> main-scene/scene (.getAnimationGroupByName (str hlq-str "-" rot-action-stem ".4-5"))))
      5 (.start (-> main-scene/scene (.getAnimationGroupByName (str hlq-str "-" rot-action-stem ".5-6"))))
      6 (.start (-> main-scene/scene (.getAnimationGroupByName (str hlq-str "-" rot-action-stem ".6-7"))))
      7 (.start (-> main-scene/scene (.getAnimationGroupByName (str hlq-str "-" rot-action-stem ".7-0")))))))
