#!/bin/bash
cd todo-service || exit
mvn clean spring-boot:build-image
rc=$?
if [ $rc -ne 0 ] ; then
  echo Could not perform spring-boot:build-image, exit code [$rc]; exit $rc
fi
cd ..
docker-compose up