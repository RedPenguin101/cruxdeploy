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

## Environmental variables

Add [environ](https://github.com/weavejester/environ) to deps

Change `router` to a function that takes an env. In `start` just pass a dict for now.

Next, set up a bash script to build and run the jar. Inbetween, set the ENV_NAME variable to Dev

```bash
#!/bin/bash
clj -M:depstar
export ENV_NAME=dev
java -cp ./target/cruxdeploy.jar clojure.main -m cruxdeploy.main
```

In main, use environ to pull out the env-name as you call the router

```clojure
(jetty/run-jetty (router {:env-name (env :env-name)}) {:port 3000 :join? false})
```

Check that you get `Hello World, this is the dev environment` when you hit `http://localhost:3000/hello-world`

## Add a test

```clojure
(ns cruxdeploy.main-test
  (:require [clojure.test :refer [deftest is]]))

(deftest test
  (is (= 2 (+ 1 1))))
```

Add another alias in tests

```clojure
:run-tests {:extra-paths ["test"]
                       :extra-deps  {com.cognitect/test-runner
                                     {:git/url "https://github.com/cognitect-labs/test-runner.git"
                                      :sha     "209b64504cb3bd3b99ecfec7937b358a879f55c1"}}
                       :main-opts   ["-m" "cognitect.test-runner"
                                     "-d" "test"]}
```

Check the tests are being picked up and pass with `clj -M:run-tests`

## Add circle CI for continuous integration

make a new file _.circleci/config.yml_

```yaml
version: 2
jobs:
  build:
    working_directory: ~/cruxdeploy
    docker:
      - image: circleci/clojure:openjdk-11-tools-deps-1.10.1.536

    environment:
      JVM_OPTS: -Xmx3200m
    steps:
      - checkout
      - restore_cache:
          key: cruxdeploy-{{ checksum "deps.edn" }}
      - run: clojure -R:test -Spath
      - save_cache:
          paths:
            - ~/.m2
            - ~/.gitlibs
          key: cruxdeploy-{{ checksum "deps.edn" }}
      - run: clojure -A:run-tests
```
