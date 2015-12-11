#!/bin/sh

docker rm -f mongo-vacation-planner
docker run -p 27017:27017 --name mongo-vacation-planner -d mongo
