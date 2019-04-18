;; Copyright (c) 2019 Bastien Guerry <bzg@bzg.fr>

;; SPDX-License-Identifier: EPL-2.0
;; License-Filename: LICENSES/EPL-2.0.txt

(ns retweet.core
  (:require [twttr.api :as api]
            [twttr.auth :as auth]
            [clojure.java.io :as io]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.core :as appenders])
  (:gen-class))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Setup logging

(timbre/set-config!
 {:level     :debug
  :output-fn (partial timbre/default-output-fn {:stacktrace-fonts {}})
  :appenders
  {:println (timbre/println-appender {:stream :auto})
   :spit    (appenders/spit-appender {:fname "log.txt"})}})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Setup credentials
;;
;; Environment variables are
;; CONSUMER_KEY: The application consumer key
;; CONSUMER_SECRET: The application consumer secret
;; ACCESS_TOKEN: The application access token
;; ACCESS_TOKEN_SECRET: The application access token secret

(defn creds []
  (try
    (auth/env->UserCredentials)
    (catch Exception e
      (timbre/error (ex-data e)))))

(defn config []
  (try
    (read-string (slurp "config.edn"))
    (catch Exception e
      (timbre/error (ex-data e)))))

(defn get-user-id [user-screen-name]
  (try
    (:id (api/users-show (creds) :params {:screen_name user-screen-name}))
    (catch Exception e
      (timbre/warn (str "Can't get user id for " user-screen-name)))))

;; Define the set of twitter ids to watch
(def ids (set (map get-user-id (:accounts (config)))))

;; Define the set of twitter hashtags to watch
(def hashtags (:hashtags (config)))

;; Define the regexp for the set of hashtags to watch
(def hashtags-regexp
  (re-pattern
   (str "(?is).*(?:"
        (clojure.string/join "|" (map #(format "(#%s)" %) hashtags)) ").*")))

(defn statuses-filter
  "Return a lazy sequence of tweets authored by :accounts."
  [credentials]
  (timbre/info "Following users")
  (->> (api/statuses-filter credentials :params {:follow (clojure.string/join "," ids)})
       (filter (fn [status] (->> status :user :id (contains? ids))))))

(defn maybe-retweet
  "Retweet `statuses` if they match `hashtags-regexp`."
  [credentials statuses]
  (doseq [status statuses]
    (let [text (get-in status [:extended_tweet :full_text] (:text status))]
      (when (re-matches hashtags-regexp text)
        (try
          (api/statuses-retweet-id credentials :params {:id (:id status)})
          (catch Exception e
            (timbre/error (str "Caught exception: " (.toString e)))))
        (timbre/info "Retweeting status " (:id status))))))

(defn retweet
  "Filter through users' statuses and maybe retweet."
  [credentials]
  (->> (statuses-filter credentials)
       (maybe-retweet credentials)))

(defn -main []
  (timbre/info "Listening to tweets...")
  (try
    (retweet (creds))
    (catch Exception e
      (timbre/fatal (str "Bot died: " (.toString e))))))
