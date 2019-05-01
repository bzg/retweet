;; Copyright (c) 2019 Bastien Guerry <bzg@bzg.fr>
;; SPDX-License-Identifier: EPL-2.0
;; License-Filename: LICENSES/EPL-2.0.txt

(ns retweet.test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]))

(deftest test-config
  (testing "Checking if RETWEET_CONFIG points to an existing file."
    (is (.exists (io/file (System/getenv "RETWEET_CONFIG"))))))
