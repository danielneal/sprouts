(ns sprouts.entry-point
  (:require
   [shadow.expo :as shadow-expo]
   [dreams.app :as dreams]
   #_[buoyancy.app :as buoyancy]
   [helix.core :refer [defnc $ $$ <>]]
   [sprouts.native :as n]
   [sprouts.style6 :as style6 :refer [s]]
   [cljs-bean.core :refer [->clj ->js]]
   [hx.react :as hx]
   ["react" :as react]
   ["react-native" :as rn]
   ["expo" :as expo :refer [SplashScreen]]
   [goog.math :as math]))

#_(defnc Tile
    [props]
    (let [s (useStyle)
          {:keys [text]} props]
      (n/View {:style (s [:bg-brand1 :h8 :min-w8 :max-w12 :jcc :aic])}
        (n/Text {:style (s [:ui0])}
          text))))

#_(defnc Grid
    [props]
    (let [{:keys [gutter max-width items]
           :or {gutter 8
                max-width 150}} props
          s (useStyle)
          [layout setLayout] (react/useState nil)
          columns (math/safeCeil (/ (:width layout) max-width))
          width (int (/ (:width layout) columns))]
      (println "rerendering" layout columns width)
      (n/View {:style (s [:fww :fg1 :fdr])
               :onLayout (fn [^js e]
                           (setLayout (->clj (.-layout (.-nativeEvent e)))))}
        (for [item items
              :let [{:keys [id]} item]]
          ^{:key id}
          (n/View {:style #js {:width width
                               :height width
                               :backgroundColor "#FFFFFF"}}
            (n/Text (pr-str item)))))))

(defnc App
  [props]
  (n/View {:style (s [:jcsa :bg-ui0 :b-brand1 :w100 :h100])}))

(defn start

  "Entry point for ui, called every hot reload"
  {:dev/after-load true}
  []
  (shadow-expo/render-root
    ($ dreams/App)))

(defn init
  "Initialization function, called once at app start up"
  []
  (start))
