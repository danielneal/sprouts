(ns sprouts.style5
  "Style system based on tachyons"
  (:require [cljs-bean.core :refer [->js]]))

(def flex
  "Static flex properties"
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

(defn spacing [opts]
  "Dynamic margin and padding properties (dependent on rem)
   ma0 ... ma10            margin: 0|0.25|0.5|1|2|3|4|5|6|7|8 rem
   ml|mr|mb|mt [0-10]      marginLeft, marginRight, marginBottom, marginTop
   mh [0-10]               marginHorizontal
   mv [0-10]               marginVertical"
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
  "Dynamic heights and widths, depends on rem
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

(defn colors
  "Dynamic color properties"
  [opts]
  (let [{:keys [palette]} opts]
    (into {}
      (for [[c hex] palette
            [pre prop] [[nil "color"]
                        ["b-" "borderColor"]
                        ["bg-" "backgroundColor"]]]
        [(keyword (str pre (name c))) {prop hex}]))))

(defn absolute
  "Absolute positioning and offsets, depends on rem
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

(def border-width
  "Static border width properties
   ba                     borderWidth: 1
   bl|br|bt|bb            borderLeftWidth: 1 | borderRightWidth: 1 ..."
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
  "Dynamic border radius properties
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

(def static-tachyons
  (merge
    flex
    border-width))

(declare *opts*)

(def tachyons
  (memoize
    (fn [opts]
      (merge
        static-tachyons
        (absolute opts)
        (colors opts)
        (spacing opts)
        (dims opts)
        (border-radius opts)
        (font-size opts)))))

#?(:cljs
   (defn s*
     "Function to convert dynamic styles to js object"
     [args]
     (let [m (tachyons *opts*)]
       (->js (reduce (fn [acc k]
                   (println acc k)
                   (merge acc (or (get m k) (when (map? k) k))))
           {}
           args)))))

#?(:clj
   (defmacro s
     "Macro to convert styles to js object.
      Dynamic styles (that depend on options like palette)
      are computed at runtime, static styles that don't
      depend on anything are computed statically at compile  time"
     [args#]
     (let [{static# true
            dynamic# false} (group-by #(contains? static-tachyons %) args#)]
       `(do
          (cljs.core/array
            (cljs.core/js-obj
              ~@(mapcat identity
                  (reduce (fn [acc# k#]
                            (println k#)
                            (merge acc# (get static-tachyons k#)))
                    {}
                    static#)))
            (s* ~dynamic#))
          (println)))))

(defn init! [opts]
  (set! *opts* opts))
