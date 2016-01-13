# Vacation Planner
Demo application using Polymer, BPM Suite KIE Server, Microservices in VertX, MongoDB, and Docker

DevConf 2016 - [Assemble Business Applications with BPM Back-end](https://devconfcz2016.sched.org/event/5m1W/assemble-business-applications-with-bpm-back-end)

![alt text](https://raw.githubusercontent.com/ibek/vacation-planner/master/docs/screenshots/Awaiting%20Vacation%20Requests.png "Awaiting Vacation Requests")

Requirements

* docker, npm, bower
* customized BPM KIE Server (https://github.com/ibek/droolsjbpm-integration) --- PRs in progress [Query Process With Vars](https://issues.jboss.org/browse/DROOLS-1020) and Force complete task operation

Execution steps

* start database
* start usermgmt-service
* start vacation-service
* start vacation-process
* start vacation-app/app using "python3 -m http.server"
* add users using usermgmt-service via REST
* open http://localhost:8000/

![alt text](https://raw.githubusercontent.com/ibek/vacation-planner/master/docs/screenshots/Request%20Vacation%20Process.png "Request Vacation Process")
