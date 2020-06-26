(ns cube-test.projectile
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]))

(defn create [idx init-pos vel]
  (let [parms (js-obj "diameter" 0.1 "segments" 16)
        key-str (str "prj-" idx)
        projectile (bjs/MeshBuilder.CreateSphere key-str parms)]
    (set! (.-position projectile) init-pos)
    (set! (.-material projectile) main-scene/blue-mat)
    (set! (.-physicsImpostor projectile)
      (bjs/PhysicsImpostor. projectile bjs/PhysicsImpostor.SphereImposter
                           (js-obj "mass" 1.01 "restitution" 0.9) main-scene/scene)))
  {:name (str "prj-" idx)
   :init-pos (.clone init-pos)
   :vel (.clone vel)
   :life 250})

(defn dispose [idx]
  (let [key-str (str "prj-" idx)
        prj-phys (.getMeshByName main-scene/scene key-str)]
    (.dispose prj-phys)))

(defn create-js [idx init-pos vel]
  (let [parms (js-obj "diameter" 0.1 "segments" 16)
        key-str (str "prj-" idx)
        projectile (bjs/MeshBuilder.CreateSphere key-str parms)]
    (set! (.-position projectile) init-pos)
    (set! (.-material projectile) main-scene/blue-mat)
    (set! (.-physicsImpostor projectile)
      (bjs/PhysicsImpostor. projectile bjs/PhysicsImpostor.SphereImposter
                           (js-obj "mass" 1.01 "restitution" 0.9) main-scene/scene)))
  ; (js-obj "name" (str "prj-" idx)
  ;         "init-pos" (.clone init-pos)
  ;         "vel" (.clone vel)
  ;         "life" 250)
  #js {:name (str "prj-" idx)
        :init-pos (.clone init-pos)
        :vel (.clone vel)
        :life 250})
  ; {:name (str "prj-" idx)
  ;  :init-pos (.clone init-pos)
  ;  :vel (.clone vel)
  ;  :life 250})
