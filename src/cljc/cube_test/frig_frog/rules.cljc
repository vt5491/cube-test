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
  [cube-test.utils.common :as common]))

(declare rules)
(declare session)
(declare query-train-id-cnt)
; ::stop-player
; [:what
;  [::player ::x x]
;  [::window ::width window-width]
;  :then
;  (when (> x window-width)
;    (o/insert! ::player ::x window-width))]
    ; {::print-time
    ;  [:what
    ;   [::time ::total tt]
    ;   :then
    ;   (println tt)]})
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
      ::train-id-cnt
      [:what
        [::train-id-cnt ::new-cnt n]]
      ::game-piece
      [:what
        [id ::x x]
        [id ::y y]]
      ::game-piece-move
      [:what
        [id ::x x]
        [id ::y y]
        :then
        (prn "rules.game-piece-move matched: id=" id)
        (let [
              ; piece-type (nth (re-matches #"^([a-z]*)[-]*\d*" id) 1)
              piece-type id]
          (prn "piece-type=" piece-type)
          ; (condp = piece-type
          ;   "ball"   (ff.ball/draw-ball id x y)
          ;   "player" (ff.player/draw-player id x y)
          ;   "cube-test.frig-frog.rules/player" (ff.player/draw-player id x y))
          (cond
            ; (re-matches #"^cube-test.frig-frog.rules/player.*" id)
            (= :cube-test.frig-frog.rules/player id)
            (ff.player/draw-player id x y)
            (re-matches #"^ball.*" id)
            (ff.ball/draw-ball id x y)))]

            ; (re-matches #"^cube-test.frig-frog.rules/player.*" id)
            ; (ff.player/draw-player id x y
            ;
            ;      (re-matches #"^ball.*" id)
            ;   (ff.ball/draw-ball id x y))
      ::game-piece-glide
      [:what
        [::time ::delta dt]
        [id ::x x {:then false}]
        [id ::y y]
        [id ::vx vx]
        [id ::vy vy]
        :then
        (let [a 7]
          ; (prn "rules: game-piece-glide matched: dt=" dt ",vx=" vx)
          (ff.ball/move-ball id (* vx dt 0.001) (* vy dt 0.001)))]
      ::frog
      [:what
        [::frog ::x x]
        [::frog ::y y]]
      ::player
      [:what
        [::player ::x x]
        [::player ::y y]
        [::player ::vx vx]
        [::player ::vy vy]]
      ::left-ctrl
      [:what
       [::left-ctrl ::thumbstick axes]
       :then
       (prn "rules: left-ctrl activated, axes=" axes)
       (prn "point a")
       (prn "session= "(o/query-all @*session))
       ; (prn "session.player= "(o/query-all @*session :cube-test.frig-frog.rules/player))
       ; (prn "ball= "(o/query-all @*session "ball-1"))
       (prn "session.player= "(o/query-all @*session ::player))
       ; (let [player-pos (o/query-all @*session "player")])
       (let [player-pos (o/query-all @*session :cube-test.frig-frog.rules/player)
             x (-> (first player-pos) (:x))
             y (-> (first player-pos) (:y))]
          (ff.player/move-player "player" 0 0.1))]}))
       ;  (prn "rules: player-pos=" player-pos)
       ;  (prn "hi"))
       ; (prn "point b")]}))


(defn init-session []
  (prn "rules.init-session: entered")
  (set! *session (atom (reduce o/add-rule (o/->session) rules))))
;;
;; commands
;;
(defn swap-session [f]
  (prn "rules.swap-session, f=" f)
  (swap! *session f))
;;
;; game-piece
;;
(defn init-game-piece [id row col vx vy]
  (swap! *session
    (fn [session]
      (-> session
          (o/insert id ::x (* col ff.board/tile-width))
          (o/insert id ::y (* row ff.board/tile-height))
          (o/insert id ::vx vx)
          (o/insert id ::vy vy)
          o/fire-rules))))

(defn game-piece-move [id x y]
  (swap! *session
    (fn [session]
      (-> session
          (o/insert id ::x x)
          (o/insert id ::y y)
          o/fire-rules))))
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

; (defn move-frog [x y]
;   (swap! *session
;     (fn [session]
;       (-> session
;           (o/insert ::train-id-cnt ::new-cnt new-cnt)
;           o/fire-rules))))

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
; (defn init-player []
;   (swap! *session
;     (fn [session]
;       (-> session
;           (o/insert ::player ::x 5)
;           (o/insert ::player ::y 0)
;           o/fire-rules))))

;;
;; left-ctrl
;;
(defn update-left-ctrl-thumbstick [axes]
  (prn "rules.update-left-ctrl-thumbstick: axes=" axes)
  (swap! *session
    (fn [session]
      (-> session
          (o/insert ::left-ctrl ::thumbstick axes)
          o/fire-rules))))
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

(defn query-frog []
  ; (prn "rules: train-id-cnt=" (o/query-all @*session ::train-id-cnt))
  (let [frg (o/query-all @*session ::frog)]
    (prn "rules: frog.x=" frg)
    frg))

(defn query-train-id-cnt []
  ; (prn "rules: train-id-cnt=" (o/query-all @*session ::train-id-cnt))
  (let [cnt (o/query-all @*session ::train-id-cnt)]
    (prn "rules: train-id-cnt=" cnt)
    cnt))

;;
;; tick
;;
; (swap! *session
;   (fn [session]
;     (-> session
;         (o/insert ::time ::total 100)
;         o/fire-rules)))
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


    ; (o/insert ::time ::delta dt)
  ; (o/insert ::time { ::delta (.getDeltaTime cube-test.main-scene/engine)})
  ; o/fire-rules)
