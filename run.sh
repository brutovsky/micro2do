#!/bin/bash
mvn clean install
cd todo-service || exit
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
docker build -t todo-service-image .
cd ..
docker-compose up