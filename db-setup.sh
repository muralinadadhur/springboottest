#!/bin/bash
# This script follows the below steps to apply flyway DB migrations.
# 1. Download and Install POSTGRES using defined yum repo.
# 2. Create superuser username & password and database (which is used by the service to which flyway migrations will be applied.)
# 3. Download and install flyway.
# 4. Apply flyway migration.
# 5. Generate schema source using jOOQ code gen.

pushd /

yum install -y wget
yum install -y tar

#Step1: Download and Install POSTGRES Creating your own yum repo
touch /etc/yum.repos.d/pgdg.repo
echo $'[pgdg11] \n
name=PostgreSQL 11 $releasever - $basearch \n
baseurl=https://download.postgresql.org/pub/repos/yum/11/redhat/rhel-7.5-x86_64 \n
enabled=1 \n
gpgcheck=0 \n
gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-PGDG-11' > /etc/yum.repos.d/pgdg.repo
sed -i "s/rhel-\$releasever-\$basearch/rhel-7.5-x86_64/g" "/etc/yum.repos.d/pgdg.repo"
yum -y groupinstall "PostgreSQL Database Server 11 PGDG"

#Step2: Initialize POSTGRES DB
su postgres -c "/usr/pgsql-11/bin/pg_ctl -D /var/lib/pgsql/data/ initdb"
su postgres -c "/usr/pgsql-11/bin/pg_ctl  -D /var/lib/pgsql/data/ start"
su postgres -c "psql --command \"CREATE USER <USER_NAME> WITH SUPERUSER PASSWORD '<PASSWORD>';\"" # Exp: su postgres -c "psql --command \"flashtools WITH SUPERUSER PASSWORD 'flashtools';\""
su postgres -c "createdb -O <USER_NAME> <DB_NAME>" # Exp : su postgres -c "createdb -O flashtools deck"
su postgres -c "psql -U postgres -c 'show config_file'"
su postgres -c "/usr/pgsql-11/bin/pg_ctl  -D /var/lib/pgsql/data/ stop"

# Get the Flyway CLI. Eventually, we can replace the Flyway Gradle
# plugin with the CLI.
if [  $FLYWAY_ENABLED = "true" ]; then
    wget -qO- https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/6.0.1/flyway-commandline-6.0.1-linux-x64.tar.gz | tar xvz

    # Unpack the Flyway CLI
    ln -s `pwd`/flyway-6.0.1/flyway /usr/local/bin
    chmod 777 /usr/local/bin/flyway
fi

popd

#Start DB Server
su postgres -c "/usr/pgsql-11/bin/pg_ctl -D /var/lib/pgsql/data/ start"

#Create DB schema and extension if anything needed.
PGUSER=<USER_NAME> PGPASSWORD=<PASSWORD> PGHOST=localhost PGDATABASE=<DB_NAME> psql -c "create schema <SCHEMA_NAME>; create extension \"<EXTENSION_NAME>\";" #Exp: PGUSER=flashtools PGPASSWORD=flashtools PGHOST=localhost PGDATABASE=deck psql -c "create schema core; create extension \"uuid-ossp\";"

#Step4: Apply flyway migration.
if [  $FLYWAY_ENABLED = "true" ]; then
    flyway -url=jdbc:postgresql://localhost:5432/mydeck -user=flashtools -password=flashtools -schemas=public,public -locations=filesystem:./src/main/resources/db/migration migrate
fi

#Setp5: Generate JOOQ schema source .
if [  $JOOQ_ENABLED = "true" ]; then
    ./gradlew --no-daemon --info -PArtifactRelease=$IS_RELEASE_BUILD -PAPP_NAME=$APP_NAME -Penv=other -PCHEGG_ENV=$CHEGG_ENV generateDbJooqSchemaSource
fi

#Stop DB Server
su postgres -c "/usr/pgsql-11/bin/pg_ctl -D /var/lib/pgsql/data/ stop"
