(ns dreams.app
  (:require [sprouts.async-storage :as async-storage]
            [sprouts.style :as s]
            [sprouts.revealer :as r :refer [Revealer]]
            [dreams.ui :as ui :refer [s]]
            [dreams.questions :as q]
            [cljs-bean.core :refer [->clj]]
            [hx.react :as hx :refer [defnc]]
            ["react-native" :as rn]
            ["react" :as react]
            ["@react-navigation/core" :refer [useNavigation useRoute]]
            ["@react-navigation/native" :refer [NavigationNativeContainer]]
            ["@react-navigation/stack" :refer [createStackNavigator]]
            ["react-native-safe-area-context" :refer [useSafeArea]]
            ["react-native-gesture-handler" :as gh]
            ["react-native-gesture-handler/Swipeable" :default Swipeable]
            ["date-fns" :as d]
            ["@expo/vector-icons" :refer [Entypo Ionicons]]
            ["react" :as react]
            ["react-native" :as rn]))

(def diary-context
  (react/createContext))

(defn useDiary
  "Not ideal storing this all in one lot, but just a demo"
  []
  (let [[diary setDiary] (async-storage/useAsyncStorage {:k "diary3"})]
    {:diary diary
     :record-dream (fn [dream]
                     (setDiary (conj (or diary []) dream)))
     :remove-dream (fn [dream]
                     (setDiary (into [] (remove #(= % dream)) diary)))
     :edit-dream (fn [dream]
                   (setDiary (mapv (fn [x]
                                     (if (= (:recorded-at x)
                                            (:recorded-at dream))
                                       dream
                                       x)) diary)))}))

(defnc HelpButtonOverlay
  []
  (let [navigation (useNavigation)]
    [rn/View {:style (s [:absolute-fill :aife :jcfe])
              :pointerEvents "box-none"}
     [rn/View {:style (s [:pa3])}
      [ui/HelpButton {:onPress (fn []
                                 (.navigate navigation "dream-questions"))}]]]))

(def Stack (createStackNavigator))

(defnc AddDreamCta
  []
  (let [navigation (useNavigation)]
    [rn/View {:style (s [:pa4 :aic])}
     [ui/TextButton
      {:onPress (fn []
                  (.navigate navigation "dream-form" #js {:action :add-dream
                                                          :title "Add Dream"}))
       :text "Record Dream"}]]))

(defnc ModalScreen
  [props]
  (let [{:keys [style children title]
         :or {style :modal1}} props
        insets (useSafeArea)
        [backgroundColor textColor] (case style
                                      :modal1 [:bg-ui0 :ui1]
                                      :modal2 [:bg-brand0 :ui1])]
    [rn/View {:style (merge {:paddingTop (.-top insets)}
                            (s [:absolute-fill backgroundColor]))}
     [rn/Text {:style (s [textColor :f3 :tc :pb3])}
      title]
     [:<> children]
     [ui/TopRightCloseButtonLight]
     [HelpButtonOverlay]]))

(defnc DreamHeader
  []
  [rn/View {:style [:aic :fg1]}
   [rn/Text {:style (s [:ui1 :f3 :tc :pb3])}
    "Dream Diary"]])

(defn format-date
  "Formats a date & time using date-fns"
  [d]
  (d/format d "do MMMM yyyy HH':'mm"))

(def incidents
  [{:id :dream
    :text "Dream"}
   {:id :mistake
    :text "Mistake"}
   {:id :pang
    :text "Pang"}])

(def incidents-by-id
  (into {} (map (juxt :id identity)) incidents))

(defnc IncidentSelector
  [props]
  (let [{:keys [onSelect incident]} props]
    [ui/SelectorButtons
     {:onSelect onSelect
      :selected #{incident}
      :items incidents}]))

(defnc DreamForm
  [props]
  (let [route (useRoute)
        navigation (useNavigation)
        {:keys [params]} (->clj route)
        {:keys [title dream disable action]} params
        {:keys [edit-dream diary record-dream]} (react/useContext diary-context)
        [dreamState setDreamState] (react/useState (or dream
                                                       {:recorded-at (js/Date.)
                                                        :content ""
                                                        :analysis ""
                                                        :incident :dream}))
        {:keys [incident recorded-at content analysis]} dreamState
        changeContent (react/useCallback
                        (fn [text]
                          (setDreamState (fn [state]
                                           (assoc state :content text)))))
        changeAnalysis (react/useCallback
                         (fn [text]
                           (setDreamState (fn [state]
                                            (assoc state :analysis text)))))
        selectIncident (react/useCallback
                         (fn [incident]
                           (setDreamState (fn [state]
                                            (assoc state :incident incident)))))
        onSubmit (react/useCallback
                   (fn []
                     (println dreamState)
                     (case action
                       (:analyse-dream :edit-dream) (edit-dream dreamState)
                       :add-dream (record-dream dreamState))
                     (.goBack navigation))
                   #js [action dreamState])
        ContentField (case action
                       :analyse-dream [ui/Text1 content]
                       [ui/MultilineTextInput
                        {:numberOfLines 10
                         :multiline true
                         :defaultValue content
                         :onChangeText changeContent}])
        AnalysisField [ui/MultilineTextInput
                       {:numberOfLines 10
                        :multiline true
                        :defaultValue analysis
                        :onChangeText changeAnalysis}]
        SubmitButton [ui/TextButton
                      {:onPress onSubmit
                       :text "Save Dream"}]]
    [ModalScreen {:title title
                  :style :modal2}
     [r/Revealer {:style (s [:ph2 :pv3])}
      [rn/Text {:style (s [:ui1 :f4 :pb2])}
       "Date"]
      [rn/Text {:style (s [:ui1 :fwb :f5 :pb4])}
       (format-date recorded-at)]
      [rn/Text {:style (s [:ui1 :f4 :pb2])}
       "Incident"]
      [rn/View {:style (s [:pb4])}
       [IncidentSelector {:onSelect selectIncident
                          :incident incident}]]
      [rn/Text {:style (s [:ui1 :f4 :pb2])}
       "Content"]
      [rn/View {:style (s [:pb4])}
       ContentField]
      [rn/Text {:style (s [:ui1 :f4 :pb2])}
       "Analysis"]
      [rn/View {:style (s [:pb4])}
       AnalysisField]
      [rn/View {:style (s [:aic :pb6 :jcc])}
       SubmitButton]]]))

(defnc EmptyState
  []
  [rn/View {:style (s [:jcc :aic])}
   [rn/View {:style (s [:pb2 :aic :jcc])}
    [Ionicons {:name "ios-cloudy-night"
               :size 150
               :style (s [:ui1])}]]
   [rn/Text {:style (s [:ui1 :f4 :tc :pb1])}
    "No dreams yet?"]
   [rn/Text {:style (s [:ui1 :f5 :tc])}
    "Crack out the cocoa, \n it's time for a good old snooze..."]])

(def dream-context
  (react/createContext))

(defnc DreamActions
  [props]
  (let [{:keys [edit-dream remove-dream analyse-dream]} props]
    [rn/View {:style (s [:fg1 :fdr :bg-ui1 :jcfe :aic])}
     [rn/TouchableOpacity {:style (s [:aic :pa2])
                           :onPress analyse-dream}
      [Ionicons {:name "md-clipboard"
                 :size 50
                 :style (s [:ui0])}]
      [rn/Text "Analyse dream"]]
     [rn/TouchableOpacity {:style (s [:aic :pa2])
                           :onPress edit-dream}
      [Ionicons {:name "ios-brush"
                 :size 50
                 :style (s [:ui0])}]
      [rn/Text "Edit dream"]]
     [rn/TouchableOpacity {:style (s [:aic :pa2])
                           :onPress remove-dream}
      [Ionicons {:name "ios-trash"
                 :size 50
                 :style (s [:ui0])}]
      [rn/Text "Delete dream"]]]))

(defnc Dream
  [dream]
  (let [{:keys [recorded-at incident content analysis]} dream
        {:keys [remove-dream]} (react/useContext diary-context)
        navigation (useNavigation)
        swipeable (react/useRef)
        remove-dream (react/useCallback
                       (fn []
                         (remove-dream dream)
                         (.close (.-current swipeable)))
                       #js [dream])
        edit-dream (react/useCallback
                     (fn []
                       (.navigate navigation "dream-form" {:dream dream
                                                           :action :edit-dream
                                                           :title "Edit Dream"})
                       (.close (.-current swipeable)))
                     #js [dream])
        analyse-dream (react/useCallback
                        (fn []
                          (.navigate navigation "dream-form" {:dream dream
                                                              :action :analyse-dream
                                                              :title "Analyse Dream"})
                          (.close (.-current swipeable)))
                        #js [dream])
        renderRightActions (react/useCallback
                             (fn []
                               (hx/f [DreamActions
                                      {:analyse-dream analyse-dream
                                       :remove-dream remove-dream
                                       :edit-dream edit-dream}]))
                             #js [remove-dream edit-dream analyse-dream])
        {:keys [text]} (get incidents-by-id incident)
        HeadingText [rn/View {:style (s [:pb2])}
                     [ui/Text2 text]
                     [ui/Text1 (format-date recorded-at) ]]
        ContentText [ui/Text1 {:numberOfLines 3}
                     content]]
    [Swipeable {:renderRightActions renderRightActions
                :ref swipeable}
     [gh/TouchableOpacity {:style (s [:bg-brand0 :btw1 :bbw1 :b-ui1 :pa3])
                           :onPress analyse-dream}
      HeadingText
      ContentText]]))

(defnc DreamsList
  []
  (let [{:keys [diary]} (react/useContext diary-context)]
    [gh/FlatList
     {:showsVerticalScrollIndicator false
      :keyExtractor (fn [item]
                      (let [{:keys [recorded-at]} (->clj item)]
                        (str recorded-at)))
      :data (some-> diary rseq to-array)
      :renderItem (fn [^js obj]
                    (let [{:keys [item]} (->clj obj)]
                      (hx/f [Dream item])))}]))

(defnc Diary
  [props]
  (let [insets (useSafeArea)
        {:keys [diary]} (react/useContext diary-context)]
    [rn/View {:style (merge (s [:absolute-fill :bg-brand0 :ph2])
                            {:paddingTop (.-top insets)})}
     [DreamHeader]
     (if (seq diary)
       [DreamsList]
       [EmptyState])
     [AddDreamCta]
     [HelpButtonOverlay]]))

(defnc DreamQuestion
  [props]
  (let [{:keys [question notes]} props]
    [rn/View
     [rn/Text {:style (s [:ui1 :f5 :pb2])}
      question]
     [rn/Text {:style (s [:ui1 :f6 :pb4])}
      notes]]))

(defnc DreamQuestions
  [props]
  (let [insets (useSafeArea)]
    [ModalScreen {:style :modal1
                  :title "Dream Questions"}
     [rn/ScrollView {:style (s [:fg1 :pa3])}
      (for [question q/dream-questions]
        [DreamQuestion question])]]))

(defnc App
  []
  (let [ref (react/useRef)]
    [NavigationNativeContainer
     [:provider {:context diary-context
                 :value (useDiary)}
      [Stack.Navigator {:initialRouteName "diary"
                        :headerMode "none"
                        :mode "modal"}
       [Stack.Screen {:name "diary"
                      :component Diary}]
       [Stack.Screen {:name "dream-questions"
                      :component DreamQuestions}]
       [Stack.Screen {:name "dream-form"
                      :component DreamForm}]]]]))
