#!/bin/bash
clj -M:depstar
export ENV_NAME=dev
java -cp ./target/cruxdeploy.jar clojure.main -m cruxdeploy.main