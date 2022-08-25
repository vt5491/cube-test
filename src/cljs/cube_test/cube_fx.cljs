(ns cube-test.cube-fx
  (:require-macros [cube-test.macros :as macros])
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [cube-test.projectile :as projectile]))

(declare create-projectile-old)
(declare create-projectile)
(declare create-projectile-js)
(declare create-projectiles)
(declare update-projectile)
(declare update-projectile-js)
(def spin-cube)
(def spin-cube-ang-vel (bjs/Vector3.Zero))
(def update-projectiles-timer-id nil)
(def create-projectiles-timer-id nil)
(def spin-cube-init-pos (bjs/Vector3. 0 2 0))
(def projectile-idle-vel (bjs/Vector3. 0.1 0 0))
(def projectile-vel (.clone projectile-idle-vel))
(def ^:dynamic *projectile-idx* (atom 0))
(def projectiles (atom {}))
(def projectiles-js (js-obj))
(def pause-projectiles false)

(defn init [db]
  (prn "cube-fx: main-scene=" (db :main-scene))
  (let [scene (db :main-scene)]
    (set! spin-cube (bjs/MeshBuilder.CreateBox. "cube" (js-obj "height" 1 "width" 1 "depth" 1) scene))
    (set! (.-position spin-cube) spin-cube-init-pos)
    (set! (.-material spin-cube) main-scene/green-mat)
    (set! (.-wireframe main-scene/green-mat) true))
  (prn "cube-fx.init: projectile=" (create-projectile-old 0 (bjs/Vector3. 2 1 0)))
  (let [pos (.-position spin-cube)
        rot (.-rotation spin-cube)
        vel (bjs/Vector3. 0.2 0 0)
        quat90 (.toQuaternion (.add rot (bjs/Vector3. 0 (/ js/Math.PI 2.0) 0)))
        vel90 (.rotateByQuaternionToRef vel quat90 (bjs/Vector3.))
        rot180 (.add rot (bjs/Vector3. 0 (/ js/Math.PI 1.0) 0))
        quat180 (.toQuaternion rot180)
        vel180 (.rotateByQuaternionToRef vel quat180 (bjs/Vector3.))]
    (create-projectile-js pos (.multiplyByFloats vel 3 3 3))
    (create-projectile-js pos vel90)
    (create-projectile-js pos vel180)))

(defn reset-spin-projectile-old [idx]
 (let [projectile (.getMeshByName main-scene/scene (str "projectile-" idx))
       phys-imp (.-physicsImpostor projectile)]
   (set! (.-position projectile) (bjs/Vector3. 2 1 0))
   (.setAngularVelocity phys-imp (bjs/Vector3.Zero.))
   (.setLinearVelocity phys-imp (bjs/Vector3.Zero.))))

(defn prj-idx-to-str [idx]
  (str "prj-" idx))

(defn prj-idx-to-key [idx]
  (keyword (prj-idx-to-str idx)))

(defn reset-projectile [idx]
  (let [key (prj-idx-to-key idx)
        prj-logical (get @projectiles key)
        prj-phys (.getMeshByName main-scene/scene (prj-idx-to-str idx))
        init-pos (:init-pos prj-logical)
        imposter (.-physicsImpostor prj-phys)]
    (set! (.-position prj-phys) init-pos)
    (reset! projectiles (assoc-in @projectiles [key :life] 25))
    (.setAngularVelocity imposter (bjs/Vector3.Zero.))
    (.setLinearVelocity imposter (bjs/Vector3.Zero.))))

(defn reset-projectiles []
  (doseq [k (keys @projectiles)
          idx (subs (name k) 4)]
    (reset-projectile idx)))

(defn create-projectile-js [pos vel]
  (let [idx (swap! *projectile-idx* inc)
        prj (projectile/create-js idx pos vel)
        key-str (str "prj-" idx)
        key (keyword key-str)]
    (aset projectiles-js key-str prj)))

(defn create-projectile [pos vel]
  (let [idx (swap! *projectile-idx* inc)
        prj (projectile/create idx pos vel)
        key (keyword (str "prj-" idx))]
    (reset! projectiles (assoc @projectiles key prj))))

(defn create-projectiles-js []
  (print "create-projectiles: entered")
  (let [pos (.-position spin-cube)
        rot (.-rotation spin-cube)
        vel (bjs/Vector3. 0.2 0 0)
        quat90 (.toQuaternion (.add rot (bjs/Vector3. 0 (/ js/Math.PI 2.0) 0)))
        vel90 (.rotateByQuaternionToRef vel quat90 (bjs/Vector3.))
        rot180 (.add rot (bjs/Vector3. 0 (/ js/Math.PI 1.0) 0))
        quat180 (.toQuaternion rot180)
        vel180 (.rotateByQuaternionToRef vel quat180 (bjs/Vector3.))]
    (create-projectile-js pos vel)
    (create-projectile-js pos vel90)
    (create-projectile-js pos vel180))
  (set! create-projectiles-timer-id nil))

(defn create-projectiles []
  (let [pos (.-position spin-cube)
        rot (.-rotation spin-cube)
        vel (bjs/Vector3. 0.2 0 0)
        quat90 (.toQuaternion (.add rot (bjs/Vector3. 0 (/ js/Math.PI 2.0) 0)))
        vel90 (.rotateByQuaternionToRef vel quat90 (bjs/Vector3.))
        rot180 (.add rot (bjs/Vector3. 0 (/ js/Math.PI 1.0) 0))
        quat180 (.toQuaternion rot180)
        vel180 (.rotateByQuaternionToRef vel quat180 (bjs/Vector3.))]
    (create-projectile pos vel)
    (create-projectile pos vel90)
    (create-projectile pos vel180))
  (set! create-projectiles-timer-id nil))

(defn create-projectile-old [idx pos]
  (let [projectile (bjs/MeshBuilder.CreateBox. (str "projectile-" idx) (js-obj "height" 0.1 "width" 0.1 "depth" 0.1) main-scene/scene)]
    (set! (.-position projectile) pos)
    (set! (.-material projectile) main-scene/red-mat)
    (set! (.-physicsImpostor projectile)
      (bjs/PhysicsImpostor. projectile bjs/PhysicsImpostor.BoxImposter
                           (js-obj "mass" 1.01 "restitution" 0.9) main-scene/scene))
    projectile))

(defn update-projectile-js [idx]
  (try
    (macros/when-let* [
                       key-str (str "prj-" idx)
                       key (keyword key-str)
                       prj-logical (aget projectiles-js key-str)
                       prj-phys (.getMeshByName main-scene/scene key-str)
                       old-pos (.-position prj-phys)
                       old-life (.-life prj-logical)]
      (set! (.-position prj-phys)(.add old-pos (.multiplyByFloats (.-vel prj-logical) 0.1 0.1 0.1)))
      (aset projectiles-js key-str "life" (dec old-life))
      (when (< old-life 0)
        (js-delete projectiles-js key-str)
        (.dispose prj-phys)))
    (catch js/Error e
      (do
        (prn "update-projectile-js: caught error " e)))))

(defn update-projectile [idx]
  (try
    (macros/when-let* [
                       key-str (str "prj-" idx)
                       key (keyword key-str)
                       prj-logical (get @projectiles key)
                       prj-phys (.getMeshByName main-scene/scene key-str)
                       old-pos (.-position prj-phys)
                       old-life (get-in prj-logical [:life])]
      (set! (.-position prj-phys)(.add old-pos (.multiplyByFloats (:vel prj-logical) 0.1 0.1 0.1)))
      (reset! projectiles (assoc-in @projectiles [key :life] (dec old-life)))
      (when (< old-life 0)
        (reset! projectiles (dissoc @projectiles key))
        (.dispose prj-phys)))
    (catch js/Error e
      (do
        (prn "update-projectile: caught error " e)))))

(defn update-projectiles-js []
  (try
    (doseq [k (js-keys projectiles-js)]
      (update-projectile-js (subs (name k) 4)))
    (catch js/Error e (prn "update-projectiles-js: caught error " e)))
  (set! update-projectiles-timer-id nil))

(defn update-projectiles []
  (try
    (doseq [k (keys @projectiles)]
      (update-projectile (subs (name k) 4)))
    (catch js/Error e (prn "update-projectiles: caught error " e)))
  ; (println "now resetting update-projectiles-timer-id")
  (set! update-projectiles-timer-id nil))

(defn update-spin-ang-vel [delta-vec]
  (.addInPlace spin-cube-ang-vel delta-vec)
  (re-frame/dispatch [:update-projectile-vel]))

(defn update-projectile-vel []
  (let [wx (.-x spin-cube-ang-vel)
        wy (.-y spin-cube-ang-vel)
        wz (.-z spin-cube-ang-vel)
        ;; yes, wy is x and wx is y
        abs-vel (bjs/Vector3. (Math/abs wy) (Math/abs wx) (Math/abs wz))]
    (set! projectile-vel (.add (.multiplyByFloats abs-vel 49 50 50) projectile-idle-vel))
    (prn "update-projectile-vel: new vel=" projectile-vel)))

(defn toggle-pause-projectiles []
  (js-debugger)
  (set! pause-projectiles (not pause-projectiles)))

(defn tick []
  (when (.-y spin-cube-ang-vel)
    (let [rot-y (-> spin-cube .-rotation .-y)
          rot-y-inc (* (.-y spin-cube-ang-vel) (.getDeltaTime main-scene/engine))]
      (set! (-> spin-cube .-rotation .-y) (+ rot-y rot-y-inc))))
  (when (not pause-projectiles)
    (update-projectiles)
    (if (not create-projectiles-timer-id)
      (let [cb (fn []
                 (create-projectiles))]
        (set! create-projectiles-timer-id (js/setTimeout cb 100 0))))))
