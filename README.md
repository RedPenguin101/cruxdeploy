# Cruxdeploy

An attempt to set up an application

* two environments (dev and prod)
* CICD Pipeline
* A crux database running on RocksDB
* Cloud deployment on a virtual server

## The plan

1. Set up a hello world app
2. Build to uberjar
3. Setup API with stateless app
4. Setup environment variables and use in build step
5. Deploy to cloud with single environment
6. Add database
7. Add pipeline

## Hello World

Setup a new project CruxDeploy.

Add deps.edn

`{:paths ["src"]}`

Add _src/cruxdeploy/main.clj_

```clojure
(ns cruxdeploy.main)

(defn -main [_]
  (println "Hello World"))
```

Run from console

```bash
$ clj -X cruxdeploy.main/-main
Hello World
```

## Build to Uberjar

With [depstar](https://github.com/seancorfield/depstar).

```clojure
{:paths ["src"]
 :deps {org.clojure/clojure  {:mvn/version "1.10.1"}}
 :aliases {:depstar
           {:extra-deps {seancorfield/depstar {:mvn/version "1.1.133"}}
            :main-opts ["-m" "hf.depstar.uberjar" "target/cruxdeploy.jar"]}}}
```

Build the jar with

`clj -M:depstar`

Run it with

```
▶ java -cp ./target/cruxdeploy.jar clojure.main -m cruxdeploy.main
Hello World
```

### API with stateless app

Add ring + reitit to deps

```clojure
{:paths ["src"]
 :deps {org.clojure/clojure  {:mvn/version "1.10.1"}
        ring/ring            {:mvn/version "1.8.1"}
        metosin/reitit       {:mvn/version "0.5.5"}}
 :aliases {:depstar
           {:extra-deps {seancorfield/depstar {:mvn/version "1.1.133"}}
            :main-opts ["-m" "hf.depstar.uberjar" "target/cruxdeploy.jar"]}}}
```

Expand main

```clojure
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
```

Build, run, check you can hit the endpoint `http://localhost:3000/hello-world`
