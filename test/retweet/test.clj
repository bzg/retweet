(ns retweet.test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]))

(deftest test-config
  (testing "Checking if RETWEET_CONFIG points to an existing file."
    (is (.exists (io/file (System/getenv "RETWEET_CONFIG"))))))
