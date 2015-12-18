#!/bin/sh

docker rm -f mongo-vacation-planner
docker run -p 27017:27017 -v /home/ibek/Temp/mongodb/data:/data/db --name mongo-vacation-planner -d mongo --smallfiles
