(ns buoyancy.app
  (:require
   ["react-native" :as rn]
   ["react" :as react]
   ["react-native-gesture-handler" :as gh]
   ["scheduler" :refer
    [unstable_scheduleCallback
     unstable_UserBlockingPriority
     unstable_NormalPriority
     unstable_LowPriority
     unstable_next]]
   ["react-native-reanimated"
    :refer
    [Easing
     Easing.inOut
     Easing.ease
     Transition
     Transition.Together
     Transition.Change
     Transition.Sequence
     Transition.In
     Transition.Out
     Transitioning
     Transitioning.View]
    :default Animated]
   [sprouts.animated :as animated]
   [sprouts.firebase :as firebase]
   [hx.react :as hx :refer [defnc]]
   [hx.hooks :as hooks]
   [buoyancy.channel :as channel]
   [buoyancy.ui :as ui :refer [s]]
   [clojure.set :as set]
   [cljs-bean.core :refer [->clj ->js]]))

(def currentChannel (react/createContext))

(defnc LoadingChannel
  []
  [rn/View {:style (s [:fg1 :aic :jcc])}
   [rn/Text {:style (s [:f5 :ui0 :mr3])}
    "Loading..."]])

(defnc Statements
  [props]
  (let [{:keys [topic-id]} props
        [{:keys [statements-by-topic]} _] (react/useContext currentChannel)
        statements (get statements-by-topic topic-id)]
    [rn/View
     (for [statement statements
           :let [{:keys [:firebase/id points title]} statement]]
       [rn/View {:key id
                 :style (s [:fdr :mb2 :aic])}
        [rn/View {:style (s [:w3 :h2 :b-ui0 :bw1 :br2 :aic :jcc :mr2])}
         [rn/Text {:style (s [:f6 :fwb])}
          points]]
        [rn/Text {:key id
                  :style (s [:ui0 :f5])}
         title]])]))

(defnc AddTopic
  [props]
  (let [[{:keys [channel-id channel-ref]} dispatch] (react/useContext currentChannel)]
    [rn/View {:style (s [:w100 :aic :jcc :pv3])}
     [rn/TouchableOpacity {:style (s [:br3 :bw1 :bg-ui2 :pv3 :w10 :aic :jcc])
                           :onPress (fn []
                                      (dispatch {:action/type :topic/edit}))}
      [rn/Text {:style (s [:f4 :fwb :ui1])}
       "Add topic"]]]))

(defnc Topic
  [props]
  (let [{:keys [topic]} props
        [_ dispatch] (react/useContext currentChannel)
        {:keys [firebase/id title]} topic]
    [rn/TouchableOpacity {:key id
                          :onPress (fn []
                                     (dispatch {:action/type :topic/edit
                                                :topic-id id}))
                          :style (s [:ui0 :pa2 :bbw1 :b-ui0])}
     [rn/Text {:style (s [:ui0 :fwb :f4 :mb3])} title]
     [Statements {:topic-id id}]]))

(defnc Topics
  [props]
  (let [[{:keys [topics]} _] (react/useContext currentChannel)]
    [rn/ScrollView {:style (s [:fg1 :ph2 :h0])
                    :showsVerticalScrollIndicator false}
     (for [topic (->> topics
                      (sort-by :points)
                      (reverse))]
       [Topic {:topic topic}])
     [AddTopic]]))

(defnc ChannelTitle
  [props]
  (let [[channel _] (react/useContext currentChannel)
        title (get-in channel [:channel :title])]
    [rn/Text {:style (s [:ui0 :f3 :fwb :tc :pb2])}
     title]))

(defnc StatementSelector
  [props]
  (let [[{:keys [popup/content statements-by-topic]} dispatch] (react/useContext currentChannel)
        {:keys [edit-topic/topic-id edit-topic/selected-statement]} content
        statements (get statements-by-topic topic-id)]
    [rn/ScrollView {:style (s [:h16])}
     (for [{:keys [:firebase/id title points]} statements]
       [rn/TouchableOpacity {:key id
                             :onPress #(dispatch {:action/type :statement/select
                                                  :statement/id id})
                             :style (s [:fdr])}
        [rn/Text {:style (if (= id selected-statement)
                           (s [:ui1])
                           (s [:ui0]))}
         title]])]))

(def props= (fn [a b]
              (= (->clj a) (->clj b))))
(defnc Points
  [props]
  {:wrap [(react/memo props=)]}
  (let [{:keys [n width on-select]} props
        index (animated/Value. nil)
        callback (react/useCallback (fn [args]
                                      (unstable_scheduleCallback
                                        (fn []
                                          (on-select (aget args 0)))
                                        unstable_UserBlockingPriority))
                                    #js [])
        on-scroll (animated/event (->js [{:nativeEvent {:contentOffset {:x (fn [index]
                                                                             (animated/animation
                                                                               [:block
                                                                                [[:set index [:ceil [:divide index width]]]
                                                                                 [:onChange index [:call [index] callback]]]]))}}}]))]
    [animated/ScrollView {:horizontal true
                          :scrollEventThrottle 16
                          :onScroll on-scroll
                          :showsHorizontalScrollIndicator false
                          :contentContainerStyle (s [:fdr :aic])
                          :style (s [:ass :w100 :h3])}
     [rn/View {:style (s [:aic :fdr])}
      (for [i (range n)]
        [rn/View {:key i
                  :style (merge (s [:aic :jcc])
                                {:width width})}
         [rn/Text {:style (s [:ph2 :f3 :ui1 :fwb])}
          (str i)]])]]))

(defnc PointsSelector
  [props]
  (let [[selected setSelected] (hooks/useState nil)
        on-select (react/useCallback (fn [i]
                                       (setSelected i))
                                     #js [])]
    [rn/View {:style (s [:w100 :aic])}
     [rn/Text {:style (s [:ui1 :f2 :fwb])}
      (str selected)]
     [Points {:n 100 :width 60 :on-select on-select}]]))

(defnc EditTopic
  [props]
  (let [[{:keys [popup/content topics-by-id statements-by-topic]} dispatch] (react/useContext currentChannel)
        {:keys [edit-topic/topic-id]} content
        topic (get topics-by-id topic-id)
        {:keys [title]} topic]
    [rn/View {:style (s [:absolute :bg-ui2 :bottom0 :w100 :pa3])}
     [rn/Text {:style (s [:ui1 :f4 :fwb :tc])} title]
     [StatementSelector]
     [PointsSelector]
     [rn/View {:style (s [:w100 :aic])}
      [rn/TouchableOpacity {:onPress (fn []
                                       (dispatch {:action/type :topic/cancel-edit}))}
       [rn/Text {:style (s [:ui1])}
        "Cancel"]]]]))

(def slide-in-out
  (hx/f [Transition.Together
         [Transition.Out {:type "slide-bottom"
                          :durationMs 200
                          :interpolation "easeInOut"}]
         [Transition.In {:type "slide-bottom"
                         :durationMs 200
                         :interpolation "easeInOut"}]]))
(defnc Popup
  []
  (let [[{:keys [popup/active?]} _] (react/useContext currentChannel)
        ref (react/useRef)]
    (react/useLayoutEffect (fn []
                             (.animateNextTransition (.-current ^js ref)))
                           #js [active?])
    [rn/View {:style (s [:absolute-fill])
              :pointerEvents "box-none"}
     [ui/Overlay {:overlay/active? active?}]
     [Transitioning.View {:ref ref
                          :style (s [:absolute-fill])
                          :pointerEvents "box-none"
                          :transition slide-in-out}
      (when active?
        [EditTopic])]]))

(defnc Channel
  [props]
  (let [{:keys [channel-id]} props
        [{:keys [loading?]} _ :as channel] (channel/useChannel {:channel-id channel-id})
        ref (react/useRef)]
    [:provider {:value channel
                :context currentChannel}
     (if loading?
       [LoadingChannel]
       [rn/View {:style (s [:pt4 :fg1])}
        [ChannelTitle]
        [Topics]
        [Popup]])]))

(def firebase-config
  #js {:apiKey "AIzaSyBHCE5FdTTFdTBYdbAUuOTrkAmSXxzMM4w"
       :authDomain "https://buoyancy-riv.firebaseio.com"
       :databaseURL "https://buoyancy-riv.firebaseio.com"
       :storageBucket "buoyancy-riv.appspot.com"
       :projectId "buoyancy-riv"})

(defnc App []
  (let [[ready setReady] (react/useState)]
    (hooks/useEffect
      (fn []
        (firebase/init firebase-config)
        (setReady true))
      [])
    [rn/View {:style (s [:bg-ui1 :h100 :w100])}
     (when ready
       [Channel {:channel-id "E5SIvw394mraPudqdHET"}])]))
