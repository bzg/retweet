;; Copyright (c) 2019 Bastien Guerry <bzg@bzg.fr>

;; SPDX-License-Identifier: EPL-2.0
;; License-Filename: LICENSES/EPL-2.0.txt

(defproject retweet "0.3.5"
  :url "https://github.com/bzg/retweet"
  :license {:name "Eclipse Public License v2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [twttr "3.2.2"]]
  :description "Bot to retweet tweets from users with hashtags."
  :main retweet.core
  :profiles {:uberjar {:aot :all}})
