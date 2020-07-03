(ns cube-test.face-slot.rotor
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]))

; (def rotor-anim-0-7)
; (def rotor-anim-7-6)
; (def rotor-anim-6-5)
; (def rotor-anim-5-4)
; (def rotor-anim-4-3)
; (def rotor-anim-3-2)
; (def rotor-anim-2-1)
; (def rotor-anim-1-0)

; (defn slot-rotor-loaded [new-meshes particle-systems skeletons hlq]
;   (prn "slot-rotor-loaded: new-meshes=" new-meshes)
;   (prn "count new-meshes=" (count new-meshes))
;   (println "slot-rotor-loaded: hlq=" hlq)
;   (set! rotor-anim-0-7 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.0-7")))
;   (set! rotor-anim-7-6 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.7-6")))
;   (set! rotor-anim-6-5 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.6-5")))
;   (set! rotor-anim-5-4 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.5-4")))
;   (set! rotor-anim-4-3 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.4-3")))
;   (set! rotor-anim-3-2 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.3-2")))
;   (set! rotor-anim-2-1 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.2-1")))
;   (set! rotor-anim-1-0 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.1-0")))
;   (.stop rotor-anim-0-7)
;   (.stop rotor-anim-7-6)
;   (.stop rotor-anim-6-5)
;   (.stop rotor-anim-5-4)
;   (.stop rotor-anim-4-3)
;   (.stop rotor-anim-3-2)
;   (.stop rotor-anim-2-1)
;   (.stop rotor-anim-1-0)
;   (let [rotor-anim-0-7 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.0-7"))]
;     (println "name anim-0-7=" (.-name rotor-anim-0-7))
;     (println "id anim-0-7=" (.-id rotor-anim-0-7))
;     (set! (.-name rotor-anim-0-7) (str hlq "-" (.-name rotor-anim-0-7)))))

(defn slot-rotor-loaded [new-meshes particle-systems skeletons hlq user-cb]
  ; (js-debugger)
  (doall (map #(do
                 (when (= (.-name %1) "rotor")
                   (set! (.-name %1) (str hlq "-" (.-name %1)))
                   (set! (.-id %1) (str hlq "-" (.-id %1)))))
              new-meshes))
  (let [rotor-anim-0-7 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.0-7"))
        rotor-anim-7-6 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.7-6"))
        rotor-anim-6-5 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.6-5"))
        rotor-anim-5-4 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.5-4"))
        rotor-anim-4-3 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.4-3"))
        rotor-anim-3-2 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.3-2"))
        rotor-anim-2-1 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.2-1"))
        rotor-anim-1-0 (-> main-scene/scene (.getAnimationGroupByName "rotorAction.1-0"))]
    (.stop rotor-anim-0-7)
    (.stop rotor-anim-7-6)
    (.stop rotor-anim-6-5)
    (.stop rotor-anim-5-4)
    (.stop rotor-anim-4-3)
    (.stop rotor-anim-3-2)
    (.stop rotor-anim-2-1)
    (.stop rotor-anim-1-0)
    (set! (.-name rotor-anim-0-7) (str hlq "-" (.-name rotor-anim-0-7)))
    (set! (.-name rotor-anim-7-6) (str hlq "-" (.-name rotor-anim-7-6)))
    (set! (.-name rotor-anim-6-5) (str hlq "-" (.-name rotor-anim-6-5)))
    (set! (.-name rotor-anim-5-4) (str hlq "-" (.-name rotor-anim-5-4)))
    (set! (.-name rotor-anim-4-3) (str hlq "-" (.-name rotor-anim-4-3)))
    (set! (.-name rotor-anim-3-2) (str hlq "-" (.-name rotor-anim-3-2)))
    (set! (.-name rotor-anim-2-1) (str hlq "-" (.-name rotor-anim-2-1)))
    (set! (.-name rotor-anim-1-0) (str hlq "-" (.-name rotor-anim-1-0))))
  (when user-cb (user-cb)))


(defn load-rotor [path file hlq user-cb]
  (println "rotor.load-rotor: path=" path ", file=" file)
  (.ImportMesh bjs/SceneLoader ""
               path
               file
               main-scene/scene
               #(slot-rotor-loaded %1 %2 %3 hlq user-cb)))

; (defn anim-bwd [start-face]
;   (condp = start-face
;     0 (.play rotor-anim-0-7)
;     7 (.play rotor-anim-7-6)
;     6 (.play rotor-anim-6-5)
;     5 (.play rotor-anim-5-4)
;     4 (.play rotor-anim-4-3)
;     3 (.play rotor-anim-3-2)
;     2 (.play rotor-anim-2-1)
;     1 (.play rotor-anim-1-0)))

(defn anim-bwd [hlq start-face]
  (condp = start-face
    0 (.play (-> main-scene/scene (.getAnimationGroupByName (str hlq "-" "rotorAction.0-7"))))
    7 (.play (-> main-scene/scene (.getAnimationGroupByName (str hlq "-" "rotorAction.7-6"))))
    6 (.play (-> main-scene/scene (.getAnimationGroupByName (str hlq "-" "rotorAction.6-5"))))
    5 (.play (-> main-scene/scene (.getAnimationGroupByName (str hlq "-" "rotorAction.5-4"))))
    4 (.play (-> main-scene/scene (.getAnimationGroupByName (str hlq "-" "rotorAction.4-3"))))
    3 (.play (-> main-scene/scene (.getAnimationGroupByName (str hlq "-" "rotorAction.3-2"))))
    2 (.play (-> main-scene/scene (.getAnimationGroupByName (str hlq "-" "rotorAction.2-1"))))
    1 (.play (-> main-scene/scene (.getAnimationGroupByName (str hlq "-" "rotorAction.0-1"))))))
