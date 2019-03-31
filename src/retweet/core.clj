(ns retweet.core
  (:gen-class)
  (:require [twttr.api :as api]
            [twttr.auth :as auth]
            [clojure.tools.logging :as log]))

(defn creds [] (auth/env->UserCredentials))

(defn get-user-id [user-screen-name]
  (:id (api/users-show (creds) :params {:screen_name user-screen-name})))

;; CONSUMER_KEY, CONSUMER_SECRET, ACCESS_TOKEN, and ACCESS_TOKEN_SECRET
(def config (read-string (slurp "config.edn")))

(def ids (set (map get-user-id (:accounts config))))

(def hashtags (:hashtags config))

(def hashtags-regexp
  (re-pattern
   (str "(?is).*(?:"
        (clojure.string/join "|" (map #(format "(#%s)" %) hashtags)) ").*")))

(defn statuses-filter-eig
  "Return a lazy sequence of tweets authored by :accounts."
  [credentials]
  (log/info "Following EIG users")
  (->> (api/statuses-filter credentials :params {:follow (clojure.string/join "," ids)})
       (filter (fn [status] (->> status :user :id (contains? ids))))))

(defn maybe-retweet
  "Retweet `statuses` if they match `hashtags-regexp`."
  [credentials statuses]
  (doseq [status statuses]
    (let [text (get-in status [:extended_tweet :full_text] (:text status))]
      (when (re-matches hashtags-regexp text)
        (api/statuses-retweet-id credentials :params {:id (:id status)})
        (log/info "Retweeting status " (:id status))))))

(defn retweet
  "Filter through EIG community statuses and maybe retweet."
  [credentials]
  (->> (statuses-filter-eig credentials)
       (maybe-retweet credentials))
  (log/info "Finished!"))

(defn -main []
  (log/info "Listening to tweets...")
  (retweet (creds)))
