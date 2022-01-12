#!/bin/bash
mvn clean install -DskipTests
rc=$?
if [ $rc -ne 0 ] ; then
  echo Could not perform mvn clean install, exit code [$rc]; exit $rc
fi
docker build -t custom-keycloak ./keycloak
cd todo-service || exit
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
docker build -t todo-service-image .
cd ..
docker-compose up