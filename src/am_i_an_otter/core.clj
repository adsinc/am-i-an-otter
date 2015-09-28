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
(declare page-upvote-otter)

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

(defn page-upvote-otter [])