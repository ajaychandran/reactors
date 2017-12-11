#!/bin/bash

SCRIPT_PATH=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
SBT_OPTS="-Xms2G -Xmx4G -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256M"

if [ "$JENKINS_NIGHTLY_BUILD" == "true" ]; then
  SBT_ARGS="-Dsbt.log.noformat=true"
fi

if hash cygpath.exe 2>/dev/null; then
  echo "Using cygpath to convert path to SBT."
  SBT_CYG_JAR_PATH=`realpath "${SCRIPT_PATH}/sbt-launch.jar"`
  SBT_JAR_PATH=`cygpath.exe -w "${SBT_CYG_JAR_PATH}"`
  echo "Using Windows path: ${SBT_JAR_PATH}"
  SBT_ARGS="-Djline.terminal=jline.UnixTerminal -Dsbt.cygwin=true ${SBT_ARGS}"
else
  echo "No cygpath, apparently not using Cygwin."
  SBT_JAR_PATH="${SCRIPT_PATH}/sbt-launch.jar"
fi

SBT_CMD="java ${SBT_OPTS} ${SBT_ARGS} -jar \"${SBT_JAR_PATH}\""

if hash cygpath.exe 2>/dev/null; then
  stty -icanon min 1 -echo > /dev/null 2>&1
fi

echo "Running: ${SBT_CMD}"
echo "Arguments: $@"
eval ${SBT_CMD} $@

if hash cygpath.exe 2>/dev/null; then
  stty icanon echo > /dev/null 2>&1
fi
