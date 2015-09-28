(ns am-i-an-otter.core
  (:use compojure.core)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.multipart-params :as mp]))

(load "imports")
(load "otters-db")
(load "otters")

(defroutes main-routes
           (GET "/" [] (page-compare-otters))
           (GET ["/upvote/:id" :id #"[0-9]+"] [id] (page-upvote-otter id))
           (GET "/upload" [] (page-start-upload-otter))
           (GET "/votes" [] (page-otter-votes))


           )