(ns buoyancy.ui
  (:require [sprouts.style :as style]
            [sprouts.animated :as animated]
            [hx.react :as hx :refer [defnc]]
            ["react" :as react]))

(def s
  (style/init
    {:palette {:ui0 "#333344"
               :ui1 "#FFFFEE"
               :ui2 "#551199"
               :status0 "#5b9851"
               :status1 "#f5a623"
               :status2 "#b5071c"}
     :rem 15}))

(defnc Overlay
  [props]
  (let [{:keys [overlay/active?]} props
        ref (react/useRef (animated/Value. 0))
        opacity (.-current ref)]
    (react/useEffect (fn []
                       (let [anim (animated/timing opacity #js {:toValue (if active? 0.8 0)
                                                                :duration 200
                                                                :easing animated/in-out})]
                         (.start anim)))
                     #js [active?])
    [animated/View {:style (merge (s [:absolute-fill :bg-ui0])
                                  {:opacity opacity})
                    :pointerEvents (if active? "auto" "none")}]))
