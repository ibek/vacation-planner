#!/bin/sh

docker rm -f usermgmt-service
docker run -p 8182:8080 --link mongo-vacation-planner:mongo --name usermgmt-service -d ibek/usermgmt-service
