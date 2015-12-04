FROM vertx/vertx3

ENV VERTICLE_HOME /usr/verticles
ENV VERTICLE_NAME link.bek.poc.UserServiceVerticle

EXPOSE 8080

COPY ./verticles $VERTICLE_HOME

ENTRYPOINT ["sh", "-c"]
CMD ["vertx run -Dmongo.host=${MONGO_PORT_27017_TCP_ADDR} -Dmongo.port=${MONGO_PORT_27017_TCP_PORT} $VERTICLE_NAME -cp $VERTICLE_HOME/*"]
