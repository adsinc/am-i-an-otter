(ns am-i-an-otter.core
  (:use compojure.core)
  (:use hiccup.core)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.multipart-params :as mp]))

(load "imports")
(load "otters-db")
(load "otters")

(declare page-compare-otters)

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

(defn page-compare-otters)