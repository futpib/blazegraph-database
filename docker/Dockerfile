FROM maven:3.8.8-eclipse-temurin-21-alpine as builder

WORKDIR /usr/src/app

COPY ./ ./

ARG GIT_COMMIT="GIT_COMMIT docker build argument was not set during build"
ARG GIT_BRANCH="GIT_BRANCH docker build argument was not set during build"
ENV GIT_COMMIT=$GIT_COMMIT
ENV GIT_BRANCH=$GIT_BRANCH

RUN ./scripts/buildWar.sh

FROM jetty:9.4.53-jre21-alpine-eclipse-temurin

ENV JAVA_OPTS="-Xmx1g"

WORKDIR /var/lib/jetty

COPY --from=builder /usr/src/app/artifacts/blazegraph.war ./webapps/blazegraph.war
COPY --from=builder /usr/src/app/docker/RWStore.properties ./RWStore.properties
COPY --from=builder /usr/src/app/docker/entrypoint.sh ./entrypoint.sh

USER root

RUN set -xe; \
    mkdir /data; \
    chown jetty:jetty /data; \
    chmod a+rw /data;

USER jetty

VOLUME /data

CMD ./entrypoint.sh
