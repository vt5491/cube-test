(ns cube-test.frig-frog.train
  (:require
   [re-frame.core :as rf]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [cube-test.base :as base]))

;; we need this so we don't get a compiler warning when referencing mesh metadata.
(set! *warn-on-infer* false)

; (if (nil? bar) baz bar)
(def train-cube-width 1.2)

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

(defn get-train-by-id [trains id]
  ; (into [] (filter (fn [x] (= (:id x) id)) trains)))
  (prn "train.get-train-by-id: trains=" trains ",id=" id)
  (let [r (into [] (filter #(= (:id %1) id) trains))]
    (prn "r=" r)
    (first r)))
  ; (let [r (into [] (filter (fn [x] (= (:id x) id)) trains))]
  ;   r))

; (defn update-train-by-id [id trains updates])
(defn update-train-by-id [trains id updates]
  (let [train (get-train-by-id trains id)
        new-train (doall (map (fn [[k v]]
                                (prn "map: k=" k ",v=" v)
                                (if (get-in updates [k])
                                  [k (get-in updates [k])]
                                  [k v]))
                              train))
        new-train-2 (into {} new-train)]
    (prn "update-train-by-id: updates=" updates)
    (prn "update-train-by-id: train=" train)
    (prn "update-train-by-id: new-train=" new-train)
    (prn "update-train-by-id: new-train-2=" new-train-2)
    new-train-2))

;; methods that update the graphical trains at the BJS level
(defn add-train-mesh-cube [id idx pos vx vy]
  (let [scene main-scene/scene
        ; id (:id train)
        cube (bjs/MeshBuilder.CreateBox. (str (name id) "-" idx) (js-obj "height" 1 "width" 1 "depth" 1) scene)]
    (set! (.-position cube) pos)
    ; (bjs/AddTagsTo cube (name id))
    (bjs/Tags.AddTagsTo cube (name id))
    (bjs/Tags.AddTagsTo cube "train")
    (set! (.-metadata cube) (js-obj "vx" vx, "vy" vy))))
      ; (bjs/Vector3.
      ;      (* (:init-col train) train-cube-width)
      ;      train-cube-width
      ;      (* (:init-row train) train-cube-width)))))
      ; right-screen (bjs/MeshBuilder.CreateBox.
      ;               "right-screen"
      ;               (js-obj "height" 4 "width" 4 "depth" 0.1)
      ;               main-scene/scene))

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
        vx (-> train-mesh (.-metadata) (.-vx))
        vy (-> train-mesh (.-metadata) (.-vy))
        current-pos (.-position train-mesh)
        delta-pos (bjs/Vector3. (* vx delta-time 0.001) 0 (* vy delta-time 0.001))
        new-pos (.add current-pos delta-pos)]
      ; (prn "train.move-train-mesh: vx=" vx ",vy=" vy)
      (set! (.-position train-mesh) new-pos)))
      ; (when (or (> (.-x new-pos) 5) (< (.-x new-pos) -5))
      ;   (rf/dispatch))))

(defn tick []
  (let [scene main-scene/scene
        engine main-scene/engine
        delta-time (.getDeltaTime engine)
        train-meshes (.getMeshesByTags scene "train")]
    ; (prn "train.tick: train-mesh count=" (count train-meshes))))
    (doall (map #(move-train-mesh %1 delta-time) train-meshes))))
