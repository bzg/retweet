(ns retweet.test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [twttr.auth :as auth]))

(deftest test-config
  (testing "Checking if RETWEET_CONFIG points to an existing file."
    (is (.exists (io/file (System/getenv "RETWEET_CONFIG"))))))

(deftest test-auth
  (testing "Checking if auth environment variables are strings."
    (is (and (string? (System/getenv "TWITTER_CONSUMER_KEY"))
             (string? (System/getenv "TWITTER_CONSUMER_SECRET"))
             (string? (System/getenv "TWITTER_ACCESS_TOKEN"))
             (string? (System/getenv "TWITTER_ACCESS_TOKEN_SECRET"))))))
