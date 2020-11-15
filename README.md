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
