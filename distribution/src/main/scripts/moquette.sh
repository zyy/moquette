#!/bin/sh
#
# Copyright (c) 2012-2014 Andrea Selva
#

echo "                                                                         "
echo "  ___  ___                       _   _        ___  ________ _____ _____  "
echo "  |  \/  |                      | | | |       |  \/  |  _  |_   _|_   _| "
echo "  | .  . | ___   __ _ _   _  ___| |_| |_ ___  | .  . | | | | | |   | |   "
echo "  | |\/| |/ _ \ / _\ | | | |/ _ \ __| __/ _ \ | |\/| | | | | | |   | |   "
echo "  | |  | | (_) | (_| | |_| |  __/ |_| ||  __/ | |  | \ \/' / | |   | |   "
echo "  \_|  |_/\___/ \__, |\__,_|\___|\__|\__\___| \_|  |_/\_/\_\ \_/   \_/   "
echo "                   | |                                                   "
echo "                   |_|                                                   "
echo "                                                                         "                                                                      
                                                                      
                                                             

cd "$(dirname "$0")"

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

# Only set MOQUETTE_HOME if not already set
[ -f "$MOQUETTE_HOME"/bin/moquette.sh ] || MOQUETTE_HOME=`cd "$PRGDIR/.." ; pwd`
export MOQUETTE_HOME

# Set JavaHome if it exists
if [ -f "${JAVA_HOME}/bin/java" ]; then 
   JAVA=${JAVA_HOME}/bin/java
else
   JAVA=java
fi
export JAVA

LOG_FILE=$MOQUETTE_HOME/config/moquette-log.properties
MOQUETTE_PATH=$MOQUETTE_HOME/
#LOG_CONSOLE_LEVEL=info
#LOG_FILE_LEVEL=fine
JAVA_OPTS_SCRIPT="-XX:+HeapDumpOnOutOfMemoryError -Djava.awt.headless=true"

start(){
nohup $JAVA -server $JAVA_OPTS $JAVA_OPTS_SCRIPT -Dlog4j.configuration="file:$LOG_FILE" -Dmoquette.path="$MOQUETTE_PATH" -cp "$MOQUETTE_HOME/lib/moquette-broker-0.6-SNAPSHOT.jar:$MOQUETTE_HOME/lib/*" org.eclipse.moquette.server.Server $1>/dev/null 2>&1 &
echo $! > $MOQUETTE_HOME/bin/moquette.pid 
}

stop(){
if [ -f $MOQUETTE_HOME/bin/moquette.pid ]; then 
  PID=$(cat $MOQUETTE_HOME/bin/moquette.pid)
  kill -9 $PID
  rm -f $MOQUETTE_HOME/bin/moquette.pid
fi
}

case "$1" in
start)
  echo "Starting moquette..."
  start
  echo "Stop complete!"
  ;;
stop)
  echo "Stoping moquette..."
  stop
  echo "Stop complete!"
  ;;
restart)
  echo "Restarting moquette..."
  stop
  start
  echo "Restart complete!"
  ;;
*)
  printf 'Usage: %s {start|stop|restart}\n' "$prog"
  exit 1
  ;;
esac
