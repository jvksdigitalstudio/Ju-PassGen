#!/bin/sh
#
# Gradle startup script for POSIX compatible shells

APP_HOME=$(cd "$(dirname "$0")" && pwd)
APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")

# JVM args — sin comillas extra alrededor
DEFAULT_JVM_OPTS="-Xmx64m -Xms64m"

die() {
    echo "ERROR: $*" >&2
    exit 1
}

# Buscar Java
if [ -n "$JAVA_HOME" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
    [ -x "$JAVACMD" ] || die "JAVA_HOME inválido: $JAVA_HOME"
else
    JAVACMD="java"
    command -v java >/dev/null 2>&1 || die "java no encontrado en PATH"
fi

CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

exec "$JAVACMD" $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS \
    "-Dorg.gradle.appname=$APP_BASE_NAME" \
    -classpath "$CLASSPATH" \
    org.gradle.wrapper.GradleWrapperMain "$@"
