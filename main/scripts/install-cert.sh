#!/bin/sh

# This script is invoked to install certs. Invoke it from docker-entrypoint.sh with right parameters
export CERT_HOSTNAME=$1
export PORT=443
export KEYSTORE_FILE=$JAVA_HOME/lib/security/cacerts
export KEYSTORE_PASS=changeit

# get the SSL certificate
openssl s_client -connect ${CERT_HOSTNAME}:${PORT} </dev/null \
    | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > ${CERT_HOSTNAME}.cert
echo "downloaded the cert"

# create a keystore and import certificate
$JAVA_HOME/bin/keytool -import -noprompt -trustcacerts \
    -alias ${CERT_HOSTNAME} -file ${CERT_HOSTNAME}.cert \
    -keystore ${KEYSTORE_FILE} -storepass ${KEYSTORE_PASS}
echo "install the cert"

# verify we've got it.
$JAVA_HOME/bin/keytool -list -v -keystore ${KEYSTORE_FILE} -storepass ${KEYSTORE_PASS} -alias ${CERT_HOSTNAME}
echo "verified cert for host $CERT_HOSTNAME in keytool"