#! /bin/bash
echo "Before executing the writeNpmrc function..."
writeNpmrc() {
    export JFROG_LOCATION=$1
    if [ -z "$JFROG_LOCATION" ]; then
        JFROG_LOCATION="//chegg.jfrog.io/chegg/api/npm/npm/"
    fi

    echo "Writing .npmrc to point all npm package retrievals to $JFROG_LOCATION"

    if [ -z "$JFROG_TOKEN" ]; then
        echo "No JFROG_TOKEN variable set."
        echo "This token should be set in your subgroup or project's GitLab variables."
        echo "Please speak with SRE to have a token generated and installed."
        exit 1
    fi

    # Decode token from global var:
    DECODED_TOKEN=$(echo $JFROG_TOKEN | base64 -d)

    printf "registry=https:${JFROG_LOCATION}\n" >> ~/.npmrc
    printf "@chegg:registry=https:${JFROG_LOCATION}\n" >> ~/.npmrc
    printf "${JFROG_LOCATION}:_authToken=${DECODED_TOKEN}\n" >> ~/.npmrc
    printf "${JFROG_LOCATION}:always-auth=true\n" >> ~/.npmrc
}
writeNpmrc
echo "After executing the writeNpmrc function..."
