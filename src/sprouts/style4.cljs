(ns sprouts.style4
  "Style system based on tachyons"
  (:require [cljs-bean.core :as bean])
  (:require-macros [sprouts.style4]))

(declare *current-theme*)
(declare registry)

(defn s*
  "Function for dynamic styles"
  [args]
  (bean/->js
    (reduce (fn [acc k]
              (merge acc
                (cond
                  (keyword? k) (get-in registry [*current-theme* k])
                  (map? k) k)))
      {}
      args)))
