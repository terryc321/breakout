(ns ^:figwheel-hooks breakout.core
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]))


(def title (atom "Breakout II"))
(def score (atom 0))



;; lets make it 3x3 board arbitrary
;; put ball somewhere in middle
;; be moving 
;; (def game-state (atom {:ball {:x 1.5 :y 2.0 :vx 0.1 :vy -0.1}    ;;:bat  {:x 0 :y 0 :vx 0 :vy 0}
;;                        }))


(def ball-state (atom {:x 1.5 :y 2.0 :vx 0.53 :vy 1.0 }))


(def bat-state (atom {:x 1.5 :y 2.0 :vx 0.1 :vy -0.1 }))

(def bricks-state (atom []))



;; old
(def board-state (atom [0 0 0
                        0 0 0
                        0 0 0]))

(def no-winner (atom true))
(def winner (atom true))

(def stats (atom "score: 0 level: 0"))


(def brick1 (atom {:x 0.5 :y 1 :width 0.5 :height 0.4 :color "green" :visible true}))

(def brick2 (atom {:x 2.5 :y 1 :width 0.5 :height 0.4 :color "black" :visible true}))


;; (def s1 (atom {:x 1 :y 1 :width 1.2 :height 1.2 :visible true}))
;; (def s2 (atom {:x 1 :y 1 :width 1.2 :height 1.2 :visible true}))
;; (def s3 (atom {:x 1 :y 1 :width 1.2 :height 1.2 :visible true}))
;; (def s4 (atom {:x 1 :y 1 :width 1.2 :height 1.2 :visible true}))
;; (def s5 (atom {:x 1 :y 1 :width 1.2 :height 1.2 :visible true}))
;; (def s6 (atom {:x 1 :y 1 :width 1.2 :height 1.2 :visible true}))
;; (def s7 (atom {:x 1 :y 1 :width 1.2 :height 1.2 :visible true}))
;; (def s8 (atom {:x 1 :y 1 :width 1.2 :height 1.2 :visible true}))
;; (def s9 (atom {:x 1 :y 1 :width 1.2 :height 1.2 :visible true}))
;; (def s10 (atom {:x 1 :y 1 :width 1.2 :height 1.2 :visible true}))


(def player (atom 1))





;; counter 1 is outside the viewBox 0 0 3 3
(def counter1 (atom {:x "-5" :y "-5"}))



;; recap javascript mouse events 
;; function clickEvent(e) {
;;   // e = Mouse click event.
;;   var rect = e.target.getBoundingClientRect();
;;   var x = e.clientX - rect.left; //x position within the element.
;;   var y = e.clientY - rect.top;  //y position within the element.
;; }


;; (list
;;  [0 1 2]
;;  [3 4 5]
;;  [6 7 8]
;;  [0 3 6]
;;  [1 4 7]
;;  [2 5 8]
;;  [0 4 8]
;;  [2 4 6])

;; (defn check-for-winner [sq a b c]
;;   (and (not (= (sq a) 0))
;;        (= (sq a) (sq b) (sq c))))


(defn any-winners [sq]
  
   false
    
;; 0 1 2
;; 3 4 5
;; 6 7 8
;; 0 3 6
;; 1 4 7
;; 2 5 8
;; 0 4 8
;; 2 4 6
  
  
  )




(defn you-clicked [ev]
  (let [rect (-> ev .-target .getBoundingClientRect)
        cx (.-clientX ev)
        cy (.-clientY ev)]
    (let [rleft (.-left rect)
          rtop (.-top rect)
          rright (.-right rect)
          rbot (.-bottom rect)]
      (let [dx (- cx rleft)
            dy (- cy rtop)]
        
        (console.log "event client.x = " cx  " , client.y=" cy)
        (console.log "bounding client rectangle  rect.left = " rleft
                     " rect.top=" rtop
                     " rect.right=" rright
                     " rect.bottom=" rbot)
        (console.log "Important result --->> dx = " dx " , dy=" dy)
        ))))


;; change board-state with next player
(defn next-player [p]
   (if
       (= p 1)
     2
     1))






(defn make-square-clickable [x y s bgcolor sq player]  
  (list
   [:rect {:x x
           :y y
           :width "1"
           :height "1"
           :style {:fill bgcolor}
           :on-click (fn [ev]

                       ;; when [0,0] square is clicked , place counter at centre square
                       ;; (when (and (= x 0) (= y 0))
                       ;;   (reset! counter1 (atom {:x "1.5" :y "1.5"})))
                       ;;(console.log "clickable square before : board sq :" (@board-state :sq))
                       
                       (reset! board-state {:sq (assoc sq s @player)
                                            :who-go (next-player @player)
                                            :winner false})

                       ;; (console.log "clickable square after : board sq :" (@board-state :sq))
                       ;; (console.log "you clicked on my square x,y= " x " , " y )
                       )
           }]
   ))




(defn make-square-occupied [x y s bgcolor player]
  (list
   [:rect {:x x
           :y y
           :width "1"
           :height "1"
           :style {:fill bgcolor}           
           }]
   ;; draw a circle if player = 1 of certain color
   ;; draw a circle if player = 2 of different color   
   [:circle {:cx (+ x 0.5) :cy (+ y 0.5) :r 0.45 :style {:stroke "green" :stroke-width "0.01" :fill
                                                        (if (= player 1)
                                                          "yellow"
                                                          "black")}} ]
   ))



(defn make-square-player-1 [x y s bgcolor]
  (make-square-occupied x y s bgcolor 1))

(defn make-square-player-2 [x y s bgcolor]
  (make-square-occupied x y s bgcolor 2))


(defn make-square [x y s bgcolor]
  (let [sq (get @board-state :sq)]
    (let [player (get @board-state :who-go)]
      (cond
        ;; its an empty square - when clicked places a 1 or a 2 at that location of board-state
        (= (sq s) 0) (make-square-clickable x y s bgcolor sq player)
        (= (sq s) 1) (make-square-player-1 x y s bgcolor)
        true         (make-square-player-2 x y s bgcolor)))))




;; if 0 then empty square and if clicked based on current board state , place that players counter there ,
;; update board state
;; if true or false - already a counter there , 

;; has there been a winner , if so do not act on any mouse events ??


(defn line-1 []
   ;; horz divider 1
  [:rect {:x "0"  :y "0"  :width "3" :height "0.03" :style {:fill "black"}   } ] )

(defn line-2 []
   ;; horz divider 2
   [:rect {:x "0"  :y "2.97"  :width "3" :height "0.03" :style {:fill "black"}   } ])

(defn line-3 []
   ;; vert divider 1
  [:rect {:x "0"  :y "0"  :width "0.03" :height "3" :style {:fill "black"}   } ])

(defn line-4 []
   ;; vert divider 2
  [:rect {:x "2.97"  :y "0"  :width "0.03" :height "3" :style {:fill "black"}   } ])


(defn four-lines []
  (list
   [line-1]
   [line-2]
   [line-3]
   [line-4]))

(defn lots-horizontal-lines []
  (into
   []
   (for [y (range -0.01 3.3 0.2)]
    [:rect {:x "0"
            :y y
            :width "3"
            :height "0.01"
            :style {:fill "black"}}])))


(defn lots-vertical-lines []
  (into
   []
   (for [x (range -0.01 3.3 0.2)]
    [:rect {:x x
            :y "0"
            :width "0.01"
            :height "3"
            :style {:fill "black"}}])))


;; 
;;
;; dev:cljs.user=> (into [1 2 3] '(:a :b))
;; [1 2 3 :a :b]
;; dev:cljs.user=> (into (into [1 2 3] '(:a :b)) '(:c :d))
;; [1 2 3 :a :b :c :d]
;; dev:cljs.user=> (into (into (into [1 2 3] '(:a :b)) '(:c :d)) '(:e :f))
;; [1 2 3 :a :b :c :d :e :f]
;; dev:cljs.user=> (into (into (into (into [1 2 3] '(:a :b)) '(:c :d)) '(:e :f)) '(:g :h))
;; [1 2 3 :a :b :c :d :e :f :g :h]



(defn make-click-box [x y]
  (list
   [:rect {:x x :y y :width "1"
           :height "1" :style {:fill "blue"}
           :on-click (fn [ev]
                       ;;(reset! s1 @player)
                       (reset! player (next-player @player)))

                       }]))



(defn player-coin [x y p]
  (list
   [:rect {:x x :y y :width "1" :height "1" :style {:fill "blue"}}]
   [:circle {:cx (+ x 0.5) :cy (+ y 0.5) :r 0.45 :style {:stroke "green" :stroke-width "0.01" :fill (if (= p 1) "red" "black")}}]))



;; bug with new game , like old svg dont get out the way.



;; brutally simple + effective + tested + works + easy to read + easy to comprehend
;;
;; (defn tic-tac-toe-board []  
;;   (into
;;    [:svg {:width "500" :height "500" :viewBox "0 0 3 3" }]
;;    (four-lines)))

;; forget about into and lists and hiccup code for now , lets see if get clickable area working , and it can be set and unset

;; player p in tic tac toe , last player to go has to be winner or draw
;; p either 1 or 2 meaning player 1 or player 2
(defn check-for-winner [p]
  (let [b @board-state]
    (cond
      (= p (b 0) (b 1) (b 2)) true
      (= p (b 3) (b 4) (b 5)) true
      (= p (b 6) (b 7) (b 8)) true
      (= p (b 0) (b 3) (b 6)) true
      (= p (b 1) (b 4) (b 7)) true
      (= p (b 2) (b 5) (b 8)) true
      (= p (b 0) (b 4) (b 8)) true
      (= p (b 2) (b 4) (b 6)) true
      true false)))





(defn square [x y n]
  (let  [c (@board-state n)]

    (concat
    (list
     [:rect {:x x :y y :width "1" :height "1" :style {:fill (cond
                                                              (= 0 (mod n 2)) "gold"
                                                              true "sienna"
                                                              )}
            :on-click (fn [ev]
                        (when (and (= c 0) @no-winner)
                          (reset! board-state (assoc @board-state n @player))
                          (when (check-for-winner @player)
                            (reset! no-winner false)
                            (reset! winner @player)
                            ;; launch a garish winner banner
                            (reset! stats (str "player " @player " has WON !"))
                            )
                          (when @no-winner
                            (reset! player (next-player @player))                            
                            (reset! stats (str "player "@player " to move.")))
                          ))      
            }
      ]
     )

    
    ;; either nil or list of svg things, at the moment a coloured circle 
    (if (= c 0)
      nil      
       (list 
      [:circle {:cx (+ x 0.5) :cy (+ y 0.5) :r 0.45 :style {:stroke "green" :stroke-width "0.01" :fill
                                                            (if (= c 1)
                                                              "black"
                                                              "white")}} ]
      ;; [:rect {:x (+ x 0.25) :y (+ y 0.25) :width "0.5" :height "0.5"
      ;;         :style {:fill (cond
      ;;                         (= c 1) "brown"
      ;;                         true "turquoise"
      ;;                         )}}]
      
      )))))




;; dev:tic-tac-toe.core=> (for [x (range 3)] (for [y (range 3)] (let [n (+ x (* y 3))] (list x y n))))
;; (((0 0 0) (0 1 3) (0 2 6))
;;  ((1 0 1) (1 1 4) (1 2 7)) 
;;  ((2 0 2) (2 1 5) (2 2 8)))


(defn sweet []
  (for [n (range 9)]
    (let [x (mod n 3)]
      (let [y (/ (- n x) 3)]
        (list x y n)))))




(defn tic-tac-toe-board3 []
  (reduce
   into
   [:svg {:width "500" :height "500" :viewBox "0 0 3 3" }]
   (list
     (square 0 0 0)
     (square 1 0 1)
     (square 2 0 2)
     (square 0 1 3)
     (square 1 1 4)
     (square 2 1 5)
     (square 0 2 6)
     (square 1 2 7)
     (square 2 2 8)
     )
   )
  )


(defn tic-tac-toe-board []
  (reduce into
   [:svg {:width "500" :height "500" :viewBox "0 0 3 3" }]
   (list
     (square 0 0 0)
     (square 1 0 1)
     (square 2 0 2)
     (square 0 1 3)
     (square 1 1 4)
     (square 2 1 5)
     (square 0 2 6)
     (square 1 2 7)
     (square 2 2 8)
     (four-lines)
     )
   )
  )









   





     

     












    
    ;;   (list
    ;;    (cond
    ;;      (= @s0 0) [make-click-box 0 0]
    ;;      (= @s0 1) [player-coin 0 0 1]
    ;;      (= @s0 2) [player-coin 0 0 2] 
    ;;      )))]
    ;; (list))])

   
   




;;[make-square 0 0 0 "blue"]
(defn tic-tac-toe-board2 []
  [:div {:class "game" :align "center"}
   (reduce into
           [:svg {:width "500" :height "500" :viewBox "0 0 3 3" :style {:margin "none"}}]           
           (list

            (make-square 0 0 0 "blue")
            (make-square 1 0 1 "green")
            (make-square 2 0 2 "blue")
            
            (make-square 0 1 3 "green")
            (make-square 1 1 4 "blue")
            (make-square 2 1 5 "green")
            
            (make-square 0 2 6 "blue")
            (make-square 1 2 7 "green")
            (make-square 2 2 8 "blue")
            
            (four-lines)
            
            ))])




     ;; (make-square 1 0 1 "green")
     ;; (make-square 2 0 2 "blue")
     
     ;; (make-square 0 1 3 "green")
     ;; (make-square 1 1 4 "blue")
     ;; (make-square 2 1 5 "green")
     
     ;; (make-square 0 2 6 "blue")
     ;; (make-square 1 2 7 "green")
     ;; (make-square 2 2 8 "blue")
     

    ;;[lines]

    ;; [:rect {:x "1"  :y "0" :width "1" :height "1" :style {:fill "green"}  :on-click (fn [ev] (console.log "square 2 clicked" )) } ]
    ;; [:rect {:x "2"  :y "0" :width "1" :height "1" :style {:fill "blue"}  :on-click (fn [ev] (console.log "square 3 clicked" )) } ]

    ;; ;; row 2
    ;; [:rect {:x "0"  :y "1" :width "1" :height "1" :style {:fill "green"}  :on-click (fn [ev] (console.log "square 4 clicked" )) } ]
    ;; [:rect {:x "1"  :y "1" :width "1" :height "1" :style {:fill "blue"}  :on-click (fn [ev] (console.log "square 5 clicked" )) } ]
    ;; [:rect {:x "2"  :y "1" :width "1" :height "1" :style {:fill "black"}  :on-click (fn [ev] (console.log "square 6 clicked" )) } ]

    ;; ;; row 3
    ;; [:rect {:x "0"  :y "2" :width "1" :height "1" :style {:fill "blue"}  :on-click (fn [ev] (console.log "square 7 clicked" )) } ]
    ;; [:rect {:x "1"  :y "2" :width "1" :height "1" :style {:fill "black"}  :on-click (fn [ev] (console.log "square  8 clicked" )) } ]
    ;; [:rect {:x "2"  :y "2" :width "1" :height "1" :style {:fill "green"}  :on-click (fn [ev] (console.log "square 9 clicked" )) } ]




    
    ;; [:circle {:cx "50" :cy "50" :r "40" :style {:stroke "green" :stroke-width "4" :fill "yellow"}} ]
    ;; [:circle {:cx "150" :cy "50" :r "40" :style {:stroke "green" :stroke-width "4" :fill "blue"}} ]
    ;; [:circle {:cx "250" :cy "50" :r "40" :style {:stroke "green" :stroke-width "4" :fill "yellow"}} ]
    ;; [:circle {:cx "50" :cy "150" :r "40" :style {:stroke "green" :stroke-width "4" :fill "blue"
    ;;                                              :opacity "0.5"}} ]
    ;; [:circle {:cx "150" :cy "150" :r "40" :style {:stroke "green" :stroke-width "4" :fill "yellow"}} ]
    ;; [:circle {:cx "250" :cy "150" :r "40" :style {:stroke "green" :stroke-width "4" :fill "blue"}} ]
    ;; [:circle {:cx "50" :cy "250" :r "40" :style {:stroke "green" :stroke-width "4" :fill "yellow"}} ]
    ;; (when false
    ;;   [:circle {:cx "150" :cy "250" :r "40" :style {:stroke "green" :stroke-width "4" :fill "blue"}} ]
    ;;   )
    ;; (when true
    ;;   [:circle {:cx "250" :cy "250" :r "40" :style {:stroke "green" :stroke-width "4" :fill "yellow"
    ;;                                                 :opacity "0.5"}
    ;;             :on-mouse-move #(console.log "hello world! x = " (.-clientX %) " y="(.-clientY %))
    ;;             :on-mouse-enter #(console.log "mouse entered !  ")
    ;;             :on-mouse-leave #(console.log "mouse left!! x = ")
    ;;             :on-mouse-over #(console.log "mouse over!! x = ")
    ;;             } ])

 



  
(defn new-game []
  (reset! board-state [0 0 0
                       0 0 0
                       0 0 0])
  (reset! no-winner true)
  (reset! player 1)
  (reset! winner 0)
  (reset! stats "player 1 to move")
  )


;; [:rect {:x x
;;         :y y
;;         :width "0.6"
;;         :height "0.2"
;;         :style {:fill (rand-nth ["red" "green" "blue" "black" "pink" "orange" "yellow"])}
;;         :on-click (fn [ev] true)}]   

;; this region defines the bricks that appear in level 001
(defn make-level-001 []
   (for [f
         (for [x (range 1 3 1)
               y (range 1 3 1)]
           {:x x :y y :color (rand-nth '("red" "green" "blue" "black" "pink" "orange" "yellow")) }
           )]
     (list f)))




  













(def level-001 (apply conj (make-level-001)))



(defn the-bricks2 []
  (let [bx (@ball-state :x)
        by (@ball-state :y)]
    (for [x (range 0.1 2.4 0.61)]
      (for [y (range 0.1 1.5 0.21)]

        (if (not
             (and (> bx x)
                  (> by y)
                  (< bx (+ x 0.6))
                  (< by (+ y 0.2))))
          [:rect {:x x
                  :y y
                  :width "0.6"
                  :height "0.2"
                  :style {:fill "orange"}}])))))





;; if brick is visible and ball in brick bounding box
;; set brick visibility false
;; otherwise render the brick

(defn ball-hit-visible-brick? [brick ball]
  (if (not (brick :visible))
    false
    (let [bx (ball :x)
          by (ball :y)
          x (brick :x)
          x2 (+ x (brick :width))
          y (brick :y)
          y2 (+ y (brick :height))]
      (and (> bx x) (< bx x2) (> by y) (< by y2)))))


;; (defn hit-the-bricks []
;;   ;;(doall
;;   (if (ball-hit-visible-brick? @brick1 @ball-state)
;;       (reset! brick1 (assoc @brick1 :visible false)))
;;     (if (ball-hit-visible-brick? @brick2 @ball-state)
;;       (reset! brick2 (assoc @brick2 :visible false)))    
;;     ))


(defmacro my-macro-adder
  [a b]
  `(+ ~a ~b))

(defmacro hit-macro
  [b]
  `(if (ball-hit-visible-brick? (deref ~b) (deref ball-state))
     (do
       (reset! ~b (assoc (deref ~b) :visible false))
       (swap! score inc)
       nil)))





;;(def bricks [brick1 brick2])

;; macro perhaps ?
(defn hit-the-bricks []
  (doall

   ;; (hit-macro brick1)
   ;; (hit-macro brick2)
   
   (if (ball-hit-visible-brick? @brick1 @ball-state)
     (do
       (reset! brick1 (assoc @brick1 :visible false))
       (swap! score inc)
       nil))

  (if (ball-hit-visible-brick? @brick2 @ball-state)
    (do
      (reset! brick2 (assoc @brick2 :visible false))
      (swap! score inc)
      nil))

  
  ))


;; two bricks
(defn the-bricks []
  (hit-the-bricks)
  (list
   (if (@brick1 :visible)
     [:rect {:x (@brick1 :x)
             :y (@brick1 :y)
             :width (@brick1 :width)
             :height (@brick1 :height)
             :style {:fill (@brick1 :color)}}])
   (if (@brick2 :visible)
     [:rect {:x (@brick2 :x)
             :y (@brick2 :y)
             :width (@brick2 :width)
             :height (@brick2 :height)
             :style {:fill (@brick2 :color)}}])
   ))

   

;; physics of ball
;; x2 = x1 + vx . dt
;; y2 = y1 + vy . dt
;; 
;; if x2 > 3 then vx = -vx
;; if x2 < 0 then vx = -vx
;; if y2 > 3 then vy = -vy
;; if y2 < 0 then vy = -vy
;;
;; 1 ) lets have a static ball
(defn the-ball []
        (list 
         [:circle  {:class "ball"
                    :cx (@ball-state :x)
                    :cy (@ball-state :y)
                    :r 0.04
                    :style {:stroke "green" :stroke-width "0.01" :fill "red"}}]))


;;
(defn physics-wall [x y vx vy]
  (if (> x 2.9)
    (physics-wall 2.9 y (- vx) vy)
    (if (< x 0.1)
      (physics-wall 0.1 y (- vx) vy)
      (if (> y 2.9)
        (physics-wall x 2.9 vx (- vy))
        (if (< y 0.1)
          (physics-wall x 0.1 vx (- vy))
          {:x x :y y :vx vx :vy vy})))))

    
(defn tick []
  (let [bs @ball-state
        x (bs :x)
        y (bs :y)
        vx (bs :vx)
        vy (bs :vy) ]
    (let [new-ball-state (physics-wall (+ x (* vx 0.01))
                                       (+ y (* vy 0.01))
                                       vx
                                       vy)]
      (reset! ball-state new-ball-state))))

    
    ;; (let [x2 (+ x vx)
    ;;       y2 (+ y vy)]
    ;;   (if (> x2 2.95)
        
    ;;                (< x2 0.05) 0.1)
    ;;         y2 (if (> y2 2.95) 2.9
    ;;                (< y2 0.05) 0.1)]
    ;;   (reset! ball-state {:x x2
    ;;                       :y y2
    ;;                       :vx (if (or (> x2 2.95) (< x2 0.05)) (- (+ (/ (rand) 100) vx)) vx) 
    ;;                       :vy (if (or (> y2 2.95) (< y2 0.05)) (- (+ (/ (rand) 100) vy)) vy)})))))




(defn breakout-board []
  [:div {:class "board"}
   (reduce into
           [:svg {:width "500" :height "500" :viewBox "0 0 3 3" }]
           (list
            (four-lines)            
            (the-bricks)
            (the-ball)
            ))
   ])






;;  (reset! counter1 (atom {:x "-5" :y "-5"})))

;; title of game 
;; lets have some score mechanism
;; button to start a new game
;; breakout-board is actual game bit
(defn breakout []
  [:div {:class "page"}
   [:h1 {:class "title" :style {:text-align "center" } } @title]
   [:h2 {:class "score" :style {:text-align "center" } } (str "score : " @score)]
   
   [:div {:id "newGame" :align "center"}
    [:button {:on-click (fn [ev]
                          (new-game)
                          (console.log "should be a new game !")) }
     "New Game" ]]

   [:div {:class "game" :align "center"}
    [breakout-board]]

   ;;[:img {:id "img1"           :src "img1.jpg"           :style {:visibility "shown" :opacity @opacity}}] 
   [:div {:id "div3"}
    ;;[:img {:id "img3" :src "img3.jpg" :style {:visibility "shown"}}]
    ]
   ])



;; put breakout into app on html page
(reagent/render-component [breakout]
                          (. js/document (getElementById "app")))

;; satisfy test/breakout/core_test.cljs
(defn multiply [x y] (* x y))

;; wizzy google closure dom stuff
(defn get-app-element []
  (gdom/getElement "app"))

(defn mount [el]
  (reagent/render-component [breakout] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

;;(.setInterval js/window #(js/alert "hello world") 100)
(.setInterval js/window #(tick) 30)





