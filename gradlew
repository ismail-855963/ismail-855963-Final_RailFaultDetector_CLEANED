#!/usr/bin/env sh
# Gradle wrapper shell script

DIR="$( cd "$( dirname "$0" )" && pwd )"
exec "$DIR/gradle/wrapper/gradle-wrapper.jar" "$@"
