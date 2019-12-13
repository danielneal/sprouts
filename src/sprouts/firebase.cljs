(ns sprouts.firebase
  (:require ["firebase" :as firebase]
            ["firebase/firestore" :as firestore]
            [hx.hooks :as hooks]
            [cljs-bean.core :refer [->clj ->js]]))

(defn ref
  [opts]
  (let [{:keys [collection doc]} opts]
    (-> (.firestore firebase)
        (.collection collection)
        (.doc doc))))

(defn useDocument
  [opts]
  (let [{:keys [collection doc]} opts
        [state setState] (hooks/useState [true nil])]
    (hooks/useEffect
      (fn []
        (println "listening to " collection doc)
        (let [listener (-> (.firestore firebase)
                           (.collection collection)
                           (.doc doc)
                           (.onSnapshot (fn [obj]
                                          (setState [false (merge (->clj (.data obj))
                                                                  {:firebase/id (.-id obj)})]))))]
          (fn []
            (println "removing listener for " collection doc)
            (listener))))
      [collection doc])
    state))

(defn useCollection
  [opts]
  (let [{:keys [collection query]} opts
        [state setState] (hooks/useState [true nil])
        addDocument (fn [doc]
                      (-> (.firestore firebase)
                          (.collection collection)
                          (.add doc)))]
    (hooks/useEffect
      (fn []
        (println "listnening to " collection)
        (let [o1 (-> (.firestore firebase)
                     (.collection collection))
              _ (println o1)
              o2 (reduce (fn [acc clause]
                           (println (to-array clause))
                           (.apply ^js (.-where acc) acc (to-array clause))) o1 query)
              _ (println o2)
              listener (.onSnapshot o2 (fn [obj]
                                         (println obj)
                                         (let [ret (volatile! #{})]
                                           (.forEach obj (fn [obj]
                                                           (vswap! ret conj (merge (->clj (.data obj))
                                                                                   {:firebase/id (.-id obj)}))))
                                           (setState [false @ret]))))]

          (fn []
            (println "removing listener for " collection)
            (listener))))
      [collection])
    (conj state addDocument)))

(defn init
  [config]
  (.initializeApp firebase config))
