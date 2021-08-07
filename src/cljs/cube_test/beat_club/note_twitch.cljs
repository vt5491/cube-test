;; This is responsible for creating the animations for the
;; given voices or instruments in a a particular song.
;; Basically, whenever the snare drum is hit, for example
;; then show some sort of visible animation.
(ns cube-test.beat-club.note-twitch
  (:require
   [re-frame.core :as re-frame]
   [babylonjs :as bjs]
   [cube-test.main-scene :as main-scene]
   [babylonjs-gui :as bjs-gui]))

; (def twitch-mesh)
(defrecord NoteTwitch [mesh])

(defn init [id width height pos]
  (let [mesh (bjs/MeshBuilder.CreateBox id (js-obj "width" width "height" height) main-scene/scene)
        twitch (NoteTwitch. mesh)]
      (set! (.-position mesh) pos)
      (assoc twitch :mesh mesh)))
