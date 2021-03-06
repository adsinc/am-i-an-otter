(ns am-i-an-otter.core
  (:use compojure.core)
  (:use hiccup.core)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.multipart-params :as mp])
  (:import (java.io File)
           (java.nio.file FileSystems SimpleFileVisitor FileVisitResult Files Paths)))

(load "imports")
(load "otters-db")
(load "otters")

(declare page-compare-otters)
(declare page-upvote-otter)
(declare page-otter-votes)

(defroutes main-routes
           (GET "/" [] (page-compare-otters))
           (GET ["/upvote/:id" :id #"[0-9]+"] [id] (page-upvote-otter id))
           (GET "/upload" [] (page-start-upload-otter))
           (GET "/votes" [] (page-otter-votes))

           (mp/wrap-multipart-params
             (POST "/add_otter" req (str (upload-otter req) (page-start-upload-otter))))

           (route/resources "/")
           (route/not-found "Page not found"))

(def app
  (handler/site main-routes))

(defn page-compare-otters []
  (let [otter1 (random-otter) otter2 (random-otter)]
    (.info (get-logger) (str "Otter1 = " otter1 " : Otter2 = " otter2 " : " other-pics))
    (html [:h1 "Otters say 'Hello Compojure!'"]
          [:p [:a {:href (str "/upvote/" otter1)}
                  [:img {:src (str "/img/" (get-otter-pics otter1))}]]]
          [:p [:a {:href (str "/upvote/" otter2)}
                  [:img {:src (str "/img/" (get-otter-pics otter2))}]]]
          [:p "Click " [:a {:href "/votes"} "here"]
           " to see the votes for each otter"]
          [:p "Click " [:a {:href "/upload"} "here"]
           " to upload a brand new otter"])))

(defn page-upvote-otter []
  (html [:h1 "Upload new otter"]
        [:p [:form {:action "/add_otter" :method "POST" :enctype "multipart/form-data"}
             [:input {:name "file" :type "file" :size "20"}]
             [:input {:name "submit" :type "submit" :value "submit"}]]]
        [:p "Or click " [:a {:href "/"} "here"] " to vote on some otters"]))

(defn page-otter-votes []
  (let []
    (.debug (get-logger) (str "Otters: " @otter-votes-r))
    (html [:h1 "Otter votes"]
          [:div#votes.otter-votes
           (for [x (keys @page-otter-votes)]
             [:p [:img {:src (str "/img/" (get otter-pics x))}] (get @otter-votes-r x)])])))

(def otter-img-dir "resources/public/img")
(def otter-img-dir-fq
  (str (.getAbsolutePath (File. ".")) "/" otter-img-dir))

(defn make-matcher [pattern]
  (.getPathMatcher (FileSystems/getDefault) (str "glob:" pattern)))

(defn file-find [file matcher]
  (let [fname (.getName file (- (.getNameCount file) 1))]
    (if (and (not (nil? fname)) (.matches matcher fname))
      (.toString fname)
      nil)))

(defn next-map-id [map-with-id]
  (+ 1 (nth (max (let [map-ids (keys map-with-id)]
                   (if (nil? map-ids) [0] map-ids))) 0 )))

(defn alter-file-map [file-map fname]
  (assoc file-map (next-map-id file-map) fname))

(defn make-scanner [pattern file-map-r]
  (let [matcher (make-matcher pattern)]
    (proxy [SimpleFileVisitor] []
      (visitFile [file attribs]
        (let [my-file file.
              my-attrs attribs
              file-name (file-find my-file matcher)]
          (.debug (get-logger) (str "Return form file-find " file-name))
          (if (not (nil? file-name))
            (dosync (alter file-map-r alter-file-map file-name) file-map-r)
            nil)
          (.debug (get-logger)
                  (str "After return from file-find " @file-map-r))
          FileVisitResult/CONTINUE)))))

(defn scan-for-otters [file-map-r]
  (let [my-map-r file-map-r]
    (Files/walkFileTree (Paths/get otter-img-dir-fq
                                   (into-array String []))
                        (make-scanner "*.jpg" my-map-r))
    my-map-r))

(def otter-pics (deref (scan-for-otters (ref {}))))