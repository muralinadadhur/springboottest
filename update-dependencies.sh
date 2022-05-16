#!/bin/bash
export GRADLE_LOCKS_DIR=./gradle/dependency-locks

# Remove all the lock files to ensure a clean update (this can be removed once we migrate to Gradle 7
# and a single lock file is used instead of multiple)
rm -Rf $GRADLE_LOCKS_DIR

# Generate new lock files
./gradlew --refresh-dependencies dependencies --write-locks

# Add dependency locks directory to git so it can be committed
git add $GRADLE_LOCKS_DIR
