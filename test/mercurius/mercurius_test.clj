(ns mercurius.mercurius-test
  (:require [clojure.test :refer :all]
            [datahike.api :as d]
            [mercurius.core :as m]
            [mercurius.stripe :as stripe]))

(deftest test-pay-with-db
  (testing "Testing pay, pay-monthly and get-all-payments with db."
    (let [db-cfg {:store {:backend :mem :id "mercurius"}}
          _ (d/delete-database db-cfg)]
      (binding [m/conn (m/setup-db db-cfg)]
        (is (not (nil? (m/pay "customer creation" #{"private"} 500 "usd" stripe/test-destination-account)))
            (is (not (nil? (m/pay-monthly "mercurius monthly subscription" #{"private"} 1000 "usd" stripe/test-destination-account)))
                (is (= (count (m/get-all-payments)) 2))))))))

