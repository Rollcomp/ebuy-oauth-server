FROM openjdk:8-alpine

ARG DIR=/opt/ebuy/oauth-server

ENV DIR $DIR

WORKDIR $DIR

COPY target/*.jar $DIR/

ENTRYPOINT java  $JAVA_OPTS $DEBUG -cp ebuy-oauth-server-0.0.1-SNAPSHOT.jar org.ebuy.OauthServerApplication


