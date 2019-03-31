(defproject retweet "0.3"
  :url "https://github.com/bzg/retweet"
  :license {:name "Eclipse Public License"
            :url  "https://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.logging "0.4.1"]
                 [ch.qos.logback/logback-classic "1.2.3"]
                 [twttr "3.2.1"]]
  :description "Bot to retweet tweets from users with hashtags."
  :main retweet.core
  :profiles {:uberjar {:aot :all}})
