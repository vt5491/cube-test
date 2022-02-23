;; scene-l1 is referenced by many, reference to few.
(ns cube-test.frig-frog.train
  (:require
   [re-frame.core :as rf]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   ; [cube-test.frig-frog.scene-l1 :as ff.scene-l1]
   [cube-test.base :as base]))
   ; [cube-test.frig-frog.events :as ff-events]))

;; we need this so we don't get a compiler warning when referencing mesh metadata.
(set! *warn-on-infer* false)

; (if (nil? bar) baz bar)
(def train-cube-width 1.2)
(def velocity-factor 0.002)
(def animate-trains true)
(def debug-tmp nil)
; navigator.xr.isSessionSupported('immersive-vr').then( (x) => {console.log("x=", x)})

;;
;; utilites
;;
;; return "tr-1" for "tr-1-0", for instance.
(defn get-id-stem [id-str]
  (-> (re-find #"^(tr-\d{1,4})-(\d{1,4})" id-str) (second)))
;;

;;
;; app/db level
;;
(defn init [opts db]
  (let [db-1 (if (not (contains? db :trains)) (assoc db :trains []) db)
        train (-> (hash-map)
                  (assoc :id (if (nil? (:id opts)) 0 (:id opts)))
                  (assoc :vx (if (nil? (:vx opts)) 0 (:vx opts)))
                  (assoc :vy (if (nil? (:vy opts)) 0 (:vy opts)))
                  (assoc :length (if (nil? (:length opts)) 1 (:length opts)))
                  (assoc :init-col (if (nil? (:init-col opts)) 0 (:init-col opts)))
                  (assoc :init-row (if (nil? (:init-row opts)) 0 (:init-row opts))))
        db-2 (assoc db-1 :trains (conj (:trains db-1) train))]
    db-2))

;; drop train hash at index 'idx' in a trains vector
(defn drop-train-idx [trains idx]
  (into [] (keep-indexed #(when-not (= %1 idx) %2) trains)))

;; drop train by id in :trains vector
(defn drop-train-id [trains id]
  (into [] (keep #(when-not (= (:id %1) id) %1) trains)))

(defn get-train-by-id [trains id]
  ; (into [] (filter (fn [x] (= (:id x) id)) trains)))
  (prn "train.get-train-by-id: trains=" trains ",id=" id)
  (let [r (into [] (filter #(= (:id %1) id) trains))]
    (prn "r=" r)
    (first r)))
  ; (let [r (into [] (filter (fn [x] (= (:id x) id)) trains))]
  ;   r))

; (defn update-train-by-id [id trains updates])
(defn update-train-id [trains id updates]
  (let [train (get-train-by-id trains id)
        new-train (doall (map (fn [[k v]]
                                (prn "map: k=" k ",v=" v)
                                (if (get-in updates [k])
                                  [k (get-in updates [k])]
                                  [k v]))
                              train))
        new-train-2 (into {} new-train)]
    (prn "update-train-id: updates=" updates)
    (prn "update-train-id: train=" train)
    (prn "update-train-id: new-train=" new-train)
    (prn "update-train-id: new-train-2=" new-train-2)
    new-train-2))

;; methods that update the graphical trains at the BJS level
;;
;; mesh level
;;
(defn add-train-mesh-cube [id idx pos vx vy]
  (let [scene main-scene/scene
        ; id (:id train)
        cube (bjs/MeshBuilder.CreateBox. (str (name id) "-" idx) (js-obj "height" 1 "width" 1 "depth" 1) scene)]
    (set! (.-position cube) pos)
    ; (bjs/AddTagsTo cube (name id))
    (bjs/Tags.AddTagsTo cube (name id))
    (bjs/Tags.AddTagsTo cube "train")
    (set! (.-metadata cube) (js-obj "vx" vx, "vy" vy "animate" false))))
      ; (bjs/Vector3.
      ;      (* (:init-col train) train-cube-width)
      ;      train-cube-width
      ;      (* (:init-row train) train-cube-width)))))
      ; right-screen (bjs/MeshBuilder.CreateBox.
      ;               "right-screen"
      ;               (js-obj "height" 4 "width" 4 "depth" 0.1)
      ;               main-scene/scene))
    ; (set! (.-onMeshReadyObservable cube) #(prn "mesh " %1 " is now ready"))))

(defn add-train-mesh [train]
  (let [scene main-scene/scene
        id (:id train)
        length (:length train)
        init-col (:init-col train)
        init-row (:init-row train)
        vx (:vx train)
        vy (:vy train)]
      (prn "add-train-mesh: length=" length)
      (dotimes [i length]
        (prn "add-train-mesh: i=" i)
        (let [pos (bjs/Vector3. (+ init-col (* i train-cube-width))
                                train-cube-width
                                (* init-row train-cube-width))]
                                ; (+ init-row (* i train-cube-width)))]
          (add-train-mesh-cube id i pos vx vy)))))

(defn drop-train-mesh [train]
  (let [scene main-scene/scene
        id (name (:id train))
        ; mesh (.getMeshByID scene id)
        meshes (.getMeshesByTags scene id)]
      (prn "train.drop-train-mesh: train=" train ", id=" id)
      (prn "train.drop-train-mesh: meshes=" meshes ", count=" (count meshes))
      (doall (map #(.dispose %1) meshes))))
      ; (.dispose mesh)))

(defn move-train-mesh [train-mesh delta-time]
  (let [;vx (.-vx (.-metadata train-mesh))
        id (.-id train-mesh)
        vx (-> train-mesh (.-metadata) (.-vx))
        vy (-> train-mesh (.-metadata) (.-vy))
        current-pos (.-position train-mesh)
        ; delta-time 50.00
        delta-pos (bjs/Vector3. (* vx delta-time velocity-factor) 0 (* vy delta-time 0.001))
        new-pos (.add current-pos delta-pos)]
      ; (prn "train.move-train-mesh: vx=" vx ",vy=" vy ",id=" id ",new-pos=" new-pos)
      (set! (.-position train-mesh) new-pos)
      ; (when (or (> (.-x new-pos) 9.6) (< (.-x new-pos) 0)))
      (when (or (> (.-x new-pos) (* cube-test.frig-frog.board.n-cols cube-test.frig-frog.game.quanta-width))
                (< (.-x new-pos) 0))
        (let [id-stem-str (-> (re-find #"^(tr-\d{1,4})-(\d{1,4})" id) (second))
              id-stem-kw (keyword id-stem-str)]
          (prn "id=stem-str=" id-stem-str ", id-stem-kw=" id-stem-kw)
          (.dispose train-mesh)
          ; (set! (.-isVisible train-mesh) false)
          (let [scene main-scene/scene
                meshes-by-stem (.getMeshesByTags scene id-stem-str)]
              (prn "move-train-mesh: meshes-by-stem.count=" (count meshes-by-stem))
              (when (= (count meshes-by-stem) 0)
                (rf/dispatch [:cube-test.frig-frog.events/drop-train-id id-stem-kw])))))))
                ; (rf/dispatch [:cube-test.frig-frog.events/reset-train-mesh train-mesh])))))))

(defn reset-train-mesh
  "reset a dropped off train mesh to it's inital start pos, and restart the animation"
  [train-mesh db]
  (prn "train.reset-train-mesh: train-mesh=" train-mesh)
  (let [id-str (get-id-stem (.-id train-mesh))
        id-kw (keyword id-str)
        train (get-train-by-id (:trains db) id-kw)
        init-row (:init-row train)
        init-col (:init-col train)
        quanta-width (:quanta-width db)
        old-pos (.-position train-mesh)]
      (prn "reset-train-mesh: train=" train ",init-row=" init-row ",id-kw=" id-kw)
      (set! (.-position train-mesh) (bjs/Vector3. (* init-col quanta-width)
                                                  quanta-width
                                                  (* init-row quanta-width)))
      (prn "reset-train-mesh: old-pos=" old-pos ",new-pos=" (.-position train-mesh))
      (set! (.-isVisible train-mesh) true)))

;; general housekeeping
;;
(defn tick []
  (let [scene main-scene/scene
        engine main-scene/engine
        delta-time (.getDeltaTime engine)
        train-meshes (.getMeshesByTags scene "train")]
    (prn "train.tick: train-mesh count=" (count train-meshes))
    ; (when animate-trains)
    (when true
      (doall (map
               #(do
                   ; (prn "tick: true test=" (true? (-> %1 (.-metadata) (.-animate))) ",animate=" (-> %1 (.-metadata) (.-animate)))
                   ; (prn "tick: %1=" %1)
                   ; (js-debugger)
                   (when (-> %1 (.-metadata) (.-animate))
                     (move-train-mesh %1 delta-time)))
                ; (fn [tr]
                ;   (prn "train: tr=" tr)
                ;   (set! debug-tmp tr)
                ;   (js-debugger))
                train-meshes)))))
