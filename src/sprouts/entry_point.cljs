(ns sprouts.entry-point
  (:require
   [shadow.expo :as shadow-expo]
   [hx.react :as hx :refer [defnc]]
   [hx.hooks :as hooks]
   [dreams.app :as dreams]
   [buoyancy.app :as buoyancy]
   ["expo" :as expo :refer [SplashScreen]]))

(defn start
  "Entry point for ui, called every hot reload"
  {:dev/after-load true}
  []
  (shadow-expo/render-root (hx/f [buoyancy/App])))

(defn init
  "Initialization function, called once at app start up"
  []
  (start))
