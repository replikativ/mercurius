(ns mercurius.config
  (:require [environ.core :refer [env]]
            [clojure.string :as str]
            [taoensso.timbre :as timbre]))

(defn validate-config [config]
  (if (nil? (:stripe-api-key config))
    (do
      (timbre/error "Missing Stripe API key" {:config config})
      (throw (ex-info "Missing Stripe API key" {:config config})))
    (if (empty? (:user-tags config))
      (do
        (timbre/error "Missing user tags" {:config config})
        (throw (ex-info "Missing user tags" {:config config})))
      (if (nil? (:path config))
        (do
          (timbre/error "Missing path" {:config config})
          (throw (ex-info "Missing path" {:config config})))
        config))))

(def ^:dynamic config 
  (let [config {:user-tags (into #{} (str/split (env :USER_TAGS "private") #","))
                :stripe-api-key (env :STRIPE_API_KEY "sk_test_Hrs6SAopgFPF0bZXSN3f6ELN")
                :path (env :PATH "./mercurius")}]
    (validate-config config)))

