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
        [keyboard-visible? _] (keyboard/useKeyboard)
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
        [keyboardPadding setKeyboardPadding] (hooks/use-state 0)]
    ;;  a context that provides the "reveal" callback which will take a ref and reveal it
    (helix/provider {:context context
                     :value reveal}
      ($ AnimatedScrollView
        {:ref outer
         :keyboardShouldPersistTaps "handled"
         :showsVerticalScrollIndicator false
         :scrollEventThrottle 1
         :style #js {:backgroundColor "pink"}
         & (dissoc props :children :reveal-offset)}
        (n/View {:ref inner}
          children)
        (when keyboard-visible?
          (n/View {:style #js {:height keyboard/*keyboard-height*}}))))))
