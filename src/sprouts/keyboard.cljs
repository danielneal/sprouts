(ns sprouts.keyboard
  (:require [hx.react :refer [defnc]]
            [hx.hooks :as hooks]
            ["react" :as react]
            ["react-native" :as rn :refer [Keyboard]]
            [cljs-bean.core :refer [->clj]]))

(def ^:dynamic *keyboard-height* 300)

(defn useKeyboard
  "Returns a pair of [visible dismiss], where state
  reflects the current visibility and height of the keyboard.
  "
  []
  (let [[visible setVisible] (react/useState false)
        dismiss (react/useCallback
                  (fn []
                    (.dismiss Keyboard)
                    (setVisible false)))
        onShow (react/useCallback (fn [_]
                                    (setVisible true)))
        onHide (react/useCallback (fn [_]
                                    (setVisible false)))
        afterShow (react/useCallback (fn [^js e]
                                       (let [height (get-in (->clj e) [:endCoordinates :height])]
                                         ;; record the real keyboard height for next time
                                         (when (pos? height)
                                           (set! *keyboard-height* height)))))]
    (hooks/useEffect
      (fn []
        (.addListener Keyboard "keyboardWillShow" onShow)
        (.addListener Keyboard "keyboardWillHide" onHide)
        (.addListener Keyboard "keyboardDidShow" afterShow)
        (fn []
          (.removeListener Keyboard "keyboardWillShow" onShow)
          (.removeListener Keyboard "keyboardWillHide" onHide))))
    [visible dismiss]))
