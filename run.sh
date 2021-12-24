#!/bin/bash
mvn clean install
rc=$?
if [ $rc -ne 0 ] ; then
  echo Could not perform mvn clean install, exit code [$rc]; exit $rc
fi
cd todo-service || exit
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
docker build -t todo-service-image .
cd ..
docker-compose up