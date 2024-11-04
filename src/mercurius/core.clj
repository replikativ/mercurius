(ns mercurius.core
  "Payment provider for open source licenses with paid functions."
  (:require [datahike.api :as d]
            [mercurius.stripe :as stripe]
            [mercurius.config :refer [config]]
            [taoensso.timbre :as timbre]
            [clojure.spec.alpha :as s]))

(def db-cfg {:store {:backend :file :path (:path config)}})

(def schema [{:db/ident :payment/id
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "The unique identifier for a payment."}
             {:db/ident :payment/amount
              :db/valueType :db.type/long
              :db/cardinality :db.cardinality/one
              :db/doc "The amount of the payment."}
             {:db/ident :payment/currency
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "The currency of the payment."}
             {:db/ident :payment/payee
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "The payee identifier for the payment."}
             {:db/ident :payment/user-tags
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/many
              :db/doc "The user type of the payment."} 
             {:db/ident :payment/created-at
             :db/valueType :db.type/instant
              :db/cardinality :db.cardinality/one
              :db/doc "The time the payment was created."}])

(defn setup-db [cfg]
 (try
    (let [cfg (d/create-database cfg)
          conn (d/connect cfg)]
      (d/transact conn schema)
      conn)
    (catch Exception _e
      (d/connect cfg))) )

(def ^:dynamic conn (setup-db db-cfg))

(comment
  (d/delete-database db-cfg)

  )


(s/fdef pay :args (s/cat :payment-id string?
                         :user-tags set?
                         :amount number?
                         :currency string?
                         :payee string?)
        :ret any?)
(defn pay
  "Pay once for a service."
  [payment-id user-tags amount currency payee]
  (when (some (:user-tags config) user-tags)
    (let [res (stripe/create-payment-intent amount currency payee)]
      (timbre/debug "Payment status:" res)
      (d/transact conn [{:payment/id payment-id
                         :payment/amount amount
                         :payment/currency currency
                         :payment/payee payee
                         :payment/user-tags user-tags
                         :payment/created-at (java.util.Date.)}]))))

(defn now []
  (.getTime (java.util.Date.)))

(s/fdef pay-monthly :args (s/cat :payment-id string?
                                 :user-tags set?
                                 :amount number?
                                 :currency string?
                                 :payee string?)
        :ret any?)
(defn pay-monthly
  "Pay monthly for a service. Payment id should identify the payment and will be used to check if the payment has already been made this month."
  [payment-id user-tags amount currency payee]
  (when (some (:user-tags config) user-tags)
    (let [;; fetch prior payment intents from database
          paid? (d/q '[:find ?created-at-time .
                       :in $ ?payment-id ?payee-id
                       :where
                       [?p :payment/id ?payment-id]
                       [?p :payment/payee ?payee-id]
                       [?p :payment/created-at ?created-at]
                       [(.getTime ?created-at) ?created-at-time]
                       [(* 30 24 60 60 1000) ?month]
                       [(mercurius.core/now) ?now]
                       [(+ ?created-at-time ?month) ?next]
                       [(< ?now ?next)]]
                     @conn payment-id payee)]
      (when-not paid?
        (let [res (stripe/create-payment-intent amount currency payee)]
          (timbre/debug "Payment status:" res)
          (d/transact conn [{:payment/id payment-id
                             :payment/amount amount
                             :payment/currency currency
                             :payment/payee payee
                             :payment/user-tags user-tags
                             :payment/created-at (java.util.Date.)}]))))))

(comment
  (pay "payee creation" ["private"] 10 "usd" "stripe_connect_id_for_developer")

  (pay-monthly "mercurius monthly subscription" ["private"] 1000 "usd"  "stripe_connect_id_for_developer")

  )

(s/fdef get-all-payments :args (s/cat)
        :ret (s/coll-of map?))
(defn get-all-payments
  "Get all payments from the database."
  []
  (->>
   (d/q '[:find ?id ?amount ?currency ?payee ?user-tags ?created-at
          :where
          [?p :payment/id ?id]
          [?p :payment/amount ?amount]
          [?p :payment/currency ?currency]
          [?p :payment/payee ?payee]
          [?p :payment/user-tags ?user-tags]
          [?p :payment/created-at ?created-at]]
        @conn)
   (mapv (fn [[id amount currency payee user-tags created-at]]
           {:id id
            :amount amount
            :currency currency
            :payee payee
            :user-tags user-tags
            :created-at created-at}))))

(comment
  (get-all-payments)

  )
