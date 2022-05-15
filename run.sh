#!/bin/bash
cd todo-service || exit
mvn clean spring-boot:build-image
rc=$?
if [ $rc -ne 0 ] ; then
  echo Failed todo-service-image build
  echo Could not perform spring-boot:build-image, exit code [$rc]; exit $rc
fi
cd ..
cd media-service || exit
mvn clean spring-boot:build-image
rc=$?
if [ $rc -ne 0 ] ; then
  echo Failed media-service-image build
  echo Could not perform spring-boot:build-image, exit code [$rc]; exit $rc
fi
cd ..
docker-compose up