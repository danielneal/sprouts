(ns sprouts.style4
  "Style system based on tachyons"
  (:require-macros [sprouts.style4])
  (:require [cljs-bean.core :as bean]))

(def ^:dynamic *registry* nil)
(def ^:dynamic *current-theme* "no-preference")

(defn set-current-theme!
  "Sets the current style theme"
  [theme]
  (set! *current-theme* theme))

(defn s*
  "Function for dynamic styles"
  [args]
  (bean/->js
    (reduce (fn [acc k]
              (merge acc
                (cond
                  (keyword? k) (get-in *registry* [*current-theme* k])
                  (map? k) k)))
      {}
      args)))
