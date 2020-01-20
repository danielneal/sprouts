(ns sprouts.style3
  "Style system based on tachyons"
  #?(:cljs (:require-macros [sprouts.style3])))

(defn flex
  "Flex properties"
  [opts]
  {:fg1 {"flexGrow" 1}
   :fs0 {"flexShrink" 0}
   :fdr {"flexDirection" "row"}
   :fdrr {"flexDirection" "row-reverse"}
   :fdcr {"flexDirection" "column-reverse"}
   :fww {"flexWrap" "wrap"}
   :aifs {"alignItems" "flex-start"}
   :aic {"alignItems" "center"}
   :aife {"alignItems" "flex-end"}
   :jcc {"justifyContent" "center"}
   :jcfs {"justifyContent" "flex-start"}
   :jcfe {"justifyContent" "flex-end"}
   :jcsb {"justifyContent" "space-between"}
   :jcsa {"justifyContent" "space-around"}
   :asfs {"alignSelf" "flex-start"}
   :asfe {"alignSelf" "flex-end"}
   :asc {"alignSelf" "center"}
   :ass {"alignSelf" "stretch"}
   :oh {"overflow" "hidden"}})

(defn spacing
  "Margin and padding properties
   ma0 ... ma10            margin: 0|0.25|0.5|1|2|3|4|5|6|7|8 rem
   ml|mr|mb|mt [0-10]      marginLeft, marginRight, marginBottom, marginTop
   mh [0-10]               marginHorizontal
   mv [0-10]               marginVertical"
  [opts]
  (let [{:keys [rem]} opts
        scale [["0" 0]
               ["1" 0.25]
               ["2" 0.5]
               ["3" 1]
               ["4" 2]
               ["5" 3]
               ["6" 4]
               ["7" 5]
               ["8" 6]
               ["9" 7]
               ["10" 8]
               ["11" 9]
               ["12" 10]]]
    (into {}
      (for [[pre prop] [["ma" "margin"]
                        ["ml" "marginLeft"]
                        ["mr" "marginRight"]
                        ["mt" "marginTop"]
                        ["mb" "marginBottom"]
                        ["mh" "marginHorizontal"]
                        ["mv" "marginVertical"]
                        ["pa" "padding"]
                        ["pl" "paddingLeft"]
                        ["pr" "paddingRight"]
                        ["pt" "paddingTop"]
                        ["pb" "paddingBottom"]
                        ["ph" "paddingHorizontal"]
                        ["pv" "paddingVertical"]]
            [s fac] scale]
        [(keyword (str pre s)) {prop (int (* fac rem))}]))))

(defn dims
  "Heights and widths
   h0 ... h16 0-16rem"
  [opts]
  (let [{:keys [rem]} opts
        scale [["0" 0]
               ["1" 1]
               ["2" 2]
               ["3" 3]
               ["4" 4]
               ["5" 5]
               ["6" 6]
               ["7" 7]
               ["8" 8]
               ["9" 9]
               ["10" 10]
               ["11" 11]
               ["12" 12]
               ["13" 13]
               ["14" 14]
               ["15" 15]
               ["16" 16]
               ["25" "25%"]
               ["50" "50%"]
               ["75" "75%"]
               ["100" "100%"]]]
    (into {}
      (for [[pre prop] [["h" "height"]
                        ["w" "width"]
                        ["max-h" "maxHeight"]
                        ["max-w" "maxWidth"]
                        ["min-h" "minHeight"]
                        ["min-w" "minWidth"]]
            [s x] scale]
        [(keyword (str pre s)) {prop (if (string? x) x (int (* x rem)))}]))))

(defn absolute
  "Absolute positioning and offsets
   absolute                     position: absolute
   top|right|bottom|left-0      top|right|bottom|left: 0 rem
                     ... 1                         ... 1 rem
                     ... 2                         ... 2 rem
   absolute-fill                position: absolute, top/left/right/bottom: 0  "
  [opts]
  (let [{:keys [rem]} opts
        scale [["0" 0]
               ["1" 1]
               ["2" 2]
               ["3" 3]
               ["4" 4]]]
    (into {:absolute {"position" "absolute"}
           :absolute-fill {"position" "absolute"
                           "top" 0
                           "left" 0
                           "right" 0
                           "bottom" 0}}
      (for [pre ["top" "right" "left" "bottom"]
            [s fac] scale]
        [(keyword (str pre s)) {s (int (* fac rem))}]))))

(defn border-width
  "Border width properties
   ba                     borderWidth: 1
   bl|br|bt|bb            borderLeftWidth: 1 | borderRightWidth: 1 ..."
  [opts]
  (let [scale [["0" 0]
               ["1" 1]
               ["2" 2 ]]]
    (into {}
      (for [[pre prop] [["bw" "borderWidth"]
                        ["blw" "borderLeftWidth"]
                        ["brw" "borderRightWidth"]
                        ["btw" "borderTopWidth"]
                        ["bbw" "borderBottomWidth"]]
            [s width] scale]
        [(keyword (str pre s)) {prop width}]))))

(defn border-radius
  "Border radius properties
   br0 ... br5            borderRadius: 0|0.125|0.25|0.5|1]2 rem
   br50                   borderRadius: 50%"
  [opts]
  (let [{:keys [rem]} opts
        scale [["0" 0]
               ["1" 0.125]
               ["2" 0.25]
               ["3" 0.5]
               ["4" 1]
               ["5" 2]]]
    (into {:br50 {"borderRadius" "50%"}}
      (for [[s fac] scale]
        [(keyword (str "br" s)) {"borderRadius" (int (* fac rem))}]))))

(defn font-size
  "Font size properties
   f1 ... f6              fontSize: 3|2.25|1.5|1.25|1|0.875 rem"
  [opts]
  (let [{:keys [font-rem rem]} opts
        scale [["1" 3]
               ["2" 2.25]
               ["3" 1.5]
               ["4" 1.25]
               ["5" 1]
               ["6" 0.875]
               ["7" 0.625]]]
    (into {}
      (for [[s fac] scale]
        [(keyword (str "f" s)) {"fontSize" (int (* fac (or font-rem rem)))}]))))

(defn font-weight
  "Font weight properties
   fw"
  [opts]
  {:fwb {"fontWeight" "bold"}})

(defn text-align
  "Text align properties
   tl|tc|tr|tj            textAlign: left|center|right|justify"
  [opts]
  {:tl {"textAlign" "left"}
   :tc {"textAlign" "center"}
   :tr {"textAlign" "right"}
   :tj {"textAlign" "justify"}})

(defn text-decoration
  "Text decoration line properties"
  [opts]
  {:tdu {"textDecorationLine" "underline"
         "textDecorationStyle" "solid"}
   :tdl {"textDecorationLine" "line-through"
         "textDecorationStyle" "solid"}})

(defn opacity
  "Opacity properties
   o10|20|...|100        opacity: 0.1|0.2|...|1
   o05                   opacity: 0.05
   o025                  opacity: 0.025"
  [opts]
  (let [scale [["0" 0]
               ["025" 0.025]
               ["05" 0.05]
               ["10" 0.1]
               ["20" 0.2]
               ["30" 0.3]
               ["40" 0.4]
               ["50" 0.5]
               ["60" 0.6]
               ["70" 0.7]
               ["80" 0.8]
               ["90" 0.9]
               ["100" 1]]]
    (into {}
      (for [[s o] scale]
        [(keyword (str "o" s)) {"opacity" o}]))))

(defn colors
  "Font family & weight properties"
  [opts]
  (let [{:keys [palette]} opts]
    (into {}
      (for [[c hex] palette
            [pre prop] [[nil "color"]
                        ["b-" "borderColor"]
                        ["bg-" "backgroundColor"]]]
        [(keyword (str pre (name c))) {prop hex}]))))

(defn shadow
  "Shadow properties"
  [opts]
  (let [shadow-color "#2a2a2a"
        text-shadow-color "rgba(0,0,0,0.7)"]
    {:tsh {"textShadowRadius" 5
           "textShadowOffset" {"width" 1 "height" 1}
           "textShadowColor" text-shadow-color}
     :sh1 {"shadowRadius" 2
           "shadowOffset" {"height" 2
                           "width" 0}
           "shadowColor" shadow-color
           "shadowOpacity" 0.25}
     :sh2 {"shadowRadius" 3
           "shadowOffset" {"height" 2
                           "width" 0}
           "shadowColor" shadow-color
           "shadowOpacity" 0.5}
     :sh1-android {"elevation" 2}
     :sh2-android {"elevation" 4}}))

(defn line-heights
  [opts]
  {:lh0 {"lineHeight" 1}
   :lh1 {"lineHeight" 1.25}
   :lh2 {"lineHeight" 1.5}
   :lh3 {"lineHeight" 1.75}})

(defn scale-line-height
  "Postprocess step to scale lineheight according to
   provided font size."
  [m]
  (let [{:keys [fontSize lineHeight]} m]
    (cond
      (and lineHeight fontSize) (assoc m :lineHeight (int (* fontSize lineHeight)))
      (and lineHeight (not fontSize)) (do (println "Font size must be provided if lineheight is required") m)
      :else m)))

(defn tachyons
  [opts]
  (merge
    (flex opts)
    (spacing opts)
    (dims opts)
    (absolute opts)
    (border-width opts)
    (border-radius opts)
    (font-size opts)
    (font-weight opts)
    (text-align opts)
    (text-decoration opts)
    (opacity opts)
    (colors opts)
    (shadow opts)
    (line-heights opts)))


#?(:clj
   (def registry (atom nil))

   :cljs
   (def ^:dynamic *registry* nil))

#?(:cljs
   (def ^:dynamic *current-theme* "no-preference"))

#?(:cljs
   (defn set-current-theme!
     "Sets the current style theme"
     [theme]
     (set! *current-theme* theme)))

#?(:clj
   (defmacro init!
     "Initializes tachyons with the provided themes"
     [opts]
     (let [{:keys [default-theme themes]
            :or {default-theme "light"}} opts]
       (reset! registry
         (into {"no-preference" (tachyons (get themes default-theme))}
           (for [[k v] themes]
             [k (tachyons v)])))
       `(set! *registry* ~(deref registry)))))

#?(:cljs
   (defn s*
     "Function for dynamic styles"
     [args]
     (println "dynamic")
     (reduce (fn [acc k]
               (merge acc (get-in *registry* [*current-theme* k])))
       {}
       args)))

#?(:clj
   (defmacro s
     "Macro to statically convert styles to js object"
     [args#]
     (assert (some? @registry) "Must call init! to set themes themes before using `s` macro")
     (if (every? keyword? args#)
       `(do
          (println "static")
          (case *current-theme*
            ~@(mapcat (fn [[k# m#]]
                        `[~k# ~(->> (reduce (fn [acc# k#]
                                              (merge acc# (get m# k#)))
                                      {}
                                      args#)
                                 (mapcat identity)
                                 (js-obj))])
                @registry)))
       `(s* ~args#))))


#?(:clj
   (println
     (macroexpand '(s [:bg-ui1 :w100]))))
