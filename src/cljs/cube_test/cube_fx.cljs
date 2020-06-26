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
; (def create-projectile-timer-id nil)
(def create-projectiles-timer-id nil)
(def spin-cube-init-pos (bjs/Vector3. 0 2 0))
(def projectile-idle-vel (bjs/Vector3. 0.1 0 0))
; (def projectile-vel (bjs/Vector3. 0.1 0 0))
(def projectile-vel (.clone projectile-idle-vel))
; (def projectile-lifespan 5)
; (def projectiles (atom []))
(def projectile-idx (atom 0))
(def projectiles (atom {}))
(def projectiles-js (js-obj))
(def pause-projectiles false)

(defn init [db]
  (prn "cube-fx: main-scene=" (db :main-scene))
  (let [scene (db :main-scene)]
    (set! spin-cube (bjs/MeshBuilder.CreateBox. "cube" (js-obj "height" 1 "width" 1 "depth" 1) scene))
    ; (set! (.-position spin-cube)(bjs/Vector3. 0 1 0))
    (set! (.-position spin-cube) spin-cube-init-pos)
    (set! (.-material spin-cube) main-scene/green-mat)
    (set! (.-wireframe main-scene/green-mat) true))
  (prn "cube-fx.init: projectile=" (create-projectile-old 0 (bjs/Vector3. 2 1 0)))
  ; (reset! projectiles (conj @projectiles (atom (projectile/create (.-position spin-cube) (bjs/Vector3. -0.1 0 0))))))
  ; (let [idx (reset! projectile-idx (+ @projectile-idx 1))
  ;       prj (projectile/create idx (.-position spin-cube) (bjs/Vector3. -0.1 0 0))
  ;       key (keyword (str "prj-" idx))]
  ;   (reset! projectiles (assoc @projectiles key prj))))
  (let [pos (.-position spin-cube)
        rot (.-rotation spin-cube)
        vel (bjs/Vector3. 0.2 0 0)
        ; vel (bjs/Vector3. 2.2 0 0)
        quat90 (.toQuaternion (.add rot (bjs/Vector3. 0 (/ js/Math.PI 2.0) 0)))
        vel90 (.rotateByQuaternionToRef vel quat90 (bjs/Vector3.))
        rot180 (.add rot (bjs/Vector3. 0 (/ js/Math.PI 1.0) 0))
        quat180 (.toQuaternion rot180)
        vel180 (.rotateByQuaternionToRef vel quat180 (bjs/Vector3.))]
    ; (create-projectile pos (bjs/Vector3. -0.1 0 0))
    ; (create-projectile pos (bjs/Vector3. -0.2 0 0))
    ; (create-projectile pos (.multiplyByFloats vel 3 3 3))
    ; (create-projectile pos vel90)
    ; (create-projectile pos vel180)
    (create-projectile-js pos (.multiplyByFloats vel 3 3 3))
    (create-projectile-js pos vel90)
    (create-projectile-js pos vel180)))

  ; (set! (.-y spin-cube-ang-vel) 0.1))

(defn reset-spin-projectile-old [idx]
  ; (js-debugger)
  ; (prn "reset-spin-projectile: idx=" idx)
  (let [projectile (.getMeshByName main-scene/scene (str "projectile-" idx))
        phys-imp (.-physicsImpostor projectile)]
    (set! (.-position projectile) (bjs/Vector3. 2 1 0))
    ; (js-debugger)
     ;; give it a new imposter
     ; (set! (.-physicsImpostor projectile
     ; (bjs/PhysicsImpostor. projectile bjs/PhysicsImpostor.BoxImposter
     ;                       (js-obj "mass" 1.01 "restitution" 0.9) main-scene/scene))))
    ; (-> projectile .-physicsImpostor .setAngularVelocity (bjs/Vector3. 0 0 0))
    ; (-> projectile .-physicsImpostor .setLinearVelocity (bjs/Vector3. 0 0 0))
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
  (let [idx (swap! projectile-idx inc)
        prj (projectile/create-js idx pos vel)
        key-str (str "prj-" idx)
        key (keyword key-str)]
    (print "create-projectile: creating " key)
    (aset projectiles-js key-str prj)
    (println "projectile-count=" (count (js-keys projectiles-js)))))
    ; (reset! projectiles (assoc @projectiles key prj))))

(defn create-projectile [pos vel]
  ; (let [idx (reset! projectile-idx (+ @projectile-idx 1))])
  (let [idx (swap! projectile-idx inc)
        prj (projectile/create idx pos vel)
        key (keyword (str "prj-" idx))]
    (print "create-projectile: creating " key)
    (reset! projectiles (assoc @projectiles key prj)))
  ;; reset the timer pop, so we can run again
  ; (set! create-projectile-timer-id nil)
  (println "projectile count=" (count @projectiles)))

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
  (print "create-projectiles: entered")
  (let [pos (.-position spin-cube)
        rot (.-rotation spin-cube)
        vel (bjs/Vector3. 0.2 0 0)
        quat90 (.toQuaternion (.add rot (bjs/Vector3. 0 (/ js/Math.PI 2.0) 0)))
        vel90 (.rotateByQuaternionToRef vel quat90 (bjs/Vector3.))
        rot180 (.add rot (bjs/Vector3. 0 (/ js/Math.PI 1.0) 0))
        quat180 (.toQuaternion rot180)
        vel180 (.rotateByQuaternionToRef vel quat180 (bjs/Vector3.))]
    ; (create-projectile pos (bjs/Vector3. -0.1 0 0))
    ; (create-projectile pos (bjs/Vector3. -0.2 0 0))
    (create-projectile pos vel)
    (create-projectile pos vel90)
    (create-projectile pos vel180))
  (set! create-projectiles-timer-id nil))

; sphere.physicsImpostor = new BABYLON.PhysicsImpostor(sphere, BABYLON.PhysicsImpostor.SphereImpostor, { mass: 1, restitution: 0.9 }, scene)));
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
                       ; prj-logical (get @projectiles key)
                       prj-logical (aget projectiles-js key-str)
                       prj-phys (.getMeshByName main-scene/scene key-str)
                       old-pos (.-position prj-phys)
                       ; old-life (get-in prj-logical [:life])
                       old-life (.-life prj-logical)]
      ; (set! (.-position prj-phys)(.add old-pos (.multiplyByFloats (:vel prj-logical) 0.1 0.1 0.1)))
      (set! (.-position prj-phys)(.add old-pos (.multiplyByFloats (.-vel prj-logical) 0.1 0.1 0.1)))
      ; (reset! projectiles (assoc-in @projectiles [key :life] (dec old-life)))
      (aset projectiles-js key-str "life" (dec old-life))
      (when (< old-life 0)
        (println "update-projectils-js: now deleting key=" key)
        ; (reset! projectiles (dissoc @projectiles key))
        (js-delete projectiles-js key-str)
        (.dispose prj-phys)))
    (catch js/Error e
      (do
        (prn "update-projectile-js: caught error " e)))))

(defn update-projectile [idx]
  ; (js-debugger)
  ; (prn "update-projectile: idx=" idx ", projectiles=" @projectiles)
  ; (let [projectile (.getMeshByName main-scene/scene (str "projectile-" idx))])
  ; (let [projectile (.getMeshByName main-scene/scene (str "projectile-" 0))
  ;       old-pos (.-position projectile)]
  ;   ; (set! (.-position projectile) (bjs/Vector3. (-> old-pos .-x (+ 0.1)) (.-y old-pos) (.-z old-pos)))
  ;   (set! (.-position projectile) (.add old-pos projectile-vel)))
  ;; update the "new" projectile
  ; (let [proj-logical @(nth @projectiles 0)
  ;       proj-phys (.getMeshByName main-scene/scene "prj-2")
  ;       old-pos (:pos proj-logical)]
  ;   (set! (.-position proj-phys)(.add old-pos (:vel proj-logical)))
  ;   ; (reset! (nth @b 0) (assoc @(nth @b 0) :a 17)))
  ;   ;; update the logical pos to agree with physical pos.
  ;   ;;TODO: don't update logical pos to agree with physical.
  ;   ;; but we do need to keep it an atom becuase we will need to update
  ;   ;; the vel
  ;   (reset! (nth @projectiles 0)(assoc @(nth projectiles 0) :pos (.-position proj-phys))))
  ; ;; and reset the timer id, so it can be set again.
        ; (macros/when-let* [key-str (str "prj-" idx)
  ; (macros/when-let* [])
  (try
    (macros/when-let* [
                       key-str (str "prj-" idx)
                       key (keyword key-str)
                       prj-logical (get @projectiles key)
                       prj-phys (.getMeshByName main-scene/scene key-str)
                       old-pos (.-position prj-phys)
                       old-life (get-in prj-logical [:life])]
      ; (println "vel prj-logical=" (:vel prj-logical))
      ; (println "getDeltaTime=" (.getDeltaTime main-scene/engine))
      ; (set! (.-position prj-phys)(.add old-pos (* (:vel prj-logical) (/ (.getDeltaTime main-scene/engine) 1000))))
      ; (set! (.-position prj-phys)(.add old-pos (.multiply  (:vel prj-logical) bjs/Vector3.One)))
      (set! (.-position prj-phys)(.add old-pos (.multiplyByFloats (:vel prj-logical) 0.1 0.1 0.1)))
      ; (set! (.-position prj-phys)(.add old-pos (:vel prj-logical)))
      ; (reset! (get projectiles key))
      (reset! projectiles (assoc-in @projectiles [key :life] (dec old-life)))
      ; (println "update: prl-logicial.life for key " key-str ",old-life=" old-life ", new-life=" (get-in @projectiles [key :life]))
      (when (< old-life 0)
        (println "now deleting key=" key)
        ; (assoc projectiles (dissoc @projectiles key))
        ; (js-debugger)
        (reset! projectiles (dissoc @projectiles key))
        ; (println "projectiles=" @projectiles)
        (.dispose prj-phys)))
    (catch js/Error e
      (do
        (prn "update-projectile: caught error " e)))))
        ; (js-debugger)))))
  ; (set! update-projectile-timer-id nil))

(defn update-projectiles-js []
  (try
    (doseq [k (js-keys projectiles-js)]
      (update-projectile-js (subs (name k) 4)))
    (catch js/Error e (prn "update-projectiles-js: caught error " e)))
  ; (println "now resetting update-projectiles-timer-id")
  (set! update-projectiles-timer-id nil))

(defn update-projectiles []
  (try
    ; (println "keys projectiles=" (keys @projectiles))
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
          ; rot-y-inc (* (.-y spin-cube-ang-vel)(-> (.getDeltaTime main-scene/engine) (/ 1000)))]
      (set! (-> spin-cube .-rotation .-y) (+ rot-y rot-y-inc))))
  ; (prn "cube-fx: tick: spin-cube.rotation=" (.-rotation spin-cube))
  ; (js/setTimeout #(swap! temp-atom inc) 1000)
  ; (let [projectile])
  ; (js/setTimeout #(swap! temp-atom inc) 1000)
  ; (println "tick: pause-projectiles=" pause-projectiles)
  (when (not pause-projectiles)
    ; (update-projectiles-js)
    (update-projectiles)
    ; (if (not update-projectiles-timer-id)
    ;   ; (set! update-projectile-timer-id (js/setTimeout update-projectile 1000 0))
    ;   (let [cb (fn []
    ;              (prn "hi from cb")
    ;              ; (update-projectile 1)
    ;              (update-projectiles))]
    ;     (set! update-projectiles-timer-id (js/setTimeout cb 1000 0))))
    (if (not create-projectiles-timer-id)
      (let [cb (fn []
                 ; (println "hi from create-projectiles cb")
                 ; (update-projectile 1)
                 (create-projectiles))]
                 ; (create-projectiles-js))]
        ; (prn "now setting create-projectile-timer-id")
        (set! create-projectiles-timer-id (js/setTimeout cb 2000 0))))))
        ; (prn "create-projectile-timer-id=" create-projectile-timer-id)))))
