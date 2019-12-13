(ns sprouts.animated
  (:require
   ["react-native-reanimated" :default Animated :refer [Easing]]
   [hx.hooks :as hooks]
   [cljs-bean.core :refer [->js]]))

(def Value (.-Value Animated))
(def Clock (.-Clock Animated))
(def View (.-View Animated))
(def event (.-event Animated))
(def ScrollView (.-ScrollView Animated))
(def Text (.-Text Animated))
(def useCode (.-useCode Animated))
(def timing (.-timing Animated))
(def in-out (.inOut Easing (.-ease Easing)))

(defn createAnimatedComponent [c]
  (.createAnimatedComponent Animated c))

(def nodes
  {:abs (.-abs Animated)
   :acc (.-acc Animated)
   :acos (.-acos Animated)
   :add (.-add Animated)
   :and (.-and Animated)
   :asin (.-asin Animated)
   :atan (.-atan Animated)
   :block (.-block Animated)
   :call (.-call Animated)
   :ceil (.-ceil Animated)
   :clockRunning (.-clockRunning Animated)
   :color (.-color Animated)
   :concat (.-concat Animated)
   :cond (.-cond Animated)
   :cos (.-cos Animated)
   :debug (.-debug Animated)
   :defined (.-defined Animated)
   :diff (.-diff Animated)
   :diffClamp (.-diffClamp Animated)
   :divide (.-divide Animated)
   :eq (.-eq Animated)
   :event (.-event Animated)
   :exp (.-exp Animated)
   :floor (.-floor Animated)
   :greaterOrEq (.-greaterOrEq Animated)
   :greaterThan (.-greaterThan Animated)
   :interpolate (.-interpolate Animated)
   :lessOrEq (.-lessOrEq Animated)
   :lessThan (.-lessThan Animated)
   :log (.-log Animated)
   :max (.-max Animated)
   :min (.-min Animated)
   :modulo (.-modulo Animated)
   :multiply (.-multiply Animated)
   :neq (.-neq Animated)
   :not (.-not Animated)
   :onChange (.-onChange Animated)
   :or (.-or Animated)
   :pow (.-pow Animated)
   :proc (.-proc Animated)
   :round (.-round Animated)
   :set (.-set Animated)
   :sin (.-sin Animated)
   :sqrt (.-sqrt Animated)
   :startClock (.-startClock Animated)
   :stopClock (.-stopClock Animated)
   :sub (.-sub Animated)
   :tan (.-tan Animated)})

(defn animation
  "Compiles a Animated animation from a vector of nodes.
   This saves us having to use interop for everything, and instead we can use cljs data."
  [x]
  (cond
    (and (vector? x) (keyword? (first x))) (let [[n & args] x]
                                             (.apply (get nodes n) (get nodes n) (to-array (remove nil? (map animation args)))))
    (vector? x) (to-array (remove nil? (map animation x)))
    :else x))

(defn initial-clock-state
  "Returns a clojure map for initial clock state"
  []
  #js {:finished (Value. 0)
       :position (Value. 0)
       :time (Value. 0)
       :frameTime (Value. 0)})

(defn runClock
  "Animated block that starts a clock if not started, updates it when running, and stops when finished"
  [opts]
  (let [{:keys [clock clock-state config callback]} opts]
    [:block
     [[:cond
       [:not [:clockRunning clock]]
       [[:set (.-finished clock-state) 0]
        [:set (.-time clock-state) 0]
        [:set (.-position clock-state) 0]
        [:set (.-frameTime clock-state) 0]
        [:startClock clock]]]
      [:timing clock clock-state config]
      [:debug "position" (.-position clock-state)]
      [:cond (.-finished clock-state)
       [:block
        [(when callback
           [:call
            [(.-finished clock-state)]
            callback])
         [:stopClock clock]]]]
      (.-position clock-state)]]))

(defn useAnimation
  [node deps]
  (let [result (animation node)]
    (useCode result (to-array deps))))
