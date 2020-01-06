(ns sprouts.entry-point
  (:require
   [shadow.expo :as shadow-expo]
   [dreams.app :as dreams]
   [buoyancy.app :as buoyancy]
   [helix.core :refer [defnc $ $$ <>]]
   [sprouts.ui :as ui]
   [sprouts.style2 :as style2]
   [cljs-bean.core :refer [->clj ->js]]
   [hx.react :as hx]
   [dreams.app :as dreams]
   [buoyancy.app :as buoyancy]
   ["react" :as react]
   ["react-native" :as rn]
   ["expo" :as expo :refer [SplashScreen]]
   [goog.math :as math]))

(def useStyle
  (style2/init
    {:palettes {"dark" {:ui0 "#000000"
                        :ui1 "#FFFFFF"
                        :brand1 "#FFAA00"}
                "light" {:ui0 "#FFFFFF"
                         :ui1 "#000000"
                         :brand1 "#FFAA00"}}
     :rem 15
     :font-rem 15}))

(defnc Tile
  [props]
  (let [s (useStyle)
        {:keys [text]} props]
    (ui/View {:style (s [:bg-brand1 :h8 :min-w8 :max-w12 :jcc :aic])}
      (ui/Text {:style (s [:ui0])}
        text))))

(defnc Grid
  [props]
  (let [{:keys [gutter max-width items]
         :or {gutter 8
              max-width 150}} props
        s (useStyle)
        [layout setLayout] (react/useState nil)
        columns (math/safeCeil (/ (:width layout) max-width))
        width (int (/ (:width layout) columns))]
    (println "rerendering" layout columns width)
    (ui/View {:style (s [:fww :fg1 :fdr])
              :onLayout (fn [^js e]
                          (setLayout (->clj (.-layout (.-nativeEvent e)))))}
      (for [item items
            :let [{:keys [id]} item]]
        ^{:key id}
        (ui/View {:style #js {:width width
                              :height width
                              :backgroundColor "#FFFFFF"}}
          (ui/Text (pr-str item)))))))

(defnc App
  []
  (let [s (useStyle)]
    (ui/View {:style #js {:backgroundColor "#EEAABB"
                          :width "100%"
                          :height "100%"
                          :top 0
                          :left 0
                          :bottom 0
                          :right 0
                          :position "absolute"}}
      (ui/Text {:style (s [:pt3])}
        "oi oi oi")
      (ui/Text {:style (s [:pt3])}
        "bobobo")
      (ui/Text {:style (s [:bg-ui0])}
        "Some text")
      ($ Grid {:items
               [{:id 1
                 :text "This"}
                {:id 2
                 :text "That"}
                {:id 3
                 :text "The other"}
                {:id 4
                 :text "basdfas"}
                {:id 5
                 :text "Asdf"}]})
      (ui/TouchableOpacity {:style #js {:backgroundColor "#FFAAFF"}}
        (ui/ActivityIndicator {:size  "large"
                               :color "red"})))))

(defn start
  "Entry point for ui, called every hot reload"
  {:dev/after-load true}
  []
  (shadow-expo/render-root ;;($ App)
    (hx/f [dreams/App])))

(defn init
  "Initialization function, called once at app start up"
  []
  (start))
