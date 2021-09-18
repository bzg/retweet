;; Copyright (c) 2019-2021 Bastien Guerry <bzg@bzg.fr>
;; SPDX-License-Identifier: EPL-2.0
;; License-Filename: LICENSES/EPL-2.0.txt

(ns core
  (:require [twttr.api :as api]
            [twttr.auth :as auth]
            [clojure.string :as s])
  (:gen-class))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Setup credentials
;;
;; The RETWEET_CONFIG environment variable contains the path for the
;; configuration file.

(defn config []
  (try
    (read-string (slurp (System/getenv "RETWEET_CONFIG")))
    (catch Exception e
      (println (ex-data e)))))

(defn credentials []
  (try
    (auth/map->UserCredentials
     (select-keys (config)
                  [:consumer-key :consumer-secret :user-token :user-token-secret]))
    (catch Exception e
      (println (ex-data e)))))

(defn get-user-id [user-screen-name]
  (try
    (:id (api/users-show (credentials) :params {:screen_name user-screen-name}))
    (catch Exception e
      (println (str "Can't get user id for " user-screen-name
                    "Exception: " e)))))

;; Define the set of twitter ids to watch
(def ids (set (map get-user-id (:accounts (config)))))

;; Define the set of twitter hashtags to watch
(def hashtags (:hashtags (config)))

;; Define the regexp for the set of hashtags to watch
(def hashtags-regexp
  (re-pattern
   (str "(?is).*(?:"
        (s/join "|" (map #(format "(#%s)" %) hashtags)) ").*")))

(defn statuses-filter
  "Return a lazy sequence of tweets authored by :accounts."
  [credentials]
  (println "Following users")
  (filter (fn [status] (->> status :user :id (contains? ids)))
          (api/statuses-filter
           credentials :params {:follow (s/join "," ids)})))

(defn maybe-retweet
  "Retweet `statuses` if they match `hashtags-regexp`."
  [credentials statuses]
  (doseq [status statuses]
    (let [text (get-in status [:extended_tweet :full_text] (:text status))]
      (when (re-matches hashtags-regexp text)
        (try
          (api/statuses-retweet-id credentials :params {:id (:id status)})
          (catch Exception e
            (println (str "Caught exception: " (str e)))))
        (println "Retweeting status " (:id status))))))

(defn retweet
  "Filter through users' statuses and maybe retweet."
  [credentials]
  (maybe-retweet credentials (statuses-filter credentials)))

(defn -main []
  (println "Listening to tweets...")
  (try
    (retweet (credentials))
    (catch Exception e
      (println (str "Bot died: " (str e))))))
