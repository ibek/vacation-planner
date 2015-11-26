####### BASE ############
FROM jboss/wildfly:8.1.0.Final

####### MAINTAINER ############
MAINTAINER "Ivo Bek" "ibek@redhat.com"

####### ENVIRONMENT ############
ENV JAVA_OPTS -XX:MaxPermSize=256m -Xms256m -Xmx512m
ENV JBOSS_BIND_ADDRESS 0.0.0.0
ENV KIE_SERVER_VERSION 6.3.0.Final
LABEL org.kie.kie-server.artifact=org.kie.server:kie-server:war:webc
EXPOSE 8080

####### SYSTEM USERS FOR DEVELOPMENT ############
USER root
# Set a password for root & jboss users (for any further system operations, etc)
RUN echo "root:rootman" | chpasswd && \
echo "jboss:jboss" | chpasswd

####### KIE-SERVER & WILDFLY8 CONFIG FILES ############
# Latest WAR from Maven repository & Custom Wildfly configuration files

# Wildfly custom configuration.
ADD config/kie-server-users.properties $JBOSS_HOME/standalone/configuration/kie-server-users.properties
ADD config/kie-server-roles.properties $JBOSS_HOME/standalone/configuration/kie-server-roles.properties
ADD config/standalone-full-kie-server.xml $JBOSS_HOME/standalone/configuration/standalone-full-kie-server.xml

# Custom kie-server Wildfly startup scripts.
ADD config/start_kie-server.sh $JBOSS_HOME/bin/start_kie-server.sh

# Add the kie-server WAR artifact from the Maven repository.
RUN yum -y install wget
RUN wget https://repository.jboss.org/nexus/service/local/repositories/central/content/org/kie/server/kie-server/${KIE_SERVER_VERSION}/kie-server-${KIE_SERVER_VERSION}-ee7.war -O $JBOSS_HOME/standalone/deployments/kie-server-archive.war

# Work with unpacked WAR archives in WildFly, so other images can override webapp files (such as persistence.xml,etc)
RUN unzip -q -d $JBOSS_HOME/standalone/deployments/kie-server.war $JBOSS_HOME/standalone/deployments/kie-server-archive.war && \
touch $JBOSS_HOME/standalone/deployments/kie-server.war.dodeploy && \
rm -rf $JBOSS_HOME/standalone/deployments/kie-server-archive.war

# Set right permissions for jboss user.
RUN chown -R jboss:jboss $JBOSS_HOME/standalone/deployments/* && \
chmod +x $JBOSS_HOME/bin/*.sh && \
chown jboss:jboss $JBOSS_HOME/bin/start_kie-server.sh && \
chown jboss:jboss $JBOSS_HOME/standalone/configuration/kie-server-users.properties && \ 
chown jboss:jboss $JBOSS_HOME/standalone/configuration/kie-server-roles.properties && \
chown jboss:jboss $JBOSS_HOME/standalone/configuration/standalone-full-kie-server.xml && \
chown -R jboss:jboss $JBOSS_HOME/modules/system/layers/base/*

# Switchback to jboss user
USER jboss

####### COMMAND ############
WORKDIR $JBOSS_HOME/bin/
CMD ["./start_kie-server.sh"]
