(defproject am-i-an-otter "0.1.0-SNAPSHOT"
  :description "Am I an Otter ot Not?"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [compojure "1.4.0"]
                 [com.amplify/hiccup "1.0.0-rc.1"]
                 [log4j "1.2.17" :exclusions [javax.mail/mail
                                              javax.jsm/jms
                                              com.sun.jdmk/jmxtools
                                              com.sun.jmx/jmxri]]
                 [org.slf4j/slf4j-api "1.7.12"]
                 [org.slf4j/slf4j-log4j12 "1.7.12"]]
  :dev-dependencies [[lein-ring "0.9.7"]]
  :ring {:handler am-i-an-otter.core/app})
