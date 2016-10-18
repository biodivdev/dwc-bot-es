(ns dwc-bot-es.core
  (:require [dwc-bot-es.ipt-feed :as ipt-feed])
  (:require [dwc-bot-es.config :as config])
  (:require [dwc-io.archive :as dwca]
            [dwc-io.fixes :as fixes])
  (:require [clojure.data.json :as json])
  (:require [batcher.core :refer :all])
  (:require [clojure.core.async :refer [<! <!! >! >!! go go-loop chan close!]])
  (:require [clj-http.lite.client :as http])
  (:require [taoensso.timbre :as log])
  
  (:gen-class))

(defn now
  "Get current timestamp"
  [] (int (/ (System/currentTimeMillis) 1000)))

(defn all-resources
  "Get all resources of all inputs ipts"
  [] (flatten (map ipt-feed/get-resources (config/inputs))))

(defn source-name
  [source] (second (re-find #"r=([a-zA-Z0-9_\-]+)" source)))

(defn point
  [occ] 
  (when (and (not (nil? (:decimalLatitude occ))) 
             (not (nil? (:decimalLongitude occ))))
    (try
      (let [lat (Double/valueOf (:decimalLatitude occ))
            lon (Double/valueOf (:decimalLongitude occ))]
         (when (and (>= lat -90.0)
                    (<= lat 90.0)
                    (>= lon -180.0)
                    (<= lon 180.0))
          {:lat lat :lon lon}))
      (catch Exception e nil))))

(defn metadata
  "Assoc metadata to the row"
  [src row] 
  (let [id (str (source-name src) ":" (or (:occurrenceID row) (:taxonID row)))]
    (assoc row :id id
               :identifier id
               :timestamp (now)
               :point (point row)
               :source src)))

(defn prepare-row
  [src row-type row]
  "Prepare to send to elasticsearch"
   (let [doc (metadata src row)]
     [{:index {:_index config/index :_type (name row-type) :_id (:id doc)}}
      doc]))

(defn fix
  [row-type rows]
  (if (= :taxon row-type) 
    (fixes/fix-taxon rows)
    (if (= :occurrence row-type)
      (fixes/fix-occ rows))))

(defn make-body
  [src row-type rows] 
  (->> rows
    (fix row-type)
    (map (partial prepare-row src row-type))
    (flatten)
    (map json/write-str)
    (interpose "\n")
    (apply str)))

(defn bulk-insert
  "Insert/delete/update the bulk of rows"
  [src row-type rows]
  (when (> (count rows) 0)
    (let [body (make-body src row-type rows)]
       (try
           (http/post (str config/es "/" config/index "/_bulk") {:body body})
           (log/info "Saved" (count rows) " " (name row-type) " from " src)
         (catch Exception e
           (do (log/warn "Error saving" (.getMessage e))
               (log/warn "Error body" body)
               (log/warn e)))))))

(defn run
  "Run the crawler in a single resource(dwca)"
  [row-type rec]
 (let [source (:link rec)]
   (log/info "->" source)
   (let [waiter (chan 1)
         batch  (batcher {:size (* 1 1024)
                          :fn (partial bulk-insert source row-type)
                          :end waiter})]
     (try
       (dwca/read-archive-stream (:dwca rec)
         (fn [row] (>!! batch row)))
       (catch Exception e
         (log/warn "Failed to read or process" source e)))
     (close! batch)
     (<!! waiter))))

(defn -main
  "Keep on running the bot on all sources. 
   Return an status atom that can swap to :stop to stop the bot."
  [ & args ] 
   (config/setup)
   (let [looping (atom true)]
     (while @looping
       (do
         (swap! looping (fn [_] config/should-loop))
         (log/info "Bot Active")
         (let [recs  (all-resources)]
           (log/info "Got" (count recs) "resources")
           (doseq [rec recs]
             (log/info "Resource" rec)
             (try
               (let [taxon? (dwca/checklist? (:dwca rec))
                     occ?   (dwca/occurrences? (:dwca rec))
                     rtype  (if taxon? :taxon (if occ? :occurrence))]
               (when (or taxon? occ?)
                 (run rtype rec)))
               (catch Exception e (log/warn "Exception runing" rec e)))))
         (when @looping
           (do
             (log/info "Will rest.")
             (Thread/sleep (* 1 60 60 1000))))))))

