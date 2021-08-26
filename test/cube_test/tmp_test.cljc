(ns cube-test.tmp-test)

(def h {:models
        {:ybot-rumba {:is-loaded true, :is-enabled true, :is-playing false},
         :ybot-head-bang {:is-loaded true, :is-enabled false, :is-playing false}}})

(prn "hi h=" h)

(let [ {{{:keys [:is-enabled]} {:keys :ybot-rumba}} :models} h]
  (prn "val=" models))

(let [{:keys [:models]} h]
  (prn "val=" models))

(let [{models :models} h]
  (prn "val=" models))

;; works
(let [{{:keys [ybot-rumba]} :models} h]
  (prn "val=" ybot-rumba))

;; triple..works
(let [{{{:keys [is-loaded]} :ybot-rumba} :models} h]
  (prn "val=" is-loaded))

;; works too.
(let [{{{is-loaded :is-loaded} :ybot-rumba} :models} h]
  (println "Joe is a" is-loaded "wielding a"))

(def multiplayer-game-state
  {:joe {:class "Ranger"
         :weapon "Longbow"
         :score 100}
   :jane {:class "Knight"
          :weapon "Greatsword"
          :score 140}
   :ryan {:class "Wizard"
          :weapon "Mystic Staff"
          :score 150}})

(let [{{:keys [class weapon]} :joe} multiplayer-game-state]
  (println "Joe is a" class "wielding a" weapon))
