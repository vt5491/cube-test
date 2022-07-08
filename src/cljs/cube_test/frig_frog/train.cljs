;; scene-l1 is referenced by many, reference to few.
(ns cube-test.frig-frog.train
  (:require
   [re-frame.core :as rf]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [cube-test.base :as base]
   [cube-test.utils :as utils]))

;; we need this so we don't get a compiler warning when referencing mesh metadata.
(set! *warn-on-infer* false)

; (if (nil? bar) baz bar)
(def train-cube-width 1.2)
(def velocity-factor 0.002)
(def animate-trains true)
(def debug-tmp nil)

;;
;; utilites
;;
(defn get-id-stem [id-str]
  (-> (re-find #"^(tr-\d{1,4})-(\d{1,4})" id-str) (second)))
;;
(defn toggle-animation []
  (set! animate-trains (not animate-trains)))

;;
;; app/db level
;;
(defn init [opts db]
  (let [;db @db
        db-1 (if (not (contains? db :trains)) (assoc db :trains []) db)
        train (-> (hash-map)
                  (assoc :id-stem (if (nil? (:id-stem opts)) 0 (:id-stem opts)))
                  (assoc :vx (if (nil? (:vx opts)) 0 (:vx opts)))
                  (assoc :vy (if (nil? (:vy opts)) 0 (:vy opts)))
                  (assoc :length (if (nil? (:length opts)) 1 (:length opts)))
                  (assoc :init-col (if (nil? (:init-col opts)) 0 (:init-col opts)))
                  (assoc :init-row (if (nil? (:init-row opts)) 0 (:init-row opts))))
        db-2 (assoc db-1 :trains (conj (:trains db-1) train))]
      db-2))

(defn init-2 [opts db]
  (let [r (init opts @db)]
    (swap! db (fn [x] r))))

;; drop train hash at index 'idx' in a trains vector
(defn drop-train-idx [trains idx]
  (into [] (keep-indexed #(when-not (= %1 idx) %2) trains)))

;; drop train by id in :trains vector
(defn drop-train-id-stem [trains id-stem]
  (into [] (keep #(when-not (= (:id-stem %1) id-stem) %1) trains)))

(defn get-train-by-id-stem [trains id-stem]
  (let [r (into [] (filter #(= (:id-stem %1) id-stem) trains))]
    (first r)))

; (defn update-train-by-id [id trains updates])
(defn update-train-id-stem [trains id-stem updates]
  (let [train (get-train-by-id-stem trains id-stem)
        new-train (doall (map (fn [[k v]]
                                (if (get-in updates [k])
                                  [k (get-in updates [k])]
                                  [k v]))
                              train))
        new-train-2 (into {} new-train)]
    new-train-2))

;; methods that update the graphical trains at the BJS level
;;
;; mesh level
;;
(defn add-train-mesh-cube [id-stem idx pos vx vy]
  (let [scene main-scene/scene
        cube (bjs/MeshBuilder.CreateBox. (str (name id-stem) "-" idx) (js-obj "height" 1 "width" 1 "depth" 1) scene)]
    (set! (.-position cube) pos)
    (bjs/Tags.AddTagsTo cube (name id-stem))
    (bjs/Tags.AddTagsTo cube "train")
    (set! (.-metadata cube) (js-obj "vx" vx, "vy" vy "animate" false))
    (set! (.-isVisible cube) false)))

(defn add-train-mesh [train]
  (let [scene main-scene/scene
        id-stem (:id-stem train)
        length (:length train)
        init-col (:init-col train)
        init-row (:init-row train)
        vx (:vx train)
        vy (:vy train)]
      (dotimes [i length]
        (let [pos (bjs/Vector3. (+ init-col (* i train-cube-width))
                                train-cube-width
                                (* init-row train-cube-width))]
          (add-train-mesh-cube id-stem i pos vx vy)))
      (let [
            train-meshes (.getMeshesByTags scene (name id-stem))]
        (utils/sleep
         (fn [x] (doall (map #(do
                                ; (prn "map %1=" %1)
                                (set! (-> %1 (.-metadata) (.-animate)) true)
                                (set! (.-isVisible %1) true))
                          train-meshes)))
         50))))

(defn drop-train-mesh [train]
  (let [scene main-scene/scene
        id (name (:id-stem train))
        meshes (.getMeshesByTags scene id)]
      (doall (map #(.dispose %1) meshes))))

(defn move-train-mesh [train-mesh delta-time]
  (let [;vx (.-vx (.-metadata train-mesh))
        id (.-id train-mesh)
        vx (-> train-mesh (.-metadata) (.-vx))
        vy (-> train-mesh (.-metadata) (.-vy))
        current-pos (.-position train-mesh)
        delta-time-capped (if (> delta-time 20)
                            20
                            delta-time)
        delta-pos (bjs/Vector3. (* vx delta-time-capped velocity-factor) 0 (* vy delta-time-capped 0.001))
        new-pos (.add current-pos delta-pos)]
      (when (> delta-time 100)(prn "train.move-train-mesh: delta-time=" delta-time ", delta-time-capped=" delta-time-capped))
      (set! (.-position train-mesh) new-pos)
      (when (or (> (.-x new-pos) (* cube-test.frig-frog.board.n-cols cube-test.frig-frog.game.quanta-width))
                (< (.-x new-pos) 0))
        (let [id-stem-str (-> (re-find #"^(tr-\d{1,4})-(\d{1,4})" id) (second))
              id-stem-kw (keyword id-stem-str)]
          (.dispose train-mesh)
          (let [scene main-scene/scene
                meshes-by-stem (.getMeshesByTags scene id-stem-str)]
              (when (= (count meshes-by-stem) 0)
                (rf/dispatch [:cube-test.frig-frog.events/drop-train-id-stem id-stem-kw])))))))

(defn reset-train-mesh
  "reset a dropped off train mesh to it's inital start pos, and restart the animation"
  [train-mesh db]
  (let [id-str (get-id-stem (.-id train-mesh))
        id-kw (keyword id-str)
        train (get-train-by-id-stem (:trains db) id-kw)
        init-row (:init-row train)
        init-col (:init-col train)
        quanta-width (:quanta-width db)
        old-pos (.-position train-mesh)]
      (set! (.-position train-mesh) (bjs/Vector3. (* init-col quanta-width)
                                                  quanta-width
                                                  (* init-row quanta-width)))
      (set! (.-isVisible train-mesh) true)))

;; general housekeeping
;;
(defn tick []
  (let [scene main-scene/scene
        engine main-scene/engine
        delta-time (.getDeltaTime engine)
        train-meshes (.getMeshesByTags scene "train")]
    (when (> delta-time 30))
      ; (prn "***train.tick: delta-time=" delta-time))
    (when animate-trains
      (doall (map
               #(do
                   (when (-> %1 (.-metadata) (.-animate))
                     (move-train-mesh %1 delta-time)))
                train-meshes)))))
