(ns tictactoe.frontend.app
  (:require [reagent.dom :as r.dom]
            [reagent.core :as r]))


(defonce state (r/atom {:board (vec (repeat 9 nil))
                        :active "X"
                        :turns 0
                        :scores {"X" 0 "O" 0}}))

(def wins [["0" "1" "2"]
           ["3" "4" "5"]
           ["6" "7" "8"]
           ["0" "3" "6"]
           ["1" "4" "7"]
           ["2" "5" "8"]
           ["0" "4" "8"]
           ["2" "4" "6"]])

(defn check-win [board player]
  (some (fn [line]
          (every? (fn [index]
                    (= (board (js/parseInt index)) player))
                  line))
        wins))

(defn set-element-value [elem-id v]
  (set! (.-innerHTML (.getElementById js/document elem-id)) v))

(defn update-score-board []
  (doseq [player ["X" "O"]]
    (set-element-value player (get-in @state [:scores player]))))

(defn game-over [winner]
  (when winner
    (js/alert (str winner " won!"))
    (swap! state update-in [:scores winner] inc)
    (update-score-board))
  (reset! state (assoc @state :board (vec (repeat 9 nil))
                       :active "X"
                       :turns 0)))

(defn handle-click [index]
  (let [{:keys [board active turns]} @state]
    (when (nil? (board index))
      (swap! state update :board assoc index active)
      (swap! state update :turns inc)
      (if (check-win (:board @state) active)
        (game-over active)
        (if (= turns 8)
          (do (js/alert "It's a tie!")
              (game-over nil))
          (swap! state update :active #(if (= % "X") "O" "X")))))))

(defn render-square [index]
  [:li {:on-click #(handle-click index)
        :class (get-in @state [:board index])}])

(defn game-board []
  [:div
   (for [i (range 9)]
     ^{:key i} [render-square i])])

(defn ^:dev/after-load start []
  (r.dom/render [game-board] (.getElementById js/document "ttt")))

(defn init []
  (start))