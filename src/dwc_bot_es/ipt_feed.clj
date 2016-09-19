(ns dwc-bot-es.ipt-feed
  (:require [clojure.data.xml :as xml])
  (:require [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.coerce :as c])
  (:require [clojure.java.io :as io])
  (:require [taoensso.timbre :as log]))

(defn parse-date
  "Parse date from IPT RSS into a date object"
  [date]
  (try
    (f/parse (f/with-locale (f/formatters :rfc822) (java.util.Locale. "en")) date)
    (catch Exception e
      (f/parse (f/with-locale (f/formatters :rfc822) (java.util.Locale. "pt")) date))))

(defn date-to-timestamp
  "Format a date object as timestamp"
  [date]
   (String/valueOf (c/to-long (parse-date date))))

(defn get-tag-value
  "Extract the value of a tag inside an element"
  [el tag] 
   (first
     (:content
       (first
         (filter
           #(= (:tag %) tag)
           (:content el))))))

(defn item-to-resource
  "Transform an ipt rss item xml tag into a hashmap"
  [item] 
  {:title (get-tag-value item :title)
   :link  (get-tag-value item :link)
   :pub   (get-tag-value item :pubDate)
   :dwca  (str (get-tag-value item :dwca) 
               "&timestamp=" 
               (date-to-timestamp (get-tag-value item :pubDate)))})

(defn get-resources
  "Get all the resources of an IPT rss URL"
  [source] 
  (try
    (log/info "Will load source" source)
    (let [rss (xml/parse (io/reader (str source "/rss.do")))]
      (->> rss
        (:content)
        (first)
        (:content)
        (filter #(= (:tag %) :item))
        (map item-to-resource)))
    (catch Exception e 
      (log/warn "Fail to load" source e))))

