(ns sprouts.entry-point
  (:require
   [shadow.expo :as shadow-expo]
   [dreams.app :as dreams]
   [buoyancy.app :as buoyancy]
   [helix.core :refer [defnc $ $$ <>]]
   [sprouts.native :as n]
   [sprouts.style4 :as style4 :refer [s]]
   [cljs-bean.core :refer [->clj ->js]]
   [hx.react :as hx]
   [dreams.app :as dreams]
   [buoyancy.app :as buoyancy]
   ["react" :as react]
   ["react-native" :as rn]
   ["expo" :as expo :refer [SplashScreen]]
   [goog.math :as math]))

#_(style4/init!
    {:themes
     {"dark" {:palette {:ui0 "#EFAAEF"
                        :ui1 "#FFFFFF"
                        :brand1 "#FFAA00"}
              :rem 15
              :font-rem 15}
      "light" {:palette {:ui0 "#DDDD00"
                         :ui1 "#000000"
                         :brand1 "#FFAA00"}
               :rem 15
               :font-rem 15}}
     :default-theme "light"})

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

#_(defnc App
    []
    (let [s (useStyle)]
      (n/View {:style #js {:backgroundColor "#EEAABB"
                           :width "100%"
                           :height "100%"
                           :top 0
                           :left 0
                           :bottom 0
                           :right 0
                           :position "absolute"}}
        (n/Text {:style (s [:pt3])}
          "oi oi oi")
        (n/Text {:style (s [:pt3])}
          "bobobo")
        (n/Text {:style (s [:bg-ui0])}
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
        (n/TouchableOpacity {:style #js {:backgroundColor "#FFAAFF"}}
          (n/ActivityIndicator {:size  "large"
                                :color "red"})))))

(defnc App3
  []
  (let [st (s [:bg-brand1 :w100 :h100 :absolute])]
    (n/View
      {:style st})))

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
