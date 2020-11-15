(ns cruxdeploy.main
  (:require [ring.adapter.jetty :as jetty]
            [reitit.ring :as ring]
            [ring.util.response :as rr]
            [environ.core :refer [env]]))

(defn router [env]
  (ring/ring-handler
   (ring/router
    ["/hello-world" {:get (fn [_] (rr/response (str "Hello World, this is the " (:env-name env) " environment")))}])))

(defn start []
  (println "Starting Router on 3000")
  (jetty/run-jetty (router {:env-name (env :env-name)}) {:port 3000 :join? false}))

(defn -main []
  (start))