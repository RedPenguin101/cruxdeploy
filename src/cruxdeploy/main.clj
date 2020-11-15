(ns cruxdeploy.main
  (:require [ring.adapter.jetty :as jetty]
            [reitit.ring :as ring]
            [ring.util.response :as rr]))

(def router
  (ring/ring-handler
   (ring/router
    ["/hello-world" {:get (fn [_] (rr/response "Hello World"))}])))

(defn start []
  (println "Starting Router on 3000")
  (jetty/run-jetty router {:port 3000 :join? false}))

(defn -main []
  (start))