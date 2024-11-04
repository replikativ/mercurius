(ns mercurius.stripe
  (:require
   [mercurius.config :refer [config]]
   [babashka.http-client :as http]
   [jsonista.core :as json]))

(def stripe-api-key (:stripe-api-key config))

(def test-destination-account "stripe_connect_id_for_developer")

(defn create-payment-intent
  [amount currency destination-account]
  (let [url "https://api.stripe.com/v1/payment_intents"
        headers {"Authorization" (str "Bearer " stripe-api-key)
                 "Content-Type" "application/x-www-form-urlencoded"}
        params (merge
                {"amount" (str amount)
                 "currency" currency
                 "automatic_payment_methods[enabled]" "true"}
                ;; when not testing, we can specify the destination account
                (when-not (= destination-account test-destination-account)
                  {"transfer_data[destination]" destination-account}))
        response (http/post url
                            {:headers headers
                             :form-params params})]
    (json/read-value (:body response))))


(comment
;; Example usage
  (def payment-intent (create-payment-intent 1000 "usd"
                                             "cus_ABC123" ;"pm_card_visa"
                                             ))

  (println "Payment Intent:" payment-intent)

  )

