(ns ^:figwheel-hooks breakout.core
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]))


;; title
(def title (atom "Breakout II"))
(def score (atom 0))


(def initialized (atom false))
(def ball-state (atom {:x 1.5 :y 2.0 :vx 0.53 :vy 1.0 }))
(def bricks (atom 0))

(def previous-ball (atom {:x 1.5 :y 2.0 :vx 0.53 :vy 1.0 }))
(def bat-state (atom {:x 1.5 :y 2.0 :vx 0.1 :vy -0.1 }))


(def brick1 (atom {:x 0.5 :y 1 :width 0.5 :height 0.4 :color "green" :visible true}))
(def brick2 (atom {:x 2.5 :y 1 :width 0.5 :height 0.4 :color "black" :visible true}))


(defn line-1 []
  [:rect {:x "0"  :y "0"  :width "3" :height "0.03" :style {:fill "black"}   } ] )

(defn line-2 []
   [:rect {:x "0"  :y "2.97"  :width "3" :height "0.03" :style {:fill "black"}   } ])

(defn line-3 []
  [:rect {:x "0"  :y "0"  :width "0.03" :height "3" :style {:fill "black"}   } ])

(defn line-4 []
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


(defn make-brick [x y]
  {:x x
   :y y
   :width 0.4
   :height 0.1
   :visible true
   :color (rand-nth ["red" "green" "blue" "black" "pink" "orange" "yellow"])
   }
  )


(defn make-new-bricks [n]
  "make n new bricks"
  (for [x (range 0 2.75 0.25) y (range 0 1.5 0.11)]
    (make-brick x y)))


(defn test-bricks []
  (for [x (range 0 2.75 0.5) y (range 0 1.5 0.1)]
    {:x x :y y :width 0.25 :height 0.1}
    ))


(defn new-game []
  (reset! initialized false)
  (reset! score 0)
  (reset! ball-state {:x 1.5 :y 2.0 :vx 0.53 :vy 1.0})
  (reset! bat-state  {:x 1.5 :y 2.0 :vx 0.1 :vy -0.1})
  (reset! bricks (make-new-bricks 10)))


;; this region defines the bricks that appear in level 001
(defn make-level-001 []
   (for [f
         (for [x (range 1 3 1)
               y (range 1 3 1)]
           {:x x :y y :color (rand-nth '("red" "green" "blue" "black" "pink" "orange" "yellow")) }
           )]
     (list f)))

(def level-001 (apply conj (make-level-001)))


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


(defn hit? [x y b]
  (let [x1 (b :x)
        y1 (b :y)
        x2 (+ x1 (b :width))
        y2 (+ y1 (b :height))]
    (and (>= x x1) (<= x x2)
         (>= y y1) (<= y y2))))

(defn hit-the-bricks [x y bs]
  (reset! bricks
          (vec 
           (filter (fn [b] (not (hit? x y b))) 
                   bs)
           )))

;; two bricks
(defn the-bricks [x y bs]
    (hit-the-bricks x y bs)
    (for [b bs]
      [:rect
       {:x (b :x)
        :y (b :y)
        :width "0.25"
        :height  "0.1"
        :style {:fill (b :color)}}
       ]
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
                    :r 0.02
                    :style {:stroke "green" :stroke-width "0.01" :fill "blue"}}]))

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
    (let [new-ball-state (physics-wall (+ x (* vx 0.02))
                                       (+ y (* vy 0.02))
                                       vx
                                       vy)]
      (reset! ball-state new-ball-state))))

(defn breakout-board []
  ;;(console.log "ball x= " (@ball-state :x) ", y = " (@ball-state :y))
  [:div {:class "board"}
   (reduce into
           [:svg {:width "500" :height "500" :viewBox "0 0 3 3" }]
           (list
            (four-lines)            
            (vec (the-bricks (@ball-state :x) (@ball-state :y) @bricks))
            (the-ball)
            ))
   ])

;; title of game 
;; lets have some score mechanism
;; button to start a new game
(defn breakout []
  (when (not @initialized)
    (new-game)
    (swap! initialized not))
  
  [:div {:class "page"}
   [:h1 {:class "title" :style {:text-align "center" } } @title]
   [:h2 {:class "score" :style {:text-align "center" } } (str "score : " @score)]
   
   [:div {:id "newGame" :align "center"}
    [:button {:on-click (fn [ev]
                          (new-game)
                          (console.log "should be a new game !")) }
     "New Game" ]]

   [:div {:class "game" :align "center"}   [breakout-board]]

   
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





