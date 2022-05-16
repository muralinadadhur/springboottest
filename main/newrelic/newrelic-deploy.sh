#!/bin/bash
# This script sends deployment notifications . ref - https://docs.newrelic.com/docs/agents/java-agent/instrumentation/record-deployments-java-agent
export GIT_REVISION=`cat ../image-info.txt  | grep "GIT_COMMIT" | sed -e "s/.*=//g"`
java -jar newrelic.jar deployment --environment=$CHEGG_ENV --revision=$GIT_REVISION
