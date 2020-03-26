(ns amino.db
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [clojure.set :as set]))

(def api-key "fhlIkO9OdffApFj43m89vHhTtpWi8phdqbG6zen8")
(def food-search-url "https://api.nal.usda.gov/fdc/v1/search")
(def food-details-url "https://api.nal.usda.gov/fdc/v1/")

(defn veg-search [veg]
  (-> (http/post (str food-search-url "?api_key=" api-key)
        {:form-params {"generalSearchInput" (str "raw +" veg)}
         :content-type :json})
    :body
    (json/parse-string)
    (get-in ["foods" 0])
    (select-keys ["fdcId" "description"])))

(defn food-details [id]
  (-> (http/get (str food-details-url id "?api_key=" api-key))
    :body
    (json/parse-string)
    (get "foodNutrients")))

(defn veg-details [veg]
  (let [{:strs [fdcId description]} (veg-search veg)]
    (into #{}
      (for [entry (food-details fdcId)
            :when (= (get entry "type") "FoodNutrient")]
        (let [name (get-in entry ["nutrient" "name"])
              amount (get entry "amount")
              unit (get-in entry ["nutrient" "unitName"])]
          {:name name
           :amount amount
           :unit unit})))))

;; taken from https://www.ncbi.nlm.nih.gov/books/NBK234922/
(def essential-aminos
  "mg/kg of the essential amino acids, for adults"
  #{{:name "Phenylalanine"
     :required 14}
    {:name "Valine"
     :required 10}
    {:name "Threonine"
     :required 7}
    {:name "Tryptophan"
     :required 3.5}
    {:name "Methionine"
     :required 13}
    {:name "Leucine"
     :required 14}
    {:name "Isoleucine"
     :required 10}
    {:name "Histidine"
     :required 12}})

#_(defn user-essential-aminos [opts]
  (let [{:keys [mass-in-kg id]} opts]
    (into #{}
      (map (fn [amino]
             (let [{:strs [name required]} amino]
               {"name" name
                "required" required
                "userRequired" (* mass-in-kg required)})))
      required)))

(http/get "https://api.nal.usda.gov/fdc/v1/169228?api_key=fhlIkO9OdffApFj43m89vHhTtpWi8phdqbG6zen8")


(def foods
  ["asparagus"
   "potatoes"
   "white rice"
   "brown rice"
   "lentils"
   "black bean"
   "kidney beans"
   "eggplant"
   "chickpea"
   "broccoli"])

(def nutrient-info
  (for [food foods]
    [food (set/join (veg-details food) essential-aminos)]))


(set/join (veg-details "potatoes") essential-aminos)
