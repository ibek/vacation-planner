#!/bin/sh

docker rm -f vacation-process
docker run -p 8081:8080 --link vacation-service:svacation --link usermgmt-service:suser --name vacation-process -d ibek/vacation-process
