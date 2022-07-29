;; rules is referred by many, refer to few.
;; NO: rules is referred by few, refer to many.
(ns cube-test.frig-frog.rules
 (:require
  [re-frame.core :as rf]
  [babylonjs :as bjs]
  [odoyle.rules :as o]
  [cube-test.frig-frog.board :as ff.board]
  [cube-test.frig-frog.ball :as ff.ball]
  [cube-test.frig-frog.player :as ff.player]
  [cube-test.frig-frog.tile :as ff.tile]
  [cube-test.main-scene :as main-scene]
  [cube-test.utils.common :as common]
  [cube-test.utils :as utils]))

(declare rules)
(declare session)
(declare query-train-id-cnt)
(declare query-all-rules)
(declare query-all)
(declare player-move-to)
(declare player-move-tile-delta)
(declare init-ball)
(declare ball-move-to-2)
(declare player-to-ball-dist)
(declare ball-to-player-dist)
(declare ball-player-collision-test)
(declare reset-last-tile)

;;
;; session
;;
;; Note: session needs to come before rules, otherwise you'll get a compile error if you reference
;; *session in the ruleset.
(def ^:dynamic *session)

;;
;; rule set
;;
(def rules
  (o/ruleset
    {
      ::time-dt-btm-ball
      [:what
        [::time ::delta dt]
        [id ::x x]
        [id ::y y]
        [id ::sub-id sub-id]
        :then
        (ball-player-collision-test id sub-id ::btm-player)
        (ball-player-collision-test id sub-id ::top-player)]

      ::train-id-cnt
      [:what
        [::train-id-cnt ::new-cnt n]]

      ::ball
      [:what
        [id ::sub-id sub-id]
        [id ::x x]
        [id ::y y]
        [id ::vx vx]
        [id ::vy vy]
        [id ::anim anim]
        :then
        (ff.ball/draw-ball id sub-id x y)]

      ::btm-ball
      [:what
        [id ::sub-id sub-id]
        [id ::x x]
        [id ::y y]
        [id ::vx vx]
        [id ::vy vy]
        [id ::anim anim]]

      ::top-ball
      [:what
        [id ::sub-id sub-id]
        [id ::x x]
        [id ::y y]
        [id ::vx vx]
        [id ::vy vy]
        [id ::anim anim]]

      ::ball-glide
      [:what
        [::time ::delta dt]
        [id ::sub-id sub-id]
        [id ::x x]
        [id ::y y]
        [id ::vx vx]
        [id ::vy vy]
        [id ::anim anim]
        :then
        (let [
              mesh-id (common/gen-mesh-id-from-rule-id id)
              mesh-pos (ff.ball/get-mesh-pos mesh-id)
              dx (* vx dt 0.001)
              dy (* vy dt 0.001)]
          (when (and anim mesh-pos)
            (let [x (.-x mesh-pos)
                  y (.-z mesh-pos)
                  new-x (+ x dx)
                  new-y (+ y dy)
                  board-width ff.board/board-width]
              (cond
                (and (< new-x 0) (< vx 0)) (ball-move-to-2 id board-width y)
                (and (> new-x board-width) (> vx 0)) (ball-move-to-2 id 0 y)
                :else (ff.ball/move-ball id sub-id (* vx dt 0.001) (* vy dt 0.001))))))]

      ::frog
      [:what
        [::frog ::x x]
        [::frog ::y y]]

      ::player-q
      [:what
        [::player ::x x]
        [::player ::y y]
        [::player ::sub-id sub-id]]
      ::btm-player-q
      [:what
        [::btm-player ::x x]
        [::btm-player ::y y]
        [::btm-player ::sub-id sub-id]]
      ::top-player-q
      [:what
        [::top-player ::x x]
        [::top-player ::y y]
        [::top-player ::sub-id sub-id]]
      ::player
      ; ::player-move
      [:what
        [::player ::x x]
        [::player ::y y]
        :then
        ; (prn "rules: player match, x=" x ",y=" y)
        (ff.player/move-player-to "player" x y)]

      ::top-player
      [:what
        [::top-player ::x x]
        [::top-player ::y y]
        :then
        ; (prn "rules: top-player match, x=" x ",y=" y)
        (ff.player/move-player-to "top-player" x y)
        (o/insert! ::tile {::x x ::y y ::prfx "top" ::mat ff.tile/hot-tile-mat})
        (utils/sleep #(reset-last-tile ::top-player-last-pos x y) 10)]

      ::btm-player
      [:what
        [::btm-player ::x x]
        [::btm-player ::y y]
        :then
        ; (prn "rules: btm-player match, x=" x ",y=" y)
        (ff.player/move-player-to "btm-player" x y)
        (o/insert! ::tile {::x x ::y y ::prfx "btm" ::mat ff.tile/hot-tile-mat})
        (utils/sleep #(reset-last-tile ::btm-player-last-pos x y) 10)
        o/fire-rules]

      ::top-player-last-pos
      [:what
        [::top-player-last-pos ::x x]
        [::top-player-last-pos ::y y]]

      ::btm-player-last-pos
      [:what
        [::btm-player-last-pos ::x x]
        [::btm-player-last-pos ::y y]]

      ::tile
      [:what
        [::tile ::x x]
        [::tile ::y y]
        [::tile ::prfx prfx]
        [::tile ::mat mat]
        :then
        ; (prn "*rules: tile match, mat=" mat)
        (ff.tile/update-tile-mesh prfx x y mat)]}))

(defn init-session []
  (set! *session (atom (reduce o/add-rule (o/->session) rules))))
;;
;; commands
;;
(defn swap-session [f]
  (swap! *session f))
;;
;; ball
;;
(defn init-ball-pos [& {:keys [id sub-id x y vx vy anim] :as opts}]
  ; (prn "rules.init-ball-pos: id=" id ",sub-id=" sub-id ",x=" x ",y=" y ",anim=" anim)
  (swap! *session
    (fn [session]
      (-> session
          (o/insert id ::sub-id sub-id)
          (o/insert id ::x (* x ff.board/tile-width))
          (o/insert id ::y (* y ff.board/tile-height))
          (o/insert id ::vx vx)
          (o/insert id ::vy vy)
          (o/insert id ::anim true)))))

(defn ball-move-to [id x y]
  (swap! *session
    (fn [session]
      (-> session
          (o/insert id ::x x)
          (o/insert id ::y y)
          o/fire-rules))))

(defn ball-move-to-2 [id x y]
  (o/insert! id ::x x)
  (o/insert! id ::y y))

(defn ball-toggle-anim [id sub-id]
  (let [
        balls (o/query-all @*session id)
        ; _ (prn "ball-toggle-anim: balls=" balls)
        ;; TODO: add more general way to filter for appropriate id
        ball (first balls)
        anim (:anim ball)
        toggled-anim (not anim)
        ; _ (prn "ball-toggle-anim: ball=" ball ",anim=" anim)
        mesh-id (common/gen-mesh-id-from-rule-id id sub-id)
        ball-grid-pos (ff.ball/get-mesh-grid-pos mesh-id)
        row (:row ball-grid-pos)
        col (:col ball-grid-pos)]
    (swap! *session
      (fn [session]
        (-> session
          (o/insert id {::sub-id sub-id ::x row ::y col ::anim toggled-anim}))))))

(defn ball-player-collision-test [ball-id ball-sub-id player-id]
  (let [dist (ball-to-player-dist ball-id ball-sub-id player-id)]
    (when (and dist (< dist 1.0))
      (player-move-tile-delta ::top-player 0 -2 true)
      (player-move-tile-delta ::btm-player 0 -2 true))))

;;
;; frog
;;
(defn init-frog []
  (swap! *session
    (fn [session]
      (-> session
          (o/insert ::frog ::x 3)
          (o/insert ::frog ::y 7)
          o/fire-rules))))

;;
;; player
;;
(defn init-player [id row col]
  (swap! *session
    (fn [session]
      (case (name id)
        "top-player"
        (do
          (-> session
              (o/insert  ::top-player ::x col)
              (o/insert  ::top-player ::y row)))
        "btm-player"
        (do
          (-> session
              (o/insert  ::btm-player ::x col)
              (o/insert  ::btm-player ::y row)))
        o/fire-rules))))

(defn player-move-to [id x y]
  ; (prn "rules.player-move-to: x=" x ",y=" y ",id=" id)
  (swap! *session
    (fn [session]
      (-> session
          (o/insert id ::x x)
          (o/insert id ::y y)
          o/fire-rules))))

(defn player-move-to! [id x y]
  ; (prn "rules.player-move-to!: x=" x ",y=" y ",id=" id)
  (o/insert! id ::x x)
  (o/insert! id ::y y))

;; the dx,dy is in integral values of rows and cols.  We will scale
;; up to tile-width inside the function.
(defn player-move-tile-delta
  ([id dx dy] (player-move-tile-delta id dx dy false))
  ([id dx dy hard-update?]
   ; (prn "rule: player-move-tile-delta: dx=" dx ",dy=" dy ",id=" id)
   (let [player-pos (o/query-all @*session id)
         x (-> (first player-pos) (:x))
         y (-> (first player-pos) (:y))
         tile-width ff.board/tile-width
         tile-height ff.board/tile-height]
     ; (prn "rule: player-move-tile-delta: x=" x ",y=" y ",dy=" dy ",tile-width=" tile-width ",tile-height=" tile-height)
     (if hard-update?
       (player-move-to! id (+ x (* dx tile-width)) (+ y (* dy tile-height)))
       (player-move-to id (+ x (* dx tile-width)) (+ y (* dy tile-height)))))))

(defn player-to-ball-dist [b-id]
  (let [player (o/query-all @*session ::player-q)
        p-sub-id (-> player (first) (:sub-id))
        scene main-scene/scene
        b-mesh (.getMeshByID scene b-id)
        p-mesh (.getMeshByID scene p-sub-id)
        b-pos (if b-mesh (.-position b-mesh) nil)
        p-pos (if p-mesh (.-position p-mesh) nil)]
      (when (and b-pos p-pos)
        (bjs/Vector3.Distance b-pos p-pos))))

(defn ball-to-player-dist [ball-id ball-sub-id player-id]
  (let [
        ball-mesh (ff.ball/get-mesh ball-id ball-sub-id)
        ball-pos (if ball-mesh
                   (.-position ball-mesh)
                   nil)
        player-mesh (ff.player/get-mesh player-id)
        player-pos (if player-mesh
                     (.-position player-mesh)
                     nil)]
      (when (and ball-pos player-pos)
        (bjs/Vector3.Distance ball-pos player-pos))))

;; possible ids are:
(defn set-player-last-pos [id x y]
  (swap! *session
    (fn [session]
      (-> session
          (o/insert id ::x x)
          (o/insert id ::y y)))))
;;
;; tile
;;
(defn reset-last-tile [id x y]
  ; (prn "*reset-last-tile: x=" x ",y=" y)
  (let [last-pos (o/query-all @*session id)
        ; _ (prn "last-pos=" last-pos)
        last-x (-> last-pos (first) (:x))
        last-y (-> last-pos (first) (:y))
        last-row-col (ff.tile/x-y-to-row-col last-x last-y)]
      ; (prn "last-pos.x=" (:row last-row-col) ",y=" (:col last-row-col))
      (swap! *session
        (fn [session]
          (case id
            ::top-player-last-pos
             (-> session
                  (o/insert ::tile {::x last-x ::y last-y ::prfx "top" ::mat ff.tile/std-tile-mat})
                  (o/insert id ::x x)
                  (o/insert id ::y y)
                  o/fire-rules)
            ::btm-player-last-pos
             (-> session
                 (o/insert ::tile {::x last-x ::y last-y ::prfx "btm" ::mat ff.tile/std-tile-mat})
                 (o/insert id ::x x)
                 (o/insert id ::y y)
                 o/fire-rules))))))

;;
;; left-ctrl
;;
(defn update-left-ctrl-thumbstick [x y]
  (swap! *session
    (fn [session]
      (-> session
          (o/insert ::left-ctrl ::thumbstick {::x x ::y y})))))

;;
;; train
;;
(defn update-train-id-cnt [new-cnt]
  (swap! *session
    (fn [session]
      (-> session
          (o/insert ::train-id-cnt ::new-cnt new-cnt)
          o/fire-rules))))

(defn inc-train-id-cnt []
  (swap! *session
    (fn [session]
      (let [
            old-cnt (-> (query-train-id-cnt) (first) (:n))
            tmp (prn "inc-train-id-cnt: old-cnt=" old-cnt)]
        (-> session
            (o/insert ::train-id-cnt ::new-cnt (+ old-cnt 1))
            o/fire-rules)))))

;;
;; queries
;;
(defn query-all-rules []
  (let [r (o/query-all @*session)]
    (prn "rules: r=" r)
    r))

(defn query-all [id]
  (let [rs (o/query-all @*session id)]
    (prn "rules: rs=" rs)
    rs))

(defn query-frog []
  (let [frg (o/query-all @*session ::frog)]
    (prn "rules: frog.x=" frg)
    frg))

(defn query-player []
  (let [plyr (o/query-all @*session ::player-q)]
    (prn "rules: player=" plyr)
    plyr))

(defn query-train-id-cnt []
  (let [cnt (o/query-all @*session ::train-id-cnt)]
    (prn "rules: train-id-cnt=" cnt)
    cnt))

(defn query-top-player-last-pos []
  (let [last-pos (o/query-all @*session ::top-player-last-pos)]
    (prn "rules: top-player-last-pos=" last-pos)
    last-pos))

(defn query-btm-player-last-pos []
  (let [last-pos (o/query-all @*session ::btm-player-last-pos)]
    (prn "rules: btm-player-last-pos=" last-pos)
    last-pos))

(defn query-top-balls []
  (let [balls (o/query-all @*session ::top-ball)]
    (prn "rules: top-balls=" balls)
    balls))

(defn query-btm-balls []
  (let [balls (o/query-all @*session ::btm-ball)]
    (prn "rules: btm-balls=" balls)
    balls))
;;
;; admin
;;
;;
(defn reset-session []
  (prn "rules: resetting session")
  (o/reset! *session))

;; tick
;;
(defn tick []
  (let [dt (.getDeltaTime cube-test.main-scene/engine)
        dt-2  (common/round-places dt 1)]
    (swap! *session
      (fn [session]
       (-> session
           (o/insert ::time ::delta dt)
           o/fire-rules)))))
