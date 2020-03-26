(ns dreams.app
  (:require [sprouts.async-storage :as async-storage]
            [sprouts.native :as n]
            [sprouts.revealer :as r :refer [Revealer]]
            [sprouts.style6 :as style6 :refer [s]]
            [dreams.ui :as ui]
            [dreams.questions :as q]
            [cljs-bean.core :refer [->clj]]
            [helix.core :as helix :refer [$$ $ <> defnc]]
            [helix.hooks :as hooks]
            ["react-native" :as rn]
            ["react" :as react]
            ["@react-navigation/core" :refer [useNavigation useRoute]]
            ["@react-navigation/native" :refer [NavigationContainer]]
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
    (n/View {:style (s [:absolute-fill :aife :jcfe])
             :pointerEvents "box-none"}
      (n/View {:style (s [:pa3])}
        ($ ui/HelpButton {:onPress (fn []
                                     (.navigate navigation "dream-questions"))})))))


(defnc AddDreamCta
  []
  (let [navigation (useNavigation)]
    (n/View {:style (s [:pa4 :aic])}
      ($ ui/TextButton
        {:onPress (fn []
                    (.navigate navigation "dream-form" {:action :add-dream
                                                        :title "Add Dream"}))
         :text "Record Dream"}))))

(defnc ModalScreen
  [props]
  (let [{:keys [style children title]
         :or {style :modal1}} props
        insets (useSafeArea)
        [backgroundColor textColor] (case style
                                      :modal1 [:bg-ui0 :ui1]
                                      :modal2 [:bg-brand0 :ui1])]
    (n/View {:style (s [:absolute-fill backgroundColor {:paddingTop (.-top insets)}])}
      (n/Text {:style (s [textColor :f3 :tc :pb3])}
        title)
      (<> children)
      ($ ui/TopRightCloseButtonLight)
      ($ HelpButtonOverlay))))

(defnc DreamHeader
  []
  (n/View {:style [:aic :fg1]}
    (n/Text {:style (s [:ui1 :f3 :tc :pb3])}
      "Dream Diary")))

(defn format-date
  "Formats a date & time using date-fns"
  [d]
  (d/format d "do MMMM yyyy HH':'mm"))

(def incidents
  [{:id :dream
    :text "Dream"}
   {:id :slip
    :text "Slip"}
   {:id :pain
    :text "Pain"}
   {:id :coincidence
    :text "Coincidence"}])

(def incidents-by-id
  (into {} (map (juxt :id identity)) incidents))

(defnc IncidentSelector
  [props]
  (let [{:keys [onSelect incident]} props]
    ($ ui/SelectorButtons
      {:onSelect onSelect
       :selected #{incident}
       :items incidents})))

(defnc DreamForm
  [props]
  (let [route (useRoute)
        navigation (useNavigation)
        {:keys [params]} (->clj route)
        {:keys [title dream disable action]} params
        {:keys [edit-dream diary record-dream]} (react/useContext diary-context)
        [dreamState setDreamState] (hooks/use-state (or dream
                                                      {:recorded-at (js/Date.)
                                                       :content ""
                                                       :analysis ""
                                                       :incident :dream}))
        {:keys [incident recorded-at content analysis]} dreamState
        changeContent (hooks/use-callback
                        :once
                        (fn [text]
                          (setDreamState (fn [state]
                                           (assoc state :content text)))))
        changeAnalysis (hooks/use-callback
                         :once
                         (fn [text]
                           (setDreamState (fn [state]
                                            (assoc state :analysis text)))))
        selectIncident (hooks/use-callback
                         :once
                         (fn [incident]
                           (setDreamState (fn [state]
                                            (assoc state :incident incident)))))
        onSubmit (hooks/use-callback
                   [action dreamState]
                   (fn []
                     (case action
                       (:analyse-dream :edit-dream) (edit-dream dreamState)
                       :add-dream (record-dream dreamState))
                     (.goBack navigation)))
        ContentField (case action
                       :analyse-dream ($ ui/Text1 content)
                       ($ ui/MultilineTextInput
                         {:numberOfLines 10
                          :multiline true
                          :defaultValue content
                          :onChangeText changeContent}))
        AnalysisField ($ ui/MultilineTextInput
                        {:numberOfLines 10
                         :multiline true
                         :defaultValue analysis
                         :onChangeText changeAnalysis})
        SubmitButton ($ ui/TextButton
                       {:onPress onSubmit
                        :text "Save Dream"})]
    ($ ModalScreen {:title title
                    :style :modal2}
      ($ Revealer {:style (s [:ph2 :pv3])}
        (n/Text {:style (s [:ui1 :f4 :pb2])}
          "Date")
        (n/Text {:style (s [:ui1 :fwb :f5 :pb4])}
          (format-date recorded-at))
        (n/Text {:style (s [:ui1 :f4 :pb2])}
          "Incident")
        (n/View {:style (s [:pb4])}
          ($ IncidentSelector {:onSelect selectIncident
                               :incident incident}))
        (n/Text {:style (s [:ui1 :f4 :pb2])}
          "Content")
        (n/View {:style (s [:pb4])}
          ContentField)
        (n/Text {:style (s [:ui1 :f4 :pb2])}
          "Analysis")
        (n/View {:style (s [:pb4])}
          AnalysisField)
        (n/View {:style (s [:aic :pb6 :jcc])}
          SubmitButton)))))

(defnc EmptyState
  []
  (println (s [:ui1 :f4 :tc]))
  (n/View {:style (s [:jcc :aic])}
    (n/View {:style (s [:pb2 :aic :jcc])}
      ($ Ionicons {:name "ios-cloudy-night"
                   :size 150
                   :style (s [:ui1])}))
    (n/Text {:style (s [:ui1 :f4 :tc :pb1])}
      "No dreams yet?")
    (n/Text {:style (s [:ui1 :f5 :tc])}
      "Crack out the cocoa, \n it's time for a good old snooze...")))

(def dream-context
  (react/createContext))

(defnc DreamActions
  [props]
  (let [{:keys [edit-dream remove-dream analyse-dream]} props
        AnalyseButton (n/TouchableOpacity {:style (s [:aic :pa2])
                                           :onPress analyse-dream}
                        ($ Ionicons {:name "md-clipboard"
                                     :size 50
                                     :style (s [:ui0])})
                        (n/Text "Analyse dream"))
        EditButton (n/TouchableOpacity {:style (s [:aic :pa2])
                                        :onPress edit-dream}
                     ($ Ionicons {:name "ios-brush"
                                  :size 50
                                  :style (s [:ui0])})
                     (n/Text "Edit dream"))
        DeleteButton (n/TouchableOpacity {:style (s [:aic :pa2])
                                          :onPress remove-dream}
                       ($ Ionicons {:name "ios-trash"
                                    :size 50
                                    :style (s [:ui0])})
                       (n/Text "Delete dream"))]
    (n/View {:style (s [:fg1 :fdr :bg-ui1 :jcfe :aic])}
      AnalyseButton
      EditButton
      DeleteButton)))

(defnc Dream
  [dream]
  (let [{:keys [recorded-at incident content analysis]} dream
        {:keys [remove-dream]} (react/useContext diary-context)
        {:keys [text]} (get incidents-by-id incident)
        navigation (useNavigation)
        swipeable (react/useRef)
        remove-dream (hooks/use-callback
                       [dream]
                       (fn []
                         (remove-dream dream)
                         (.close (.-current swipeable))))
        edit-dream (hooks/use-callback
                     [dream]
                     (fn []
                       (.navigate navigation "dream-form" {:dream dream
                                                           :action "edit-dream"
                                                           :title "Edit Dream"})
                       (.close (.-current swipeable))))
        analyse-dream (hooks/use-callback
                        [dream]
                        (fn []
                          (.navigate navigation "dream-form" {:dream dream
                                                              :action :analyse-dream
                                                              :title "Analyse Dream"})
                          (.close (.-current swipeable))))
        HeadingText (n/View {:style (s [:pb2])}
                      ($ ui/Text2 text)
                      ($ ui/Text1 (format-date recorded-at)))
        ContentText ($ ui/Text1 {:numberOfLines 3}
                      content)
        renderRightActions (hooks/use-callback
                             :once
                             (fn []
                               ($ DreamActions
                                 {:analyse-dream analyse-dream
                                  :remove-dream remove-dream
                                  :edit-dream edit-dream})))]
    ($ Swipeable {:renderRightActions renderRightActions
                  :ref swipeable}
      ($ gh/TouchableOpacity {:style (s [:bg-brand0 :btw1 :bbw1 :b-ui1 :pa3])
                              :onPress analyse-dream}
        HeadingText
        ContentText))))

(defnc DreamsList
  []
  (let [{:keys [diary]} (react/useContext diary-context)]
    ($ gh/FlatList
      {:showsVerticalScrollIndicator false
       :keyExtractor (fn [item]
                       (let [{:keys [recorded-at]} (->clj item)]
                         (str recorded-at)))
       :data (some-> diary rseq to-array)
       :renderItem (fn [^js obj]
                     (let [{:keys [item]} (->clj obj)]
                       ($ Dream {& item})))})))

(defnc Diary
  [props]
  (let [insets (useSafeArea)
        {:keys [diary]} (react/useContext diary-context)]
    (n/View {:style (s [:absolute-fill :bg-brand0 :ph2
                        {:paddingTop (.-top insets)}])}
      ($ DreamHeader)
      (if (seq diary)
        ($ DreamsList)
        ($ EmptyState))
      ($ AddDreamCta)
      ($ HelpButtonOverlay))))

(defnc DreamQuestion
  [props]
  (let [{:keys [question notes]} props]
    (n/View
      (n/Text {:style (s [:ui1 :f5 :pb2])}
        question)
      (n/Text {:style (s [:ui1 :f6 :pb4])}
        notes))))

(defnc DreamQuestions
  [props]
  (let [insets (useSafeArea)]
    ($ ModalScreen
      (n/ScrollView {:style (s [:fg1 :pa3])}
        (for [question q/dream-questions]
          ($ DreamQuestion {:key (:question question)
                            & question}))))))

(def Stack (createStackNavigator))

(defnc App
  []
  (let [ref (react/useRef)]
    ($ NavigationContainer
      (helix/provider {:context diary-context
                       :value (useDiary)}
        ($ Stack.Navigator {:initialRouteName "diary"
                            :headerMode "none"
                            :mode "modal"}
          ($ Stack.Screen {:name "diary"
                           :component Diary})
          ($ Stack.Screen {:name "dream-questions"
                           :component DreamQuestions})
          ($ Stack.Screen {:name "dream-form"
                           :component DreamForm}))))))
