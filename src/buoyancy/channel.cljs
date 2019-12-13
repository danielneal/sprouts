(ns buoyancy.channel
  (:require ["react" :as react]
            [hx.hooks :as hooks]
            [sprouts.firebase :as firebase]))

(defmulti reducer
  (fn [state action]
    (:action/type action)))

(defmethod reducer :topic/edit
  [state action]
  (let [{:keys [topic-id]} action]
    (merge state {:popup/active? true
                  :popup/content {:edit-topic/topic-id topic-id}})))

(defmethod reducer :topic/cancel-edit
  [state _]
  (merge state {:popup/active? false
                :popup/content nil}))

(defmethod reducer :statement/select
  [state action]
  (let [{:keys [statement/id]} action]
    (update state :popup/content assoc :edit-topic/selected-statement id)))

(defmethod reducer :firebase/received-snapshot
  [state action]
  (let [{:keys [support channel statements topics]} action
        support-by-statement (group-by (fn [s]
                                         (.-id (:statement s))) support)
        statements-with-points (for [s statements]
                                 (let [{:keys [:firebase/id]} s]
                                   (assoc s :points (reduce + (map :points (get support-by-statement id))))))
        statements-by-topic (->> (group-by (fn [s]
                                             (.-id (:topic s))) statements-with-points)
                                 (map (fn [[k v]]
                                        [k (->> (sort-by :points v)
                                                (reverse))]))
                                 (into {}))
        topics-with-points (for [t topics]
                             (let [{:keys [:firebase/id]} t]
                               (assoc t :points (reduce + (map :points (get statements-by-topic id))))))
        topics-by-id (->> (for [t topics-with-points]
                             (let [{:keys [:firebase/id]} t]
                               [id t]))
                          (into {}))]
    (merge state {:loading? false
                  :channel channel
                  :topics topics-with-points
                  :topics-by-id topics-by-id
                  :statements statements-with-points
                  :statements-by-topic statements-by-topic})))

(defn useChannel
  [opts]
  (let [{:keys [channel-id]} opts
        channel-ref (firebase/ref {:collection "channels"
                                   :doc channel-id})
        [_ channel] (firebase/useDocument {:collection "channels"
                                           :doc channel-id})
        [_ topics add-topic] (firebase/useCollection {:collection "topics"
                                                      :query [["channel" "==" channel-ref]]})
        [_ statements add-statement] (firebase/useCollection {:collection "statements"
                                                              :query [["channel" "==" channel-ref]]})
        [_ support add-support] (firebase/useCollection {:collection "support"
                                                         :query [["channel" "==" channel-ref]]})
        [state dispatch] (hooks/useReducer reducer {:loading? true
                                                    :channel-id channel-id
                                                    :channel-ref channel-ref
                                                    :add-support add-support
                                                    :add-topic add-topic
                                                    :add-statement add-statement})]
    (hooks/useEffect
      (fn []
        (when (and channel (seq topics) (seq statements) (seq support))
          (dispatch {:action/type :firebase/received-snapshot
                     :channel channel
                     :support support
                     :statements statements
                     :topics topics})))
      [channel topics statements support])
    [state dispatch]))
