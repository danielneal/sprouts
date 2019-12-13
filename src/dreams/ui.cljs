(ns dreams.ui
  (:require [sprouts.style :as style]
            ["react-native" :as rn]
            [hx.react :as hx :refer [defnc]]
            ["@expo/vector-icons" :refer [Entypo]]))

(def s
  (style/init
    {:palette {:ui0 "#333344"
               :ui1 "#DDDDCC"
               :brand0 "#222244"
               :brand1 "#202033"
               :status0 "#5b9851"
               :status1 "#f5a623"
               :status2 "#b5071c"}
     :rem 15}))

(defnc HelpButton
  [props]
  [rn/TouchableOpacity
   (merge props {:style (merge (s [:sh1 :h3 :w3 :br50 :bg-ui1 :aic :jcc]))})
   [rn/Text {:style (s [:f3])} "?"]])

(defnc TextButton
  [props]
  (let [{:keys [text]} props]
    [rn/TouchableOpacity
     (-> props
         (dissoc :text)
         (merge {:style (s [:sh1 :pv2 :ph3 :br2 :bg-ui1 :aic :jcc])}))
     [rn/Text {:style (s [:f4 :tc])} text]]))

(defnc TextButton2
  [props]
  (let [{:keys [text]} props]
    [rn/TouchableOpacity
     (-> props
         (dissoc :text)
         (merge {:style (s [:sh1 :pv2 :ph3 :br2 :bg-brand0 :aic :jcc])}))
     [rn/Text {:style (s [:f4 :ui1 :tc])} text]]))

(defnc CloseButtonDark
  [props]
  [rn/TouchableOpacity props
   [Entypo {:name "cross"
            :style (s [:ui0])
            :size 30}]])

(defnc CloseButtonLight
  [props]
  [rn/TouchableOpacity props
   [Entypo {:name "cross"
            :style (s [:ui1])
            :size 30}]])
