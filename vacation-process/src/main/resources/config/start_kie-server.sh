#!/usr/bin/env bash

# Start KIE Server and link the microservices.
./standalone.sh -b $JBOSS_BIND_ADDRESS --server-config=standalone-full-kie-server.xml -Duser.service.host=${SUSER_PORT_8080_TCP_ADDR} -Dvacation.service.host=${SVACATION_PORT_8080_TCP_ADDR} -Duser.service.port=${SUSER_PORT_8080_TCP_PORT} -Dvacation.service.port=${SVACATION_PORT_8080_TCP_PORT}
exit $?
