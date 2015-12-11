#!/bin/sh

docker rm -f vacation-service
docker run -p 8181:8080 --link mongo-vacation-planner:mongo --name vacation-service -d ibek/vacation-service
