#!/bin/bash

if [ ! "$ECS_CLUSTER" == "" ]; then
  export CHEGG_ENV="${ECS_CLUSTER}"
fi


# This function sets newrelic app name as environment variable name
chegg_newrelic_app_name() {
    export CHEGG_ENV_PREFIX=`echo $CHEGG_ENV | tr '[a-z]' '[A-Z]'`
    if [ ! "$NR_APP_NAME" == "" ]; then
        export nr_app_name="${NR_APP_NAME^}"
    else
        export nr_app_name="${APP_NAME^}"
    fi
    if [ -z "${NEW_RELIC_APP_NAME+xxx}" ]; then
        export NEW_RELIC_APP_NAME="$CHEGG_ENV_PREFIX - $NEW_RELIC_PREFIX$nr_app_name$NEW_RELIC_SUFFIX"
    fi
    echo "NewRelic App name is : $NEW_RELIC_APP_NAME"
}

# ref : https://confluence.cheggnet.com/display/BOOTS/2018/03/02/Keeping+up-to-date+with+NewRelic+agent
pick_latest_newrelic_agent() {
    if [ ! "$NEWRELIC_AGENT_VERSION" == "" ]; then
        echo "configured to pick latest newrelic agent from NewRelic download site"
        mkdir -p /apps/$APP_NAME/newrelic
        pushd /apps/$APP_NAME/newrelic

        if [ "$NEWRELIC_AGENT_VERSION" == "current" ] || [ "$NEWRELIC_AGENT_VERSION" == "established" ]; then
            NEWRELIC_FILE_SUFFIX=""
        else
            NEWRELIC_FILE_SUFFIX="-$NEWRELIC_AGENT_VERSION"
        fi
        ! /usr/bin/wget https://download.newrelic.com/newrelic/java-agent/newrelic-agent/$NEWRELIC_AGENT_VERSION/newrelic$NEWRELIC_FILE_SUFFIX.jar
        ! /usr/bin/wget https://download.newrelic.com/newrelic/java-agent/newrelic-agent/$NEWRELIC_AGENT_VERSION/newrelic-agent$NEWRELIC_FILE_SUFFIX.pom
        if [[ -f "/apps/$APP_NAME/newrelic/newrelic$NEWRELIC_FILE_SUFFIX.jar" && -f "/apps/$APP_NAME/newrelic/newrelic-agent$NEWRELIC_FILE_SUFFIX.pom" ]]; then
            export NEWRELIC_AGENT_VERSION=`cat /apps/$APP_NAME/newrelic/newrelic-agent$NEWRELIC_FILE_SUFFIX.pom | grep "\<version\>.*\<version\>"| sed -e "s/.*<version>//g" | sed -e "s/<\/version.*//g"`
            export NEWRELIC_JAR_PATH=/apps/$APP_NAME/newrelic/newrelic$NEWRELIC_FILE_SUFFIX.jar
            echo "downlaoded latest version of newrelic - $NEWRELIC_AGENT_VERSION from https://download.newrelic.com"
        fi

        if [ -z "$NEW_RELIC_EXTENSION_HIKARI_VERSION" ]; then
            echo "NewRelic monitoring for hikari connection pool is disabled"
        else
            echo "NewRelic monitoring for hikari connection pool is enabled"
            mkdir -p /apps/$APP_NAME/newrelic/extensions
            ! /usr/bin/wget https://download.newrelic.com/newrelic/java-agent/extensions/hikaricp-$NEW_RELIC_EXTENSION_HIKARI_VERSION.jar -P /apps/$APP_NAME/newrelic/extensions
        fi

        popd
    fi
}

docker_entry_point() {
    echo "chegg-env is $CHEGG_ENV"
    if [ -z "${NEW_RELIC_PREFIX+xxx}" ]; then
        export NEW_RELIC_PREFIX=""
    fi
    if [ -z "${NEW_RELIC_SUFFIX+xxx}" ]; then
        export NEW_RELIC_SUFFIX=""
    fi
    chegg_newrelic_app_name
    export NEWRELIC_JAR_PATH=`find /apps/$APP_NAME/newrelic -type f -name "newrelic-agent-*.jar"`
    pick_latest_newrelic_agent
    echo "NewRelic Java agent used is : $NEWRELIC_JAR_PATH"
    export JAVA_OPTS="$JAVA_OPTS -XX:MaxRAMPercentage=$JVM_MAX_RAM_PERCENTAGE -javaagent:$NEWRELIC_JAR_PATH -Dnewrelic.environment=$CHEGG_ENV -Dspring.profiles.active=$CHEGG_ENV -Dlog4j2.formatMsgNoLookups=true"
    cd /apps/$APP_NAME/bin && /apps/$APP_NAME/bin/$APP_NAME  $RELEASE_CHECKLIST_ARGS $*
}

docker_entry_point $*
