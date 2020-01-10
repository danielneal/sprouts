(ns sprouts.revealer
  (:require [helix.hooks :as hooks]
            [helix.core :as helix :refer [$ defnc]]
            [sprouts.native :as n]
            [sprouts.keyboard :as keyboard]
            [sprouts.dimensions :as dimensions]
            [cljs-bean.core :refer [->clj]]
            ["react" :as react]
            ["react-native-reanimated" :default Animated :refer
             [Easing.inOut Easing.ease]]
            ["react-native" :as rn]
            [goog.math :as math]))


(def AnimatedValue (.-Value Animated))
(def AnimatedView (.-View Animated))
(def AnimatedScrollView (.-ScrollView Animated))
(def animated-timing (.-timing Animated))

;; A revealer is a scroll view that can reveal an element.
;; It creates a context with a single 'reveal' function
;; It optionally takes a reveal-offset so that the
;; revealed element isn't right at the top.

(def context (react/createContext))

(defnc Revealer
  [props]
  (let [{:keys [children reveal-offset]
         :or {reveal-offset 50}} props
        inner (react/useRef)
        outer (react/useRef)
        [visible _] (keyboard/useKeyboard)
        reveal (hooks/use-callback
                 :once
                 (fn [^js ref]
                   (js/setTimeout
                     (fn []
                       (when-let [r (.-current ref)]
                         (.measureLayout
                           r
                           (rn/findNodeHandle (.-current inner))
                           (fn [x y]
                             (.scrollTo (.getNode (.-current outer)) #js {:y (max (- y reveal-offset) 0)
                                                                          :animated true})))))
                     200)))
        keyboard-padding (react/useRef (AnimatedValue. 0))]
    (hooks/use-effect
      [visible]
      (fn []
        (let [anim (animated-timing
                     (.-current keyboard-padding)
                     #js {:toValue (if visible keyboard/keyboard-height 0)
                          :duration 200
                          :easing (Easing.inOut Easing.ease)})]
          (.start anim))))
    (helix/provider {:context context
                     :value reveal}
      ($ AnimatedScrollView
        {:ref outer
         :keyboardShouldPersistTaps "handled"
         :showsVerticalScrollIndicator false
         :scrollEventThrottle 1
         & (dissoc props :children :reveal-offset)}
        (n/View {:ref inner}
          children)
        ($ AnimatedView
          {:style #js {:height (.-current keyboard-padding)}})))))
