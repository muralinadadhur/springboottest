FROM amazoncorretto:11 as buildImage
LABEL maintainer="Gopinath Rangappa"

ARG APP_NAME=release-checklist
ARG IS_RELEASE_BUILD=false

# place holders for CI (jenkins/Gitlab) to send Artifactory credentials through Docker build args.
ARG ARTIFACTORY_USER_NAME=
ARG ARTIFACTORY_PASSWORD=

# placeholders for AWS credentials to retrieve secrets during the build
ARG AWS_ACCESS_KEY_ID
ARG AWS_SECRET_ACCESS_KEY
ARG AWS_SESSION_TOKEN
ARG AWS_REGION

ARG ECS_CLUSTER=dev
ARG BUILD_CHEGG_ENV=$ECS_CLUSTER

# install the unzip command if it's not available
RUN if ! [ -x "$(command -v unzip)" ]; then yum install -y unzip; fi

# copy selected files to ensure we can create docker image layer with dependencies downloaded. Next docker build will be faster if this layer is available locally
ADD gradle /appcode/$APP_NAME/gradle
ADD gradlew* /appcode/$APP_NAME/
RUN cd /appcode/$APP_NAME && ./gradlew --no-daemon -version && cd -

ADD *.gradle /appcode/$APP_NAME/

ADD . /appcode/$APP_NAME
WORKDIR /appcode/$APP_NAME

# Setup flyway if its enabled for DB Migration. Also generate schema source using jOOQ code gen tool.
ADD src/main/resources/db/migration ./src/main/resources/db/migration
ENV FLYWAY_ENABLED=false
ENV JOOQ_ENABLED=false
ADD db-setup.sh .
RUN if [ $FLYWAY_ENABLED = "true" ] || [ $JOOQ_ENABLED = "true" ]; then ./db-setup.sh; fi

RUN ./gradlew -PBUILD_CHEGG_ENV=$BUILD_CHEGG_ENV -PAPP_NAME=$APP_NAME --no-daemon clean copyDependencies bootDistZip && cd build/distributions && /usr/bin/unzip -o $APP_NAME-boot.zip

FROM amazoncorretto:11 as runImage

RUN yum install -y wget

ENV LOG_LEVEL=WARN
# 20 is the default value that comes with the logback ref: http://logback.qos.ch/manual/appenders.html#AsyncAppender
# set this to 0 if you don't to loose any INFO, DEBUG, TRACE messages.
ENV LOG_DISCARDING_THRESHOLD=20
ENV CHEGG_ENV=local
ENV APP_NAME=release-checklist
ENV JVM_MAX_RAM_PERCENTAGE=70.0
ENV LANG en_US.utf8

# Possible values are "", "current", "established" OR exact version like 4.0.1.
# If value is "", the newrelic agent version in packaged docker image will be used.
# Please refer the versions at NewRelic Java agent download site : https://download.newrelic.com/newrelic/java-agent/newrelic-agent
ENV NEWRELIC_AGENT_VERSION=current
ENV SERVER_SERVLET_CONTEXT_PATH=/$APP_NAME
ENV NEW_RELIC_PREFIX=
ENV NEW_RELIC_SUFFIX=

COPY --from=buildImage /appcode/$APP_NAME/build/distributions/$APP_NAME-boot  /apps/$APP_NAME
COPY --from=buildImage /appcode/$APP_NAME/src/main/scripts  /apps/$APP_NAME/bin
COPY --from=buildImage /appcode/$APP_NAME/src/main/resources/cmc-template.yml  /apps/$APP_NAME/bin
COPY --from=buildImage /appcode/$APP_NAME/build/dependencies/newrelic-agent-* /apps/$APP_NAME/newrelic/
COPY --from=buildImage /appcode/$APP_NAME/src/main/newrelic  /apps/$APP_NAME/newrelic

ENTRYPOINT ["/apps/release-checklist/bin/docker-entrypoint.sh"]
