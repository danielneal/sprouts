(ns dreams.ui
  (:require [sprouts.style :as style]
            [sprouts.revealer :as r]
            [hx.react :as hx :refer [defnc]]
            ["react-native" :as rn]
            ["react" :as react]
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

;; This text input will reveal itself
(defnc TextInput
  [props]
  (let [{:keys [onFocus]} props
        reveal (react/useContext r/context)
        ref (react/useRef)]
    [rn/TextInput
     (merge
       {:underlineColorAndroid "transparent"
        :autoCorrect false
        :autoCapitalize "none"
        :style (s [:br1 :ui1 :bg-ui1 :pa2 :f4 :w100])}
       props
       {:ref ref
        :onFocus (fn [e]
                   (reveal ref)
                   (when onFocus
                     (onFocus e)))})]))

(defnc MultilineTextInput
  [props]
  (let [{:keys [onFocus]} props
        reveal (react/useContext r/context)
        ref (react/useRef)]
    [rn/TextInput
     (merge
       {:underlineColorAndroid "transparent"
        :autoCorrect false
        :multiline true
        :numberOfLines 20
        :autoCapitalize "none"
        :style (s [:br1 :h10 :ui1 :bg-brand1 :pa2 :f4 :w100])}
       props
       {:ref ref
        :onFocus (fn [e]
                   (reveal ref)
                   (when onFocus
                     (onFocus e)))})]))
