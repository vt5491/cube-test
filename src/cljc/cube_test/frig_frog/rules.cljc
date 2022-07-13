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
  [cube-test.main-scene :as main-scene]
  [cube-test.utils.common :as common]))

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

;;
;; session
;;
;; Note: session needs to come before rules, otherwise you'll get a compile error if you reference
;; *session in the ruleset.
(def ^:dynamic *session)
  ; (atom (reduce o/add-rule (o/->session) rules)))
;;
;; rule set
;;
(def rules
  (o/ruleset
    {
      ::time-dt-ball
      [:what
        [::time ::delta dt]
        ; [id ::x x]
        ; [id ::y y]
        ; [id ::sub-id sub-id]
        [::ball ::x x]
        [::ball ::y y]
        [::ball ::sub-id sub-id]
        ; :when
        ; (= id ::ball)
        :then
        (do
          (let [dist (player-to-ball-dist (str "ball-" sub-id))]
            (when (and dist (< dist 1.0))
              (prn "time-dt: player-ball collision. dist=" dist)
              (player-move-tile-delta ::player 0 -2 true)
              ; (o/insert! ::player ::x 5)
              ; (o/insert! ::player ::y 1)
              ;;TODO consider doing a derived fact on p-mesh so we don't
              ;; have to pull it twice.
              (let [p (o/query-all @*session ::player-q)
                    p-id (-> p (first) (:sub-id))
                    p-mesh (.getMeshByID main-scene/scene p-id)
                    p-mesh-pos (.-position p-mesh)]
                (set! (.-position p-mesh) (bjs/Vector3. (.-x p-mesh-pos)(.-y p-mesh-pos)(- (.-z p-mesh-pos) (* 2 ff.board/tile-height))))))))]


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
        ; [id ::abc abc]
        :then
        (prn "rules.ball matched: id=" id ",sub-id=" sub-id ",x=" x ",y=" y ",vx=" vx ",vy=" vy)
        (ff.ball/draw-ball id sub-id x y)]

      ::ball-glide
      [:what
        [::time ::delta dt]
        ; [id ::x x {:then false}]
        [id ::sub-id sub-id]
        [id ::x x]
        ; [id ::y y {:then false}]
        [id ::y y]
        [id ::vx vx]
        [id ::vy vy]
        [id ::anim anim]
        :then
        (let [
              mesh-id (common/gen-mesh-id-from-rule-id id sub-id)
              ; _ (prn "ball-glide: mesh-id" mesh-id)
              mesh-pos (ff.ball/get-mesh-pos mesh-id)
              x (.-x mesh-pos)
              ;; note how mesh z equals out logical y.
              y (.-z mesh-pos)
              dx (* vx dt 0.001)
              dy (* vy dt 0.001)
              new-x (+ x dx)
              new-y (+ y dy)]
          ;;TODO try to restict in the rule before doing all the let calcs
          (when anim
            (if (< new-x 0)
              (do
                (ball-move-to-2 id 8 3))
              (ff.ball/move-ball id sub-id (* vx dt 0.001) (* vy dt 0.001)))))]
      ::frog
      [:what
        [::frog ::x x]
        [::frog ::y y]]

      ::player-q
      [:what
        [::player ::x x]
        [::player ::y y]
        [::player ::sub-id sub-id]]
      ::player
      ; ::player-move
      [:what
        [::player ::x x]
        [::player ::y y]
        ; [::player ::vx vx]
        ; [::player ::vy vy]
        :then
        (prn "rules: player match, x=" x ",y=" y)
        (ff.player/move-player-to "player" x y)]
        ; (set! cube-test.frig-frog.player.jumped false)]
      ::player-ball-collision
      [:what
        [id ::x x]
        [id ::y y]
        [id ::sub-id sub-id]
        :when
        (= id ::ball)
        :then
        (do
          (prn "pb-collision: id=" id ",sub-id" sub-id)
          (prn "pb-collision: dist=" (player-to-ball-dist (str "ball-" sub-id))))]}))

      ; ::player-ball-collision
      ; [:what
      ;   ; [id]
      ;   ; [::player ::x p-x]
      ;   ; [::player ::y p-y]
      ;   ; [id ::sub-id p-sub-id]
      ;   [::ball ::x b-x]
      ;   [::ball ::y b-y]
      ;   [id ::sub-id b-sub-id]
      ;   ; :when
      ;   ; (-> (and (> b-x (+ p-x 0.5)) (< b-x (- p-x 0.5))
      ;   ;      (and (> b-y (+ p-y 0.5)) (< b-y (- p-y 0.5)))))
      ;   :then
      ;   (prn "rules: player ball collision detected")
      ;   ; (prn "rules: ball collision: p-sub-id=" p-sub-id)
      ;   (prn "rules: ball collision: id=" id)
      ;   (prn "rules: ball collision: b-sub-id=" b-sub-id)]}))
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
(defn init-ball [id row col vx vy]
  (prn "rules.init-ball: id=" id ",row=" row ",col=" col)
  (swap! *session
    (fn [session]
      (-> session
          ; (prn "rules: inserting ball")
          (o/insert id ::x (* col ff.board/tile-width))
          (o/insert id ::y (* row ff.board/tile-height))
          (o/insert id ::vx vx)
          (o/insert id ::vy vy)
          ; (o/insert id ::dx 0)
          ; (o/insert id ::dy 0)
          o/fire-rules))))
          ; o/reset!))))
; (defn blah [& {:keys [key1 key2 key3]}])
; (defn init-ball-pos [id x y vx vy anim])
(defn init-ball-pos [& {:keys [id sub-id x y vx vy anim] :as opts}]
  ; (prn "rules.init-ball-pos: id=" id ",sub-id=" sub-id ",x=" x ",y=" y ",anim=" anim)
  (swap! *session
    (fn [session]
      (-> session
          (o/insert id ::sub-id sub-id)
          (o/insert id ::x x)
          (o/insert id ::y y)
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
  (let [balls (query-all :cube-test.frig-frog.rules/ball)
        ;; TODO: add more general way to filter for appropriate id
        ball (first balls)
        anim (:anim ball)
        toggled-anim (not anim)
        _ (prn "ball-toggle-anim: ball=" ball ",anim=" anim)
        mesh-id (common/gen-mesh-id-from-rule-id id sub-id)
        ball-grid-pos (ff.ball/get-mesh-grid-pos mesh-id)
        row (:row ball-grid-pos)
        col (:col ball-grid-pos)]
    (swap! *session
      (fn [session]
        (-> session
          (o/insert id {::sub-id sub-id ::x row ::y col ::anim toggled-anim}))))))

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
;; ball
;;
; (defn init-ball []
;   (swap! *session
;     (fn [session]
;       (-> session
;           (o/insert ::ball ::x 3)
;           (o/insert ::ball ::y 7)
;           o/fire-rules))))

;;
;; player
;;
(defn init-player [id row col]
  (swap! *session
    (fn [session]
      (-> session
          (o/insert ::player ::x 5)
          (o/insert ::player ::y 0)
          (o/insert ::player ::sub-id "player")
          o/fire-rules))))

(defn player-move-to [id x y]
  (prn "rules.player-move-to: x=" x ",y=" y ",id=" id)
  (swap! *session
    (fn [session]
      (-> session
          (o/insert ::player ::x x)
          (o/insert ::player ::y y)))))
          ;; following is nec.
          ; o/reset!))))
          ; o/fire-rules))))

(defn player-move-to! [id x y]
  (prn "rules.player-move-to!: x=" x ",y=" y ",id=" id)
  (o/insert! ::player ::x x)
  (o/insert! ::player ::y y))

;; the dx,dy is in integral values of rows and cols.  We will scale
;; up to tile-width inside the function.
(defn player-move-tile-delta
  ([id dx dy] (player-move-tile-delta id dx dy false))
  ([id dx dy hard-update?]
   (prn "rule: player-move-tile-delta: dx=" dx ",dy=" dy ",id=" id)
   (let [player-pos (o/query-all @*session :cube-test.frig-frog.rules/player)
         x (-> (first player-pos) (:x))
         y (-> (first player-pos) (:y))
         tile-width ff.board/tile-width
         tile-height ff.board/tile-height]
     (prn "rule: player-move-tile-delta: x=" x ",y=" y ",dy=" dy ",tile-width=" tile-width ",tile-height=" tile-height)
     ; (game-piece-move-to id (+ x (* dx tile-width)) (+ y (* dy tile-height)))
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
      ; (prn "pb-collision: player=" player ",p-sub-id=" p-sub-id)
      ; (prn "pb-collision: b-mesh=" b-mesh ",p-mesh=" p-mesh)
      ; (prn "pb-collision: b-pos=" b-pos ",p-pos" p-pos)
      (when (and b-pos p-pos)
            ; (prn "pb-collision: dist=" (bjs/Vector3.Distance b-pos p-pos))
        (bjs/Vector3.Distance b-pos p-pos))))
;;
;; left-ctrl
;;
; (defn update-left-ctrl-thumbstick [x y open-for-service])
(defn update-left-ctrl-thumbstick [x y]
  ; (prn "rules.update-left-ctrl-thumbstick: axes=" axes)
  ; (prn "rules.update-left-ctrl-thumbstick: x=" x ",y=" y)
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
            ; abc (query-train-id-cnt)
            ; tmp-2 (prn "inc-train-id-cnt: abc=" abc)
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
  ; (prn "rules: train-id-cnt=" (o/query-all @*session ::train-id-cnt))
  (let [frg (o/query-all @*session ::frog)]
    (prn "rules: frog.x=" frg)
    frg))

(defn query-player []
  (let [plyr (o/query-all @*session ::player-q)]
    (prn "rules: player=" plyr)
    plyr))

(defn query-train-id-cnt []
  ; (prn "rules: train-id-cnt=" (o/query-all @*session ::train-id-cnt))
  (let [cnt (o/query-all @*session ::train-id-cnt)]
    (prn "rules: train-id-cnt=" cnt)
    cnt))

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
  ; (prn "rules.tick: entered")
  (let [dt (.getDeltaTime cube-test.main-scene/engine)
        dt-2  (common/round-places dt 1)]
    ; (prn "rules.tick: dt=" dt-2)
    (swap! *session
      (fn [session]
       (-> session
           (o/insert ::time ::delta dt)
           o/fire-rules)))))
  ; o/fire-rules)
