(ns sprouts.native
  (:require [helix.core :as hx]
            #?(:cljs ["react-native" :as rn]))
  #?(:cljs (:require-macros [sprouts.native])))

(def tags
  '[ActivityIndicator
    Button
    DatePickerIOS
    DrawerLayoutAndroid
    FlatList
    Image
    ImageBackground
    InputAccessoryView
    KeyboardAvoidingView
    MaskedViewIOS
    Modal
    Picker
    PickerIOS
    ProgressBarAndroid
    ProgressViewIOS
    RefreshControl
    SafeAreaView
    ScrollView
    SectionList
    SegmentedControlIOS
    Slider
    StatusBar
    Switch
    TabBarIOS
    Text
    TextInput
    ToolbarAndroid
    TouchableHighlight
    TouchableNativeFeedback
    TouchableOpacity
    TouchableWithoutFeedback
    View
    ViewPagerAndroid
    VirtualizedList])

#?(:clj
   (defn gen-tag
     [t]
     `(defmacro ~t [& args#]
        `(hx/$ ~(symbol "rn" ~(str t))
           ~@args#))))

#?(:clj
   (defmacro gen-tags
     []
     `(do
        ~@(for [t tags]
            (gen-tag t)))))

#?(:clj
   (gen-tags))
